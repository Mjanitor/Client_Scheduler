package com.example.clientscheduler;

import Helper.ClientQuery;
import Helper.JDBC;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public TextField username;
    public TextField password;
    public Button loginButton;
    public Label location;
    public Label bad_pw;
    public Label timeZone;
    public static boolean french;

    private Parent root;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Locale locale = Locale.getDefault();
        System.out.println(locale);

        if (locale.equals(Locale.FRANCE) || french) {
            Locale.setDefault(new Locale("fr", "FR"));
            rb = ResourceBundle.getBundle("com.example.clientscheduler.Nat_fr");
            french = true;
        } else {
            Locale.setDefault(new Locale("en", "US"));
            rb = ResourceBundle.getBundle("com.example.clientscheduler.Nat_en");
            french = false;
        }

        System.out.println(french);

        username.setPromptText(rb.getString("Username"));
        password.setPromptText(rb.getString("Password"));
        loginButton.setText(rb.getString("Login"));
        timeZone.setText(rb.getString("timezone"));
        bad_pw.setText(rb.getString("login_failure"));
        location.setText(ZoneId.systemDefault().toString());
    }

    public void checkAppointments() throws SQLException, ParseException, IOException {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalTime now = LocalTime.now();
        String formatted_now = timeFormatter.format(now);

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
            if ((time_difference <= 900000) && (time_difference >= 0 && upcoming == false) && (date2.getDate() == date1.getDate())) {
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
        boolean logged_in = false;
        logged_in = ClientQuery.login(user_entry, user_pw);

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

            Locale.setDefault(new Locale("en", "US"));

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();

            FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1323, 493);
            stage.setScene(scene);
            stage.setTitle("Client Scheduler");
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