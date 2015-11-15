package eldemizt;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Zach Eldemire on 11/10/15.
 */
public class getStory {
    String user = "root";
    String pwd = "Theblood5";
    String dbURL = "jdbc:mysql://localhost:3306/program3";
    Connection conn = null;
    Statement statement = null;
    ResultSet rs = null;
    String storyName;
    String file = "/tmp/servlet2.log";
    Log Log = new Log(file);

    getStory(String storyName) {
        this.storyName = storyName;
    }
    getStory(){}

    public void connect() throws IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL,user,pwd);

        } catch (Exception err) {
            throw new IOException(err);
        }
    }

    protected String getText() {
        String content = null;
        try {
            connect();
            Statement stmt = conn.createStatement();
            String sql = "select content from book WHERE title='"+storyName+"'";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                content = rs.getString("content");
            }
            rs.close();
            stmt.close();
        } catch (IOException | SQLException e) {
            Log.log("Could not find book: " + storyName);
            e.printStackTrace();
        }
        return content;
    }

    protected ArrayList<String> getTitle() {

        try {
            connect();
            statement = conn.createStatement();
            rs = statement.executeQuery("SELECT * FROM book");
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> titles = new ArrayList<>();

        try {
            while (rs.next()) {
                titles.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            Log.log("No books in database");
            e.printStackTrace();
        }
        return titles;
    }
}
