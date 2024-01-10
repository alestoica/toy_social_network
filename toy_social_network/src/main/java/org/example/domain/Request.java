package org.example.domain;

public class Request extends Entity<Long> {
    private User from;
    private User to;
    private String status;

    public Request(User from, User to) {
        this.from = from;
        this.to = to;
        this.status = "pending";
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return super.toString() + "{ " +
                "from: " + from +
                ", to: " + to +
                ", status: " + status;
    }
}
