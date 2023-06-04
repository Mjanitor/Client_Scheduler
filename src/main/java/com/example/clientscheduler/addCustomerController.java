package com.example.clientscheduler;

import Helper.ClientQuery;
import Helper.JDBC;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
import java.util.ResourceBundle;

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
        System.out.println(division.getValue());
        ResultSet rs = ps.executeQuery();
        System.out.println(rs);
        if (rs.next()) {
            System.out.println("result!");
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        int ID = count;
        String name = custName.getText();
        String address = custAddress.getText();
        String post = custPost.getText();
        String phone = custPhone.getText();
        String created = (String) dtf.format(now);
        String created_by = "admin"; //TODO
        String updated = (String) dtf.format(now);
        String updated_by = "admin"; //TODO
        String divID = rs.getString("division_id");

        ClientQuery.addCust(ID, name, address, post, phone, created,
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
