package fsi.studymyselft.nguyenthanhthi.chatapp.data;

import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.Message;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;

/**
 * Created by thanhthi on 14/06/2018.
 */

public class ItemListDialog {

    private User user;
    private Message recentMessage;

    public ItemListDialog() {
    }

    public ItemListDialog(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(Message recentMessage) {
        this.recentMessage = recentMessage;
    }

}
