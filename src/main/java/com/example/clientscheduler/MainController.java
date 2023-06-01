package com.example.clientscheduler;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Helper.ClientQuery;
import Helper.JDBC;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.util.concurrent.TimeUnit;

public class MainController implements Initializable {

    public ToggleGroup apptView;
    @FXML
    public TableView tester = new TableView();
    public Button logout;
    public Button delAppt;
    public Button modAppt;
    public Button addAppt;
    public Button logFiles;
    public RadioButton viewCust;
    public RadioButton viewWeek;
    public RadioButton viewMonth;
    public RadioButton viewAll;
    private ObservableList<ObservableList> data;

    public void refresh() throws SQLException, IOException {
        ClientQuery.update();
        buildTable();
        ClientQuery.select();
        buildTable();
    }

    public void addAppt() {

    }

    public void buildTable() {
        data = FXCollections.observableArrayList();
        String SQL = "SELECT * from APPOINTMENTS";
        try {
            JDBC.openConnection();
            if (viewCust.isSelected()) {
                //SQL FOR SELECTING ALL OF CUSTOMERS
                SQL = "SELECT * from APPOINTMENTS WHERE Type LIKE 'PLANNING SESSION'";
            }

            //ResultSet
            PreparedStatement ps = JDBC.connection.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery();
            System.out.println("Column Count: " + rs.getMetaData().getColumnCount());

            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             *********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                tester.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
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
                System.out.println("Row [1] added " + row);
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            tester.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buildTable();
    }
}

