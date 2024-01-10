package com.example.final_social_network.Domain;

import java.time.LocalDate;

public class MessageDTO extends Entity<Long> {
    private final Long from;
    private final Long to;
    private final String message;
    private final LocalDate data;
    private final Long repliedMessage;

    public MessageDTO(Long from, Long to, String message, LocalDate data) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.repliedMessage = null;
    }

    public MessageDTO(Long from, Long to, String message, LocalDate data, Long idReplied) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.repliedMessage = idReplied;
    }

    public Long getFrom() {
        return from;
    }

    public Long getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }
    public LocalDate getData() {
        return data;
    }
    public Long getRepliedMessage() { return repliedMessage; }
}
