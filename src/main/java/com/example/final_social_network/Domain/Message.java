package com.example.final_social_network.Domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {
    private Long from;
    private List<Long> to;
    private String message;
    private LocalDate data;
    private Long repliedMessage;

    public Message(Long from, List<Long> to, String message, LocalDate data) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.repliedMessage = null;
    }

    public Message(Long from, List<Long> to, String message, LocalDate data, Long replied) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.repliedMessage = replied;
    }

    public Long getFrom() {
        return from;
    }

    public List<Long> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }
    public LocalDate getData() {
        return data;
    }
    public Long getRepliedMessage() {
        return repliedMessage;
    }


    public void setFrom(Long from) {
        this.from = from;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setRepliedMessage(Long repliedMessage) {
        this.repliedMessage = repliedMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Message message1 = (Message) o;
        return Objects.equals(from, message1.from) && Objects.equals(to, message1.to) && Objects.equals(message, message1.message) && Objects.equals(data, message1.data) && Objects.equals(repliedMessage, message1.repliedMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to, message, data, repliedMessage);
    }
}