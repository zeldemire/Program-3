package eldemizt.model;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * Created by Zach Eldemire on 11/9/15.
 * This class handles the login testing for the website and REST.
 */
public class Login extends HttpServlet {
    String user = "root";
    String pwd = "Theblood5";
    String dbURL = "jdbc:mysql://localhost:3306/program3";
    /*
    String user = "383-sql";
    String pwd = "test123";
    String dbURL = "jdbc:mysql://localhost/383Story";
     */
    Connection conn = null;
    String password, username;
    String file = "/tmp/servlet2.log";
    eldemizt.model.Log Log = new Log(file);

    public Login(String password, String username) {
        this.password = password;
        this.username = username;
    }

    /**
     * Used to get connection to database.
     * @throws IOException
     */
    public void connect() throws IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL,user,pwd);

        } catch (Exception err) {
            throw new IOException(err);
        }
    }

    /**
     * Tests to see if the given password matches the one in the database.
     * @return true if passwords match, false if they don't.
     */
    public boolean testPassword() {
        String content = null;
        try {
            connect();
            Statement stmt = conn.createStatement();
            String sql = "SELECT user_name,password FROM login WHERE user_name='" + username + "'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                content = rs.getString("password");
            }
            rs.close();
            stmt.close();
        } catch (IOException | SQLException e) {
            Log.log("Invalid password for username: " + username);
            e.printStackTrace();
        }
        return content != null && content.equals(generateHash());
    }

    /**
     * Creates a hash of the password.
     * @return the hashed password.
     */
    public String generateHash() {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(password.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    /**
     * Creates an API key to be used with REST and the website.
     * @return API key
     */
    public String generateAPIKey() {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(password.getBytes());
            md.update(username.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    /**
     * Tests to see if given username and password are an administrators.
     * @return true if it is an admin false if not
     */
    public boolean isAdmin() {
        try {
            connect();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM login WHERE user_name='admin' AND password=?;");
            ps.setString(1,generateHash());
            ResultSet rs = ps.executeQuery();

            return rs.next();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        Login login = new Login("test", "test");
        System.out.println(login.testPassword());
    }
}

