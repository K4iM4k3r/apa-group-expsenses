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

    @Deprecated
    public float getPositionFactor(boolean positive){
        float posFactor = (float)(members.size()-1) / (float) members.size();
        float negFactor = (float) (1.00/ members.size());
        return positive? posFactor: negFactor;
    }

    public Map<String, Float> getBalanceTable(String userId){
        Map<String, Float> result = new HashMap<>();
        for(Position p : positions){
            Map<String, Float> tmp = p.getBalance(userId, members);
            tmp.forEach((k,v)-> result.merge(k,v,Float::sum));
        }
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
