package com.example.clientscheduler;

import Helper.ClientQuery;
import Helper.JDBC;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.io.PrintWriter.*;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controls the FXML elements for the main application window and includes methods and data types to act on that window.
 *
 * FUTURE ENHANCEMENT: One thing that wasn't asked of me but would be cool to implement is filtering without having to input the "enter" key.  I think it's much sleeker to have
 your results filter as you type, but the way it is now is okay.  That's just the thing that most jumped out at me when going through the functionality.
 */
public class LoginController implements Initializable {

    public TextField username;
    public TextField password;
    public Button loginButton;
    public Label location;
    public Label bad_pw;

    private Parent root;

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
        loginButton.setText(rb.getString("Login"));
        location.setText(ZoneId.systemDefault().toString());
    }

    public void checkAppointments() throws SQLException, ParseException, IOException {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalTime now = LocalTime.now();
        String formatted_now = timeFormatter.format(now);
        System.out.println("Current Time: " + formatted_now);

        String sql = "Select * FROM APPOINTMENTS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Date date1 = sdf.parse(formatted_now);
        boolean upcoming = false;
        String appt_id = null;
        String appt_date = null;
        String appt_time = null;

        while (rs.next()) {
            LocalDateTime appt_start = LocalDateTime.parse(rs.getString(6), dtf);
            LocalDate date = appt_start.toLocalDate();
            LocalTime time = appt_start.toLocalTime();

            String converted_time = MainController.convertToLocal(time, date);
            appt_start = LocalDateTime.parse(converted_time, dtf);
            time = appt_start.toLocalTime();

            Date date2 = sdf.parse(time.format(timeFormatter));

            long time_difference = (date2.getTime() - date1.getTime());
            if (time_difference <= 900000 && time_difference >= 0 && upcoming == false) {
                upcoming = true;
                appt_id = rs.getString(1);
                appt_date = date.toString();
                appt_time = time.toString();
            }
        }
        if (upcoming) {
            Alert appointment_soon = new Alert(Alert.AlertType.INFORMATION);
            appointment_soon.setTitle("Appointment Soon!");
            appointment_soon.setHeaderText("Appointment ID #" + appt_id + " at: " + appt_time + ", " + appt_date);
            appointment_soon.setContentText("There is an upcoming appointment within the next 15 minutes!");
            appointment_soon.showAndWait();
        } else {
            Alert no_appointment_soon = new Alert(Alert.AlertType.INFORMATION);
            no_appointment_soon.setTitle("No Appointments Soon");
            no_appointment_soon.setHeaderText("No Appointments");
            no_appointment_soon.setContentText("There are no upcoming appointments.");
            no_appointment_soon.showAndWait();
        }
    }

    public void onLogin() throws SQLException, ParseException, IOException {
        bad_pw.setVisible(false);

        String user_entry = username.getText();
        String user_pw = password.getText();

        // Change these two lines to log in
        boolean logged_in = true;
        //logged_in = ClientQuery.login(user_entry, user_pw);

        String filename = "login_activity.txt", item;

        if (logged_in == true) {
            // Writing success
            try {
                FileWriter log_writer = new FileWriter(filename, true);
                PrintWriter outputFile = new PrintWriter(log_writer);
                item = "User '" + user_entry + "' successfully logged in at '" + ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "' UTC";
                outputFile.println(item);

                outputFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();

            FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1323, 493);
            stage.setScene(scene);
            stage.show();

            checkAppointments();

            fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("addCustomer.fxml"));
            root = fxmlLoader.load();

            // Passing username to different controllers
            addCustomerController addCustomerController = fxmlLoader.getController();
            addCustomerController.getUserName(user_entry);
        } else {
            // Writing failure
            try {
                FileWriter log_writer = new FileWriter(filename, true);
                PrintWriter outputFile = new PrintWriter(log_writer);
                item = "User '" + user_entry + "' gave invalid log-in at '" + ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "' UTC";
                outputFile.println(item);

                outputFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            bad_pw.setVisible(true);
        }
    }
}