package eldemizt;

/**
 * Created by Zach Eldemire on 11/14/15.
 * Program 3
 * CSE 383
 */
import java.io.IOException;
import java.sql.*;
import org.json.*;

public class APIKeys {
    String user = "root";
    String pwd = "Theblood5";
    String dbURL = "jdbc:mysql://localhost:3306/program3";
    /*
    String user = "383-sql";
    String pwd = "test123";
    String dbURL = "jdbc:mysql://localhost/383Story";
     */
    Connection conn = null;

    public void connect() throws IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL,user,pwd);

        } catch (Exception err) {
            throw new IOException(err);
        }
    }

    public String JSONGetAPIKey(String user, String password) {
        String key = null;

        Login client = new Login(user, password);
        if (!client.testPassword(password))
            key = getAPIKey(user, password);

        JSONObject json = new JSONObject();
        if (key  == null) {
            json.put("key","ERROR - invalid user");
            return json.toString();
        }

        json.put("APIkey",key);
        return json.toString();
    }

    public String getAPIKey(String username, String password) {
        try {
            connect();
            String key = new Login(username, password).generateHash();
            if (keyCheck(username, key)) {
                PreparedStatement stmt = conn.prepareStatement("insert into API_keys (`APIkey`,`user_name`) values (?,?)");
                stmt.setString(1, key);
                stmt.setString(2, username);
                stmt.executeUpdate();
                return key;
            } else return key;
        } catch (Exception err) {
            System.err.println("Error getting api key " + err.toString());
            err.printStackTrace();
            return null;
        }
    }

    private boolean keyCheck(String user, String key) {
        try {
            connect();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM API_keys WHERE user_name=? AND APIkey=?");
            stmt.setString(1,user);
            stmt.setString(2,key);
            ResultSet rs = stmt.executeQuery();
            return !rs.next();
        } catch (Exception err) {
            System.err.println("Error getting api key " + err.toString());
            err.printStackTrace();
            return false;
        }
    }

    public boolean keyCheckNoUser(String key) {
        try {
            connect();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM API_keys WHERE user_name=?");
            stmt.setString(1,key);
            ResultSet rs = stmt.executeQuery();
            return !rs.next();
        } catch (Exception err) {
            System.err.println("Error getting api key " + err.toString());
            err.printStackTrace();
            return false;
        }
    }
}
