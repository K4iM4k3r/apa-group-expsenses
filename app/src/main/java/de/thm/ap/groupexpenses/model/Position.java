package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class Position implements Serializable {

    private int pid;
    private HistoryValue<String> topic;
    private HistoryValue<Float> value;
    private String info;
    private String date;
    private String creatorId;

    public Position(){}

    public Position(int positionId, String creatorId, String topic, Float value){
        this.pid = positionId;
        this.creatorId = creatorId;
        this.topic = new HistoryValue<>(topic);
        this.value = new HistoryValue<>(value);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        this.date = day + "." + month + "." + year;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTopic() {
        return topic.get();
    }

    public void setTopic(String topic) {
        this.topic.set(topic);
    }

    public Float getValue() {
        return value.get();
    }

    public void setValue(Float value) {
        this.value.set(value);
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public Map<Long, String> getTopicHistory() {
        return topic.getHistory();
    }
    public Map<Long, Float> getValueHistory() {
        return value.getHistory();
    }

    public float getFactorizedValue(float factor){
        return (getValue() * factor);
    }

    @NonNull
    @Override
    public String toString() {
        return topic + ": " + value.toString() + " by " + creatorId;
    }
}
