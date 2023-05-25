module com.example.clientscheduler {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.clientscheduler to javafx.fxml;
    exports com.example.clientscheduler;
}