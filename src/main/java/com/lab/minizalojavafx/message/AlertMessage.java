package com.lab.minizalojavafx.message;

import javafx.scene.control.Alert;

public class AlertMessage {
    private Alert alert;

    public void success(String content) {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success Message");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void error(String content) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
