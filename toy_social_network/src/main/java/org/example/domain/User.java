package org.example.domain;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private ArrayList<Long> friends;
    private String username;
    private String password;

    public User(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.friends = new ArrayList<>();
        this.username = username;
        this.password = password;
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

    public ArrayList<Long> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Long> friends) {
        this.friends = friends;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFriend(User other) {
        Function<Long, Boolean> func = friend -> other.getId().equals(friend);
        for(Long friend : friends)
            return func.apply(friend);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof User that))
            return false;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

//    @Override
//    public String toString() {
//        return super.toString() + "{ " + "first name: " + firstName + ", last name: " + lastName + ", friends: " + friends + " }";
//    }

    @Override
    public String toString() {
        return firstName;
    }
}
