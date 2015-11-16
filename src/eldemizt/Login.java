package eldemizt;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * Created by Zach Eldemire on 11/9/15.
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
    Log Log = new Log(file);

    Login(String password, String username) {
        this.password = password;
        this.username = username;
    }

    public void connect() throws IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL,user,pwd);

        } catch (Exception err) {
            throw new IOException(err);
        }
    }

    protected boolean testPassword(String password) {
        String content = null;
        if (!username.equals("test")) {
            Log.log("Invalid username: " + username);
            return false;
        }
        try {
            connect();
            Statement stmt = conn.createStatement();
            String sql = "select user_name,password from login WHERE user_name='"+username+"'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                content = rs.getString("password");
            }
            rs.close();
            stmt.close();
        } catch (IOException | SQLException e) {
            Log.log("Invalid password for username: " + username);
            e.printStackTrace();
        }
        return password.equals(content);
    }

    protected String generateHash() {
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
}

