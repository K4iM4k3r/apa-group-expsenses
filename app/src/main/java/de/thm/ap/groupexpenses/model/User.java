package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/* setter and getter are needed by firestore api
 *to store the models in the database
 * therefor ethe unused suppress
 */
@SuppressWarnings("unused")
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

    /**
     * Empty Constructor is requirement of firestore
     */
    public User() {
    }

    /**
     * Create an User with User ID and email
     * @param uid Id of user
     * @param email email of user
     */
    public User(String uid, String email) {
        this.uid = uid;
        this.firstName = "";
        this.lastName = "";
        this.nickname = "";
        this.info = "";
        this.email = email;
        this.events = new ArrayList<>();
        this.profilePic = null;
        this.joinDate = Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Creates an user of the following parameters
     * @param uid User ID
     * @param firstName first name of user
     * @param lastName last name of user
     * @param nickname nickname
     * @param email email
     * @param events List of EIDs where he is member
     * @param profilePic Url of the profile pic in firebase storage
     */
    public User(String uid, String firstName, String lastName, String nickname, String email, List<String> events, String profilePic) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.email = email;
        this.events = events;
        this.info = "";
        this.profilePic = profilePic;
        this.joinDate = Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Return the User Id
     * @return User Id
     */
    public String getUid() {
        return uid;
    }

    /**
     * Return the first name of the user
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Return the last name of the user
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Return the Nickname of the user
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Return the email of the user
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Return the join data on the app
     * @return join date as Long since 1970
     */
    public Long getJoinDate() {
        return joinDate;
    }

    /**
     * Return the info about the user
     * @return user info
     */
    public String getInfo() {
        return info;
    }

    /**
     * Return the list of the events of the user
     * @return event list
     */
    public List<String> getEvents() {
        return events;
    }

    /**
     * Return the uri of the Profile pic
     * @return uri of profile pic
     */
    public String getProfilePic() {
        return profilePic;
    }

    /**
     * Return the list of friends containing the uid
     * @return fiend list
     */
    public List<String> getFriendsIds() {
        return friendsIds;
    }

    /**
     * Set the list of the friends UIDs
     * @param friendsIds list of friends UIDs
     */
    public void setFriendsIds(List<String> friendsIds) {
        this.friendsIds = friendsIds;
    }

    /**
     * Add a friend to the list
     * @param friendId new friend
     */
    public void addFriend(String friendId) {
        if (friendsIds == null) friendsIds = new ArrayList<>();
        if (!friendsIds.contains(friendId)) friendsIds.add(friendId);
    }

    /**
     * Removes a friend from the list
     * @param friendId removed friend
     */
    public void removeFriend(String friendId) {
        if (friendsIds == null) friendsIds = new ArrayList<>();
        friendsIds.remove(friendId);
    }

    /**
     * set the first name of the user
     * @param firstName new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * set the last name of the user
     * @param lastName new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Set the nickname of the user
     * @param nickname new nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Set the email of the user
     * @param email new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set the User info
     * @param info new user info
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * Set the Event list of the user containing the EIDs
     * @param events event list
     */
    public void setEvents(List<String> events) {
        this.events = events;
    }

    /**
     * Set the Profile pic uri
     * @param profilePic new profile uri
     */
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    /**
     * Add a new event to the event list of the user
     * @param event new event
     */
    public void addEvent(String event) {

        if (this.events == null) {
            this.events = new ArrayList<>();
        }
        this.events.add(event);
    }

    /**
     * Turn the join Date to an String
     * @return join Date as String
     */
    public String joinDateToString(){
        Date date = new Date(this.joinDate);
        Format format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(date);
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
