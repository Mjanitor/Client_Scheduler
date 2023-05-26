package Helper;

import com.example.clientscheduler.ClientScheduler;
import com.example.clientscheduler.HelloController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;
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
            successful_login.setTitle("Success!");
            successful_login.setContentText("Your login has been successful");
            successful_login.showAndWait();


        }
        else {
            Alert unsuccessful_login = new Alert(Alert.AlertType.ERROR);
            unsuccessful_login.setTitle("Failed!");
            unsuccessful_login.setContentText("Your login has been unsuccessful. Please try again.");
            unsuccessful_login.showAndWait();
        }

        return success;
    }
}
