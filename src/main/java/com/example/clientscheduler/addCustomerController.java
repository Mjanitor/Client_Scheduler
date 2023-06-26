package com.example.clientscheduler;

import Helper.ClientQuery;
import Helper.JDBC;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import javafx.scene.control.*;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * The Controller class that handles all elements of the Customer Addition Screen.  Functionality includes population
 * of Country and Division Combo Box values, a setter for the logged-in user's username,initial loading of the elements,
 * saving and committing elements to the local database, and canceling to return to the main
 * screen.
 */
public class addCustomerController implements Initializable {
    @FXML
    public ComboBox<String> country;
    @FXML
    public ComboBox<String> division;
    public Button custSave;
    public Button custCancel;
    public TextField custID;
    public TextField custName;
    public TextField custAddress;
    public TextField custPost;
    public TextField custPhone;

    public static int count = 0;

    public static String user_name;

    /**
     * Collects the selected country and based from that, sets the Divison ComboBox's dropdown items to be whatever the
     * associated divisions (State/Province) are for that country.
     * @throws SQLException
     */
    public void setCountry() throws SQLException {
        String selectedCountry = String.valueOf(country.getValue());

        // USA selection
        if (selectedCountry.equals("USA")) {
            String sql = "SELECT * from FIRST_LEVEL_DIVISIONS WHERE COUNTRY_ID = 1";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            String arr[] = {};

            ArrayList<String> usDivisions = new ArrayList<String>(Arrays.asList(arr));

            while (rs.next()) {
                usDivisions.add(rs.getString("Division"));
            }
            usDivisions.sort(null);

            division.setItems(FXCollections.observableArrayList(usDivisions));
            division.setPromptText("Select State/Province");

        // UK selection
        } else if (selectedCountry.equals("UK")) {
            String sql = "SELECT * from FIRST_LEVEL_DIVISIONS WHERE COUNTRY_ID = 2";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            String arr[] = {};

            ArrayList<String> ukDivisions = new ArrayList<String>(Arrays.asList(arr));

            while (rs.next()) {
                ukDivisions.add(rs.getString("Division"));
            }
            ukDivisions.sort(null);

            division.setItems(FXCollections.observableArrayList(ukDivisions));
            division.setPromptText("Select State/Province");
        // Canada Selection
        } else if (selectedCountry.equals("Canada")) {
            String sql = "SELECT * from FIRST_LEVEL_DIVISIONS WHERE COUNTRY_ID = 3";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            String arr[] = {};

            ArrayList<String> caDivisions = new ArrayList<String>(Arrays.asList(arr));

            while (rs.next()) {
                caDivisions.add(rs.getString("Division"));
            }
            caDivisions.sort(null);

            division.setItems(FXCollections.observableArrayList(caDivisions));
            division.setPromptText("Select State/Province");
        }
        division.setPromptText("Select State/Province");
    }

    /**
     * Sets the global variable user_name to the passed parameter "user" so that it may be utilized when pushing to the
     * database.
     * @param user The username of whomsoever is logged in for the purposes of pre-populating the "user" field.
     */
    public void getUserName(String user) {
        user_name = user;
    }

    /**
     * Saves and pushes all entered information into the local database.  In addition to this, it validates the inputs,
     * checking for empty fields or mis-entered information.  It also handles conversion from the text-based selection
     * of a division and converts that to an integer that can be input to that Division_ID field.
     * @throws SQLException
     */
    public void onCustSave() throws SQLException {
        // Grabbing Division ID name
        String sql = "SELECT division_id FROM first_level_divisions WHERE division = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, division.getValue());
        ResultSet rs = ps.executeQuery();
        String divID = null;
        if (rs.next()) {
            System.out.print("");
            divID = rs.getString("division_id");
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        // Setting variables to upload to DB
        int ID = count;
        String name = custName.getText();
        String address = custAddress.getText();
        String post = custPost.getText();
        String phone = custPhone.getText();
        String created = (String) dtf.format(now);
        String created_by = user_name;
        String updated = (String) dtf.format(now);
        String updated_by = user_name;

        // Validating Address format
        String US_CA_REGEX = "^[0-9]+\\h[A-Za-z]+\\h[A-Za-z]+,\\h[A-Za-z]+$";
        String US_REGEX_EXTRA = "^[0-9]+\\h[A-Za-z]+\\h[A-Za-z]+,\\h[A-Za-z]+\\h[A-Za-z]+$";
        String UK_REGEX = "^[0-9]+\\h[A-Za-z]+\\h[A-Za-z]+,\\h[A-Za-z]+,\\h[A-Za-z]+$";
        if (!address.matches(US_CA_REGEX) && !address.matches(US_REGEX_EXTRA) && !address.matches(UK_REGEX)) {
            System.out.println("Address");
            Alert fill_in_boxes = new Alert(Alert.AlertType.ERROR);
            fill_in_boxes.setTitle("Failed!");
            fill_in_boxes.setContentText("Please fill in all fields correctly.");
            fill_in_boxes.showAndWait();

            return;
        }

        // Validating ZIP
        String ZIP = "^[a-zA-Z0-9]{5}$";
        if (!post.matches(ZIP)) {
            System.out.println("ZIP");
            Alert fill_in_boxes = new Alert(Alert.AlertType.ERROR);
            fill_in_boxes.setTitle("Failed!");
            fill_in_boxes.setContentText("Please fill in all fields correctly.");
            fill_in_boxes.showAndWait();

            return;
        }

        // Validating Phone #
        String TEL_USCA = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$";
        String TEL_UK = "^[0-9]+-[0-9]{3}-[0-9]{3}-[0-9]{4}$";
        if (!phone.matches(TEL_USCA) && !phone.matches(TEL_UK)) {
            System.out.println("Tel");
            Alert fill_in_boxes = new Alert(Alert.AlertType.ERROR);
            fill_in_boxes.setTitle("Failed!");
            fill_in_boxes.setContentText("Please fill in all fields correctly.");
            fill_in_boxes.showAndWait();

            return;
        }

        // Empty Fields Check
        if (Objects.equals(custName.getText(), "") || Objects.equals(custAddress.getText(), "") ||
                Objects.equals(custPost.getText(), "") || Objects.equals(custPhone.getText(), "") ||
                created == null || created_by == null || updated == null || updated_by == null || divID == null) {

            Alert fill_in_boxes = new Alert(Alert.AlertType.ERROR);
            fill_in_boxes.setTitle("Failed!");
            fill_in_boxes.setContentText("Please fill in all fields correctly.");
            fill_in_boxes.showAndWait();

            return;
        }

        Stage stage = (Stage) custSave.getScene().getWindow();
        stage.close();

        // Pushing to DB
        ClientQuery.addCust(ID, name, address, post, phone, created,
                created_by, updated, updated_by, divID);

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
     * Closes out of the Customer Addition form and reloads the main Client Scheduling window.
     */
    public void onCustCancel () {
        Stage stage = (Stage) custCancel.getScene().getWindow();
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
     * Loads initial data for both the Country ComboBox and the Customer ID.
     * @param url standard parameter
     * @param resourceBundle standard parameter
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        count = 1;
        String countries[] = {"USA", "UK", "Canada"};
        country.setItems(FXCollections.observableArrayList(countries));

        String sql_count = "SELECT * FROM customers";
        try {
            PreparedStatement counter = JDBC.connection.prepareStatement(sql_count);
            ResultSet count_set = counter.executeQuery();
            while (count_set.next()) {
                count++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        custID.setText(String.valueOf(count));
    }
}
