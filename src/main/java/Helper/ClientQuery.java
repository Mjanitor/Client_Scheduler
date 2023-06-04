package Helper;

import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        if (success){
            Alert successful_login = new Alert(Alert.AlertType.CONFIRMATION);
            successful_login.setTitle(rb.getString("Success!"));
            successful_login.setContentText(rb.getString("successful"));
            successful_login.showAndWait();


        }
        else {
            Alert unsuccessful_login = new Alert(Alert.AlertType.ERROR);
            unsuccessful_login.setTitle(rb.getString("Failed!"));
            unsuccessful_login.setContentText(rb.getString("unsuccessful") );
            unsuccessful_login.showAndWait();
        }

        return success;
    }
}
