package com.example.clientscheduler;

import Helper.ClientQuery;
import Helper.JDBC;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

public class addApptController implements Initializable {
    public TextField apptID;
    public TextField apptType;
    public TextField apptLoc;
    public TextField apptDescr;
    public TextField apptTitle;
    public ComboBox apptContact;
    public TextField apptCust;
    public DatePicker apptStart;
    public TextField apptUser;
    public TextField apptStartTime;
    public DatePicker apptEnd;
    public TextField apptEndTime;

    public static int count = 0;
    public static String user_name;
    public Button apptSave;
    public Button apptCancel;

    public void onApptSave() throws Exception {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        System.out.println("Appt count: " + count);

        ArrayList<Object> fields = new ArrayList<Object>();

        Collections.addAll(fields, count, apptTitle.getText(), apptDescr.getText(), apptLoc.getText(), apptType.getText(), apptStart.getValue(), apptCust.getText(), apptUser.getText());

        int ID = count;
        String title = apptTitle.getText();
        String description = apptDescr.getText();
        String location = apptLoc.getText();
        String type = apptType.getText();
        LocalDate start = apptStart.getValue();
        LocalDate end = apptStart.getValue();
        String created = (String) dtf.format(now);
        String created_by = user_name;
        String updated = (String) dtf.format(now);
        String updated_by = user_name;
        String customer = apptCust.getText();
        String user = apptUser.getText();
        int contact = 0;

        // TODO: Input validation
        if (apptContact.getValue() == null){
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

        ClientQuery.addAppt(ID, title, description, location, type,
                start, end, created,
                created_by, updated, updated_by, customer, user, contact);

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
        String sql = "SELECT * from contacts";
        try {
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
