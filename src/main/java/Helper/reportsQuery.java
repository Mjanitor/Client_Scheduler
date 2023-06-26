package Helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * This class contains several functionalities specifically for the Reports page.  It handles the population of the
 * various ComboBoxes and utilizes certain Helper methods to filter out the dynamic table based upon which report is
 * requested to be run.
 */
public class reportsQuery {
    /**
     * Helper method that takes in a SQL query and a column name and returns a list of items matching those criterion
     * for the purpose of populating whichever ComboBox is needed.
     * @param sql The SQL query to be run against the database.
     * @param column The requested column name whose results need to be captured.
     * @return A list of all matching results based upon the query and column name that are passed in.
     * @throws SQLException
     */
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

    /**
     * Helped method that takes in a specific Contact ID and runs a query to return a result set of all appointments
     * matching that Contact ID.  Has a conditional to handle if the ID was null and just returns all appointments.
     * @param ID The Contact ID to be queried.
     * @return The result set of all appointments matching the supplied Contact ID.
     * @throws SQLException
     */
    public static ResultSet sortByContactID(String ID) throws SQLException {
        String SQL = null;
        PreparedStatement ps;

        if (ID == null) {
            SQL = "SELECT * FROM appointments";
            ps = JDBC.connection.prepareStatement(SQL);
        } else {
            SQL = "SELECT * FROM appointments WHERE Contact_ID = ?";
            ps = JDBC.connection.prepareStatement(SQL);
            ps.setString(1, ID);
        }

        ResultSet rs = ps.executeQuery();

        return rs;
    }

    /**
     * Helped method that takes in a specific Appointment Type and runs a query to return a result set of all
     * appointments matching that Appointment Type.  Has a conditional to handle if the Appointment Type parameter was
     * null and just returns all appointments if so.
     * @param type The Appointment Type to be queried.
     * @return The result set of all appointments matching the supplied Appointment Type.
     * @throws SQLException
     */
    public static ResultSet sortByType(String type) throws SQLException {
        String SQL = null;
        PreparedStatement ps;

        if (type == null) {
            SQL = "SELECT * FROM appointments";
            ps = JDBC.connection.prepareStatement(SQL);
        } else {
            SQL = "SELECT * FROM appointments WHERE Type LIKE ?";
            ps = JDBC.connection.prepareStatement(SQL);
            ps.setString(1, type);
        }

        ResultSet rs = ps.executeQuery();

        return rs;
    }

    /**
     * Helped method that takes in a specific month as a number and runs a query to return a result set of all
     * appointments matching the start date of that Month Integer.  Has a conditional to handle if the Month Integer
     * parameter was null and just returns all appointments if so.
     * @param month The Appointment Month Start Integer to be queried.
     * @return The result set of all appointments matching the supplied Appointment Start Month.
     * @throws SQLException
     */
    public static ResultSet sortByMonth(int month) throws SQLException {
        String SQL = null;
        PreparedStatement ps;

        // "Null" catch
        if (month == 0) {
            SQL = "SELECT * FROM Appointments";
            ps = JDBC.connection.prepareStatement(SQL);
        } else {
            SQL = "SELECT * FROM appointments WHERE MONTH(Start) = ?";
            ps = JDBC.connection.prepareStatement(SQL);
            ps.setInt(1, month);
        }

        ResultSet rs = ps.executeQuery();

        return rs;
    }

    /**
     * Helped method that takes in a specific country String and queries the customer database for all customers of a
     * certain division ID.  The conditional is based upon a range of division IDs because all divisions of the same
     * country are contained withing a certain bound in the database. Has a conditional to handle if the country
     * parameter was null and just returns all customers if so.
     * @param country The Country String parameter to be queried.
     * @return The result set of all Customers that live in the selected country.
     * @throws SQLException
     */
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

        PreparedStatement ps = JDBC.connection.prepareStatement(SQL);
        ResultSet rs = ps.executeQuery();

        return rs;
    }
}
