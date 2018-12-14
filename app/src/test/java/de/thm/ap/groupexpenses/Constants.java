package de.thm.ap.groupexpenses;

import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;

public class Constants {

    static User[] member = new User[]{
            new User(1, "Jan", "Müller", "jMueller@mail.de"),
            new User(2, "Tom", "Müller", "tMueller@mail.de"),
            new User(3, "Sina", "Müller", "sMueller@mail.de"),
            new User(4, "Mia", "Müller", "mMueller@mail.de")
    };

    static Position[] positions = new Position[]{
            new Position(member[1], "Bier", 90),
            new Position(member[2], "Sprit", 120),
            new Position(member[3], "Essen", 15)
    };

    static User creator = new User(0, "Nils", "Müller", "nMueller@mail.de");

}
