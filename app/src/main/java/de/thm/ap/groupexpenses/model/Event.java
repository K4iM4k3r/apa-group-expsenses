package de.thm.ap.groupexpenses.model;

import java.util.ArrayList;
import java.util.List;

public class Event {

    private int id;

    private String name;
    private String date;
    private String info;
    private final User creator;
    private List<User> member;
    private List<Position> positions;

    public Event(User creator, String name, String date, String info, List<User> member) {
        this.creator = creator;
        this.name = name;
        this.date = date;
        this.info = info;
        this.member = new ArrayList<>();
        this.member.add(creator);
        this.member.addAll(member);
        this.positions = new ArrayList<>();
    }
    public Event(User creator, String name, String date, String info) {
        this.creator = creator;
        this.name = name;
        this.date = date;
        this.info = info;
        this.member = new ArrayList<>();
        this.member.add(creator);
        this.positions = new ArrayList<>();
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
    public List<User> getMember() {
        return member;
    }
    public void addMember(User user) {
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

}
