package de.thm.ap.groupexpenses.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class User implements Comparable<User>, Serializable {

    private int id;

    // mandatory
    private String firstName;
    private String lastName;
    private String email;

    // optional
    Date dateOfBirth;

    public User(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean equals(User otherUser){
        return this.id == otherUser.getId();
    }

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
