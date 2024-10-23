package com.lab.minizalojavafx.controller;

import com.lab.minizalojavafx.message.AlertMessage;
import com.lab.minizalojavafx.model.Client;
import com.lab.minizalojavafx.utils.DBUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegister;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    private DBUtils dbUtils;
    private Client client;
    private AlertMessage alertMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbUtils = new DBUtils();
        client = new Client();
        alertMessage = new AlertMessage();

        btnRegister.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnRegister.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnLogin.setOnAction(this::login);
    }

    public void login(ActionEvent actionEvent) {
        dbUtils = new DBUtils();
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        client.setUsername(username);
        client.setPassword(password);

        boolean loginSuccess = client.login(dbUtils);
        if (loginSuccess) {
            alertMessage.success("Login successful");
        } else {
            alertMessage.error("Login failed");
        }
    }

}