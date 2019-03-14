package de.thm.ap.groupexpenses.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.livedata.EventListLiveData;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.view.activity.EventActivity;
import de.thm.ap.groupexpenses.view.activity.PositionActivity;

public class NotificationService extends Service {
    private List<Event> oldEventList;
    NotificationManager notificationManager;

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
        notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        // everything related to event changes
        EventListLiveData listLiveData = DatabaseHandler.getEventListLiveData(App.CurrentUser.getUid());
        listLiveData.observeForever(newEventList -> {
            if (newEventList != null) {
                if (oldEventList == null) {
                    // old event list doesn't exist, create it (first call)
                    oldEventList = newEventList;
                } else {
                    for (Event new_event : newEventList) {
                        Event old_event = getOldEvent(new_event);
                        if (old_event == null) {
                            if (oldEventList.size() < newEventList.size()) {
                                //  user has been added to an event
                                sendEventAddedNotification();
                                oldEventList = newEventList;
                                return;
                            }
                        } else {
                            for (Position new_position : new_event.getPositions()) {
                                Position old_position = getOldPosition(new_position, old_event);
                                if (old_position == null) {
                                    if (!new_position.getCreatorId().equals(App.CurrentUser.getUid())) {
                                        if (old_event.getPositions().size() < new_event.getPositions().size()) {
                                            //  position has been added to an event
                                            sendPositionAddedNotification(new_event.getEid());
                                            oldEventList = newEventList;
                                            return;
                                        }
                                    }
                                } else {
                                    if (new_position.getCreatorId().equals(App.CurrentUser.getUid())) {
                                        if (old_position.getPeopleThatDontHaveToPay().size() < new_position.getPeopleThatDontHaveToPay().size()) {
                                            sendPaymentCompletedNotification(new_event.getEid());
                                            oldEventList = newEventList;
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
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

    /**
     * Send event added notification
     */
    public void sendEventAddedNotification() {
        Intent intent = new Intent(getApplicationContext(), EventActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Notification eventNotification = new NotificationCompat.Builder(getApplicationContext(), App.newEventID)
                .setSmallIcon(R.drawable.ic_event_black_24dp)
                .setContentTitle("Geldsammler Event invite")
                .setContentText("Event einladung")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(2, eventNotification);
    }

    /**
     * Send position added notification
     */
    public void sendPositionAddedNotification(String eventEid) {
        Intent intent = new Intent(getApplicationContext(), PositionActivity.class);
        intent.putExtra("eventEid", eventEid);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Notification positionNotification = new NotificationCompat.Builder(getApplicationContext(), App.newEventID)
                .setSmallIcon(R.drawable.ic_event_black_24dp)
                .setContentTitle("Position was added to event")
                .setContentText("Position added")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(2, positionNotification);
    }

    /**
     * Send payment notification
     */
    public void sendPaymentCompletedNotification(String eventID) {
        Intent intent = new Intent(getApplicationContext(), PositionActivity.class);
        intent.putExtra("eventEid", eventID);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Notification payNotification = new NotificationCompat.Builder(this, App.PaymentID)
                .setSmallIcon(R.drawable.ic_payment_black_24dp)
                .setContentTitle("Geldsammler Bezahlung")
                .setContentText("Bezahlung ist erfolgt!")
                .setPriority(NotificationCompat.PRIORITY_HIGH) // triggers if API-Level is below Oreo
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // dismiss Notification if tapped
                .build();
        notificationManager.notify(1, payNotification);
    }
}