package org.example.utils.events;

import org.example.domain.Message;

public class MessageChangeEvent implements Event {
    private ChangeEventType type;
    private Message newData, oldData;

    public MessageChangeEvent(ChangeEventType type, Message newData) {
        this.type = type;
        this.newData = newData;
    }

    public MessageChangeEvent(ChangeEventType type, Message newData, Message oldData) {
        this.type = type;
        this.newData = newData;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Message getNewData() {
        return newData;
    }

    public Message getOldData() {
        return oldData;
    }
}