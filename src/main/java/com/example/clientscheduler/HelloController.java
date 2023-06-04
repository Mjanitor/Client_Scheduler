package com.example.clientscheduler;

import Helper.ClientQuery;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controls the FXML elements for the main application window and includes methods and data types to act on that window.
 *
 * FUTURE ENHANCEMENT: One thing that wasn't asked of me but would be cool to implement is filtering without having to input the "enter" key.  I think it's much sleeker to have
 your results filter as you type, but the way it is now is okay.  That's just the thing that most jumped out at me when going through the functionality.
 */
public class HelloController implements Initializable {

    public TextField username;
    public TextField password;
    public Button login;
    public Label location;

    /**
     * Loads all initial data pertaining to the Tables/Columns.
     *
     * @param url The URL, if any, to pull data from
     * @param rb Any included resources
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Locale locale = Locale.getDefault();

        if (locale.equals(Locale.FRANCE)) {
            Locale.setDefault(new Locale("fr", "FR"));
            rb = ResourceBundle.getBundle("com.example.clientscheduler.Nat_fr");
        } else {
            Locale.setDefault(new Locale("en", "US"));
            rb = ResourceBundle.getBundle("com.example.clientscheduler.Nat_en");
        }

        username.setPromptText(rb.getString("Username"));
        password.setPromptText(rb.getString("Password"));
        login.setText(rb.getString("Login"));
        location.setText(ZoneId.systemDefault().toString());
    }

    public void login() throws Exception {
        String user_entry = username.getText();
        String user_pw = password.getText();

        boolean logged_in = ClientQuery.login(user_entry, user_pw);
        System.out.println(logged_in);

        if (logged_in == true) {
            Stage stage = (Stage) login.getScene().getWindow();
            stage.close();

            FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1323, 493);
            stage.setScene(scene);
            stage.show();
        }
    }
}