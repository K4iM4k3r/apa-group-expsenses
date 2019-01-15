package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;

import org.apache.commons.lang3.NotImplementedException;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Position {

    //private HistoryValue<String> topic;
    //private HistoryValue<Float> value;
    private String creatorId;
    private String topic;
    private float value;

    private String info;
    private Long date;

    private List<String> peopleThatDontHaveToPay;
    
    //region constructor
    public Position(){
        // this should not be user - only for firebase!
        // throw Exception on use maybe
        peopleThatDontHaveToPay = new ArrayList<>();
    }

    public Position(@NonNull String creatorId, @NonNull String topic, @NonNull Float value){
        this(creatorId, topic, value, null);
    }

    public Position(@NonNull String creatorId, @NonNull String topic, @NonNull Float value, @Nullable String info){
        this(creatorId, topic, value, info, null);
    }

    public Position(@NonNull String creatorId, @NonNull String topic, @NonNull Float value,
                    @Nullable String info, @Nullable List<String> excludedPeople) {

        this.creatorId = creatorId;
        this.topic = topic;
        this.info = info==null?"":info;
        this.value = value;
        this.date = Calendar.getInstance().getTimeInMillis();

        this.peopleThatDontHaveToPay = new ArrayList<>();
        this.peopleThatDontHaveToPay.add(creatorId);

        if (excludedPeople != null)
            this.peopleThatDontHaveToPay.addAll(excludedPeople);
    }


    //endregion

    //region getter/setter
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
    public Long getDate() {
        return date;
    }
    public String getDateString() {
        Date date = new Date(this.date);
        Format format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(date);
    }
    public void setDate(Long date) {
        this.date = date;
    }
    public String getCreatorId() {
        return creatorId;
    }
    //endregion

    //region expense-management
    /**
     * This will release the given debtor from any of his debts.
     */
    public void removeDebtor(String debtorId){
        this.peopleThatDontHaveToPay.add(debtorId);
    }

    /**
     * Checks if the specified user is excluded from paying on this position.
     */
    public boolean isExcludedFromPayments(String debtorId) {
        return peopleThatDontHaveToPay.contains(debtorId);
    }

    /**
     * Returns debts of single person. The debt is owed to the creator.
     * @param userCount amount of users that share the costs including the creator
     * @return debts of user specified in relation to the amount of users given. This will return
     *          a value for people that are not involved in the position!
     */
    public float getDebtOfUser(String userId, int userCount) {
        if (isExcludedFromPayments(userId)) return 0.f;
        return (1.f/userCount)*value;
    }

    /**
     * Calculates the amount of money the specified user has to retrieve from the involved people.
     * @return >0:amount, 0 if user is not the creator and therefore wont get money.
     */
    public float getCredit(String creditorId, List<String> involvedPeople) {
        if (!isCreator(creditorId)) return 0.f;

        float credit = 0.f;
        for(String userId : involvedPeople){
            credit += getDebtOfUser(userId, involvedPeople.size());
        }
        return credit;
    }

    /**
     * Calculates the balance the given user has in the event.
     * Simpler form of getBalanceMap without information who owes whom.
     */
    public float getBalance(String userId, List<String> involvedPeople){

        // user is not involved
        if (!involvedPeople.contains(userId)) return 0f;

        // user is creator - gets money
        if (isCreator(userId)) {
            List<String> debtors = new ArrayList<>(involvedPeople);
            debtors.removeAll(peopleThatDontHaveToPay);
            float factor = ((float)debtors.size())/involvedPeople.size();
            return value*factor;
        }

        // user is not the creator but excluded from all payments - he has no debts or credits.
        if (isExcludedFromPayments(userId)) return 0f;

        // user is debtor, has to pay 1 part of the price
        float factor = 1f/involvedPeople.size();
        return -(value*factor);
    }

    /**
     * Gives the position balance for specified userId.
     * @return Map of debt relations
     */
    public Map<String,Float> getBalanceMap(String userId, List<String> involvedPeople){
        Map<String, Float> result = new HashMap<>();

        // user is not related to the position
        if(!involvedPeople.contains(userId))
            return result;

        // user created the position - gets money from others
        if (isCreator(userId)){
            for(String person: involvedPeople){
                float debt = getDebtOfUser(person, involvedPeople.size());
                if (Float.compare(debt, 0.f)!=0) result.put(person, debt);
            }
            return result;
        }

        // user is not the creator but excluded from all payments - he has no debts or credits.
        if(isExcludedFromPayments(userId))
            return result;

        // user is debtor, owes to creator only
        result.put(creatorId, -getDebtOfUser(userId, involvedPeople.size()));
        return result;
    }

    /**
     * Checks if all involvedPeople are excluded from payments.
     */
    public boolean isClosable(List<String> involvedPeople) {
        return peopleThatDontHaveToPay.containsAll(involvedPeople);
    }
    //endregion

    //region private methods
    private boolean isCreator(String userId){
        return this.creatorId.equals(userId);
    }
    //endregion

    @NonNull
    @Override
    public String toString() {
        return topic + ": " + value + " by " + creatorId;
    }
}
