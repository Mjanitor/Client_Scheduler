package Helper;

import com.example.clientscheduler.MainController;
import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Helper Class that holds the various SQL queries needed to populate customer appointments, appointment modifications,
 * client additions, and client modifications.  It also includes several getter methods to grab related information from
 * the database in case it needs to be used in another Class.
 */
public class ClientQuery {
    /**
     * When called, inserts all parameters into the SQL query utilizing bind variables and executes the INSERT.
     * @param ID customer ID
     * @param name customer name
     * @param address customer address
     * @param post customer zip code
     * @param phone customer phone #
     * @param created customer creation date
     * @param created_by the user who created this customer
     * @param updated the last time this customer was updated
     * @param updated_by the last user to update this customer
     * @param divID the division ID associated with this user
     * @throws SQLException
     */
    public static void addCust(int ID, String name, String address, String post, String phone,
                               String created, String created_by, String updated,
                               String updated_by, String divID) throws SQLException {
        String sql = "INSERT INTO customers VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, ID);
        ps.setString(2, name);
        ps.setString(3, address);
        ps.setString(4, post);
        ps.setString(5, phone);
        ps.setString(6, created);
        ps.setString(7, created_by);
        ps.setString(8, updated);
        ps.setString(9, updated_by);
        ps.setString(10, divID);

        ps.executeUpdate();
    }

    /**
     * When called, inserts all parameters into the SQL query utilizing bind variables and executes the UPDATE.
     * @param ID customer ID
     * @param name customer name
     * @param address customer address
     * @param post customer zip code
     * @param phone customer phone #
     * @param created customer creation date
     * @param created_by the user who created this customer
     * @param updated the last time this customer was updated
     * @param updated_by the last user to update this customer
     * @param divID the division ID associated with this user
     * @throws SQLException
     */
    public static void modCust(int ID, String name, String address, String post, String phone,
                                String created, String created_by, String updated,
                                String updated_by, String divID) throws SQLException {
        String sql = "UPDATE customers SET customer_ID = ?, customer_name = ?, Address = ?, Postal_code = ?, Phone = ?, create_date = ?, created_by = ?, last_update = ?, last_updated_by = ?, division_id = ? WHERE customer_id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, ID);
        ps.setString(2, name);
        ps.setString(3, address);
        ps.setString(4, post);
        ps.setString(5, phone);
        ps.setString(6, created);
        ps.setString(7, created_by);
        ps.setString(8, updated);
        ps.setString(9, updated_by);
        ps.setString(10, divID);
        ps.setInt(11, ID);

        ps.executeUpdate();
    }

    /**
     * When called, inserts all parameters into the SQL query utilizing bind variables and executes the INSERT.
     * @param ID Appointment ID
     * @param title Appointment Title
     * @param description Appointment description
     * @param location Appointment location
     * @param type Appointment Type
     * @param start Appointment start datetime
     * @param end Appointment end datetime
     * @param created Time that this appointment was created
     * @param created_by User who created this appointment
     * @param updated Time that this appointment was last updated
     * @param updated_by User who last updated this appointment
     * @param customer Customer ID
     * @param user User ID
     * @param contact Contact ID
     * @throws SQLException
     */
    public static void addAppt(int ID, String title, String description, String location, String type,
                               String start, String end, String created,
                               String created_by, String updated, String updated_by, int customer, String user, int contact) throws SQLException {
        String sql = "INSERT INTO APPOINTMENTS VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setInt(1, ID);
        ps.setString(2, title);
        ps.setString(3, description);
        ps.setString(4, location);
        ps.setString(5, type);
        ps.setString(6, start);
        ps.setString(7, end);
        ps.setString(8, created);
        ps.setString(9, created_by);
        ps.setString(10, updated);
        ps.setString(11, updated_by);
        ps.setInt(12, customer);
        ps.setString(13, user);
        ps.setInt(14, contact);

        ps.executeUpdate();
    }

    /**
     * When called, inserts all parameters into the SQL query utilizing bind variables and executes the UPDATE.
     * @param ID Appointment ID
     * @param title Appointment Title
     * @param desc Appointment description
     * @param loc Appointment location
     * @param type Appointment Type
     * @param start Appointment start datetime
     * @param end Appointment end datetime
     * @param created_by User who created this appointment
     * @param updated Time that this appointment was last updated
     * @param updated_by User who last updated this appointment
     * @param custID Customer ID
     * @param userID User ID
     * @param contactID Contact ID
     * @throws SQLException
     */
    public static void modAppt(int ID, String title, String desc, String loc, String type,
                               String start, String end, String created_by, String updated,
                               String updated_by, int custID, String userID, int contactID) throws SQLException {
        String sql = "UPDATE appointments SET appointment_ID = ?, title = ?, description = ?, location = ?, type = ?," +
                " start = ?, end = ?, last_update = ?, last_updated_by = ?," +
                "customer_id = ?, user_id = ?, contact_id = ?  WHERE appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setInt(1, ID);
        ps.setString(2, title);
        ps.setString(3, desc);
        ps.setString(4, loc);
        ps.setString(5, type);
        ps.setString(6, start);
        ps.setString(7, end);
        ps.setString(8, updated);
        ps.setString(9, updated_by);
        ps.setInt(10, custID);
        ps.setString(11, userID);
        ps.setInt(12, contactID);

        ps.setInt(13, ID);

        ps.executeUpdate();
    }

