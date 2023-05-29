package Helper;

import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ClientQuery {

    public static void select() throws SQLException {
        String sql = "SELECT * FROM USERS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter Username");
        String userInput = myObj.nextLine();
        System.out.println("Enter Password");
        String userPw = myObj.nextLine();

        boolean success = false;
        while(rs.next()) {
            int userId = rs.getInt("User_ID");
            String userName = rs.getString("User_Name");
            String password = rs.getString("Password");
            if (userInput.equals(userName) && userPw.equals(password)) {
                success = true;
            }
            System.out.println(userId + "  |  " + userName + "  |  " + password);
        }
        if (success){
            System.out.println("Successful Login!");
        }
        else {
            System.out.println("Failed Login!");
        }
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
