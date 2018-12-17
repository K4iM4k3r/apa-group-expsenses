package de.thm.ap.groupexpenses;

import android.app.Application;
import android.content.Context;

import java.util.Arrays;
import java.util.List;

import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
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

    public static class TestValues {

        public static User USER = new User(0, "Nils", "Müller", "nMueller@mail.de");

        public static User[] USERS = new User[]{
                new User(1, "Jan", "Müller", "jMueller@mail.de"),
                new User(2, "Tom", "Müller", "tMueller@mail.de"),
                new User(3, "Sina", "Müller", "sMueller@mail.de"),
                new User(4, "Mia", "Müller", "mMueller@mail.de")
        };

        public static User[] MANY_USERS = new User[]{
                new User(1, "Jan", "Müller", "jMueller@mail.de"),
                new User(2, "Tom", "Müller", "tMueller@mail.de"),
                new User(3, "Sina", "Müller", "sMueller@mail.de"),
                new User(4, "Mia", "Müller", "mMueller@mail.de"),
                new User(5, "Jan", "Müller", "jMueller@mail.de"),
                new User(6, "Tom", "Müller", "tMueller@mail.de"),
                new User(7, "Sina", "Müller", "sMueller@mail.de"),
                new User(8, "Mia", "Müller", "mMueller@mail.de"),
                new User(9, "Jan", "Müller", "jMueller@mail.de"),
                new User(10, "Tom", "Müller", "tMueller@mail.de"),
                new User(11, "Sina", "Müller", "sMueller@mail.de"),
                new User(124, "Mia", "Müller", "mMueller@mail.de"),
                new User(1241, "Jan", "Müller", "jMueller@mail.de"),
                new User(1232, "Tom", "Müller", "tMueller@mail.de"),
                new User(1233, "Sina", "Müller", "sMueller@mail.de"),
                new User(534, "Mia", "Müller", "mMueller@mail.de"),
                new User(6431, "Jan", "Müller", "jMueller@mail.de"),
                new User(472, "Tom", "Müller", "tMueller@mail.de"),
                new User(933, "Sina", "Müller", "sMueller@mail.de"),
                new User(2344, "Mia", "Müller", "mMueller@mail.de"),
                new User(211, "Jan", "Müller", "jMueller@mail.de"),
                new User(432, "Tom", "Müller", "tMueller@mail.de"),
                new User(633, "Sina", "Müller", "sMueller@mail.de"),
                new User(124, "Mia", "Müller", "mMueller@mail.de"),
                new User(42311, "Jan", "Müller", "jMueller@mail.de"),
                new User(122, "Tom", "Müller", "tMueller@mail.de"),
                new User(53533, "Sina", "Müller", "sMueller@mail.de"),
                new User(124, "Mia", "Müller", "mMueller@mail.de"),
                new User(31, "Jan", "Müller", "jMueller@mail.de"),
                new User(23, "Tom", "Müller", "tMueller@mail.de"),
                new User(113, "Sina", "Müller", "sMueller@mail.de"),
                new User(44353, "Mia", "Müller", "mMueller@mail.de"),
                new User(6431, "Jan", "Müller", "jMueller@mail.de"),
                new User(852, "Tom", "Müller", "tMueller@mail.de"),
                new User(3546, "Sina", "Müller", "sMueller@mail.de"),
                new User(3454, "Mia", "Müller", "mMueller@mail.de")
        };


        public static Position[] POSITIONS_1 = new Position[]{
                new Position(USERS[1], "Bier", 90.f),
                new Position(USERS[2], "Sprit", 120.f),
                new Position(USERS[3], "Essen", 15.f)
        };

        public static Position[] POSITIONS_2 = new Position[]{
                new Position(USERS[3], "Bier", 15.f),
                new Position(USERS[1], "Sprit", 10.f),
                new Position(USERS[0], "Essen", 15.f),
                new Position(USERS[0], "Tee", 15.f),
                new Position(USERS[0], "Reinigung", 15.f)
        };

        public static Event EVENT = new Event(USER, "Festival", "Morgen", "", Arrays.asList(USERS), Arrays.asList(POSITIONS_1));

        public static Event[] EVENTS = new Event[]{
                new Event(USER, "Festival: Herzberg", "11.01.2020", "", Arrays.asList(USERS), Arrays.asList(POSITIONS_1)),
                new Event(USER, "Urlaub: Spanien", "13.02.2020", "", Arrays.asList(USERS), Arrays.asList(POSITIONS_2)),
                new Event(USER, "Urlaub: Schweiz", "31.12.2020", "", Arrays.asList(USERS)),
                new Event(USER, "Festival: Tomorrowland", "16.07.2020", "", Arrays.asList(USERS))
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
