package fsi.studymyselft.nguyenthanhthi.chatapp.data.model;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thanhthi on 21/05/2018.
 */

public class Dialog implements IDialog <Message> {

    private String id;
    private String name;
    private String photo;
    private ArrayList<Message> messages;
    private ArrayList<User> members;
    private int unreadCount;

    public Dialog() {
        messages = new ArrayList<>();
        members = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return photo;
    }

    @Override
    public String getDialogName() {
        return name;
    }

    @Override
    public ArrayList<User> getUsers() {
        return members;
    }

    @Override
    public Message getLastMessage() {
        return messages.get(messages.size() - 1);
    }

    @Override
    public void setLastMessage(Message message) {
        messages.add(message);
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * messages
     */

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void addMessageToListMessages(Message message) {
        this.messages.add(message);
    }

    public void removeMessageFromListMessages(Message message) {
        this.messages.remove(message);
    }

    public void removeAllMessagesFromListMessages() {
        this.messages.clear();
    }

    /**
     * members
     */

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }

    public void addUserToListUsers(User user) {
        this.members.add(user);
    }

    public void removeUserFromListUsers(User user) {
        this.members.remove(user);
    }

}