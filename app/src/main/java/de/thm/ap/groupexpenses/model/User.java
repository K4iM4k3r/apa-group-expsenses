package de.thm.ap.groupexpenses.model;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Comparable<User>, Serializable {


    private String uid;
    // mandatory
    private String firstName;
    private String lastName;
    private String nickname;
    private String email;
    private List<String> events;

    private List<String> friendsIds;
    private Uri profilePic;

    public User(){}

    public User(String uid, String email){
        this.uid = uid;
        this.firstName = "";
        this.lastName = "";
        this.nickname = "";
        this.email = email;
        this.events = new ArrayList<>();
        this.profilePic = null;
    }

    public User(String uid, String firstName, String lastName, String nickname, String email, List<String> events, Uri profilePic) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.email = email;
        this.events = events;
        this.profilePic = profilePic;
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

    public List<String> getEvents() {
        return events;
    }

    public Uri getProfilePic() {
        return profilePic;
    }

    public List<String> getFriendsIds() {
        return friendsIds;
    }
    public void setFriendsIds(List<String> friendsIds) {
        this.friendsIds = friendsIds;
    }
    public void addFriend(String friendId){
        if (friendsIds == null) friendsIds = new ArrayList<>();
        if (!friendsIds.contains(friendId)) friendsIds.add(friendId);
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

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public void setProfilePic(Uri profilePic) {
        this.profilePic = profilePic;
    }

    public void addEvent(String event){

        if(this.events == null){
            this.events = new ArrayList<>();
        }
        this.events.add(event);
    }

    @NonNull
    @Override
    public String toString(){
        return nickname;
//        return firstName + " " + lastName;
    }

    // allows Collections.sort() by first name
    @Override
    public int compareTo(User other) {
        return this.firstName.compareTo(other.firstName);
    }
}
