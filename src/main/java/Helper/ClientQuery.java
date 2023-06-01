package Helper;

import com.example.clientscheduler.MainController;
import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ClientQuery {

    public static String select() throws SQLException {
        String sql = "SELECT * from APPOINTMENTS WHERE Type LIKE 'PLANNING SESSION'";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        String title = null;
        while (rs.next()) {
            title = rs.getString("Title");
            System.out.println(title);
        }

        return title;
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
