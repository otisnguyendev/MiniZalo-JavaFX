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

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    private Button btnRegister;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;

    private DBUtils dbUtils;
    private Client client;
    private AlertMessage alertMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbUtils = new DBUtils();
        client = new Client();
        alertMessage = new AlertMessage();

        btnRegister.setOnAction(this::register);

    }

    public void register(ActionEvent actionEvent) {
        client.setUsername(txtUsername.getText());
        client.setEmail(txtEmail.getText());
        client.setPassword(txtPassword.getText());

        if (txtUsername.getText().isEmpty() || txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty() || txtConfirmPassword.getText().isEmpty()) {
            alertMessage.error("All fields are required");
        } else if (txtPassword.getText().length() < 8) {
            alertMessage.error("Password must be at least 8 characters");
        } else if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {
            alertMessage.error("Passwords do not match");
        } else {
            try {
                if (client.register(dbUtils)) {
                    alertMessage.success("Registration successful");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) btnRegister.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    alertMessage.error("Registration failed. Please try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                alertMessage.error("An error occurred during registration.");
            }
        }
    }
}
