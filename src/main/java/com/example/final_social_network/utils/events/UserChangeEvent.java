package com.example.final_social_network.utils.events;


import com.example.final_social_network.Domain.User;

public class UserChangeEvent implements Event {
    private ChangeEventType type;
    private User oldUser;
    private User newUser;

    public UserChangeEvent(ChangeEventType type, User old_u, User new_u) {
        this.type = type;
        this.oldUser = old_u;
        this.newUser = new_u;
    }


    public ChangeEventType getType() {
        return type;
    }
    public User getOldUser() {
        return oldUser;
    }
    public User getNewUser() { return newUser; }


    public void setType(ChangeEventType type) { this.type = type; }
    public void setOldUser(User oldUser) { this.oldUser = oldUser; }
    public void setNewUser(User newUser) { this.newUser = newUser;}

}