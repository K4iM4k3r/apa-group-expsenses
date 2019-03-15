package de.thm.ap.groupexpenses;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.services.NotificationService;

public class App extends Application {
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

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            // app went to foreground, start NotificationService
            if (!NotificationService.isRunning) {
                // start NotificationService
                Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                startService(intent);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }
}

