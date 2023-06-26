package com.example.clientscheduler;

import Helper.ClientQuery;
import Helper.JDBC;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

/**
 * The Controller class that handles all elements of customer information modification.  This includes pre-populating
 * customer information fields, validating input, converting IDs to names, and pushing that information to the local
 * database.
 */
public class modCustomerController implements Initializable {
    @FXML
    public ComboBox<String> country;
    public ComboBox<String> division;
    public Button custSave;
    public Button custCancel;
    public TextField custID;
    public TextField custName;
    public TextField custAddress;
    public TextField custPost;
    public TextField custPhone;
    public int customer_ID;
    int count = 1;

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
        // UK Selection
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
     * Collects all entered field information, converts division selection to the associated integer value,
     * validates input to check for empty fields, and pushes the associated fields to the local DB.
     * @throws SQLException
     */
    public void onCustSave() throws SQLException {
        // Division collection
        String sql = "SELECT division_id FROM first_level_divisions WHERE division = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, division.getValue());
        ResultSet rs = ps.executeQuery();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        // DB variables
        int ID = 0;
        String divID = "";
        String name = "";
        String address = "";
        String post = "";
        String phone = "";
        String created = "";
        String created_by = "";
        String updated = "";
        String updated_by = "";

        // Updating the specific client based on ID
        sql = "SELECT * FROM customers WHERE CUSTOMER_ID = ?";
        ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customer_ID);
        rs = ps.executeQuery();

        while (rs.next()) {
            ID = customer_ID;
            name = custName.getText();
            address = custAddress.getText();
            post = custPost.getText();
            phone = custPhone.getText();
            created = (String) dtf.format(now);
            created_by = rs.getString("Created_By");
            updated = (String) dtf.format(now);
            updated_by = addCustomerController.user_name;
            divID = rs.getString("division_id");
        }

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
            fill_in_boxes.setContentText("Please fill in all fields.");
            fill_in_boxes.showAndWait();

            return;
        }

        Stage stage = (Stage) custSave.getScene().getWindow();
        stage.close();

        // Pushing information to the DB
        ClientQuery.modCust(ID, name, address, post, phone, created,
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
     * Closes out of the Customer Modification form and reloads the main Client Scheduling window.
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
     * Collects and prepares all customer information for usage in the main setCustomerItems method based upon selected
     * customer ID.
     * @param ID The customer ID passed in from the main window selection.
     * @throws SQLException
     */
    public void setCustomer_ID(Object ID) throws SQLException {
        customer_ID = Integer.valueOf((String) ID);
        ArrayList<String> customer_items = ClientQuery.getCustomer(customer_ID);
        setCustomerItems(customer_items);
    }

    /**
     * Takes in the parameter, items, to set all selected customer information fields.  It also sets the ComboBox items
     * that it translated from the database and allows for modification in case of location change.
     * @param items The list of database items passed in from the helper method.
     * @throws SQLException
     */
    public void setCustomerItems(ArrayList<String> items) throws SQLException {
        // Getting Country and Division data
        int div_ID = Integer.parseInt(items.get(9));
        String sql = "SELECT * FROM FIRST_LEVEL_DIVISIONS WHERE Division_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, div_ID);

        ResultSet rs = ps.executeQuery();

        // Setting country based on its range of division ID numbers
        if (div_ID < 55) {
            country.setValue("USA");
        } else if (div_ID < 73) {
            country.setValue("Canada");
        } else {
            country.setValue("UK");
        }

        // Setting fields based on passed in items
        custID.setText(items.get(0));
        custName.setText(items.get(1));
        custAddress.setText(items.get(2));
        custPost.setText(items.get(3));
        custPhone.setText(items.get(4));

        if (rs.next()) {
            division.setValue(rs.getString("Division"));
        }

        // Populating Combobox selections
        String countries[] = {"USA", "UK", "Canada"};
        country.setItems(FXCollections.observableArrayList(countries));

        // Adding US states
        String selectedCountry = String.valueOf(country.getValue());
        sql = "SELECT * from FIRST_LEVEL_DIVISIONS WHERE COUNTRY_ID = 1";
        ps = JDBC.connection.prepareStatement(sql);
        rs = ps.executeQuery();

        String arr[] = {};

        ArrayList<String> usDivisions = new ArrayList<String>(Arrays.asList(arr));

        while (rs.next()) {
            usDivisions.add(rs.getString("Division"));
        }
        usDivisions.sort(null);

        division.setItems(FXCollections.observableArrayList(usDivisions));
        division.setPromptText("Select State/Province");
    }

    /**
     * Initialization handled in other methods.
     * @param url Standard Parameter
     * @param resourceBundle Standard Parameter
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
