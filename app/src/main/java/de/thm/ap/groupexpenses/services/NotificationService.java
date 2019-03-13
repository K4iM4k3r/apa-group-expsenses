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

    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    int Your_X_SECS = 60;
    private List<Event> oldEventList;
    NotificationManager notificationManager;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        startTimer();

        return START_STICKY;
    }


    @Override
    public void onCreate() {
        notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        stopTimerTask();
        super.onDestroy();
    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, Your_X_SECS * 1000); //
        //timer.schedule(timerTask, 5000,1000); //
    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(() -> {

                    // everything related to event changes
                    EventListLiveData listLiveData = DatabaseHandler.getEventListLiveData(App.CurrentUser.getUid());
                    listLiveData.observeForever(eventList -> {
                        if (oldEventList == null) {   // old event list doesn't exist, create it (first call)
                            oldEventList = eventList;
                        } else {    // old event list exists, look for changes
                            if (oldEventList.size() < eventList.size()) {
                                //  user has been added to an event
                                sendEventAddedNotification();
                            }
                        }
                    });



                });
            }
        };
    }

    /**
     * Send event invite notification
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