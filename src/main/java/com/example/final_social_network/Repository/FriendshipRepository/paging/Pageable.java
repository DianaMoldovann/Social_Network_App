package com.example.final_social_network.Repository.FriendshipRepository.paging;

public class Pageable {
    private final int pageNr;

    private final int pageSize;

    public Pageable(int pageNr, int pageSize) {
        this.pageNr = pageNr;
        this.pageSize = pageSize;
    }

    public int getPageNr() {
        return pageNr;
    }

    public int getPageSize() {
        return pageSize;
    }
}
