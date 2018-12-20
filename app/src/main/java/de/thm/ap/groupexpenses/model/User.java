package de.thm.ap.groupexpenses.model;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

public class User implements Comparable<User> {


    private String uid;
    // mandatory
    private String firstName;
    private String lastName;
    private String nickname;
    private String email;
    private List<String> events;
    private Uri profilePic;

    public User(){}

    public User(String uid, String email){
        this.uid = uid;
        this.firstName = "";
        this.lastName = "";
        this.nickname = "";
        this.email = email;
        this.events = null;
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
        this.events.add(event);
    }

//    public boolean equals(User otherUser){
//        return this.id == otherUser.getId();
//    }

    @NonNull
    @Override
    public String toString(){
        return firstName + " " + lastName;
    }

    // allows Collections.sort() by first name
    @Override
    public int compareTo(User other) {
        return this.firstName.compareTo(other.firstName);
    }
}
