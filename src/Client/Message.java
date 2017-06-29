package Client;

public class Message {

    private MessageIDs id;
    private String json;

    public Message(MessageIDs id, String json) {
        this.id = id;
        this.json = json;
    }

    public MessageIDs getId() {
        return id;
    }

    public String getJson() {
        return json;
    }
}
