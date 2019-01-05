package de.thm.ap.groupexpenses.model;

import java.io.Serializable;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HistoryValue<T> implements Serializable {

    private T value;
    private Map<Long, T> history;
    private final long creationDate;

    public HistoryValue(T value) {
        this.value = value;
        this.history = new HashMap<>();
        this.creationDate = getCurrentTime();
        history.put(this.creationDate, value);
    }

    public void set(T value) {
        this.value = value;
        history.put(getCurrentTime(), value);
    }

    public T get() {
        return value;
    }

    public Map<Long, T> getHistory() {
        return history;
    }

    public long getCreationDate() {
        return creationDate;
    }

    private long getCurrentTime() {
        return Calendar.getInstance().getTime().getTime();
    }

    @Override
    @NonNull
    public String toString() {
        return value.toString();
    }

}
