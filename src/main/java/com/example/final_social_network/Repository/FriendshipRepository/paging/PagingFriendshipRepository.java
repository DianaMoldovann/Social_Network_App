package com.example.final_social_network.Repository.FriendshipRepository.paging;

import com.example.final_social_network.Domain.Entity;
import com.example.final_social_network.Repository.FriendshipRepository.FriendshipRepository;

public interface PagingFriendshipRepository<ID, E extends Entity<ID>> extends FriendshipRepository {
    Page<E> findFriendsOnPage(Pageable pageable, Long id);
}
