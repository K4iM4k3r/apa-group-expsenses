package de.thm.ap.groupexpenses.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Position implements Serializable{

    int id;

    private String topic;
    private float value;
    private final User creator;

    private List<String> topicHistory;
    private List<Float> valueHistory;

    public Position(User creator){
        this.creator = creator;
        topicHistory = new ArrayList<>();
        valueHistory = new ArrayList<>();
    }
    public Position(User creator, String topic, float value){
        this.creator = creator;
        this.topic = topic;
        this.value = value;
        this.topicHistory = new ArrayList<>();
        this.valueHistory = new ArrayList<>();

        topicHistory.add(topic);
        valueHistory.add(value);
    }

    public void setTopic(String topic){
        this.topic = topic;
        topicHistory.add(topic);
    }
    public void setValue(float value){
        this.value = value;
        valueHistory.add(value);
    }

    public String getTopic(){
        return topic;
    }
    public float getValue(){
        return value;
    }
    public int getId() {
        return id;
    }
    public User getCreator() {
        return creator;
    }
    public String getTopicHistory() {
        return TextUtils.join(", ", topicHistory);
    }
    public String getValueHistory() {
        return TextUtils.join(", ", valueHistory);
    }

    public float getFactorizedValue(float factor){
        return value*factor;
    }

}
