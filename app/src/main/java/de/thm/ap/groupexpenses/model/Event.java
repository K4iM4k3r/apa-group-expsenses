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
    private List<User> users;
    private List<Position> positions;

    public Event(User creator, String name, String date, String info, List<User> user, List<Position> positions) {
        this.name = name;
        this.date = date;
        this.info = info;
        this.creator = creator;
        this.users = user;
        this.positions = positions;
    }

    public Event(User creator, String name, String date, String info, List<User> member) {
        this.creator = creator;
        this.name = name;
        this.date = date;
        this.info = info;
        this.users = new ArrayList<>();
        this.users.add(creator);
        this.users.addAll(users);
        this.positions = new ArrayList<>();
    }
    public Event(User creator, String name, String date, String info) {
        this.creator = creator;
        this.name = name;
        this.date = date;
        this.info = info;
        this.users = new ArrayList<>();
        this.users.add(creator);
        this.positions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCreator() {
        return creator.toString();
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
    public List<User> getUsers() {
        return users;
    }
    public void addUser(User user) {
        this.users.add(user);
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
        float posFactor = (float)(users.size()-1) / (float) users.size();
        float negFactor = (float) (1.00/ users.size());
        return positive? posFactor: negFactor;
    }

    @NonNull
    @Override
    public String toString() { return name; }
}
