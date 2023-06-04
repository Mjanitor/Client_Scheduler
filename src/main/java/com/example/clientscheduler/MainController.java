package com.example.clientscheduler;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import Helper.JDBC;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.util.concurrent.TimeUnit;

public class MainController implements Initializable {

    public ToggleGroup apptView;
    @FXML
    public TableView tester;
    public Button logout;
    public Button delAppt;
    public Button modAppt;
    public Button addAppt;
    public Button delCust;
    public Button modCust;
    public Button addCust;
    public Button logFiles;
    public RadioButton viewCust;
    public RadioButton viewWeek;
    public RadioButton viewMonth;
    public RadioButton viewAll;
    public Label deletion;
    private ObservableList<ObservableList> data;

    public void refresh() throws Exception {
        buildTable();
        deletion.setVisible(false);
    }

    public void addCust() throws Exception {
        Stage stage = (Stage) addAppt.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("addCustomer.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1323, 493);
        stage.setScene(scene);
        stage.show();
    }

    public void modCust() throws Exception {
        Stage stage = (Stage) modAppt.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("modCustomer.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1323, 493);
        stage.setScene(scene);
        stage.show();
    }

    public void delCust() throws SQLException {
        ObservableList selection = null;

        try {
            selection = (ObservableList) tester.getSelectionModel().getSelectedItem();

            // Deleting related appointments
            String sql = "DELETE FROM appointments WHERE Customer_ID = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setObject(1, selection.get(0));

            ps.executeUpdate();

            // Deleting customer
            sql = "DELETE FROM customers WHERE Customer_ID = ?";
            PreparedStatement ps2 = JDBC.connection.prepareStatement(sql);
            ps2.setObject(1, selection.get(0));

            ps2.executeUpdate();

            buildTable();

            // Deletion Alert
            deletion.setVisible(true);
        }

        catch (NullPointerException e) {
            System.out.println("Please select a customer to delete.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void logout() throws IOException {
        Stage stage = (Stage) logout.getScene().getWindow();
        //stage.setTitle(rb.getString("scheduler")); //TODO
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 400);
        stage.setScene(scene);
        stage.show();
    }

    public void buildTable() throws SQLException {
        // Clearing all columns as to not scale to infinite columns
        tester.getItems().clear();
        tester.getColumns().clear();

        Connection c;
        data = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM Appointments";
        try {
            if (viewCust.isSelected()) {
                addCust.setVisible(true);
                modCust.setVisible(true);
                delCust.setVisible(true);
                SQL = "SELECT * from CUSTOMERS";
                addAppt.setVisible(false);
                modAppt.setVisible(false);
                delAppt.setVisible(false);
            } else {
                addAppt.setVisible(true);
                modAppt.setVisible(true);
                delAppt.setVisible(true);
                SQL = "SELECT * FROM Appointments";
                addCust.setVisible(false);
                modCust.setVisible(false);
                delCust.setVisible(false);
            }

            //ResultSet
            c = JDBC.openConnection();
            ResultSet rs = c.createStatement().executeQuery(SQL);

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
                //System.out.println("Column [" + i + "] ");
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
                //System.out.println("Row [1] added " + row);
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            tester.setItems(data);

        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
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

