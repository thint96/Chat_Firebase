package fsi.studymyselft.nguyenthanhthi.chatapp.data;

import java.util.ArrayList;

/**
 * Created by thanhthi on 18/05/2018.
 */

public class Conversation {

    private String id;
    private String name;
    private ArrayList<Message> listMessages;

    public Conversation() {
    }

    public Conversation(String id) {
        this.id = id;
    }

    public Conversation(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Conversation(String id, ArrayList<Message> listMessages) {
        this.id = id;
        this.listMessages = listMessages;
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

    public ArrayList<Message> getListMessages() {
        return listMessages;
    }

    public void setListMessages(ArrayList<Message> listMessages) {
        this.listMessages = listMessages;
    }
}
