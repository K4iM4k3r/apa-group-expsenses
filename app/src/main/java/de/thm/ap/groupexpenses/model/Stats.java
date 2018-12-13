package de.thm.ap.groupexpenses.model;

import de.thm.ap.groupexpenses.App;

public class Stats {

    private static final String TAG = Stats.class.getName();

    public static float getEventBalance(Event e){
        if (App.CurrentUser == null){
            throw new IllegalStateException("User not specified!");
        }
        return getEventBalance(App.CurrentUser, e);
    }

    public static float getEventBalance(User appuser, Event e){
        float balance = 0;
        for (Position pos: e.getPositions()) {
            //The current user made the position - gets money
            if (appuser.equals(pos.getCreator())){
                balance += pos.getFactorizedValue(e.getPositionFactor(true));
                continue;
            }
            // App user is participant - has to pay money
            balance -= pos.getFactorizedValue(e.getPositionFactor(false));
        }
        return balance;
    }

}

