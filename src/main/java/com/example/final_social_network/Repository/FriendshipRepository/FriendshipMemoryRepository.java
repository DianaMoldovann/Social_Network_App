package com.example.final_social_network.Repository.FriendshipRepository;

import com.example.final_social_network.Domain.Friendship;
import com.example.final_social_network.Domain.Tuple;
import com.example.final_social_network.Repository.MemoryRepository;

import java.util.ArrayList;
import java.util.Objects;

public class FriendshipMemoryRepository extends MemoryRepository<Tuple<Long, Long>, Friendship> implements FriendshipRepository{

    public Iterable<Long> findFriends(Long id) {
        ArrayList<Long> friends =  new ArrayList<>();
        for (Friendship friendship : entities.values()) {
            if(Objects.equals(friendship.getId().getLeft(), id)) {
                friends.add(friendship.getId().getRight());
            }
            if(Objects.equals(friendship.getId().getRight(), id)) {
                friends.add(friendship.getId().getLeft());
            }
        }
        return friends;
    }
}
