package de.thm.ap.groupexpenses;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
    public static final int newEventID = 1;
    public static final int newPositionID = 2;
    public static final int newPaymentID = 3;
    public static final int newFriendID = 4;

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
        registerActivityLifecycleCallbacks(new AppLifecycleTracker());
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
        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                // app went to foreground, stop NotificationService
                //stopService(new Intent(App.this, NotificationService.class));

                if (NotificationService.isRunning) {
                    // stop NotificationService
                    Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                    stopService(intent);
                }
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                // app went to background, start NotificationService
                // startService(new Intent(App.this, NotificationService.class));
                if (!NotificationService.isRunning) {
                    // start NotificationService
                    Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                    startService(intent);
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }

    private static ActivityManager.RunningServiceInfo getRunningServiceInfo(Class serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return service;
            }
        }
        return null;
    }
}

