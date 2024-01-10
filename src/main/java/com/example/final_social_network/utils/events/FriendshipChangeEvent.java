package com.example.final_social_network.utils.events;

import com.example.final_social_network.Domain.Friendship;

public class FriendshipChangeEvent implements Event{
    private ChangeEventType type;
    private Friendship oldFriendship;
    private Friendship newFriendship;

    public FriendshipChangeEvent(ChangeEventType type, Friendship old_f, Friendship new_f) {
        this.type = type;
        this.oldFriendship = old_f;
        this.newFriendship = new_f;
    }


    public ChangeEventType getType() {
        return type;
    }
    public Friendship getOldFriendship() {
        return oldFriendship;
    }
    public Friendship getNewFriendship() { return newFriendship; }


    public void setType(ChangeEventType type) { this.type = type; }
    public void setOldUser(Friendship oldFriendship) { this.oldFriendship = oldFriendship; }
    public void setNewUser(Friendship newFriendship) { this.newFriendship = newFriendship;}

}