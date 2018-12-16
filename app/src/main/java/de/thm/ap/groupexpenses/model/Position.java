package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.Map;

public class Position {

    int id;

    private HistoryValue topic;
    private HistoryValue value;

    private final User creator;

    public Position(User creator, String topic, Integer value){
        this.creator = creator;
        this.topic = new HistoryValue(topic);
        this.value = new HistoryValue(value);
    }

    public void setTopic(String topic){
        this.topic.set(topic);
    }
    public void setValue(Integer value){
        this.value.set(value);
    }

    public String getTopic(){
        return (String) topic.get();
    }
    public int getValue(){
        return (int) value.get();
    }
    public int getId() {
        return id;
    }
    public User getCreator() {
        return creator;
    }

    public Map<Date, String> getTopicHistory() {
        return topic.getHistory();
    }
    public Map<Date, Integer> getValueHistory() {
        return value.getHistory();
    }

    public float getFactorizedValue(float factor){
        return ((Integer)value.get()*factor);
    }

    @NonNull
    @Override
    public String toString() {
        return topic.toString() + ": " + value.toString() + " by " + creator.toString();
    }
}
