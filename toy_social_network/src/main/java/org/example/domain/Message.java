package org.example.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Message extends Entity<Long> {
    private User from;
    private ArrayList<Long> to;
    private String message;
    private String date;
    private Long reply;

    public Message(User from, ArrayList<Long> to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.reply = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        this.date = LocalDateTime.now().format(formatter);
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public ArrayList<Long> getTo() {
        return to;
    }

    public void setTo(ArrayList<Long> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return super.toString() + "{ " +
                "from: " + from +
                ", to: " + to +
                ", message: " + message +
                ", date: " + date +
                ", reply: " + reply +
                " }";
    }
}
