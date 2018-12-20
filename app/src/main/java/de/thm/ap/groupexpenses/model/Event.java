package de.thm.ap.groupexpenses.model;

import java.io.Serializable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {

    private int id;

    private String name;
    private String date;
    private String info;
    private final User creator;
    private List<User> members;
    private List<Position> positions;

    public Event(User creator, String name, String date, String info, List<User> members, List<Position> positions) {
        this(creator, name, date, info, members);
        this.positions = positions;
    }

    public Event(User creator, String name, String date, String info, List<User> members) {
        this(creator, name, date, info);
        this.members = members;

        boolean creatorFound = false;
        for(int idx = 0; idx < members.size(); ++idx){
            if(members.get(idx).getId() == creator.getId()){
                creatorFound = true;
                break;
            }
        }
        if(!creatorFound)
            this.members.add(this.creator);
    }

    public Event(User creator, String name, String date, String info) {
        this.id = (int)(Math.random() * 10000 + 1);     // rand num between 0 - 10000
        this.creator = creator;
        this.name = name;
        this.date = date;
        this.info = info;
        this.members = new ArrayList<>();
        this.members.add(this.creator);
        this.positions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public User getCreator() { return this.creator; }
    public int getId() { return id; }
    public void setId(int id) {
        this.id = id;
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
    public List<User> getMembers() {
        return members;
    }
    public void addUser(User user) {
        this.members.add(user);
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
        float posFactor = (float)(members.size()-1) / (float) members.size();
        float negFactor = (float) (1.00/ members.size());
        return positive? posFactor: negFactor;
    }

    @NonNull
    @Override
    public String toString() { return name; }
}
