package org.example.utils.events;

import org.example.controller.UserController;
import org.example.domain.User;

public class UserChangeEvent implements Event {
    private ChangeEventType type;
    private User newData, oldData;

    public UserChangeEvent(ChangeEventType type, User newData) {
        this.type = type;
        this.newData = newData;
    }

    public UserChangeEvent(ChangeEventType type, User newData, User oldData) {
        this.type = type;
        this.newData = newData;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getNewData() {
        return newData;
    }

    public User getOldData() {
        return oldData;
    }
}
