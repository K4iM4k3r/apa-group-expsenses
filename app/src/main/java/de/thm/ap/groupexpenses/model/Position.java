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

/* setter and getter are needed by firestore api
 *to store the models in the database
 * therefor ethe unused suppress
 */
@SuppressWarnings("unused")
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

    /**
     * Empty Constructor is requirement of firestore
     */
    public Position(){}

    /**
     * Create a new Position with the following parameters
     * @param creatorId UID of creator
     * @param topic name of the position
     * @param value cost value
     */
    public Position(@NonNull String creatorId, @NonNull String topic, @NonNull Float value){
        this(creatorId, topic, value, null);
    }

    /**
     * Create a new Position with the following parameters
     * @param creatorId UID of creator
     * @param topic name of the position
     * @param value cost value
     * @param info description of the position
     */
    public Position(@NonNull String creatorId, @NonNull String topic, @NonNull Float value, @Nullable String info){
        this(creatorId, topic, value, info, null);
    }

    /**
     *
     * Create a new Position with the following parameters
     * @param creatorId UID of creator
     * @param topic name of the position
     * @param value cost value
     * @param info description of the position
     * @param excludedPeople people which don´t have to pay
     */
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

    /**
     * Return the topic of the pos
     * @return topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Set the topic of the pos
     * @param topic new topic
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Return the Value of pos
     * @return cost value
     */
    public Float getValue() {
        return value;
    }

    /**
     * Set the value of the pos
     * @param value new value
     */
    public void setValue(Float value) {
        this.value = value;
    }

    /**
     * Return the description of the pos
     * @return pos description
     */
    public String getInfo() {
        return info;
    }

    /**
     * Set the description of the pos
     * @param info new pos description
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * Return the creation date of the pos as long since 1970
     * @return date of creation as long
     */
    public Long getDate() {
        return date;
    }

    /**
     * Return the date String of creation
     * @return date String of creation
     */
    public String getDateString() {
        Date date = new Date(this.date);
        Format format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(date);
    }

    /**
     * Set the creation date
     * @param date new creation date
     */
    public void setDate(Long date) {
        this.date = date;
    }

    /**
     * set the Creator UID
     * @return creator uid
     */
    public String getCreatorId() {
        return creatorId;
    }

    /**
     * Return a list of all people that don`t have to pay
     * @return list of peole that dont have to pay
     */
    public List<String> getPeopleThatDontHaveToPay() {
        return peopleThatDontHaveToPay;
    }

    //endregion

    //region expense-management
    /**
     * This will release the given debtor from any of his debts.
     * @param debtorId Debtor UID
     */
    public void removeDebtor(String debtorId){
        this.peopleThatDontHaveToPay.add(debtorId);
    }

    /**
     * Checks if the specified user is excluded from paying on this position.
     * @param debtorId debtor UID
     * @return true if user is in list, else false
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
     * @param creditorId debtor UID
     * @param involvedPeople list of involved people
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
     * @param userId  UID of user
     * @param involvedPeople list of people that are involved
     * @return the balance of the given user
     */
    float getBalance(String userId, List<String> involvedPeople){

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
     * @param userId  UID of user
     * @param involvedPeople list of people that are involved
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
     *  @param involvedPeople list of people that are involved
     * @return true is the position is closable, else false
     */
    public boolean isClosable(List<String> involvedPeople) {
        return peopleThatDontHaveToPay.containsAll(involvedPeople);
    }
    //endregion

    //region private methods

    /**
     * Checks if the given user is the creator of the pos
     * @param userId UID
     * @return true if UID is creator, else false
     */
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
