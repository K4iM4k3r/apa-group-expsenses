package de.thm.ap.groupexpenses;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static App instance;

    public static App getInstance(){
        return instance;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
