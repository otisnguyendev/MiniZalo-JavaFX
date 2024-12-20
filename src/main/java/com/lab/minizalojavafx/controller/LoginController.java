package com.lab.minizalojavafx.controller;

import com.lab.minizalojavafx.message.AlertMessage;
import com.lab.minizalojavafx.model.Client;
import com.lab.minizalojavafx.util.DBUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;

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

    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private CheckBox cbShowPassword;

    @FXML
    private Label lbForgotPassword;


    private DBUtils dbUtils;
    private Client client;
    private AlertMessage alertMessage;

    private @Setter Runnable onLoginSuccess;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbUtils = new DBUtils();
        client = new Client();
        alertMessage = new AlertMessage();

        cbShowPassword.setOnAction(event -> togglePasswordVisibility());
        lbForgotPassword.setOnMouseClicked(event -> goToForgotPassword());

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
        String username = txtUsername.getText();
        String password = cbShowPassword.isSelected() ? txtPasswordVisible.getText() : txtPassword.getText();

        client.setUsername(username);
        client.setPassword(password);

        boolean loginSuccess = client.login(dbUtils);
        if (loginSuccess) {
            alertMessage.success("Login successful");

            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client.fxml"));
                Parent root = loader.load();

                ClientController clientController = loader.getController();
                clientController.setClientName(username);

                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

                Stage loginStage = (Stage) btnLogin.getScene().getWindow();
//                loginStage.setScene(new Scene(root));
//                loginStage.show();/

                loginStage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            alertMessage.error("Login failed");
        }
    }

    private void togglePasswordVisibility() {
        if (cbShowPassword.isSelected()) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPassword.setVisible(false);
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPasswordVisible.setVisible(false);
        }
    }

    private void goToForgotPassword() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/forgot-password.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) lbForgotPassword.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
