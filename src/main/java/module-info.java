module com.lab.minizalojavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.lab.minizalojavafx to javafx.fxml;
    exports com.lab.minizalojavafx;
}