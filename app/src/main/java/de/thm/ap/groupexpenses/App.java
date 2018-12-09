package de.thm.ap.groupexpenses;

import android.app.Application;
import android.content.Context;

import java.util.Arrays;

import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;

public class App extends Application {

    public static User CurrentUser; //to be set on Login/AppStart

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

    public static class TestValues {

        public static User USER = new User(0, "Nils", "Müller", "nMueller@mail.de");

        public static User[] USERS = new User[]{
                new User(1, "Jan", "Müller", "jMueller@mail.de"),
                new User(2, "Tom", "Müller", "tMueller@mail.de"),
                new User(3, "Sina", "Müller", "sMueller@mail.de"),
                new User(4, "Mia", "Müller", "mMueller@mail.de")
        };

        public static Position[] POSITIONS_1 = new Position[]{
                new Position(USERS[1], "Bier", 90),
                new Position(USERS[2], "Sprit", 120),
                new Position(USERS[3], "Essen", 15)
        };

        public static Position[] POSITIONS_2 = new Position[]{
                new Position(USERS[3], "Bier", 15),
                new Position(USERS[1], "Sprit", 10),
                new Position(USERS[0], "Essen", 15),
                new Position(USERS[0], "Tee", 15),
                new Position(USERS[0], "Reinigung", 15)
        };

        public static Event EVENT = new Event(USER, "Festival", "Morgen", "", Arrays.asList(USERS), Arrays.asList(POSITIONS_1));

        public static Event[] EVENTS = new Event[]{
                new Event(USER, "Festival: Herzberg", "11.01.2020", "", Arrays.asList(USERS), Arrays.asList(POSITIONS_1)),
                new Event(USER, "Urlaub: Spanien", "13.02.2020", "", Arrays.asList(USERS), Arrays.asList(POSITIONS_2)),
                new Event(USER, "Urlaub: Schweiz", "31.12.2020", "", Arrays.asList(USERS)),
                new Event(USER, "Festival: Tomorrowland", "16.07.2020", "", Arrays.asList(USERS))
        };

    }
}
