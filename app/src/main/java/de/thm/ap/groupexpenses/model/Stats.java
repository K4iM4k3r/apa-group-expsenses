package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.thm.ap.groupexpenses.App;

public class Stats {

    private static final String TAG = Stats.class.getName();

    public static Map<String, Float> getGlobalBalanceTable(User user, List<Event> events){
        Map<String, Float> result = new HashMap<>();
        for (Event event : events){
            event.getBalanceTable(user.getUid()).forEach((k, v) -> result.merge(k, v, Float::sum));
        }
        return result;
    }

    /**
     * Returns the unsettled events between a creditor and a debtor
     * @param creditorID ID of the creditor
     * @param debtorID ID of the debtor
     * @return List of unsettled events
     */
    public static List<Event> getOpenEvents(String creditorID, String debtorID, List<Event> events){
        ArrayList<Event> openEvents = new ArrayList<>();
        for (Event event : events){
            Map<String, Float> table = event.getBalanceTable(creditorID);
            if(table.containsKey(debtorID))
                openEvents.add(event);
        }
        return openEvents;
    }

    public static Map<Event, Float> calculateAll(List<Event> events){
        if (App.CurrentUser == null)
            throw new IllegalStateException("User not specified!");
        return calculateAll(App.CurrentUser, events);
    }

    public static Map<Event, Float> calculateAll(User appuser, List<Event> events){
        Map<Event, Float> result = new HashMap<>();
        for (Event event: events){
            result.put(event, getEventBalance(appuser, event));
        }
        return result;
    }

    public static float getBalance(@NonNull List<Event> events){
        if (App.CurrentUser == null)
            throw new IllegalStateException("User not specified!");
        return getBalance(App.CurrentUser, events);
    }

    public static float getBalance(@NonNull User creator, @NonNull List<Event> events){
        float sum = 0.00f;
        for(Event e: events) sum += getEventBalance(creator, e);
        return sum;
    }

    public static float getEventBalance(Event e){
        if (App.CurrentUser == null)
            throw new IllegalStateException("User not specified!");
        return getEventBalance(App.CurrentUser, e);
    }

    public static float getEventBalance(User appuser, Event e){
        return e.getBalance(appuser.getUid());
    }

    public static float getPositionBalance(Position p, Event e){
        if (App.CurrentUser == null)
            throw new IllegalStateException("User not specified!");
        return getPositionBalance(App.CurrentUser, p, e);
    }

    public static float getPositionBalance(User app_user, Position p, Event e){
        return p.getBalance(app_user.getUid(), e.getMembers());
    }

}

