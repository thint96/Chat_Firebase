package fsi.studymyselft.nguyenthanhthi.chatapp.data.model;

import java.util.ArrayList;

/**
 * Created by thanhthi on 21/05/2018.
 */

public class Dialog {

    private String id;
    private String name;
    private ArrayList<Message> messages;
    private ArrayList<User> members;

    public Dialog() {
        messages = new ArrayList<>();
        members = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //----------------------------------
    //messages

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

    //----------------------------------
    //members

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