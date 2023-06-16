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
import java.sql.Time;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

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

    public void setApptID(Object ID) throws SQLException {
        modApptID = Integer.valueOf((String) ID);
        System.out.println("First Appointment ID: " + modApptID);
        ArrayList<String> appt_items = ClientQuery.getAppt(modApptID);
        System.out.println("Appointment_Items: " + appt_items);
        setApptItems(appt_items);
    }

    public void onApptSave() throws Exception {

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime();

        System.out.println(now);

        String datetime = MainController.convertToUTC(apptStartTime.getText(), apptStart.getValue());
        String end_datetime = MainController.convertToUTC(apptEndTime.getText(), apptEnd.getValue());

        LocalDateTime est_datetime = LocalDateTime.parse(MainController.convertToEST(apptStartTime.getText(), apptStart.getValue()), timeFormatter);
        System.out.println("EST datetime: " + est_datetime.getHour());

        int ID = count;
        String title = apptTitle.getText();
        String description = apptDescr.getText();
        String location = apptLoc.getText();
        String type = apptType.getText();
        String start = datetime;
        String end = end_datetime;
        String created_by = user_name;
        String updated = MainController.convertToUTC(time.toString(), date);
        String updated_by = user_name;
        String user = apptUser.getText();
        int contact = 0;
        int customer = 0;

        // Input validation
        if (est_datetime.getHour() < 8 || est_datetime.getHour() > 22) {
            bad_time.setVisible(true);
            return;
        }
        if (apptContact.getValue() == null || title == null || description == null || location == null ||
                type == null || start == null || end == null || updated == null ||
                user == null) {
            System.out.println("Try Again!");
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

        System.out.println("Mod Internal Start: " + start);

        ClientQuery.modAppt(ID, title, description, location, type,
                start, end, created_by, updated, updated_by, customer, user, contact);

        Stage stage = (Stage) apptSave.getScene().getWindow();
        stage.close();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
            Scene addScene = new Scene(fxmlLoader.load(), 1323, 493);
            Stage addStage = new Stage();
            addStage.setScene(addScene);
            addStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void onApptCancel() {
        Stage stage = (Stage) apptCancel.getScene().getWindow();
        stage.close();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
            Scene addScene = new Scene(fxmlLoader.load(), 1323, 493);
            Stage addStage = new Stage();
            addStage.setScene(addScene);
            addStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

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

        System.out.println("EST: " + LocalTime.now());
        System.out.println("UTC?: " + finalCombined.toLocalTime());

        String combined_as_string = finalCombined.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String end_combined_as_string = end_finalCombined.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

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

        apptUser.setText(items.get(12));

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
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user_name = addCustomerController.user_name;
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

        String sql_count = "SELECT * FROM appointments";
        try {
            PreparedStatement counter = JDBC.connection.prepareStatement(sql_count);
            ResultSet count_set = counter.executeQuery();
            while (count_set.next()) {
                System.out.println(count);
                count++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        apptID.setText(String.valueOf(count));
    }
}