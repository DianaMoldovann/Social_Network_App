package com.example.final_social_network.Domain;

import java.time.LocalDate;

public class FriendDTO extends Entity<Long> {
    public FriendDTO(Long id_friend, String firstNameFriend, String lastNameFriend, String emailFriend, LocalDate date) {
        super(id_friend);
        this.firstNameFriend = firstNameFriend;
        this.lastNameFriend = lastNameFriend;
        this.emailFriend = emailFriend;
        this.date = date;
    }

    private final String firstNameFriend;
    private final String lastNameFriend;
    private final String emailFriend;
    private final LocalDate date;

    public Long getIdFriend() {
        return super.getId();
    }

    public String getFirstNameFriend() {
        return firstNameFriend;
    }

    public String getLastNameFriend() {
        return lastNameFriend;
    }

    public String getEmailFriend() {
        return emailFriend;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "First Name:" +  firstNameFriend + " \n " +
                "Last Name:" +  lastNameFriend + " \n " +
                "Email:" +  emailFriend + " \n " +
                "Friends from: " +  date;
    }
}
