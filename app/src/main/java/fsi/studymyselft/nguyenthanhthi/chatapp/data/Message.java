package fsi.studymyselft.nguyenthanhthi.chatapp.data;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

/**
 * Created by thanhthi on 07/05/2018.
 */

public class Message implements IMessage {

    private String id;
    private String text;
    private User user; //user send
    private Date createdAt;

    public Message() {
    }

    public Message(String id, String text, User user) {
        this.id = id;
        this.text = text;
        this.user = user;
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

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public IUser getUser() {
        return this.user;
    }

    @Override
    public Date getCreatedAt() {
        return this.createdAt;
    }

}
