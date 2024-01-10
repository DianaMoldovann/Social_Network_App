package com.example.final_social_network.Domain;

import com.example.final_social_network.utils.FriendRequestStatus.FriendRequestStatus;

import java.time.LocalDate;

public class FriendRequest extends Entity<Tuple<Long, Long>> {
    private LocalDate dateOfRequest;
    private FriendRequestStatus status;

    public FriendRequest(Tuple<Long, Long> longLongTuple, LocalDate dateOfRequest, FriendRequestStatus status) {
        super(longLongTuple);
        this.dateOfRequest = dateOfRequest;
        this.status = status;
    }

    public LocalDate getDateOfRequest() {
        return dateOfRequest;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setDateOfRequest(LocalDate dateOfRequest) {
        this.dateOfRequest = dateOfRequest;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FriendRequest: \n" +
                " from: " + id.getLeft() + "\n" +
                " to: " + id.getRight() + "\n " +
                " date: " + dateOfRequest + "\n" +
                " status=" + status + "\n";
    }
}
