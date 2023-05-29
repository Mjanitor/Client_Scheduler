package com.example.clientscheduler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import Helper.JDBC;

import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClientScheduler extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        ResourceBundle rb;

        Locale locale = Locale.getDefault();
        System.out.println(locale.getClass());

        if (locale.equals(Locale.FRANCE)) {
            Locale.setDefault(new Locale("fr", "FR"));
            rb = ResourceBundle.getBundle("com.example.clientscheduler.Nat_fr");
        } else {
            Locale.setDefault(new Locale("en", "US"));
            rb = ResourceBundle.getBundle("com.example.clientscheduler.Nat_en");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("hello-view.fxml"));
        fxmlLoader.setResources(rb);

        Scene scene = new Scene(fxmlLoader.load(), 640, 400);
        stage.setTitle(rb.getString("Hello!"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException {
        JDBC.openConnection();

        launch();

        JDBC.closeConnection();
    }
}