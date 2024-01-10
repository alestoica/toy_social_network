package org.example.utils.events;

import org.example.domain.Request;

public class RequestChangeEvent implements Event {
    private ChangeEventType type;
    private Request newData, oldData;

    public RequestChangeEvent(ChangeEventType type, Request newData) {
        this.type = type;
        this.newData = newData;
    }

    public RequestChangeEvent(ChangeEventType type, Request newData, Request oldData) {
        this.type = type;
        this.newData = newData;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Request getNewData() {
        return newData;
    }

    public Request getOldData() {
        return oldData;
    }
}
