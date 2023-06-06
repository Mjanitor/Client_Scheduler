package com.example.clientscheduler;

import Helper.ClientQuery;
import Helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
import java.util.ResourceBundle;

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

    public void setCountry() throws SQLException {
        String selectedCountry = String.valueOf(country.getValue());
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
            System.out.println(usDivisions);

            division.setItems(FXCollections.observableArrayList(usDivisions));
            division.setPromptText("Select State/Province");

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
            System.out.println(ukDivisions);

            division.setItems(FXCollections.observableArrayList(ukDivisions));
            division.setPromptText("Select State/Province");
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
            System.out.println(caDivisions);

            division.setItems(FXCollections.observableArrayList(caDivisions));
            division.setPromptText("Select State/Province");
        }
        division.setPromptText("Select State/Province");
        System.out.println(division.getPromptText());
    }

    public void onCustSave() throws SQLException {
        Stage stage = (Stage) custSave.getScene().getWindow();
        stage.close();

        String sql = "SELECT division_id FROM first_level_divisions WHERE division = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, division.getValue());
        ResultSet rs = ps.executeQuery();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

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

        sql = "SELECT * FROM customers WHERE CUSTOMER_ID = ?";
        ps = JDBC.connection.prepareStatement(sql);
        System.out.println("Final Customer ID: " + customer_ID);
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

        ClientQuery.modCust(ID, name, address, post, phone, created,
                created_by, updated, updated_by, divID);

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

    public void onCustCancel (ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) custCancel.getScene().getWindow();
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

    public void setCustomer_ID(Object ID) throws SQLException {
        customer_ID = Integer.valueOf((String) ID);
        System.out.println("First Customer_ID: " + customer_ID);
        ArrayList<String> customer_items = ClientQuery.getCustomer(customer_ID); // TODO
        System.out.println("Customer_Items: " + customer_items);
        setCustomerItems(customer_items);
    }

    public void setCustomerItems(ArrayList<String> items) throws SQLException {
        // Getting Country and Division data
        int div_ID = Integer.parseInt(items.get(9));
        String sql = "SELECT * FROM FIRST_LEVEL_DIVISIONS WHERE Division_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, div_ID);

        ResultSet rs = ps.executeQuery();

        if (div_ID < 55) {
            country.setValue("USA");
        } else if (div_ID < 73) {
            country.setValue("Canada");
        } else {
            country.setValue("UK");
        }

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
        System.out.println(usDivisions);

        division.setItems(FXCollections.observableArrayList(usDivisions));
        division.setPromptText("Select State/Province");
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
