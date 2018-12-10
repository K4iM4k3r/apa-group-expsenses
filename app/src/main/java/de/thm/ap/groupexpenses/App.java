package de.thm.ap.groupexpenses;

import android.app.Application;
import android.content.Context;

import java.util.List;

import de.thm.ap.groupexpenses.model.User;

public class App extends Application {

    public static User CurrentUser; //to be set on Login/AppStart

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
    }

    public static String listToHTMLString(List<?> list) {
        String result = "<br>";
        for (int i = 0; i < list.size(); ++i) {
            result += list.get(i) + (i == list.size()-1 ? "" : "<br>");
        }
        result += "\b";
        return result;
    }

}
