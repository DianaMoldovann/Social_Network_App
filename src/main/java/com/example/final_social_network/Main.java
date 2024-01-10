package com.example.final_social_network;

import com.example.final_social_network.Domain.Friendship;
import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Repository.FriendshipRepository.FriendshipDBRepository;
import com.example.final_social_network.Repository.UserRepository.UserDBRepository;
import com.example.final_social_network.Service.FriendshipService;
import com.example.final_social_network.Service.UserService;
import com.example.final_social_network.UI.UI;
import com.example.final_social_network.Validator.FriendshipValidator;
import com.example.final_social_network.Validator.UserValidator;
import com.example.final_social_network.Validator.Validator;

public class Main {
    public static void main(String[] args) {
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator =  new FriendshipValidator();
        //UserMemoryRepository userRepo = new UserMemoryRepository();
        UserDBRepository userDBRepo = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Diana27082003");
        //FriendshipMemoryRepository friendshipRepo = new FriendshipMemoryRepository();
        FriendshipDBRepository friendshipDBRepo = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Diana27082003");
        UserService userService = new UserService(userDBRepo, friendshipDBRepo, userValidator);
        FriendshipService friendshipService = new FriendshipService(friendshipDBRepo, userDBRepo, friendshipValidator);
        UI ui = new UI(userService, friendshipService);
        ui.run();
    }
}
