package de.thm.ap.groupexpenses.model;

import java.io.Serializable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event implements Serializable {

    private String eid;
    private String name;
    private String date;
    private String info;
    private String creatorId;
    private List<String> members; // cleaner way with HashSet TODO
    private List<Position> positions;

    //region Constructor
    public Event(){}
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
    public void addMember(String user) {
        if (!this.members.contains(user))
            this.members.add(user);
    }
    public int getMemberCount(){
        return getMembers().size();
    }
    public List<Position> getPositions() {
        return positions;
    }
    public void addPosition(Position position){
        positions.add(position);
    }
    public void addPositions(Position... positions){
        for (Position position : positions) {
            addPosition(position);
        }
    }
    //endregion

    //region Expense-Management
    public Map<String, Float> getBalanceTable(String userId){
        Map<String, Float> result = new HashMap<>();
        for(Position p : positions){
            p.getBalanceMap(userId, members).forEach((k, v)-> result.merge(k,v,Float::sum));
        }
        return result;
    }

    public float getBalance(String userId){
        float balance = 0.f;
        for(Position p : positions){
            balance += p.getBalance(userId, members);
        }
        return balance;
    }
    //endregion

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
