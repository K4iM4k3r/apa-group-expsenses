package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Position implements Serializable {

    private int pid;
    private HistoryValue<String> topic;
    private HistoryValue<Float> value;
    private String info;
    private Long date;
    private String creatorId;
    private List<String> peopleThatDontHaveToPay;

    //region constructor
    public Position(){}
    public Position(String creatorId, String topic, Float value){
        this.creatorId = creatorId;
        this.topic = new HistoryValue<>(topic);
        this.value = new HistoryValue<>(value);
        this.peopleThatDontHaveToPay = new ArrayList<>();
        this.peopleThatDontHaveToPay.add(creatorId);
    }
    public Position(int positionId, String creatorId, String topic, Float value){
        this.pid = positionId;
        this.creatorId = creatorId;
        this.topic = new HistoryValue<>(topic);
        this.value = new HistoryValue<>(value);
        this.peopleThatDontHaveToPay = new ArrayList<>();
        this.peopleThatDontHaveToPay.add(creatorId);
    }
    public Position(int positionId, String creatorId, String topic, Float value, List<String> excludedPeople){
        this.pid = positionId;
        this.creatorId = creatorId;
        this.topic = new HistoryValue<>(topic);
        this.value = new HistoryValue<>(value);
        this.peopleThatDontHaveToPay = new ArrayList<>();
        this.peopleThatDontHaveToPay.add(creatorId);
        this.peopleThatDontHaveToPay.addAll(excludedPeople);
    }
    //endregion

    //region getter/setter
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
    //endregion

    //region public methods

    public void removeDebtor(String debtorId){
        this.peopleThatDontHaveToPay.add(debtorId);
    }
    public boolean hasDebts(String debtorId) {
        return !peopleThatDontHaveToPay.contains(debtorId);
    }

    /**
     * Returns debts of single person. The debt is owed to the creator.
     * @param userId
     * @param userCount amount of users that share the costs including the creator
     * @return
     */
    public float getDebtOfUser(String userId, int userCount) {
        if (!hasDebts(userId)) return 0.f;
        return (1.f/userCount)*value.get();
    }

    public float getCredit(String creditorId, List<String> involvedPeople) {
        if (!isCreator(creditorId)) return 0.f;

        float credit = 0.f;

        for(String userId : involvedPeople){
            credit += getDebtOfUser(userId, involvedPeople.size());
        }

        return credit;
    }

    public boolean isClosable(List<String> involvedPeople) {
        return peopleThatDontHaveToPay.containsAll(involvedPeople);
    }

    public float getFactorizedValue(float factor){
        return (getValue() * factor);
    }

    @NonNull
    @Override
    public String toString() {
        return topic + ": " + value.toString() + " by " + creatorId;
    }
    //endregion

    private boolean isCreator(String userId){
        return this.creatorId.equals(userId);
    }
}
