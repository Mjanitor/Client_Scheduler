package Helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
