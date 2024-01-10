package com.example.final_social_network.Service;

import com.example.final_social_network.Domain.*;
import com.example.final_social_network.Repository.MessageRepository.MessageDBRepository;
import com.example.final_social_network.Repository.UserRepository.UserDBRepository;
import com.example.final_social_network.Validator.Validator;
import com.example.final_social_network.utils.events.ChangeEventType;
import com.example.final_social_network.utils.events.Event;
import com.example.final_social_network.utils.events.MessageChangeEvent;
import com.example.final_social_network.utils.observer.Observable;
import com.example.final_social_network.utils.observer.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageService implements Observable<Event> {
    private final List<Observer<Event>> observers = new ArrayList<>();

    private final UserDBRepository userRepo;
    private final MessageDBRepository messageRepo;
    private final Validator<Message> messageValidator;

    public MessageService(UserDBRepository userRepo, MessageDBRepository messageRRepo, Validator<Message> val) {
        this.userRepo = userRepo;
        this.messageRepo = messageRRepo;
        this.messageValidator = val;
    }

    public Message findOne(Long idMessage) {
        Optional<Message> mess_op = messageRepo.findOne(idMessage);
        if (mess_op.isPresent()) {
            return mess_op.get();
        }
        else {
            throw new IllegalArgumentException("There s no message with this id");
        }
    }

    public void sendAMessage(Long from, List<Long> to, String message, Long replied) {
        Message mess = new Message(from, to, message, LocalDate.now(), replied);
        messageValidator.validate(mess);
        messageRepo.add(mess);
        notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, null, mess));
    }

    public Iterable<User> findAllWhoHaveAChatWithUser(Long idUser) {
        List<User> friendsWithChat = new ArrayList<>();
        Iterable<Long> usersID = messageRepo.findAllWhoHaveAChatWithUser(idUser);
        usersID.forEach(idFriend -> {
            User userFriend = userRepo.findOne(idFriend).get();
            friendsWithChat.add(userFriend);
        });
        return friendsWithChat;
    }
    public Iterable<MessageDTO> findAllMessagesBetweenTwoUsers(Long idUser, Long idFriend) {
        return messageRepo.findAllMessagesBetweenTwoUsers(idUser, idFriend);
    }
    @Override
    public void addObserver(Observer<Event> e) {observers.add(e);}

    @Override
    public void removeObserver(Observer<Event> e) {observers.remove(e);}

    @Override
    public void notifyObservers(Event t) {observers.forEach(o -> o.update(t));}
}
