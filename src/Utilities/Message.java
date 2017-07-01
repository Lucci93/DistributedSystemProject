package Utilities;

public class Message {

    private MessageIDs id;
    private String message;

    public Message(MessageIDs id, String message) {
        this.id = id;
        this.message = message;
    }

    public synchronized MessageIDs getId() {
        return id;
    }

    public synchronized String getMessage() {
        return message;
    }
}
