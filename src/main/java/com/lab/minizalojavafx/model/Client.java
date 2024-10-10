package com.lab.minizalojavafx.model;

import com.lab.minizalojavafx.utils.DBUtils;
import org.mindrot.jbcrypt.BCrypt;

public class Client {
    private String id;
    private String username;
    private String email;
    private String password;

    public Client() {
    }

    public Client(String id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean register(DBUtils dbUtils) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return false;
        }
        if (dbUtils.checkUsernameExist(username)) {
            return false;
        }
        if (dbUtils.checkEmailExist(email)) {
            return false;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        dbUtils.registerUser (username, email, hashedPassword);
        return true;
    }


}
