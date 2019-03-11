package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* setter and getter are needed by firestore api
 *to store the models in the database
 * therefor ethe unused suppress
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Event {

    private String eid;
    private String name;
    private Long date_begin;
    private Long date_end;
    private Long date_deadlineDay;
    private String info;
    private String creatorId;
    private List<String> members;
    private List<String> activeMembers;
    private List<Position> positions;

    // NonDB-Stuff

    /**
     * LifecycleStates
     */
    public enum LifecycleState {
        /**
         * Event is not started yet
         */
        UPCOMING,
        /**
         * Event is live now
         */
        LIVE,
        /**
         * Event lock, now the member can pay their depts
         */
        LOCKED,
        /**
         * Event is closed the pay period is over
         */
        CLOSED,
        /**
         * Event is broken
         */
        ERROR
    }

    //region Constructor

    /**
     * Empty Constructor is requirement of firestore
     */
    public Event() { }

    /**
     * Creates a new Event contains the following parameters
     * @param creatorId Id (UID) of Creator
     * @param name name of the event
     * @param date_begin Date of the beginning as long time since 1970
     * @param date_end Date of the ending as long
     * @param date_deadlineDay Date of the ending of payment period
     * @param info Info text of the event
     * @param members List of uid of the members
     * @param positions List of Positions
     */
    public Event(String creatorId, String name, Long date_begin, Long date_end, Long date_deadlineDay, String info, List<String> members, List<Position> positions) {
        this.name = name;
        this.date_begin = date_begin;
        this.date_end = date_end;
        this.date_deadlineDay = date_deadlineDay;
        this.info = info;
        this.creatorId = creatorId;
        this.members = new ArrayList<>();
        this.members.addAll(members);
        this.activeMembers = new ArrayList<>();
        this.activeMembers.addAll(members);
        addMember(creatorId);
        this.positions = positions;
    }

    /**
     * Creates a new Event contains the following parameters
     * @param creatorId Id (UID) of Creator
     * @param name name of the event
     * @param date_begin Date of the beginning as long time since 1970
     * @param date_end Date of the ending as long
     * @param date_deadlineDay Date of the ending of payment period
     * @param info Info text of the event
     * @param members List of uid of the members
     */
    public Event(String creatorId, String name, Long date_begin, Long date_end, Long date_deadlineDay, String info, List<String> members) {
        this(creatorId, name, date_begin, date_end, date_deadlineDay, info, members, new ArrayList<>());
    }

    /**
     * Creates a new Event contains the following parameters
     * @param creatorId Id (UID) of Creator
     * @param name name of the event
     * @param date_begin Date of the beginning as long time since 1970
     * @param date_end Date of the ending as long
     * @param date_deadlineDay Date of the ending of payment period
     * @param info Info text of the event
     */
    public Event(String creatorId, String name, Long date_begin, Long date_end, Long date_deadlineDay, String info) {
        this(creatorId, name, date_begin, date_end, date_deadlineDay, info, new ArrayList<>());
    }
    //endregion

    //region Getter/Setter/Adder

    /**
     * Return the Event ID
     * @return event id as a string
     */
    public String getEid() {
        return eid;
    }

    /**
     * Set the eid of event
     * @param eid EventId
     */
    public void setEid(String eid) {
        this.eid = eid;
    }

    /**
     * Return the name of the event
     * @return name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the event
     * @param name  name of event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the Date of beginning as a long since 1970
     * @return date as long
     */
    public Long getDate_begin() {
        return date_begin;
    }

    /**
     * Set the date of event beginning
     * @param date_begin Date as long since 1970
     */
    public void setDate_begin(Long date_begin) {
        this.date_begin = date_begin;
    }

    /**
     * Return the date of the ending of the event
     * @return date as long since 1970
     */
    public Long getDate_end() {
        return date_end;
    }

    /**
     * Set the ending date of the event
     * @param date_end Date as long since 1970
     */
    public void setDate_end(Long date_end) {
        this.date_end = date_end;
    }

    /**
     * Return the deadlineDate of the event
     * @return date as long since 1970
     */
    public Long getDate_deadlineDay() {
        return date_deadlineDay;
    }

    /**
     * Set the deadlineDate of the event
     * @param deadlineDay Date as long since 1970
     */
    public void setDate_deadlineDay(long deadlineDay) {
        this.date_deadlineDay = deadlineDay;
    }

    /**
     * Return the Info of event
     * @return Info text
     */
    public String getInfo() {
        return info;
    }

    /**
     * Set the info text of event
     * @param info info text
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * Return the UID of the creator
     * @return uid of creator
     */
    public String getCreatorId() {
        return creatorId;
    }

    /**
     * Return member list
     * @return list of UIDs of the member
     */
    public List<String> getMembers() {
        return members;
    }

    /**
     * Return the list of active Member, which are can see the event on the list
     * @return list of UIDs of active member
     */
    public List<String> getActiveMembers() {
        return activeMembers;
    }

    /**
     * Set the list of active member
     * @param activeMembers list of UIDs  of active member
     */
    public void setActiveMembers(List<String> activeMembers) {
        this.activeMembers = activeMembers;
    }

    /**
     * Removes an active member from the list
     * @param uid UID of now inactive user
     */
    public void removeActiveMember(String uid){
        this.activeMembers.remove(uid);
    }

    /**
     * Add an user to the member list
     * @param user UID of the new member
     * @return true if added successful, false else
     */
    public boolean addMember(String user) {
        if (!this.members.contains(user)) {
            this.members.add(user);
            this.activeMembers.add(user);
            return true;
        }
        return false;
    }

    /**
     * Add collection of UIDs as new member
     * @param users collection of new users
     */
    public void addMembers(String... users) {
        for (String userUid : users)
            addMember(userUid);
    }

    /**
     * Remove Member with uid
     * @param uid User Id of Member
     * @return true if successful, else false
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean removeMember(String uid){
        return members.remove(uid) && activeMembers.remove(uid);
    }
    //endregion

    //region Position-Management

    /**
     * Return List of Positions that the event contains
     * @return List of Positions
     */
    public List<Position> getPositions() {
        return positions;
    }

    /**
     * Add a new Position to the position list
     * @param position new Position
     */
    public void addPosition(Position position) {
        positions.add(position);
    }

    /**
     * Add a collections of positions to the list
     * @param positions collection of positions
     */
    public void addPositions(Position... positions) {
        for (Position position : positions) {
            addPosition(position);
        }
    }

    /**
     * Update the Positions with the updated version of the position
     * @param position updated Position
     * @return true if could updated, else false
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean updatePosition(Position position) {
        for (int idx = 0; idx < positions.size(); ++idx) {
            if (positions.get(idx).getDate().equals(position.getDate())) {
                positions.set(idx, position);
                return true;
            }
        }
        return false;
    }

    /**
     * Delete the given position from the list
     * @param position position which should be deleted
     * @return true if successful else false
     */
    public boolean deletePosition(Position position) {
        for (int idx = 0; idx < positions.size(); ++idx) {
            if (positions.get(idx).getDate().equals(position.getDate())) {
                positions.remove(idx);
                return true;
            }
        }
        return false;
    }
    //endregion

    //region Expense-Management

    /**
     * Return if the given user have no open positions
     * @param userId UID of the queried user
     * @return true if even, else false
     */
    public boolean isEven(String userId){
        return getBalanceTable(userId).isEmpty();
    }

    /**
     * Return the Balance Table of the queried user
     * @param userId UID from queried user
     * @return Map of the User to Value of Depts
     */
    public Map<String, Float> getBalanceTable(String userId) {
        Map<String, Float> result = new HashMap<>();
        for (Position p : positions) {
            p.getBalanceMap(userId, members).forEach((k, v) -> result.merge(k, v, Float::sum));
        }
        return result;
    }

    /**
     * Returns the global event balance of the specified user.
     * @param userId UID of given user
     * @return the sum of positions of the user
     */
    public float getBalance(String userId) {
        float balance = 0.f;
        for (Position p : positions) {
            balance += p.getBalance(userId, members);
        }
        return balance;
    }

    /**
     * Removes the specified users debts from all positions
     * @param userId UID of given user
     */
    public void removeAllDebtsOf(String userId){
        positions.forEach(pos -> pos.removeDebtor(userId));
    }

    /**
     * Removes all debts from the user with
     * @param userId owed to
     * @param otherUserId other UID
     */
    public void removeAllDebtsOfUserOwedToOtherUser(String userId, String otherUserId){
        positions.stream()
             .filter(position -> position.getCreatorId().equals(otherUserId))
             .forEach(position -> position.removeDebtor(userId));
    }

    /**
     * Removes all positions made by the specified user.
     * Can be used if someone was paid out by everyone or renounces his credits.
     * @param userId UID
     */
    public void removePositionsOf(String userId) {
        positions.removeIf(position -> position.getCreatorId().equals(userId));
    }
    //endregion

    //region Lifecycle

    /**
     * Checks if this event can be closed due to no open transactions.
     * @return true if it is in closed state or in upcoming with no open Transactions, else false
     */
    @Exclude
    public boolean isClosable() {

        switch (getLifecycleState()){
            case UPCOMING:
                return hasNoOpenTransactions();
            case LIVE:
            case LOCKED:
                return false;
            case CLOSED:
                return true;
            case ERROR:
            default:
                return false;
        }
    }

    /**
     * Looks if there are open Transactions
     * @return true if there are open transactions
     */
    public boolean hasOpenTransactions(){
        return !hasNoOpenTransactions();
    }

    /**
     * Looks if there are no open transactions
     * @return true if there are no open transactions
     */
    private boolean hasNoOpenTransactions(){
        for (String member : members){
            if(!isEven(member)) return false;
        }
        return true;
    }

    /**
     * Returns the LifecycleState of the event
     * @return the LifecycleState of the event
     */
    @Exclude // ignore in firebase
    public LifecycleState getLifecycleState(){

        // TODO: Consider using Server Time for no "cheating"
        
        long date_now = Calendar.getInstance().getTimeInMillis();

        if (date_now <= 0 || date_begin <= 0 || date_end <= 0 || date_deadlineDay <= 0)
            return LifecycleState.ERROR;

        if (date_now >= date_deadlineDay) return LifecycleState.CLOSED;
        if (date_now >= date_end) return LifecycleState.LOCKED;
        if (date_now >= date_begin) return LifecycleState.LIVE;

        return LifecycleState.UPCOMING;
    }
    //endregion

    /**
     * Reset all attributes to null
     */
    @VisibleForTesting
    public void destroy(){
        eid = null;
        name = null;
        date_begin = null;
        date_end = null;
        date_deadlineDay = null;
        info = null;
        creatorId = null;
        members = null;
        activeMembers = null;
        positions = null;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
