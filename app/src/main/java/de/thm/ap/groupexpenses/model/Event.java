package de.thm.ap.groupexpenses.model;

import java.io.Serializable;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {

    private String eid;
    private String name;
    private String date;
    private String info;
    private String creatorId;
    private List<String> members; // cleaner way with HashSet TODO
    private List<Position> positions;

    //region Constructor
    public Event() {
    }

    public Event(String creatorId, String name, String date, String info, List<String> members, List<Position> positions) {
        this.name = name;
        this.date = date;
        this.info = info;
        this.creatorId = creatorId;
        this.members = new ArrayList<>();
        this.members.addAll(members);
        addMember(creatorId);
        this.positions = positions;
    }

    public Event(String creatorId, String name, String date, String info, List<String> members) {
        this.creatorId = creatorId;
        this.name = name;
        this.date = date;
        this.info = info;
        this.members = new ArrayList<>();
        this.members.addAll(members);
        addMember(creatorId);
        this.positions = new ArrayList<>();
    }

    public Event(String creatorId, String name, String date, String info) {
        this.creatorId = creatorId;
        this.name = name;
        this.date = date;
        this.info = info;
        this.members = new ArrayList<>();
        this.members.add(creatorId);
        this.positions = new ArrayList<>();
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public boolean addMember(String user) {
        if (!this.members.contains(user)) {
            this.members.add(user);
            return true;
        }
        return false;
    }

    public void addMembers(String... users) {
        for (String userUid : users)
            if (!this.members.contains(userUid))
                this.members.add(userUid);
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

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
