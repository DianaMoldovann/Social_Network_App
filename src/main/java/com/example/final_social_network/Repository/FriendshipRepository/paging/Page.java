package com.example.final_social_network.Repository.FriendshipRepository.paging;

public class Page<E> {
    private final Iterable<E> elementsOnPage;

    private final int totalNrOfElems;

    public Page(Iterable<E> elementsOnPage, int totalNrOfElems) {
        this.elementsOnPage = elementsOnPage;
        this.totalNrOfElems = totalNrOfElems;
    }

    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public int getTotalNrOfElems() {
        return totalNrOfElems;
    }
}
