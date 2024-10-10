package com.lab.minizalojavafx.controller;

import com.lab.minizalojavafx.message.AlertMessage;
import com.lab.minizalojavafx.model.Client;
import com.lab.minizalojavafx.utils.DBUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegister;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtConfirmPassword;

    private DBUtils dbUtils;
    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbUtils = new DBUtils();
        client = new Client();
    }

    public void register(ActionEvent actionEvent) {
        AlertMessage alertMessage = new AlertMessage();

        client.setUsername(txtUsername.getText());
        client.setEmail(txtEmail.getText());
        client.setPassword(txtPassword.getText());

        if (txtUsername.getText().isEmpty() || txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty() || txtConfirmPassword.getText().isEmpty()) {
            alertMessage.error("All fields are required");
        } else if (txtPassword.getText().length() < 8) {
            alertMessage.error("Password must be at least 8 characters");
        } else if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {
            alertMessage.error("Passwords do not match");
        } else if (client.register(dbUtils)) {
            alertMessage.success("Registration successful");
        } else {
            alertMessage.error("Registration failed");
        }
    }
}