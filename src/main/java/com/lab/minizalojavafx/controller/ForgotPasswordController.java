package com.lab.minizalojavafx.controller;


import com.lab.minizalojavafx.message.AlertMessage;
import com.lab.minizalojavafx.util.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class ForgotPasswordController implements Initializable {
    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPasswordNew;

    @FXML
    private PasswordField txtConfirmPasswordNew;

    @FXML
    private TextField txtCheckCalculation;

    @FXML
    private Text txtCalculation1;

    @FXML
    private Text txtCalculation2;

    @FXML
    private Button btnResetPassword;

    private int expectedAnswer;

    private AlertMessage alertMessage;

    private DBUtils dbUtils;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbUtils = new DBUtils();
        generateRandomCalculation();

        btnResetPassword.setOnAction(event -> resetPassword());
    }

    private void generateRandomCalculation() {
        Random rand = new Random();
        int num1 = rand.nextInt(10);
        int num2 = rand.nextInt(10);
        expectedAnswer = num1 + num2;

        txtCalculation1.setText(String.valueOf(num1));
        txtCalculation2.setText(String.valueOf(num2));
    }

    private void resetPassword() {
        AlertMessage alertMessage = new AlertMessage();
        String email = txtEmail.getText();
        String newPassword = txtPasswordNew.getText();
        String confirmPassword = txtConfirmPasswordNew.getText();
        String calculationResult = txtCheckCalculation.getText();

        if (!dbUtils.emailExists(email)) {
            alertMessage.error("Vui lòng kiểm tra lại email.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            alertMessage.error("Vui lòng kiểm tra lại mật khẩu.");
            return;
        }

        try {
            int answer = Integer.parseInt(calculationResult);
            if (answer != expectedAnswer) {
                alertMessage.error("Vui lòng tính toán lại.");
                return;
            }
        } catch (NumberFormatException e) {
            alertMessage.error("Vui lòng nhập số hợp lệ.");
            return;
        }

        boolean resetSuccess = dbUtils.resetPassword(email, newPassword);
        if (resetSuccess) {
            alertMessage.success("Mật khẩu của bạn đã được cập nhật.");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Parent root = loader.load();
                txtEmail.getScene().setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
                alertMessage.error("Không thể quay lại trang đăng nhập.");
            }
        } else {
            alertMessage.error("Đã xảy ra lỗi khi đặt lại mật khẩu. Vui lòng thử lại.");
        }
    }
}
