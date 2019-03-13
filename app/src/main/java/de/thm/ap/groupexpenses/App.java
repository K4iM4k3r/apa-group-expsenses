package de.thm.ap.groupexpenses;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.services.NotificationService;

public class App extends Application {
    public static final String PaymentID = "payment";
    public static final String newEventID = "eventCreated";

    public static User CurrentUser; //to be set on Login/AppStart

    public static String HOST = "group-expenses-omran.firebaseapp.com";
    public static String BASE_URL = "https://" + HOST + "/";

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        createNotifcationChannels();

        registerActivityLifecycleCallbacks(new AppLifecycleTracker());
    }

    /**
     * This method creates the Notification Channels.
     * It defines the Notification message and its system importance.
     */
    private void createNotifcationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Check if the Device API-Level is Oreo or higher
            NotificationChannel payment = new NotificationChannel(
                    PaymentID,
                    "User paid his debt for position",
                    NotificationManager.IMPORTANCE_HIGH
            );
            payment.setDescription("Notifications for debt payments");
            NotificationChannel eventCreated = new NotificationChannel(
                    newEventID,
                    "You have been added to an event",
                    NotificationManager.IMPORTANCE_HIGH
            );
            eventCreated.setDescription("Notifications for event invites");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(payment);
            manager.createNotificationChannel(eventCreated);
        }
    }

    public static class TestValues {
        public static User USER = new User("0", "Nils", "Müller", "nicki", "nMueller@mail.de", null, null);

        public static User[] USERS = new User[]{
                new User("1", "Jan", "Müller", "", "jMueller@mail.de", null, null),
                new User("2", "Tom", "Müller", "", "tMueller@mail.de", null, null),
                new User("3", "Sina", "Müller", "", "sMueller@mail.de", null, null),
                new User("4", "Mia", "Müller", "", "mMueller@mail.de", null, null)
        };
    }

    public static String listToString(List<?> list) {
        String result = "";
        for (int i = 0; i < list.size(); ++i) {
            result += list.get(i) + (i == list.size() - 1 ? "" : ", ");
        }
        return result;
    }

    public static String getDateFromLong(long date) {
        Format format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(date);
    }

    private class AppLifecycleTracker implements ActivityLifecycleCallbacks {
        private int numStarted = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {}

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                // app went to foreground, stop NotificationService
                stopService(new Intent(getApplicationContext(), NotificationService.class));
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {}

        @Override
        public void onActivityPaused(Activity activity) {}

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                // app went to background, start NotificationService
                startService(new Intent(getApplicationContext(), NotificationService.class));
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

        @Override
        public void onActivityDestroyed(Activity activity) {}
    }
}

