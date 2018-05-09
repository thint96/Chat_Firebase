package fsi.studymyselft.nguyenthanhthi.chatapp.data;

/**
 * Created by thanhthi on 07/05/2018.
 */

public class Message {

    private String id;
    private String content;
    private User userSend;
    private User userReceive;

    public Message() {
    }

    public Message(String id, String content, User personSend, User userReceive) {
        this.id = id;
        this.content = content;
        this.userSend = personSend;
        this.userReceive = userReceive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUserSend() {
        return userSend;
    }

    public void setUserSend(User userSend) {
        this.userSend = userSend;
    }

    public User getUserReceive() {
        return userReceive;
    }

    public void setUserReceive(User userSend) {
        this.userSend = userSend;
    }


}
