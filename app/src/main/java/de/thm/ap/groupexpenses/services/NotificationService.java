package de.thm.ap.groupexpenses.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.livedata.EventListLiveData;
import de.thm.ap.groupexpenses.livedata.UserListLiveData;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.view.activity.EventActivity;
import de.thm.ap.groupexpenses.view.activity.FriendsActivity;
import de.thm.ap.groupexpenses.view.activity.PositionActivity;

public class NotificationService extends Service {
    public static boolean isRunning = false;
    public static boolean isCaller = false;

    private List<Event> oldEventList;
    private List<User> oldFriendsList;
    private NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        createNotificationChannels();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        /*
         everything related to event changes
         ---
         var 'isCaller' always looks if we did the action by ourselves,
         if 'true' -> cancel the notification
          */
        EventListLiveData eventListLiveData = DatabaseHandler.getEventListLiveData(currentUser.getUid());
        eventListLiveData.observeForever(newEventList -> {
            if (newEventList == null) return;
            if (oldEventList == null) {
                // old event list doesn't exist, create it (first call)
                oldEventList = newEventList;
            } else {
                if (oldEventList.size() < newEventList.size()) {
                    // an event was added
                    for (Event new_event : newEventList) {
                        Event old_event = getOldEvent(new_event);
                        if (old_event == null) {
                            if (isCaller) {
                                isCaller = false;
                                oldEventList = newEventList;
                                return;
                            }
                            // user has been added to an event and is not the caller
                            sendEventAddedNotification(new_event);
                            oldEventList = newEventList;
                            return;
                        }
                    }
                } else if (oldEventList.size() == newEventList.size()) {
                    // a position was added or a payment was fulfilled
                    for (Event new_event : newEventList) {
                        Event old_event = getOldEvent(new_event);
                        for (Position new_position : new_event.getPositions()) {
                            Position old_position = getOldPosition(new_position, old_event);
                            if (old_position == null) {
                                // position was added
                                if (isCaller) {
                                    isCaller = false;
                                    oldEventList = newEventList;
                                    return;
                                }
                                sendPositionAddedNotification(new_event);
                                oldEventList = newEventList;
                                return;
                            } else {
                                // position is not new, look for changes in 'peopleThatDontHaveToPay'
                                // if it increased in size -> payment fulfilled

                                if (isCaller) {
                                    isCaller = false;
                                    oldEventList = newEventList;
                                    return;
                                }

                                if (!new_position.getCreatorId().equals(currentUser.getUid()))
                                    continue; // it's not our position, stop searching

                                if (old_position.getPeopleThatDontHaveToPay().size() < new_position.getPeopleThatDontHaveToPay().size()) {
                                    String debtor_uid_found = null;
                                    for (String debtor_who_just_payed_uid : new_position.getPeopleThatDontHaveToPay()) {
                                        if (!old_position.getPeopleThatDontHaveToPay().contains(debtor_who_just_payed_uid)) {
                                            debtor_uid_found = debtor_who_just_payed_uid;
                                            break;
                                        }
                                    }
                                    if (debtor_uid_found != null) {
                                        DatabaseHandler.queryUser(debtor_uid_found, debtor_who_just_payed -> {
                                            sendPaymentCompletedNotification(debtor_who_just_payed.getNickname());
                                            oldEventList = newEventList;
                                            return;
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        /*
         everything related to friend list changes
         ---
         var 'isCaller' always looks if we did the action by ourselves,
         if 'true' -> cancel the notification
          */
        UserListLiveData userListLiveData = DatabaseHandler.getAllFriendsOfUser(currentUser.getUid());
        userListLiveData.observeForever(newFriendsList -> {
            if (newFriendsList == null) return;
            if (oldFriendsList == null) {
                // oldFriendsList list doesn't exist, create it (first call)
                oldFriendsList = newFriendsList;
            } else {
                if (oldFriendsList.size() > newFriendsList.size()) {
                    // user was removed, so break
                    oldFriendsList = newFriendsList;
                    return;
                }
                for (User friend : newFriendsList) {
                    User old_friend = getOldUser(friend);
                    if (old_friend == null) {
                        if (isCaller) {
                            isCaller = false;
                            oldFriendsList = newFriendsList;
                            return;
                        }
                        sendFriendAddedYouNotification(friend.getNickname());
                        oldFriendsList = newFriendsList;
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    private Event getOldEvent(Event event) {
        for (Event old_event : oldEventList) {
            if (old_event.getEid().equals(event.getEid())) return old_event;
        }
        return null;
    }

    private Position getOldPosition(Position position, Event old_event) {
        for (Position old_position : old_event.getPositions()) {
            if (old_position.getDate().equals(position.getDate())) return old_position;
        }
        return null;
    }

    private User getOldUser(User user) {
        for (User old_user : oldFriendsList) {
            if (old_user.getUid().equals(user.getUid())) return old_user;
        }
        return null;
    }

    /**
     * Send event added notification
     */
    public void sendEventAddedNotification(Event new_event) {
        Intent intent = new Intent(getApplicationContext(), PositionActivity.class);
        intent.putExtra("eventEid", new_event.getEid());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                App.newEventID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Notification newEventNotification = new NotificationCompat.Builder(getApplicationContext(), Integer.toString(App.newEventID))
                .setSmallIcon(R.drawable.ic_event_black_24dp)
                .setContentTitle(getString(R.string.notification_new_event_title))
                .setContentText(getString(R.string.notification_new_event_text, new_event.getName()))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(App.newPositionID, newEventNotification);
    }

    /**
     * Send position added notification
     */
    public void sendPositionAddedNotification(Event event) {
        Intent intent = new Intent(getApplicationContext(), PositionActivity.class);
        intent.putExtra("eventEid", event.getEid());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                App.newPositionID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Notification newPositionNotification = new NotificationCompat.Builder(getApplicationContext(), Integer.toString(App.newPositionID))
                .setSmallIcon(R.drawable.ic_event_black_24dp)
                .setContentTitle(getString(R.string.notification_new_position_title))
                .setContentText(getString(R.string.notification_new_position_text, event.getName()))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(App.newPositionID, newPositionNotification);
    }

    /**
     * Send payment notification
     */
    public void sendPaymentCompletedNotification(String debtor_nickname) {
        Intent intent = new Intent(getApplicationContext(), EventActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                App.newPaymentID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Notification payNotification = new NotificationCompat.Builder(this, Integer.toString(App.newPaymentID))
                .setSmallIcon(R.drawable.ic_payment_black_24dp)
                .setContentTitle(getString(R.string.notification_new_payment_title))
                .setContentText(getString(R.string.notification_new_payment_text, debtor_nickname))
                .setPriority(NotificationCompat.PRIORITY_MAX) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(App.newPositionID, payNotification);
    }

    /**
     * Send payment notification
     */
    public void sendFriendAddedYouNotification(String nickname_of_adder) {
        Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                App.newFriendID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Notification newFriendNotification = new NotificationCompat.Builder(this, Integer.toString(App.newFriendID))
                .setSmallIcon(R.drawable.ic_payment_black_24dp)
                .setContentTitle(getString(R.string.notification_new_friend_title))
                .setContentText(getString(R.string.notification_new_friend_text, nickname_of_adder))
                .setPriority(NotificationCompat.PRIORITY_MAX) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(App.newPositionID, newFriendNotification);
    }

    /**
     * This method creates the Notification Channels.
     * It defines the Notification message and its system importance.
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Check if the Device API-Level is Oreo or higher
            NotificationChannel new_payment = new NotificationChannel(
                    Integer.toString(App.newPaymentID),
                    "NEW_PAYMENT",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationChannel new_event = new NotificationChannel(
                    Integer.toString(App.newEventID),
                    "NEW_EVENT",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationChannel new_position = new NotificationChannel(
                    Integer.toString(App.newPositionID),
                    "NEW_POSITION",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationChannel new_friend = new NotificationChannel(
                    Integer.toString(App.newFriendID),
                    "NEW_FRIEND",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(new_payment);
            notificationManager.createNotificationChannel(new_event);
            notificationManager.createNotificationChannel(new_position);
            notificationManager.createNotificationChannel(new_friend);
        }
    }
}