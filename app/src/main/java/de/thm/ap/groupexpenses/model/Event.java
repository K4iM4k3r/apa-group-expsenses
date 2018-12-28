package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Event {

    private String eid;
    private String name;
    private String date;
    private String info;
    private String creatorId;
    private List<String> member;
    private List<Position> positions;

    public Event(){}

    public Event(String creatorId, String name, String date, String info, List<String> member, List<Position> positions) {
        this.name = name;
        this.date = date;
        this.info = info;
        this.creatorId = creatorId;
        this.member = member;
        this.positions = positions;
    }

    public Event(String creatorId, String name, String date, String info, List<String> member) {
        this.creatorId = creatorId;
        this.name = name;
        this.date = date;
        this.info = info;
        this.member = new ArrayList<>();
        this.member.add(creatorId);
        this.member.addAll(member);
        this.positions = new ArrayList<>();
    }
    public Event(String creatorId, String name, String date, String info) {
        this.creatorId = creatorId;
        this.name = name;
        this.date = date;
        this.info = info;
        this.member = new ArrayList<>();
        this.member.add(creatorId);
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
    public List<String> getMember() {
        return member;
    }
    public void addMember(String user) {
        this.member.add(user);
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

    public float getPositionFactor(boolean positive){
        float posFactor = (float)(member.size()-1) / (float)member.size();
        float negFactor = (float) (1.00/member.size());
        return positive? posFactor: negFactor;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }


    public String getCreatorId() {
        return creatorId;
    }
}
