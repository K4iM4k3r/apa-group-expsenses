package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;
import android.util.ArrayMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.thm.ap.groupexpenses.App;

public class Stats {

    private static final String TAG = Stats.class.getName();

    public Map<Event, Float> calculateAll(List<Event> events){
        if (App.CurrentUser == null)
            throw new IllegalStateException("User not specified!");
        return calculateAll(App.CurrentUser, events);
    }

    public Map<Event, Float> calculateAll(User appuser, List<Event> events){
        Map<Event, Float> result = new HashMap<>();
        for (Event event: events){
            result.put(event, getEventBalance(appuser, event));
        }
        return result;
    }

    public float getBalance(@NonNull List<Event> events){
        if (App.CurrentUser == null)
            throw new IllegalStateException("User not specified!");
        return getBalance(App.CurrentUser, events);
    }

    public float getBalance(@NonNull User creator, @NonNull List<Event> events){
        float sum = 0.00f;
        for(Event e: events) sum += getEventBalance(creator, e);
        return sum;
    }



    public float getEventBalance(Event e){
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

