package fsi.studymyselft.nguyenthanhthi.chatapp.data;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

/**
 * Created by thanhthi on 07/05/2018.
 */

public class Message implements IMessage, MessageContentType.Image {

    private String id;
    private String text;
    private User user;
    private User userReceive;
    private String imageUrl;

    public Message() {
    }

    public Message(String id, String text, User user, User userReceive) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.userReceive = userReceive;
    }

    public Message(String id, String text, User user, User userReceive, String imageUrl) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.userReceive = userReceive;
        this.imageUrl = imageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserReceive(User userReceive) {
        this.userReceive = userReceive;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public User getUserReceive() {
        return userReceive;
    }

    public String getStatus() {
        return "Sent";
    }
}
