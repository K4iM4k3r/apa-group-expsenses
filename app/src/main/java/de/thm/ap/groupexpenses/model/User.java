package de.thm.ap.groupexpenses.model;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class User implements Comparable<User> {


    private String uid;
    // mandatory
    private String firstName;
    private String lastName;
    private String nickname;
    private String email;
    private String info;
    private Long joinDate;
    private List<String> events;

    private List<String> friendsIds;
    private String profilePic;

    public User() {
    }

    public User(String uid, String email) {
        this.uid = uid;
        this.firstName = "";
        this.lastName = "";
        this.nickname = "";
        this.email = email;
        this.events = new ArrayList<>();
        this.profilePic = null;
        this.joinDate = Calendar.getInstance().getTimeInMillis();
    }

    public User(String uid, String firstName, String lastName, String nickname, String email, List<String> events, String profilePic) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.email = email;
        this.events = events;
        this.profilePic = profilePic;
        this.joinDate = Calendar.getInstance().getTimeInMillis();
    }

    public String getUid() {
        return uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getDateString() {
        //TODO: remove this if check for null after next db reset
        if (joinDate == null) {
            return "Keine Beitrittsinfo, alter Account!!";
        } else {
            Date date = new Date(this.joinDate);
            Format format = new SimpleDateFormat("dd.MM.yyyy");
            return format.format(date);
        }
    }

    public String getInfo() {
        //TODO: remove this if check for null after next db reset
        if (info == null) {
            return "User hat noch keine Info gesetzt, alter Account!";
        } else
            return info;
    }

    public List<String> getEvents() {
        return events;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public List<String> getFriendsIds() {
        return friendsIds;
    }

    public void setFriendsIds(List<String> friendsIds) {
        this.friendsIds = friendsIds;
    }

    public void addFriend(String friendId) {
        if (friendsIds == null) friendsIds = new ArrayList<>();
        if (!friendsIds.contains(friendId)) friendsIds.add(friendId);
    }

    public void removeFriend(String friendId) {
        if (friendsIds == null) friendsIds = new ArrayList<>();
        friendsIds.remove(friendId);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void addEvent(String event) {

        if (this.events == null) {
            this.events = new ArrayList<>();
        }
        this.events.add(event);
    }

    /**
     * Remove Event with eid  on User
     * @param eid Event ID
     * @return true if successful, else false
     */
    public boolean removeEvent(String eid){
        return events.remove(eid);
    }

    @NonNull
    @Override
    public String toString() {
        return nickname;
//        return firstName + " " + lastName;
    }

    // allows Collections.sort() by first name
    @Override
    public int compareTo(User other) {
        return this.firstName.compareTo(other.firstName);
    }
}
