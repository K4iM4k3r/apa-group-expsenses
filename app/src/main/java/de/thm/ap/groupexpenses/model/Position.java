package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class Position implements Serializable {

//    private HistoryValue<String> topic;
//    private HistoryValue<Integer> value;
    private int pid;
    private String topic;
    private Float value;
    private String info;
    private String date;
    private String creatorId;

    public Position(){}

    public Position(int positionId, String creatorId, String topic, Float value){
        this.pid = positionId;
        this.creatorId = creatorId;
        this.topic = topic;
        this.value = value;
//        this.topic = new HistoryValue<>(topic);
//        this.value = new HistoryValue<>(value);
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
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

//    public void setTopic(String topic){
//        this.topic.set(topic);
//    }
//    public void setValue(Integer value){
//        this.value.set(value);
//    }

//    public String getTopic(){
//        return topic.get();
//    }
//    public int getValue(){
//        return value.get();
//    }
    public String getCreatorId() {
        return creatorId;
    }

//    public Map<Date, String> getTopicHistory() {
//        return topic.getHistory();
//    }
//    public Map<Date, Integer> getValueHistory() {
//        return value.getHistory();
//    }
//
    public float getFactorizedValue(float factor){
        return (getValue() * factor);
    }

    @NonNull
    @Override
    public String toString() {
        return topic + ": " + value.toString() + " by " + creatorId;
    }
}
