package fsi.studymyselft.nguyenthanhthi.chatapp.data.model;

import com.stfalcon.chatkit.commons.models.IUser;

/**
 * Created by thanhthi on 04/05/2018.
 */

public class User implements IUser {

    private String id;
    private String email;
    private String name;
    private String avatar;
    private boolean online;

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    public User(String id, String email) {
        this.id = id;
        this.email = email;
        this.online = true;
    }

    public User(String id, String email, String avatar) {
        this.id = id;
        this.email = email;
        this.avatar = avatar;
        this.online = true;
    }

    public User(String id, String email, boolean online) {
        this.id = id;
        this.email = email;
        this.online = online;
    }

    public User(String id, String email, String avatar, boolean online) {
        this.id = id;
        this.email = email;
        this.avatar = avatar;
        this.online = online;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}