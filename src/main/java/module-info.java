module com.lab.minizalojavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jbcrypt;
    requires static lombok;
    requires java.validation;
    requires emoji.java;
    requires java.desktop;

    opens com.lab.minizalojavafx to javafx.fxml;
    opens com.lab.minizalojavafx.controller to javafx.fxml;

    exports com.lab.minizalojavafx;
    exports com.lab.minizalojavafx.server to javafx.graphics;
}
