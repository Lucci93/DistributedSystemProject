package Utilities;

import Utilities.MessageIDs;

public class Message {

    private MessageIDs id;
    private String json;

    public Message(MessageIDs id, String json) {
        this.id = id;
        this.json = json;
    }

    public synchronized MessageIDs getId() {
        return id;
    }

    public synchronized String getJson() {
        return json;
    }
}
