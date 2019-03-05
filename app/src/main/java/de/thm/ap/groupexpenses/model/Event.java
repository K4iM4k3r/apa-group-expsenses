package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.ArraySet;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.auth.User;
import com.google.protobuf.Enum;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public enum LifecycleState {
        ERROR, ONGOING, LIVE, LOCKED, CLOSED
    }

    //region Constructor
    public Event() { }

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

    public Event(String creatorId, String name, Long date_begin, Long date_end, Long date_deadlineDay, String info, List<String> members) {
        this(creatorId, name, date_begin, date_end, date_deadlineDay, info, members, new ArrayList<>());
    }

    public Event(String creatorId, String name, Long date_begin, Long date_end, Long date_deadlineDay, String info) {
        this(creatorId, name, date_begin, date_end, date_deadlineDay, info, new ArrayList<>());
    }
    //endregion

    //region Getter/Setter/Adder
    public String getEid() {
        return eid;
    }
    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Long getDate_begin() {
        return date_begin;
    }
    public void setDate_begin(Long date_begin) {
        this.date_begin = date_begin;
    }

    public Long getDate_end() {
        return date_end;
    }
    public void setDate_end(Long date_end) {
        this.date_end = date_end;
    }

    public Long getDate_deadlineDay() {
        return date_deadlineDay;
    }
    public void setDate_deadlineDay(long deadlineDay) {
        this.date_deadlineDay = deadlineDay;
    }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    public String getCreatorId() {
        return creatorId;
    }
    public List<String> getMembers() {
        return members;
    }


    public List<String> getActiveMembers() {
        return activeMembers;
    }

    public void setActiveMembers(List<String> activeMembers) {
        this.activeMembers = activeMembers;
    }


    public void removeActiveMember(String uid){
        this.activeMembers.remove(uid);
    }
    public boolean addMember(String user) {
        if (!this.members.contains(user)) {
            this.members.add(user);
            this.activeMembers.add(user);
            return true;
        }
        return false;
    }
    public void addMembers(String... users) {
        for (String userUid : users)
            if (!this.members.contains(userUid))
                this.members.add(userUid);
    }
    /**
     * Remove Member with uid
     * @param uid User Id of Member
     * @return true if successful, else false
     */
    public boolean removeMember(String uid){
        return members.remove(uid) && activeMembers.remove(uid);
    }
    //endregion

    //region Position-Management
    public List<Position> getPositions() {
        return positions;
    }

    public void addPosition(Position position) {
        positions.add(position);
    }

    public void addPositions(Position... positions) {
        for (Position position : positions) {
            addPosition(position);
        }
    }

    public boolean updatePosition(Position position) {
        for (int idx = 0; idx < positions.size(); ++idx) {
            if (positions.get(idx).getDate().equals(position.getDate())) {
                positions.set(idx, position);
                return true;
            }
        }
        return false;
    }

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
    public boolean isEven(String userId){
        return getBalanceTable(userId).isEmpty();
    }

    public Map<String, Float> getBalanceTable(String userId) {
        Map<String, Float> result = new HashMap<>();
        for (Position p : positions) {
            p.getBalanceMap(userId, members).forEach((k, v) -> result.merge(k, v, Float::sum));
        }
        return result;
    }

    /**
     * Returns the global event balance of the specified user.
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
     */
    public void removeAllDebtsOf(String userId){
        positions.forEach(pos -> pos.removeDebtor(userId));
    }

    /**
     * Removes all debts from the user with
     * @param userId owed to
     * @param otherUserId
     */
    public void removeAllDebtsOfUserOwedToOtherUser(String userId, String otherUserId){
        positions.stream()
             .filter(position -> position.getCreatorId().equals(otherUserId))
             .forEach(position -> position.removeDebtor(userId));
    }

    /**
     * Removes all positions made by the specified user.
     * Can be used if someone was paid out by everyone or renounces his credits.
     */
    public void removePositionsOf(String userId) {
        positions.removeIf(position -> position.getCreatorId().equals(userId));
    }
    //endregion

    //region Lifecycle
    /**
     * Checks if this event can be closed due to no open transactions.
     */
    @Exclude
    public boolean isClosable() {

        switch (getLifecycleState()){
            case ONGOING:
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

    public boolean hasOpenTransactions(){
        return !hasNoOpenTransactions();
    }

    private boolean hasNoOpenTransactions(){
        for (String member : members){
            if(!isEven(member)) return false;
        }
        return true;
    }

    @Exclude // ignore in firebase
    public LifecycleState getLifecycleState(){

        // TODO: Consider using Server Time for no "cheating"
        
        long date_now = Calendar.getInstance().getTimeInMillis();

        if (date_now <= 0 || date_begin <= 0 || date_end <= 0 || date_deadlineDay <= 0)
            return LifecycleState.ERROR;

        if (date_now >= date_deadlineDay) return LifecycleState.CLOSED;
        if (date_now >= date_end) return LifecycleState.LOCKED;
        if (date_now >= date_begin) return LifecycleState.LIVE;

        return LifecycleState.ONGOING;
    }
    //endregion

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
        positions = null;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
