package com.lab.minizalojavafx.model;

import com.lab.minizalojavafx.client.ClientHandler;
import com.lab.minizalojavafx.util.DBUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private int id;

    @NotEmpty
    private String username;

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

    @Getter
    private ClientHandler clientHandler;

    public boolean register(DBUtils dbUtils) {
        if (dbUtils.checkUsernameExist(username) || dbUtils.checkEmailExist(email)) {
            return false;
        }
        return dbUtils.registerUser(username, email, password);
    }

    public boolean login(DBUtils dbUtils) {
        if (!dbUtils.checkUsernameExist(username)) {
            return false;
        }
        return dbUtils.loginUser(username, password);
    }
}
