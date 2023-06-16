package Helper;

import javafx.collections.FXCollections;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class reportsQuery {
    public static ArrayList<String> getComboBoxList(String sql, String column) throws SQLException {
        // Populating Type
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        String arr[] = {};

        ArrayList<String> results = new ArrayList<String>(Arrays.asList(arr));

        while(rs.next()) {
            results.add(rs.getString(column));
        }
        results.sort(null);

        return results;
    }

    public static ResultSet sortByContactID(String column) throws SQLException {
        String SQL = null;
        PreparedStatement ps;

        if (column == null) {
            SQL = "SELECT * FROM appointments";
            ps = JDBC.connection.prepareStatement(SQL);
        } else {
            SQL = "SELECT * FROM appointments WHERE Contact_ID = ?";
            ps = JDBC.connection.prepareStatement(SQL);
            ps.setString(1, column);
        }

        ResultSet rs = ps.executeQuery();

        return rs;
    }

    public static ResultSet sortByType(String column) throws SQLException {
        String SQL = null;
        PreparedStatement ps;

        if (column == null) {
            SQL = "SELECT * FROM appointments";
            ps = JDBC.connection.prepareStatement(SQL);
        } else {
            SQL = "SELECT * FROM appointments WHERE Type LIKE ?";
            ps = JDBC.connection.prepareStatement(SQL);
            ps.setString(1, column);
        }

        ResultSet rs = ps.executeQuery();

        return rs;
    }

    public static ResultSet sortByMonth(int month) throws SQLException {
        String SQL = null;
        PreparedStatement ps;

        if (month == 0) {
            SQL = "SELECT * FROM customers";
            ps = JDBC.connection.prepareStatement(SQL);
        } else {
            SQL = "SELECT * FROM appointments WHERE MONTH(Start) = ?";
            ps = JDBC.connection.prepareStatement(SQL);
            ps.setInt(1, month);
        }

        ResultSet rs = ps.executeQuery();

        return rs;
    }

    public static ResultSet sortByCountry(String country) throws SQLException {
        String SQL = null;

        if (country == null) {
            SQL = "SELECT * FROM customers";
        }

        if (Objects.equals(country, "U.S")) {
            SQL = "SELECT * FROM customers WHERE Division_ID BETWEEN 1 AND 54";
        } else if (Objects.equals(country, "UK")) {
            SQL = "SELECT * FROM customers WHERE Division_ID BETWEEN 60 AND 72";
        } else if (Objects.equals(country, "Canada")) {
            SQL = "SELECT * FROM customers WHERE Division_ID BETWEEN 101 AND 104";
        }

        System.out.println(SQL);
        PreparedStatement ps = JDBC.connection.prepareStatement(SQL);
        ResultSet rs = ps.executeQuery();

        return rs;
    }
}
