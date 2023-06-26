package com.example.clientscheduler;

import Helper.ClientQuery;
import Helper.JDBC;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Date;

/**
 * The Controller class that handles all elements of the Appointment Addition Screen.  Functionality includes initial
 * loading of the elements, saving and committing elements to the local database, and canceling to return to the main
 * screen.
 */
public class addApptController implements Initializable {
    public TextField apptID;
    public TextField apptType;
    public TextField apptLoc;
    public TextField apptDescr;
    public TextField apptTitle;
    public ComboBox apptContact;
    public ComboBox apptCust;
    public DatePicker apptStart;
    public TextField apptUser;
    public TextField apptStartTime;
    public DatePicker apptEnd;
    public TextField apptEndTime;

    public static int count = 0;
    public static String user_name;
    public Button apptSave;
    public Button apptCancel;
    public Label bad_time;
    public Label existing_appt;

    /**
     * Collects all information entered into the various appointment fields and reaches out to the MainController and
     * ClientQuery helper classes to convert time zones and insert database information.  In addition, input validation
     * is implemented to make sure that all times are converted to UTC, and it validates any input so that all fields are correctly
     * populated including overlapping appointments, empty fields, and appointments within business hours.
     * @throws Exception
     */
    public void onApptSave() throws Exception {
        // Removing all error messages
        bad_time.setVisible(false);
        existing_appt.setVisible(false);

        // Setting datetime variables and formatter
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start_datetime;
        String end_datetime;
        LocalDateTime est_datetime;
        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime();

        // Attempting to convert given values to UTC and EST for database upload and overlap comparison, respectively
        try {
            start_datetime = MainController.convertToUTC(apptStartTime.getText(), apptStart.getValue());
            end_datetime = MainController.convertToUTC(apptEndTime.getText(), apptStart.getValue());
            est_datetime = LocalDateTime.parse(MainController.convertToEST(apptStartTime.getText(), apptStart.getValue()), dtf);
        }
        catch (DateTimeParseException e) {
            Alert fill_in_boxes = new Alert(Alert.AlertType.ERROR);
            fill_in_boxes.setTitle("Failed!");
            fill_in_boxes.setContentText("Please fill in all fields.");
            fill_in_boxes.showAndWait();

            return;
        }

        // Variables to be uploaded to the database
        int ID = count;
        String title = apptTitle.getText();
        String description = apptDescr.getText();
        String location = apptLoc.getText();
        String type = apptType.getText();
        String start = start_datetime;
        String end = end_datetime;
        String created = MainController.convertToUTC(time.toString(), date);
        String created_by = user_name;
        String updated = MainController.convertToUTC(time.toString(), date);
        String updated_by = user_name;

        // Input validation variables
        int contact = 0;
        int customer = 0;
        int user = 0;
        boolean overlapping = false;

        // Converting contacts to values
        switch ((String) apptContact.getValue()) {
            case "Anika Costa": contact = 1;
                break;
            case "Daniel Garcia": contact = 2;
                break;
            case "Li Lee": contact = 3;
                break;
        }

        // Converting customers to values
        switch ((String) apptCust.getValue()) {
            case "Daddy Warbucks": customer = 1;
                break;
            case "Lady McAnderson": customer = 2;
                break;
            case "Dudley Do-Right": customer = 3;
                break;
        }

        // Converting users to values
        switch ((String) apptUser.getText()) {
            case "test": user = 1;
                break;
            case "admin": user = 2;
                break;
        }

        // ********** Input validation **********

        // Overlapping Check
        Dictionary<String, String> dict = ClientQuery.getApptTimes(ID);
        Enumeration<String> elements = dict.keys();
        while (elements.hasMoreElements()) {
            String key = elements.nextElement();
            LocalDateTime datetime_key = LocalDateTime.parse(key, dtf);
            LocalDateTime datetime_value = LocalDateTime.parse(dict.get(key), dtf);

            // Converting all time variables to the same value for comparison
            Date start1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(MainController.convertToEST(apptStartTime.getText(), apptStart.getValue()));
            Date end1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(MainController.convertToEST(apptEndTime.getText(), apptStart.getValue()));
            Date start2 = Date.from(datetime_key.atZone(ZoneId.of("UTC")).toInstant());
            Date end2 = Date.from(datetime_value.atZone(ZoneId.of("UTC")).toInstant());

            // Comparison
            overlapping = MainController.isOverlapping(start1, end1, start2, end2);
            if (overlapping) {
                break;
            }

            // Start Time before End time check
            if (!MainController.startBeforeEnd(start1, end1)) {
                Alert startEnd = new Alert(Alert.AlertType.ERROR);
                startEnd.setTitle("Failed!");
                startEnd.setContentText("Please place the start time before the end time.");
                startEnd.showAndWait();

                return;
            }
        }

        // Shows overlapping
        if (overlapping) {
            existing_appt.setVisible(true);
            return;
        }

        // Outside of Business Hours Check
        if (est_datetime.getHour() < 8 || est_datetime.getHour() > 22) {
            bad_time.setVisible(true);
            return;
        }

        // Empty Fields Check
        if (apptContact.getValue() == "" || apptTitle.getText() == "" || apptDescr.getText() == "" || apptLoc.getText() == "" ||
                apptType.getText() == "" || start == "" || end == "" || created == "" || updated == "" ||
                user == 0) {
            Alert fill_in_boxes = new Alert(Alert.AlertType.ERROR);
            fill_in_boxes.setTitle("Failed!");
            fill_in_boxes.setContentText("Please fill in all fields.");
            fill_in_boxes.showAndWait();

            return;
        }

        // Pushing to database
        ClientQuery.addAppt(ID, title, description, location, type,
                start, end, created, created_by, updated, updated_by, customer, String.valueOf(user), contact);

        // Closing current and loading main window
        Stage stage = (Stage) apptSave.getScene().getWindow();
        stage.close();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
            Scene addScene = new Scene(fxmlLoader.load(), 1323, 493);
            Stage addStage = new Stage();
            addStage.setScene(addScene);
            stage.setTitle("Client Scheduler");
            addStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes out of the Appointment Addition form and reloads the main Client Scheduling window.
     */
    public void onApptCancel() {
        // Closing current window
        Stage stage = (Stage) apptCancel.getScene().getWindow();
        stage.close();

        // Loading main window
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
            Scene addScene = new Scene(fxmlLoader.load(), 1323, 493);
            Stage addStage = new Stage();
            addStage.setScene(addScene);
            addStage.setTitle("Client Scheduler");
            addStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the initial data of the Appointment Addition form.  In addition, there are four fields with information
     * that needs to be pre-loaded as they are either uneditable or have a ComboBox that needs to be populated with
     * existing choices.
     * @param url standard parameter
     * @param resourceBundle standard parameter
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user_name = addCustomerController.user_name;
        apptUser.setText(user_name);
        String sql;
        try {
            // Populating Contacts
            sql = "SELECT * from contacts";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            String arr[] = {};

            ArrayList<String> contacts = new ArrayList<String>(Arrays.asList(arr));

            while(rs.next()) {
                contacts.add(rs.getString("Contact_Name"));
            }
            contacts.sort(null);

            apptContact.setItems(FXCollections.observableArrayList(contacts));
            apptContact.setPromptText("Select Contact");

            // Populating Customers
            sql = "SELECT * from customers";
            ps = JDBC.connection.prepareStatement(sql);
            rs = ps.executeQuery();
            String arr2[] = {};

            ArrayList<String> customers = new ArrayList<String>(Arrays.asList(arr2));

            while(rs.next()) {
                customers.add(rs.getString("Customer_Name"));
            }
            customers.sort(null);

            apptCust.setItems(FXCollections.observableArrayList(customers));
            apptCust.setPromptText("Select Customer");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Setting the ID based upon largest appointment ID
        String sql_count = "SELECT MAX(Appointment_ID) FROM appointments";
        try {
            PreparedStatement counter = JDBC.connection.prepareStatement(sql_count);
            ResultSet count_set = counter.executeQuery();
            if (count_set.next()) {
                count = count_set.getInt("MAX(Appointment_ID)") + 1;
            } else {
                count = 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        apptID.setText(String.valueOf(count));
    }
}
