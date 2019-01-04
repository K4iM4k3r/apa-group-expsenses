package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class Position implements Serializable {

//    private HistoryValue<String> topic;
//    private HistoryValue<Integer> value;
    private String topic;
    private Integer value;
    private String creatorId;

    public Position(){}

    public Position(String creatorId, String topic, Integer value){
        this.creatorId = creatorId;
        this.topic = topic;
        this.value = value;
//        this.topic = new HistoryValue<>(topic);
//        this.value = new HistoryValue<>(value);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
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
    public String getcreatorId() {
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
