package com.example.final_social_network.Service;

import com.example.final_social_network.Domain.*;
import com.example.final_social_network.Repository.FriendRequestRRepository.FriendRequestDBRepository;
import com.example.final_social_network.Repository.FriendshipRepository.FriendshipRepository;
import com.example.final_social_network.Repository.UserRepository.UserDBRepository;
import com.example.final_social_network.utils.FriendRequestStatus.FriendRequestStatus;
import com.example.final_social_network.utils.events.ChangeEventType;
import com.example.final_social_network.utils.events.Event;
import com.example.final_social_network.utils.events.RequestChangeEvent;
import com.example.final_social_network.utils.observer.Observable;
import com.example.final_social_network.utils.observer.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FriendRequestService implements Observable<Event> {
    private final FriendRequestDBRepository requestRepo;
    private final FriendshipRepository friendshipRepo;
    private final UserDBRepository userRepo;
    private final List<Observer<Event>> observers = new ArrayList<>();

    public FriendRequestService(FriendRequestDBRepository requestRepo, FriendshipRepository friendshipRepo, UserDBRepository userRepo) {
        this.requestRepo = requestRepo;
        this.friendshipRepo = friendshipRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void addObserver(Observer<Event> e) {observers.add(e);}

    @Override
    public void removeObserver(Observer<Event> e) {observers.remove(e);}

    @Override
    public void notifyObservers(Event t) {observers.forEach(o -> o.update(t));}

    public void sendAFriendRequest(Long from_id, Long to_id) {
        if (Objects.equals(from_id, to_id)) {
            throw new IllegalArgumentException("you have selected your profile! \n " +
                    "You cannot send yourself a friend request!");
        }
        if (friendshipRepo.findOne(new Tuple<>(from_id, to_id)).isPresent()) {
            throw new IllegalArgumentException("you are already friends with this user!");
        }
        if (requestRepo.findOne(new Tuple<>(to_id, from_id)).isPresent() &&
                requestRepo.findOne(new Tuple<>(to_id, from_id)).get().getStatus() == FriendRequestStatus.PENDING) {
            throw new IllegalArgumentException("This user already sent you a friend request! \n" +
                    "If you want to be friends, please accept the request!");
        }
        Optional<FriendRequest> result = requestRepo.findOne(new Tuple<>(from_id, to_id));
        if (result.isPresent() && result.get().getStatus().equals(FriendRequestStatus.REJECTED)) {
            requestRepo.update(new FriendRequest(new Tuple<>(from_id, to_id), LocalDate.now(), FriendRequestStatus.PENDING));
            notifyObservers(new RequestChangeEvent(ChangeEventType.UPDATE, null, null));
            return;
        }
        if (result.isPresent() && result.get().getStatus().equals(FriendRequestStatus.PENDING)) {
            throw new IllegalArgumentException("you have already sent a friend request to this user");
        }
        FriendRequest request = new FriendRequest(new Tuple<>(from_id, to_id), LocalDate.now(), FriendRequestStatus.PENDING);
        if (requestRepo.add(request).isPresent() ) {
            throw new IllegalArgumentException("You have already sent a friend request to this user!");
        }else {
            notifyObservers(new RequestChangeEvent(ChangeEventType.ADD, null, request));
        }
    }

    public void acceptAFriendRequest(Long from_id, Long to_id) {
        FriendRequest friendRequest = requestRepo.findOne(new Tuple<>(from_id, to_id)).get();
        friendRequest.setStatus(FriendRequestStatus.APPROVED);
        requestRepo.update(friendRequest);
        friendshipRepo.add(new Friendship(new Tuple<>(from_id, to_id)));
        notifyObservers(new RequestChangeEvent(ChangeEventType.UPDATE, null, friendRequest));
    }

    public void declineAFriendRequest(Long from_id, Long to_id) {
        FriendRequest friendRequest = requestRepo.findOne(new Tuple<>(from_id, to_id)).get();
        friendRequest.setStatus(FriendRequestStatus.REJECTED);
        requestRepo.update(friendRequest);
        notifyObservers(new RequestChangeEvent(ChangeEventType.UPDATE, null, friendRequest));
    }

    public Iterable<FriendDTO> getAllFriendRequestOfUser(Long idUSer) {
        Iterable<FriendRequest> friendRequests = requestRepo.getAllFriendRequestOfUser(idUSer);
        List<FriendDTO> friendsWhoRequest = new ArrayList<>();
        friendRequests.forEach(fr -> {
            User userWhoWantsToBeFriend = userRepo.findOne(fr.getId().getLeft()).get();
            FriendDTO friendWhoRequest = new FriendDTO(fr.getId().getLeft(),
                    userWhoWantsToBeFriend.getFirstName(),
                    userWhoWantsToBeFriend.getLastName(),
                    userWhoWantsToBeFriend.getEmail(),
                    fr.getDateOfRequest());
            friendsWhoRequest.add(friendWhoRequest);
        });
        return friendsWhoRequest;
    }
}
