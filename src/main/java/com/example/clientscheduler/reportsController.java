package com.example.clientscheduler;

import Helper.JDBC;
import Helper.reportsQuery;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class reportsController implements Initializable {
    public Label total;
    public Button back;
    public Button logout;
    public RadioButton byCountry;
    public RadioButton byMonth;
    public RadioButton byType;
    public ToggleGroup apptView;
    public RadioButton byID;
    public Text totalCust;
    public ComboBox typeCombo;
    public Label type;
    public ComboBox contactCombo;
    @FXML
    public TableView reportsTable;
    private ObservableList<ObservableList> reportsData;
    public Label contact;
    private Parent root;
    public int results_count = 0;


    public void onLogout() throws IOException {
        Stage stage = (Stage) logout.getScene().getWindow();
        //stage.setTitle(rb.getString("scheduler")); //TODO
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 400);
        stage.setScene(scene);
        stage.show();
    }
    public void onBack() {
        Stage stage = (Stage) back.getScene().getWindow();
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

    public void buildTable() throws SQLException {
        String column = null;
        PreparedStatement ps = null;
        boolean country = false;

        String local = TimeZone.getDefault().getDisplayName().toString();
        if (local.equals("Eastern Standard Time")) {
            local = "America/New_York";
        }

        // Clearing all columns as to not scale to infinite columns
        reportsTable.getItems().clear();
        reportsTable.getColumns().clear();

        reportsData = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM Appointments";

        String comboSQL = null;
        ResultSet rs = null;

        if (byID.isSelected()) {
            results_count = 0;
            String array_contact = null;
            total.setText("Total Appointments: ");
            ArrayList<String> contacts = new ArrayList<String>();

            comboSQL = "SELECT * from appointments";
            column = "Contact_ID";

            contacts = reportsQuery.getComboBoxList(comboSQL, column);
            typeCombo.setItems(FXCollections.observableArrayList(contacts));
            typeCombo.setPromptText("Select Contact ID");

            try {
                array_contact = typeCombo.getValue().toString();
            } catch (NullPointerException e) {
                System.out.println("No selection.");
            }

            rs = reportsQuery.sortByContactID(array_contact);
        }

        if (byType.isSelected()) {
            results_count = 0;
            String array_type = null;
            total.setText("Total Appointments: ");
            ArrayList<String> types = new ArrayList<String>();

            comboSQL = "SELECT * from appointments";
            column = "Type";

            types = reportsQuery.getComboBoxList(comboSQL, column);
            typeCombo.setItems(FXCollections.observableArrayList(types));
            typeCombo.setPromptText("Select Type");

            try {
                array_type = typeCombo.getValue().toString();
            } catch (NullPointerException e) {
                System.out.println("No selection.");
            }

            rs = reportsQuery.sortByType(array_type);

        }
        if (byMonth.isSelected()) {
            results_count = 0;
            String array_month = null;
            total.setText("Total Appointments: ");
            ArrayList<String> months = new ArrayList<String>();

            months.add("January");
            months.add("February");
            months.add("March");
            months.add("April");
            months.add("May");
            months.add("June");
            months.add("July");
            months.add("August");
            months.add("September");
            months.add("October");
            months.add("November");
            months.add("December");

            typeCombo.setItems(FXCollections.observableArrayList(months));
            typeCombo.setPromptText("Select Month");

            try {
                array_month = typeCombo.getValue().toString();
            } catch (NullPointerException e) {
                System.out.println("No selection.");
            }

            int month_num = 0;
            switch (array_month) {
                case "January": month_num = 1;
                    break;
                case "February": month_num = 2;
                    break;
                case "March": month_num = 3;
                    break;
                case "April": month_num = 4;
                    break;
                case "May": month_num = 5;
                    break;
                case "June": month_num = 6;
                    break;
                case "July": month_num = 7;
                    break;
                case "August": month_num = 8;
                    break;
                case "September": month_num = 9;
                    break;
                case "October": month_num = 10;
                    break;
                case "November": month_num = 11;
                    break;
                case "December": month_num = 12;
                    break;
            }

            rs = reportsQuery.sortByMonth(month_num);

        }
        if (byCountry.isSelected()) {
            results_count = 0;
            String array_country = null;
            ArrayList<String> countries = new ArrayList<String>();

            total.setText("Total Customers: ");
            System.out.println("Inside Country");
            country = true;

            countries.add("U.S");
            countries.add("UK");
            countries.add("Canada");

            typeCombo.setPromptText("Select Country");
            typeCombo.setItems(FXCollections.observableArrayList(countries));

            try {
                array_country = typeCombo.getValue().toString();
            } catch (NullPointerException e) {
                System.out.println("No selection.");
            }

            rs = reportsQuery.sortByCountry(array_country);
            System.out.println("Country RS: " + rs);
        }

        if (rs == null) {
            System.out.println("Null: " + rs);
            ps = JDBC.connection.prepareStatement(SQL);
            rs = ps.executeQuery(SQL);
        }

        if (country) {
            SQL = "SELECT * FROM Customers";
        }
        System.out.println(SQL);

        /**
         * ********************************
         * TABLE COLUMN ADDED DYNAMICALLY *
         *********************************
         */
        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
            //We are using non property style for making dynamic table
            final int j = i;
            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
            String finalSQL = SQL;
            int finalI = i;
            System.out.println("Final I: " + finalI);
            System.out.println("Final SQL: " + finalSQL);
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {

                    // Converting Time Zone
                    if ((finalI == 5) || (finalI == 6 && finalSQL == "SELECT * FROM Appointments") || (finalI == 7) || (finalI == 9 && finalSQL == "SELECT * FROM Appointments")) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime date_time = LocalDateTime.parse((CharSequence) param.getValue().get(finalI), dtf);

                        LocalDate date = date_time.toLocalDate();
                        LocalTime local_time = date_time.toLocalTime();

                        String converted_time = MainController.convertToLocal(local_time, date);

                        return new SimpleStringProperty(converted_time);
                    }
                    return new SimpleStringProperty(param.getValue().get(j).toString());

                }
            });

            reportsTable.getColumns().addAll(col);
        }

        /**
         * ******************************
         * Data added to ObservableList *
         *******************************
         */
        while (rs.next()) {
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(rs.getString(i));
            }
            reportsData.add(row);
            results_count++;
        }

        //FINALLY ADDED TO TableView
        reportsTable.setItems(reportsData);
        totalCust.setText(String.valueOf(results_count));

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            buildTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
