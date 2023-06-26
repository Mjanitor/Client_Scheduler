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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Pre-loads and allows for modification of Client Appointments.  This class pulls the selected appointment's info
 * from the database and pre-populates the fields so that only necessary modifications need to be entered.  Like the
 * Appointment Addition controller, this too, plans for input validation.
 */
public class modApptController implements Initializable {
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
    public int modApptID;
    public Label bad_time;
    public Label existing_appt;

    /**
     * This method grabs all database information pertaining to the selected appointment, particularly the ID, and calls
     * the setApptItems method to populate.
     * @param ID The passed-in Appointment ID number collected from the database upon window initialization.
     * @throws SQLException
     */
    public void setApptID(Object ID) throws SQLException {
        modApptID = Integer.valueOf((String) ID);
        ArrayList<String> appt_items = ClientQuery.getAppt(modApptID);
        setApptItems(appt_items);
    }

    /**
     * This method gathers any modifications to the selected appointment and uploads them to the database.  Before it
     * does that, it makes sure to convert all times to UTC and validates any input so that all fields are correctly
     * populated including overlapping appointments, empty fields, and appointments within business hours.
     * @throws Exception
     */
    public void onApptSave() throws Exception {
        // Removing all error messages
        bad_time.setVisible(false);
        existing_appt.setVisible(false);

        String datetime;
        String end_datetime;
        LocalDateTime est_datetime;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime();

        // Converting times
        try {
            datetime = MainController.convertToUTC(apptStartTime.getText(), apptStart.getValue());
            end_datetime = MainController.convertToUTC(apptEndTime.getText(), apptStart.getValue());

            est_datetime = LocalDateTime.parse(MainController.convertToEST(apptStartTime.getText(), apptStart.getValue()), timeFormatter);
        } catch (DateTimeParseException e) {
            Alert fill_in_boxes = new Alert(Alert.AlertType.ERROR);
            fill_in_boxes.setTitle("Failed!");
            fill_in_boxes.setContentText("Please fill in all fields.");
            fill_in_boxes.showAndWait();

            return;
        }

        // DB variables
        int ID = count;
        String title = apptTitle.getText();
        String description = apptDescr.getText();
        String location = apptLoc.getText();
        String type = apptType.getText();
        String start = datetime;
        String end = end_datetime;
        String created = MainController.convertToUTC(time.toString(), date);
        String created_by = user_name;
        String updated = MainController.convertToUTC(time.toString(), date);
        String updated_by = user_name;
        String user = apptUser.getText();

        int contact = 0;
        int customer = 0;
        int user_id = 0;
        boolean overlapping = false;

        // Input validation

        // Overlapping Check
        Dictionary<String, String> dict = ClientQuery.getApptTimes(ID);
        Enumeration<String> elements = dict.keys();
        while (elements.hasMoreElements()) {
            String key = elements.nextElement();
            LocalDateTime datetime_key = LocalDateTime.parse(key, dtf);
            LocalDateTime datetime_value = LocalDateTime.parse(dict.get(key), dtf);

            Date start1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(MainController.convertToEST(apptStartTime.getText(), apptStart.getValue()));
            Date end1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(MainController.convertToEST(apptEndTime.getText(), apptStart.getValue()));
            Date start2 = Date.from(datetime_key.atZone(ZoneId.of("UTC")).toInstant());
            Date end2 = Date.from(datetime_value.atZone(ZoneId.of("UTC")).toInstant());

            overlapping = MainController.isOverlapping(start1, end1, start2, end2);
            if (overlapping && (dict.size() > 1)) {
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
                apptType.getText() == "" || start == "" || end == "" || created == "" || updated == "") {
            Alert fill_in_boxes = new Alert(Alert.AlertType.ERROR);
            fill_in_boxes.setTitle("Failed!");
            fill_in_boxes.setContentText("Please fill in all fields.");
            fill_in_boxes.showAndWait();

            return;
        }

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
            case "test": user_id = 1;
                break;
            case "admin": user_id = 2;
                break;
        }

        // Pushing to DB
        ClientQuery.modAppt(ID, title, description, location, type,
                start, end, created_by, updated, updated_by, customer, String.valueOf(user_id), contact);

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
     * Closes out of the Appointment Modification form and reloads the main Client Scheduling window.
     */
    public void onApptCancel() {
        Stage stage = (Stage) apptCancel.getScene().getWindow();
        stage.close();

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
     * Main method that acquires items from the setApptID method and populates the form with existing database info.
     * For ID handling, it grabs the associated IDs from the database and utilizes switch statements to verify which ID
     * belongs to which person.
     * @param items The list of items correlating to the appointment from the database.
     * @throws SQLException
     */
    public void setApptItems(ArrayList<String> items) throws SQLException {
        // Getting date and time for appointment
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime appt_time = LocalDateTime.parse(items.get(5), dtf);
        LocalDateTime appt_end_time = LocalDateTime.parse(items.get(6), dtf);

        LocalDate date = appt_time.toLocalDate();
        LocalTime start_time = appt_time.toLocalTime();
        LocalTime end_time = appt_end_time.toLocalTime();

        LocalDateTime start_combined = LocalDateTime.of(date, start_time);
        LocalDateTime end_combined = LocalDateTime.of(date, end_time);

        ZonedDateTime combinedZoned = start_combined.atZone(ZoneId.systemDefault());
        ZonedDateTime finalCombined = combinedZoned.withZoneSameInstant(ZoneId.systemDefault());

        ZonedDateTime end_combinedZoned = end_combined.atZone(ZoneId.systemDefault());
        ZonedDateTime end_finalCombined = end_combinedZoned.withZoneSameInstant(ZoneId.systemDefault());

        String combined_as_string = finalCombined.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String end_combined_as_string = end_finalCombined.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // DB variables
        apptID.setText(items.get(0));
        count = Integer.valueOf(apptID.getText());
        apptTitle.setText(items.get(1));
        apptDescr.setText(items.get(2));
        apptLoc.setText(items.get(3));
        apptType.setText(items.get(4));;
        apptStart.setValue(date);
        apptEnd.setValue(date);
        apptStartTime.setText(combined_as_string);
        apptEndTime.setText(end_combined_as_string);

        String user = null;

        // Converting contacts to values
        switch (items.get(13)) {
            case "1": apptContact.setValue("Anika Costa");;
                break;
            case "2": apptContact.setValue("Daniel Garcia");;
                break;
            case "3": apptContact.setValue("Li Lee");;
                break;
        }

        // Converting customers to values
        switch (items.get(13)) {
            case "1": apptCust.setValue("Daddy Warbucks");;
                break;
            case "2": apptCust.setValue("Lady McAnderson");;
                break;
            case "3": apptCust.setValue("Dudley Do-Right");;
                break;
        }

        // Converting users to values
        switch (items.get(12)) {
            case "1": user = "test";
                break;
            case "2": user = "admin";
                break;
        }

        apptUser.setText(user);
    }

    /**
     * Initializes the form including both the contact and customer ComboBoxes and sets the ID to the maximum per the
     * database MAX.
     * @param url Standard Parameter
     * @param resourceBundle Standard Parameter
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user_name = addCustomerController.user_name;
        apptUser.setText(user_name);
        count = 1;
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