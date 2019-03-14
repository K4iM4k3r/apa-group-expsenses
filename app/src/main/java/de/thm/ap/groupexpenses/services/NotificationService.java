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
    private int NOTIFICATION = 1; // unique identifier for our notification
    public static boolean isRunning = false;
    public static NotificationService instance = null;

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
        instance = this;
        isRunning = true;
        notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        createNotificationChannels();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // everything related to event changes
        EventListLiveData eventListLiveData = DatabaseHandler.getEventListLiveData(currentUser.getUid());
        eventListLiveData.observeForever(newEventList -> {
            if (newEventList != null) {
                if (oldEventList == null) {
                    // old event list doesn't exist, create it (first call)
                    oldEventList = newEventList;
                } else {
                    for (Event new_event : newEventList) {
                        Event old_event = getOldEvent(new_event);
                        if (old_event == null) {
                            if (oldEventList.size() < newEventList.size()) {
                                // user has been added to an event
                                sendEventAddedNotification(new_event);
                                oldEventList = newEventList;
                                return;
                            }
                        } else {
                            for (Position new_position : new_event.getPositions()) {
                                Position old_position = getOldPosition(new_position, old_event);
                                if (old_position == null) {
                                    if (!new_position.getCreatorId().equals(currentUser.getUid())) {
                                        if (old_event.getPositions().size() < new_event.getPositions().size()) {
                                            // position has been added to an event
                                            sendPositionAddedNotification(new_event);
                                            oldEventList = newEventList;
                                            return;
                                        }
                                    }
                                } else {
                                    if (new_position.getCreatorId().equals(currentUser.getUid())) {
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
                }
            }
        });

        // everything related to friend list changes
        UserListLiveData userListLiveData = DatabaseHandler.getAllFriendsOfUser(currentUser.getUid());
        userListLiveData.observeForever(newFriendsList -> {
            if (newFriendsList != null) {
                if (oldFriendsList == null) {
                    // oldFriendsList list doesn't exist, create it (first call)
                    oldFriendsList = newFriendsList;
                } else {
                    for (User friend : newFriendsList) {
                        User old_friend = getOldUser(friend);
                        if (old_friend == null) {
                            if (oldFriendsList.size() < newFriendsList.size()) { // a user added you
                                sendFriendAddedYouNotification(friend.getNickname());
                                oldFriendsList = newFriendsList;
                                return;
                            }
                        }
                    }
                }
            }

        });

    }

    @Override
    public void onDestroy() {
        isRunning = false;
        instance = null;
        notificationManager.cancel(NOTIFICATION); // remove NotificationService
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
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), App.newEventID, intent, 0);

        Notification newEventNotification = new NotificationCompat.Builder(getApplicationContext(), Integer.toString(App.newEventID))
                .setSmallIcon(R.drawable.ic_event_black_24dp)
                .setContentTitle("Neues Event")
                .setContentText("Du wurdest zu dem Event " + new_event.getName() + " hinzugefügt!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        startForeground(NOTIFICATION, newEventNotification);
    }

    /**
     * Send position added notification
     */
    public void sendPositionAddedNotification(Event event) {
        Intent intent = new Intent(getApplicationContext(), PositionActivity.class);
        intent.putExtra("eventEid", event.getEid());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), App.newPositionID, intent, 0);

        Notification newPositionNotification = new NotificationCompat.Builder(getApplicationContext(), Integer.toString(App.newPositionID))
                .setSmallIcon(R.drawable.ic_event_black_24dp)
                .setContentTitle("Neue Position")
                .setContentText("Eine neue Position wurde zum Event " + event.getName() + " hinzugefügt!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        startForeground(NOTIFICATION, newPositionNotification);
    }

    /**
     * Send payment notification
     */
    public void sendPaymentCompletedNotification(String debtor_nickname) {
        Intent intent = new Intent(getApplicationContext(), EventActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), App.newPaymentID, intent, 0);

        Notification payNotification = new NotificationCompat.Builder(this, Integer.toString(App.newPaymentID))
                .setSmallIcon(R.drawable.ic_payment_black_24dp)
                .setContentTitle("Bezahlung erfolgt")
                .setContentText(debtor_nickname + " hat Schulden bei dir beglichen!")
                .setPriority(NotificationCompat.PRIORITY_MAX) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFICATION, payNotification);
    }

    /**
     * Send payment notification
     */
    public void sendFriendAddedYouNotification(String nickname_of_adder) {
        Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), App.newFriendID, intent, 0);

        Notification newFriendNotification = new NotificationCompat.Builder(this, Integer.toString(App.newFriendID))
                .setSmallIcon(R.drawable.ic_payment_black_24dp)
                .setContentTitle("Neue Freundschaft")
                .setContentText(nickname_of_adder + " hat sie hinzugefügt!")
                .setPriority(NotificationCompat.PRIORITY_MAX) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFICATION, newFriendNotification);
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