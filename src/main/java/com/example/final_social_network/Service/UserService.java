package com.example.final_social_network.Service;

import com.example.final_social_network.Domain.Tuple;
import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Repository.FriendshipRepository.FriendshipRepository;
//import com.example.final_social_network.Repository.Repository;
import com.example.final_social_network.Repository.UserRepository.UserDBRepository;
import com.example.final_social_network.Validator.Validator;
import com.example.final_social_network.utils.EcryptPassword.PasswordManager;
import com.example.final_social_network.utils.events.ChangeEventType;
import com.example.final_social_network.utils.events.Event;
import com.example.final_social_network.utils.events.UserChangeEvent;
import com.example.final_social_network.utils.observer.Observable;
import com.example.final_social_network.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UserService implements Observable<Event> {
    //private final Repository<Long, User> userRepo;
    private final UserDBRepository userRepo;
    private final FriendshipRepository friendshipRepo;
    private final Validator<User> val;

    private final List<Observer<Event>> observers = new ArrayList<>();


    public UserService(UserDBRepository repo, FriendshipRepository friendshipRepo, Validator<User> val) {
        this.userRepo = repo;
        this.friendshipRepo = friendshipRepo;
        this.val = val;
    }

//    public UserService(Repository<Long, User> repo, FriendshipRepository friendshipRepo, Validator<User> val) {
//        this.userRepo = repo;
//        this.friendshipRepo = friendshipRepo;
//        this.val = val;
//    }


    public User findOne(Long id) {
        Optional<User> u_op = userRepo.findOne(id);
        if (u_op.isEmpty()) {
            throw new IllegalArgumentException("There is no user with the entered ID.");
        }
        else return u_op.get();
    }

    public User findOne(String email) {
        Optional<User> u_op = userRepo.findOne(email);
        if (u_op.isEmpty()) {
            throw new IllegalArgumentException("There is no user with the entered email.");
        }
        else return u_op.get();
    }

    public Iterable<User> getAll() {
        return userRepo.getAll();
    }


    public void add(String firstName, String lastName, String email, String password) {
        Optional<User> userWithSameEmail = userRepo.findOne(email);
        if (userWithSameEmail.isPresent()) {
            throw new IllegalArgumentException("Is there already an account with this email ! ");
        }

        String encryptPassword = PasswordManager.hashPassword(password);
        User u = new User(firstName, lastName, email, encryptPassword);
        val.validate(u);
        Optional<User> u_op = userRepo.add(u);
        if (u_op.isPresent()) {
            throw new IllegalArgumentException("Is there already a user with this ID! \n " + u_op.get());
        }
        else {
            notifyObservers(new UserChangeEvent(ChangeEventType.ADD, null, u));
        }
    }


    public User delete(Long id) {
        Optional<User> userToDelete_optional = userRepo.findOne(id);
        if (userToDelete_optional.isEmpty())
            throw new IllegalArgumentException("There no user with this ID! \n");
        else {
            User userToDelete =  userToDelete_optional.get();
            List<Long> friendships = (List<Long>) friendshipRepo.findFriends(userToDelete.getId());
            userRepo.delete(id);
            if (friendships != null) {
                for (Long idFriend : friendships) {
                    friendshipRepo.delete(new Tuple<>(id, idFriend));
                    friendshipRepo.delete(new Tuple<>(idFriend, id));
                }
            }
            notifyObservers(new UserChangeEvent(ChangeEventType.DELETE, userToDelete,null ));
            return userToDelete;
        }
    }


    public void update(Long id, String firstName, String lastName, String email, String password) {
        User new_u = new User(firstName, lastName, email, password);
        new_u.setId(id);
        val.validate(new_u);
        Optional<User> u_op = userRepo.update(new_u);
        if (u_op.isEmpty()) {
            throw new IllegalArgumentException("There no user with this ID! \n");
        }
        else {
            notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE, u_op.get(), new_u));
        }
    }

    public Iterable<User> getAllFiltered(String filter) {
        return userRepo.getAllFiltered(filter);
    }


    @Override
    public void addObserver(Observer<Event> e) { observers.add(e); }

    @Override
    public void removeObserver(Observer<Event> e) {observers.remove(e);}

    @Override
    public void notifyObservers(Event t) { observers.forEach(o -> o.update(t)); }
}
