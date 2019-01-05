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
    private Long date;
    private String creatorId;

    public Position(){}

    public Position(String creatorId, String topic, Float value){
        this.creatorId = creatorId;
        this.topic = new HistoryValue<>(topic);
        this.value = new HistoryValue<>(value);
    }

    public Position(int positionId, String creatorId, String topic, Float value){
        this.pid = positionId;
        this.creatorId = creatorId;
        this.topic = new HistoryValue<>(topic);
        this.value = new HistoryValue<>(value);
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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
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
