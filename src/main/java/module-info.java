module com.lab.minizalojavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jbcrypt;
    requires static lombok;
    requires java.validation;

    opens com.lab.minizalojavafx to javafx.fxml;
    opens com.lab.minizalojavafx.controller to javafx.fxml;

    exports com.lab.minizalojavafx;
}
