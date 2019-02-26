package de.thm.ap.groupexpenses;

import android.app.Application;
import android.content.Context;

import java.util.List;

import de.thm.ap.groupexpenses.model.User;

public class App extends Application {

    public static User CurrentUser; //to be set on Login/AppStart

    public static String HOST = "www.group-expenses-omran.firebaseapp.com";
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

    public static String listToHTMLString(List<?> list) {
        String result = "";
        for (int i = 0; i < list.size(); ++i) {
            result += list.get(i) + (i == list.size()-1 ? "" : ", ");
        }
        result += "\b";
        return result;
    }

    public static String listToString(List<?> list) {
        String result = "";
        for (int i = 0; i < list.size(); ++i) {
            result += list.get(i) + (i == list.size()-1 ? "" : ", ");
        }
        return result;
    }

}
