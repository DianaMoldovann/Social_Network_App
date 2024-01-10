package com.example.final_social_network.Repository.FriendshipRepository;

import com.example.final_social_network.Domain.Friendship;
import com.example.final_social_network.Domain.Tuple;
import com.example.final_social_network.Repository.Repository;


public interface FriendshipRepository extends Repository<Tuple<Long, Long>, Friendship> {
    Iterable<Long> findFriends(Long id);
}
