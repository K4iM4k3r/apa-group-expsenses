package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Position implements Serializable {

    private int id;
    private HistoryValue<String> topic;
    private HistoryValue<Integer> value;
    private final User creator;

    public Position(User creator, String topic, Float value){
        this.creator = creator;
        this.topic = new HistoryValue<>(topic);
        this.value = new HistoryValue<>(value);
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setTopic(String topic){
        this.topic.set(topic);
    }
    public void setValue(Float value){
        this.value.set(value);
    }

    public int getId() {
        return id;
    }
    public String getTopic(){
        return topic.get();
    }
    public float getValue(){
        return (float) value.get();
    }
    public float getId() {
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
        return ((Float)value.get()*factor);
    }

    @NonNull
    @Override
    public String toString() {
        return topic.toString() + ": " + value.toString() + " by " + creator.toString();
    }
}
