package fsi.studymyselft.nguyenthanhthi.chatapp.data;

/**
 * Created by thanhthi on 04/05/2018.
 */

public class User {

    private String id;
    private String email;

    public User() {
    }

    public User(String email) {
        this.email = email;
    }

    public User(String id, String email) {
        this.id = id;
        this.email = email;
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
}
