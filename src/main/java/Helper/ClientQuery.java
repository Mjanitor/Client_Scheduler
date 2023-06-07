package Helper;

import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClientQuery {

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

    public static void modAppt(int ID, String title, String desc, String loc, String type,
                               String start, String end, String create_date, String created_by, String updated,
                               String updated_by, String custID, String userID, int contactID) throws SQLException {
        String sql = "UPDATE appointments SET appointment_ID = ?, title = ?, description = ?, location = ?, type = ?," +
                " start = ?, end = ?, create_date = ?, created_by = ?, last_update = ?, last_updated_by = ?," +
                "customer_id = ?, user_id = ?, contact_id = ?  WHERE appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setInt(1, ID);
        ps.setString(2, title);
        ps.setString(3, desc);
        ps.setString(4, loc);
        ps.setString(5, type);
        ps.setString(6, start);
        ps.setString(7, end);
        ps.setString(8, create_date);
        ps.setString(9, created_by);
        ps.setString(10, updated);
        ps.setString(11, updated_by);
        ps.setString(12, custID);
        ps.setString(13, userID);
        ps.setInt(14, contactID);
        ps.setInt(15, ID);

        System.out.println("ID: " + ID);
        System.out.println("Start: " + start);

        ps.executeUpdate();
    }

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

    public static ArrayList<String> getAppt(int id) throws SQLException {
        String sql = "SELECT * from appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, id);

        ArrayList<String> apptItems = new ArrayList<String>();
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            apptItems.add(rs.getString("Appointment_ID"));
            apptItems.add(rs.getString("Title"));
            apptItems.add(rs.getString("Description"));
            apptItems.add(rs.getString("Location"));
            apptItems.add(rs.getString("Type"));
            apptItems.add(rs.getString("Start"));
            apptItems.add(rs.getString("End"));
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

    public static void addAppt(int ID, String title, String description, String location, String type,
                               String start, String end, String created,
                               String created_by, String updated, String updated_by, String customer, String user, int contact) throws SQLException {
        String sql = "INSERT INTO APPOINTMENTS VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setInt(1, ID);
        ps.setString(2, title);
        ps.setString(3, description);
        ps.setString(4, location);
        ps.setString(5, type);
        ps.setString(6, start.toString());
        ps.setString(7, end.toString());
        ps.setString(8, created);
        ps.setString(9, created_by);
        ps.setString(10, updated);
        ps.setString(11, updated_by);
        ps.setString(12, customer);
        ps.setString(13, user);
        ps.setInt(14, contact);

        ps.executeUpdate();
    }

    public static void update() throws SQLException {
        String sql = "UPDATE APPOINTMENTS SET TITLE = ? WHERE Type = 'Planning Session'";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setString(1, "test");

        ps.executeUpdate();
    }

    public static boolean login(String user, String pass) throws SQLException {
        ResourceBundle rb;

        Locale locale = Locale.getDefault();
        System.out.println(locale.getClass());

        if (locale.equals(Locale.FRANCE)) {
            Locale.setDefault(new Locale("fr", "FR"));
            rb = ResourceBundle.getBundle("com.example.clientscheduler.Nat_fr");
        } else {
            Locale.setDefault(new Locale("en", "US"));
            rb = ResourceBundle.getBundle("com.example.clientscheduler.Nat_en");
        }

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
        System.out.println(user + "  |  " + pass);
        if (success == true){
            Alert successful_login = new Alert(Alert.AlertType.CONFIRMATION);
            successful_login.setTitle(rb.getString("Success!"));
            successful_login.setContentText(rb.getString("successful"));
            successful_login.showAndWait();
        }
        else if (success == false){
            Alert unsuccessful_login = new Alert(Alert.AlertType.ERROR);
            unsuccessful_login.setTitle(rb.getString("Failed!"));
            unsuccessful_login.setContentText(rb.getString("unsuccessful") );
            unsuccessful_login.showAndWait();
        }

        return success;
    }
}
