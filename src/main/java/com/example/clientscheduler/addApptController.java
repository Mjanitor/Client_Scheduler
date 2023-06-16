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
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.Date;

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

    public void onApptSave() throws Exception {
        // Removing all error messages
        bad_time.setVisible(false);
        existing_appt.setVisible(false);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime();

        String start_datetime = MainController.convertToUTC(apptStartTime.getText(), apptStart.getValue());
        String end_datetime = MainController.convertToUTC(apptEndTime.getText(), apptStart.getValue());
        LocalDateTime est_datetime = LocalDateTime.parse(MainController.convertToEST(apptStartTime.getText(), apptStart.getValue()), dtf);
        System.out.println("EST datetime: " + est_datetime.getHour());

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
        String user = apptUser.getText();
        int contact = 0;
        int customer = 0;
        boolean overlapping = false;

        // Input validation
        Dictionary<String, String> dict = ClientQuery.getApptTimes();
        Enumeration<String> elements = dict.keys();
        while (elements.hasMoreElements()) {
            String key = elements.nextElement();
            LocalDateTime datetime_key = LocalDateTime.parse(key, dtf);
            LocalDateTime datetime_value = LocalDateTime.parse(dict.get(key), dtf);

            LocalDate key_date = datetime_key.toLocalDate();
            LocalTime key_time = datetime_key.toLocalTime();

            String converted_key = MainController.convertToEST(key_time.toString(), key_date);

            LocalDate value_date = datetime_value.toLocalDate();
            LocalTime value_time = datetime_value.toLocalTime();

            String converted_value = MainController.convertToEST(value_time.toString(), value_date);

            Date start1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(MainController.convertToEST(apptStartTime.getText(), apptStart.getValue()));
            Date end1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(MainController.convertToEST(apptEndTime.getText(), apptStart.getValue()));
            Date start2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(converted_key);
            Date end2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(converted_value);

            overlapping = MainController.isOverlapping(start1, end1, start2, end2);
            System.out.println(overlapping);
            System.out.println("OVERLAPPINGGGGGGGGGGGGG CHECK");
        }
        if (overlapping) {
            existing_appt.setVisible(true);
            return;
        }
        if (est_datetime.getHour() < 8 || est_datetime.getHour() > 22) {
            bad_time.setVisible(true);
            return;
        }
        if (apptContact.getValue() == null || title == null || description == null || location == null ||
                type == null || start == null || end == null || created == null || updated == null ||
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

        ClientQuery.addAppt(ID, title, description, location, type,
                start, end, created, created_by, updated, updated_by, customer, user, contact);

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
                count++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        apptID.setText(String.valueOf(count));
    }
}
