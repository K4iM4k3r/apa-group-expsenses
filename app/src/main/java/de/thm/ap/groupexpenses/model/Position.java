package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class Position implements Serializable {

    private int id;
    private String info;
    private String date;
    private HistoryValue<String> topic;
    private HistoryValue<Float> value;
    private final User creator;

    public Position(User creator, String topic, Float value){
        this.id = (int)(Math.random() * 10000 + 1);     // rand num between 0 - 10000
        this.creator = creator;
        this.topic = new HistoryValue<>(topic);
        this.value = new HistoryValue<>(value);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        this.date = day + "." + month + "." + year;

        this.info = "";
    }

    public Position(User creator, String topic, Float value, String info){
        this(creator, topic, value);
        this.info = info;
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setInfo(String info) { this.info = info; }
    public String getInfo() {return info; }
    public void setValue(Float value){
        this.value.set(value);
    }
    public float getValue(){ return value.get(); }
    public void setTopic(String topic){
        this.topic.set(topic);
    }
    public String getTopic(){
        return topic.get();
    }

    public User getCreator() {
        return creator;
    }

    public Map<Date, String> getTopicHistory() {
        return topic.getHistory();
    }
    public Map<Date, Float> getValueHistory() {
        return value.getHistory();
    }

    public float getFactorizedValue(float factor){
        return (value.get()*factor);
    }

    @NonNull
    @Override
    public String toString() {
        return topic.toString() + ": " + value.toString() + " by " + creator.toString();
    }
}
