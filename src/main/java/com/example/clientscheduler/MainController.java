package com.example.clientscheduler;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

import Helper.JDBC;
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
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.stage.Stage;
import javafx.util.BuilderFactory;
import javafx.util.Callback;

import java.util.TimeZone;

/**
 * The Controller class that handles all the functionality and events that occur on the main page of the application.
 * This is a very robust class that is mainly used to add/modify/delete customers and appointments but also allows
 * functionality for things ranging from dynamic table production with supplied database results to time conversion
 * and appointment overlap (de)confirmation.
 */
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

    public Button reports;
    public RadioButton viewCust;
    public RadioButton viewWeek;
    public RadioButton viewMonth;
    public RadioButton viewAll;
    public Label deletion;
    public Label selectionError;
    private ObservableList<ObservableList> data;
    private Parent root;

    /**
     * Lambda function of no parameters that simplifies display of the dynamic table.  Class is set in the "Table"
     * interface file.  It checks if any radio buttons are selected and modifies the result set accordingly but then
     * queries the database for all listed results and iterates over loops to build out the table so long as more results
     * exist.  This vastly improves my previous method implementation because I can piece out the function and allow for
     * a more streamlined version of table production.
     */
    Table buildTable = () -> {String local = TimeZone.getDefault().getDisplayName();
        if (local.equals("Eastern Standard Time")) {
            local = "America/New_York";
        }

        // Clearing all columns as to not scale to infinite columns
        if (tester != null) {
            tester.getItems().clear();
            tester.getColumns().clear();
        }

        data = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM Appointments";

        // Filtering by Month or Week
        GregorianCalendar calendar = new GregorianCalendar();
        LocalDate now = LocalDate.now();

        int week = calendar.get(GregorianCalendar.WEEK_OF_MONTH);
        int month = now.getMonthValue();

        try {
            if (viewCust.isSelected()) {
                addCust.setVisible(true);
                modCust.setVisible(true);
                delCust.setVisible(true);
                SQL = "SELECT * from CUSTOMERS";
                addAppt.setVisible(false);
                modAppt.setVisible(false);
                delAppt.setVisible(false);
            } else if (viewAll.isSelected()){
                addAppt.setVisible(true);
                modAppt.setVisible(true);
                delAppt.setVisible(true);
                SQL = "SELECT * FROM Appointments";
                addCust.setVisible(false);
                modCust.setVisible(false);
                delCust.setVisible(false);
            } else if (viewMonth.isSelected()) {
                addAppt.setVisible(true);
                modAppt.setVisible(true);
                delAppt.setVisible(true);
                SQL = "SELECT * FROM Appointments";
                addCust.setVisible(false);
                modCust.setVisible(false);
                delCust.setVisible(false);
                switch (month) {
                    case 1: SQL = "SELECT * FROM appointments WHERE MONTH(START) = 1";
                        break;
                    case 2: SQL = "SELECT * FROM appointments WHERE MONTH(START) = 2";
                        break;
                    case 3: SQL = "SELECT * FROM appointments WHERE MONTH(START) = 3";
                        break;
                    case 4: SQL = "SELECT * FROM appointments WHERE MONTH(START) = 4";
                        break;
                    case 5: SQL = "SELECT * FROM appointments WHERE MONTH(START) = 5";
                        break;
                    case 6: SQL = "SELECT * FROM appointments WHERE MONTH(START) = 6";
                        break;
                    case 7: SQL = "SELECT * FROM appointments WHERE MONTH(START) = 7";
                        break;
                    case 8: SQL = "SELECT * FROM appointments WHERE MONTH(START) = 8";
                        break;
                    case 9: SQL = "SELECT * FROM appointments WHERE MONTH(START) = 9";
                        break;
                    case 10: SQL = "SELECT * FROM appointments WHERE MONTH(START) = 10";
                        break;
                    case 11: SQL = "SELECT * FROM appointments WHERE MONTH(START) = 11";
                        break;
                    case 12: SQL = "SELECT * FROM appointments WHERE MONTH(START) = 12";
                        break;
                }
            } else if (viewWeek.isSelected()) {
                addAppt.setVisible(true);
                modAppt.setVisible(true);
                delAppt.setVisible(true);
                SQL = "SELECT * FROM Appointments";
                addCust.setVisible(false);
                modCust.setVisible(false);
                delCust.setVisible(false);
                switch (week) {
                    case 1: SQL = "SELECT * FROM appointments WHERE WEEK(Start, 5) - WEEK(DATE_SUB(Start, INTERVAL DAYOFMONTH(Start) - 1 DAY), 5) + 1 = 1";
                        break;
                    case 2: SQL = "SELECT * FROM appointments WHERE WEEK(Start, 5) - WEEK(DATE_SUB(Start, INTERVAL DAYOFMONTH(Start) - 1 DAY), 5) + 1 = 2";
                        break;
                    case 3: SQL = "SELECT * FROM appointments WHERE WEEK(Start, 5) - WEEK(DATE_SUB(Start, INTERVAL DAYOFMONTH(Start) - 1 DAY), 5) + 1 = 3";
                        break;
                    case 4: SQL = "SELECT * FROM appointments WHERE WEEK(Start, 5) - WEEK(DATE_SUB(Start, INTERVAL DAYOFMONTH(Start) - 1 DAY), 5) + 1 = 4";
                        break;
                    case 5: SQL = "SELECT * FROM appointments WHERE WEEK(Start, 5) - WEEK(DATE_SUB(Start, INTERVAL DAYOFMONTH(Start) - 1 DAY), 5) + 1 = 5";
                        break;
                }
            }

            PreparedStatement ps = JDBC.connection.prepareStatement(SQL);
            System.out.println(SQL);
            ResultSet rs = ps.executeQuery(SQL);

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
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {

                        // Converting Time Zone
                        if ((finalI == 5) || (finalI == 6 && finalSQL == "SELECT * FROM Appointments") || (finalI == 7) || (finalI == 9 && finalSQL == "SELECT * FROM Appointments")) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime date_time = LocalDateTime.parse((CharSequence) param.getValue().get(finalI), dtf);

                            LocalDate date = date_time.toLocalDate();
                            LocalTime local_time = date_time.toLocalTime();

                            String converted_time = convertToLocal(local_time, date);

                            return new SimpleStringProperty(converted_time);
                        }
                        return new SimpleStringProperty(param.getValue().get(j).toString());

                    }
                });

                tester.getColumns().addAll(col);
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
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            tester.setItems(data);

        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }};

    /**
     * Lambda function that enables quick production of an error message for various reasons, such as no selection,
     * overlaps, and others.  I found that the lambda implementation was much more efficient as instead of creating a
     * new error ALERT for each necessary time, I can simply call this lambda.
     */
    UTC errorHandler = (String title, String content) -> {Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    };

    /**
     * A simple method that is attached to the radio buttons that builds the results table dynamically and hide any
     * previous displayed interface messages.
     * @throws Exception
     */
    public void refresh() throws Exception {
        buildTable.dynamicTable();
        deletion.setVisible(false);
        selectionError.setVisible(false);
    }

    /**
     * Method to simply close the main window and open the "Reports" screen.
     * @throws IOException
     */
    public void onReports() throws IOException {
        Stage stage = (Stage) reports.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("reports.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1323, 493);
        stage.setScene(scene);
        stage.setTitle("Report Viewer");
        stage.show();
    }

    /**
     * Method to simply close the main window and open the "Add Customer" screen.
     * @throws Exception
     */
    public void addCust() throws Exception {
        Stage stage = (Stage) addAppt.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("addCustomer.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1323, 493);
        stage.setScene(scene);
        stage.setTitle("Add Customer");
        stage.show();
    }

    /**
     * Method to simply close the main window and open the "Modify Customer" screen.  Also has a conditional to detect
     * if the customer selection was null, display an error alert, then return to the main window.
     * @throws Exception
     */
    public void modCust() throws Exception {
        ObservableList selection = (ObservableList) tester.getSelectionModel().getSelectedItem();

        // Catching no selection
        if (selection == null) {
            errorHandler.showError("Failed!", "Please select a customer.");

            return;
        }

        try {
            Stage stage = (Stage) modAppt.getScene().getWindow();
            stage.close();

            FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("modCustomer.fxml"));
            root = fxmlLoader.load();

            modCustomerController modCustomerController = fxmlLoader.getController();
            modCustomerController.setCustomer_ID(selection.get(0));

            Scene scene = new Scene(root, 1323, 493);
            stage.setScene(scene);
            stage.setTitle("Modify Customer");
            stage.show();
        } catch (NullPointerException e) {
            errorHandler.showError("Failed!", "Please select a customer.");
        }
    }

    /**
     * Method to query the selected customer agaist the database and remove them from the database.  After deletion, the
     * dynamic table is built again and a "successful deletion" message is shown.
     * @throws SQLException
     */
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

            buildTable.dynamicTable();

            // Deletion Alert
            deletion.setVisible(true);
        }

        catch (NullPointerException e) {
            errorHandler.showError("Failed!", "Please select a customer.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to simply close the main window and open the "Add Appointment" screen.
     * @throws Exception
     */
    public void addAppt() throws Exception {
        Stage stage = (Stage) addAppt.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("addAppointment.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 952, 673);
        stage.setScene(scene);
        stage.setTitle("Add Appointment");
        stage.show();
    }

    /**
     * Method to simply close the main window and open the "Modify Appointment" screen.  Also has a conditional to
     * detect if the appointment selection was null, display an error alert, then return to the main window.
     * @throws Exception
     */
    public void modAppt() throws Exception {
        ObservableList selection = (ObservableList) tester.getSelectionModel().getSelectedItem();

        // Checking no selection
        if (selection == null) {
            errorHandler.showError("Failed!", "Please select an appointment.");

            return;
        }
        try {
            Stage stage = (Stage) modAppt.getScene().getWindow();
            stage.close();

            FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("modAppt.fxml"));
            root = fxmlLoader.load();

            modApptController modApptController = fxmlLoader.getController();
            modApptController.setApptID(selection.get(0));

            Scene scene = new Scene(root, 952, 673);
            stage.setScene(scene);
            stage.setTitle("Modify Appointment");
            stage.show();
        } catch (NullPointerException e) {
            errorHandler.showError("Failed!", "Please select an appointment.");
        }
    }

    /**
     * Method to query the selected appointment agaist the database and remove them from the database.  After deletion,
     * the dynamic table is built again and a "successful deletion" message is shown.
     * @throws SQLException
     */
    public void delAppt() throws SQLException {
        ObservableList selection = null;

        try {
            selection = (ObservableList) tester.getSelectionModel().getSelectedItem();
            String appt_id = (String) selection.get(0);
            String appt_type = (String) selection.get(4);
            System.out.println("ID: " + appt_id + " Type: " + appt_type);

            // Deleting related appointments
            String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, Integer.valueOf((String) selection.get(0)));

            ps.executeUpdate();

            buildTable.dynamicTable();

            // Deletion Alert
            deletion.setText("Appointment #" + appt_id + " Type: '" + appt_type + "' - Successfully deleted!");
            deletion.setVisible(true);
        }

        catch (NullPointerException e) {
            errorHandler.showError("Failed!", "Please select an appointment.");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to simply close the main window, log the user out of the system, and re-open the Login page.
     * @throws IOException
     */
    public void logout() throws IOException {
        // Accounting for language
        Locale locale = Locale.getDefault();
        System.out.println("Main French: " + LoginController.french);

        if (locale.equals(Locale.FRANCE)) {
            Locale.setDefault(new Locale("fr", "FR"));
        } else {
            Locale.setDefault(new Locale("en", "US"));
        }

        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setTitle("Client Scheduler");
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(ClientScheduler.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 400);
        stage.setScene(scene);
        stage.setTitle("Client Scheduler");
        stage.show();
    }

    /**
     * Helper method that takes in a military-formatted time and a date and converts them to UTC, typically for upload
     * into the local database.
     * @param input_time The given time parameter, as a String.
     * @param date The given date parameter, as a LocalDate.
     * @return A combined datetime in UTC, as a String.
     */
    public static String convertToUTC(String input_time, LocalDate date) {
        LocalTime time = LocalTime.parse(input_time);

        LocalDateTime combined = LocalDateTime.of(date, time);

        ZonedDateTime combinedZoned = combined.atZone(ZoneId.systemDefault());
        ZonedDateTime finalCombined = combinedZoned.withZoneSameInstant(ZoneId.of("UTC"));

        String combined_as_string = finalCombined.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00"));

        return combined_as_string;
    }

    /**
     * Helper method that takes in a military-formatted time and a date and converts them to EST, typically for display
     * in the dynamic table or to check if appointments are within working office hours.
     * @param input_time The given time parameter, as a String.
     * @param date The given date parameter, as a LocalDate.
     * @return A combined datetime in EST, as a String.
     */
     public static String convertToEST(String input_time, LocalDate date) {
        LocalTime time = LocalTime.parse(input_time);

        LocalDateTime combined = LocalDateTime.of(date, time);

        ZonedDateTime local_time = combined.atZone(ZoneId.systemDefault());
        ZonedDateTime est_time = local_time.withZoneSameInstant(ZoneId.of("America/New_York"));

        String combined_as_string = est_time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00"));

        return combined_as_string;
    }

    /**
     * Helper method that takes in a military-formatted time and a date and converts them to the user's local time,
     * typically for display in the dynamic table.
     * @param input_time The given time parameter, as a LocalTime.
     * @param date The given date parameter, as a LocalDate.
     * @return A combined datetime in user's local time, as a String.
     */
     public static String convertToLocal(LocalTime input_time, LocalDate date) {
        LocalDateTime combined = LocalDateTime.of(date, input_time);

        // Default Time
        ZonedDateTime combinedZoned = combined.atZone(ZoneId.of("UTC"));
        ZonedDateTime finalCombined = combinedZoned.withZoneSameInstant(ZoneId.systemDefault());

        String combined_as_string = finalCombined.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00"));

        return combined_as_string;
    }

    /**
     * Helper method that takes in the start and end datetimes of two separate entries and has a conditional to first
     * check if the dates are on the same day and return false if not or else it will compare the times to see if those
     * overlap and return true if so.
     * @param start1 The start of the first supplied appointment date.
     * @param end1 The end of the first supplied appointment date.
     * @param start2 The start of the second supplied appointment date.
     * @param end2 The end of the second supplied appointment date.
     * @return Boolean of whether the two appointment dates overlap.
     */
    public static boolean isOverlapping(java.util.Date start1, java.util.Date end1, java.util.Date start2, java.util.Date end2) {
        if (start1.getDate() != start2.getDate()) {
            return false;
        } else {
            return (start1.before(end2) && start2.before(end1));
        }
    }

    /**
     * This method takes in a starting appointment time and compares it to the corresponding ending appointment time to
     * verify whether the starting time is before the ending time for input validation purposes.
     * @param start The start datetime of the supplied appointment date.
     * @param end The end datetime of the supplied appointment date.
     * @return A boolean of whether the starting appointment time is before the ending appointment time.
     */
    public static boolean startBeforeEnd(java.util.Date start, java.util.Date end) {
        if (start.getTime() > end.getTime()) {
            return false;
        }
        return true;
    }

    /**
     * Upon initialization, a dynamic table is built with the default selection of displaying all appointments.
     * @param url Standard Parameter.
     * @param rb Standard Parameter.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Returning to English
        Locale.setDefault(new Locale("en", "US"));

        buildTable.dynamicTable();
    }
}