    /**
     * Helper method which takes in a specific customer ID number, parses and executes the SQL query, and returns the
     * list of items that belong to that specific customer.
     * @param id The ID number to fetch the list of customer column items for.
     * @return A list of items that belong to a specific customer of a certain ID number.
     * @throws SQLException
     */
    public static ArrayList<String> getCustomer(int id) throws SQLException {
        String sql = "SELECT * from customers WHERE CUSTOMER_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, id);

        ArrayList<String> customerItems = new ArrayList<String>();
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            customerItems.add(rs.getString("Customer_ID"));
            customerItems.add(rs.getString("Customer_Name"));
            customerItems.add(rs.getString("Address"));
            customerItems.add(rs.getString("Postal_Code"));
            customerItems.add(rs.getString("Phone"));
            customerItems.add(rs.getString("Create_Date"));
            customerItems.add(rs.getString("Created_By"));
            customerItems.add(rs.getString("Last_Update"));
            customerItems.add(rs.getString("Last_Updated_By"));
            customerItems.add(rs.getString("Division_ID"));
        }

        return customerItems;
    }

    /**
     * Helper method which takes in a specific Appointment ID number, parses and executes the SQL query, and returns the
     * list of items that belong to that specific appointment.
     * @param id The ID number to fetch the list of appointment column items for.
     * @return A list of items that belong to a specific appointment of a certain ID number.
     * @throws SQLException
     */
    public static ArrayList<String> getAppt(int id) throws SQLException {
        String sql = "SELECT * from appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, id);

        ArrayList<String> apptItems = new ArrayList<String>();
        ResultSet rs = ps.executeQuery();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime appt_start = null;
        LocalDateTime appt_end = null;

        if (rs.next()) {
            appt_start = LocalDateTime.parse(rs.getString(6), dtf);
            appt_end = LocalDateTime.parse(rs.getString(7), dtf);
            LocalDate date = appt_start.toLocalDate();
            LocalTime time = appt_start.toLocalTime();

            String start = MainController.convertToLocal(time, date);
            date = appt_end.toLocalDate();
            time = appt_end.toLocalTime();

            String end = MainController.convertToLocal(time, date);

            apptItems.add(rs.getString("Appointment_ID"));
            apptItems.add(rs.getString("Title"));
            apptItems.add(rs.getString("Description"));
            apptItems.add(rs.getString("Location"));
            apptItems.add(rs.getString("Type"));
            apptItems.add(start);
            apptItems.add(end);
            apptItems.add(rs.getString("Create_Date"));
            apptItems.add(rs.getString("Created_By"));
            apptItems.add(rs.getString("Last_Update"));
            apptItems.add(rs.getString("Last_Updated_By"));
            apptItems.add(rs.getString("Customer_ID"));
            apptItems.add(rs.getString("User_ID"));
            apptItems.add(rs.getString("Contact_ID"));
        }

        return apptItems;
    }

    /**
     * Helper method which takes, as a parameter, an appointment ID number.  The method then takes this appointment ID
     * number and runs a query for all appointment start and end times for appointments NOT matching that ID and returns
     * them as a hash map.  This is done to compare any potential overlapping appointment times upon entry.
     * @param ID An appointment ID number to compare against when checking appointments from the database.
     * @return A hash map of the start/end times of all listed appointments as a set of key/value pairs.
     * @throws SQLException
     */
    public static Dictionary<String, String> getApptTimes(Integer ID) throws SQLException {
        String sql = "SELECT Start, End from appointments WHERE Appointment_ID != ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, ID);
        Dictionary<String, String> dict = new Hashtable<>();
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            dict.put(rs.getString("Start"), rs.getString("End"));
        }

        return dict;
    }

    /**
     * Helper method that takes in the attempted username/password and compares them to potential matches in the DB.  If
     * there is a match, this function returns true and false if not.
     * @param user A given username
     * @param pass A given password
     * @return Boolean of whether those credentials matched those in the database.
     * @throws SQLException
     */
    public static boolean login(String user, String pass) throws SQLException {
        ResourceBundle rb;
        Locale locale = Locale.getDefault();

        // Sets the resource bundle to check if the Locale is French, and translate accordingly
        if (locale.equals(Locale.FRANCE)) {
            Locale.setDefault(new Locale("fr", "FR"));
            rb = ResourceBundle.getBundle("com.example.clientscheduler.Nat_fr");
        } else {
            Locale.setDefault(new Locale("en", "US"));
            rb = ResourceBundle.getBundle("com.example.clientscheduler.Nat_en");
        }

        // Grabs the list of all usernames and passwords
        String sql = "SELECT User_Name, Password FROM USERS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        boolean success = false;

        while(rs.next()) {
            String userName = rs.getString("User_Name");
            String password = rs.getString("Password");
            if (user.equals(userName) && pass.equals(password)) {
                success = true;
            }
        }

        // Confirms login if there is a match
        if (success == true){
            Alert successful_login = new Alert(Alert.AlertType.CONFIRMATION);
            successful_login.setTitle(rb.getString("Success!"));
            successful_login.setHeaderText(rb.getString("Success"));
            successful_login.setContentText(rb.getString("successful"));
            successful_login.showAndWait();
        }


        return success;
    }
}
