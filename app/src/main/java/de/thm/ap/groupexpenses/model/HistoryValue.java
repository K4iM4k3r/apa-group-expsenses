package de.thm.ap.groupexpenses.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HistoryValue<T> {

    private T value;
    private Map<Date, T> history;
    private final Date creationDate;

    public HistoryValue(T value){
        this.value = value;
        this.history = new HashMap<>();
        this.creationDate = getCurrentTime();
        history.put(this.creationDate, value);
    }

    public void set(T value){
        this.value = value;
        history.put(getCurrentTime(), value);
    }

    public T get(){
        return value;
    }

    public Map<Date, T> getHistory() {
        return history;
    }

    public Date getCreationDate(){
        return creationDate;
    }

    private Date getCurrentTime(){
        return Calendar.getInstance().getTime();
    }

    @Override
    public String toString(){
        return value.toString();
    }


}
