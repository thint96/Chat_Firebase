package fsi.studymyselft.nguyenthanhthi.chatapp.data.model;

/**
 * Created by thanhthi on 12/06/2018.
 */

public class MessageRecent {

    private String id;
    private Message message;

    public MessageRecent() {
    }

    public MessageRecent(String id) {
        this.id = id;
    }

    public MessageRecent(String id, Message message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
