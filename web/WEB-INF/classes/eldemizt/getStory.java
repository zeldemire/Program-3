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
    /*
    String user = "383-sql";
    String pwd = "test123";
    String dbURL = "jdbc:mysql://localhost/383Story";
     */
    Connection conn = null;
    Statement statement = null;
    ResultSet rs = null;
    String file = "/tmp/servlet2.log";
    Log Log = new Log(file);

    getStory(){}

    public void connect() throws IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL,user,pwd);

        } catch (Exception err) {
            throw new IOException(err);
        }
    }

    protected String getText(int page, int bookID) {
        String content = null;
        try {
            connect();
            Statement stmt = conn.createStatement();
            String sql = "select Content from Book WHERE Book_id="+bookID+" AND Page_num="+page+"";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                content = rs.getString("content");
            }
            rs.close();
            stmt.close();
        } catch (IOException | SQLException e) {
            Log.log("Could not find bookID: " + bookID);
            e.printStackTrace();
        }
        return getPage(content);
    }

    protected String getPage(String page) {
        page = page.replace("<PAGE>", "");
        page = page.replace("</PAGE>", "");
        return page;
    }

    protected int getBookInfo(String book , boolean info) {
        PreparedStatement ps;
        try {
            connect();
            if (info) {
                ps = conn.prepareStatement("SELECT Book_id FROM Book WHERE Title=?");
                ps.setString(1, book);
                rs = ps.executeQuery();

                if(rs.next()) return rs.getInt("Book_id");
                else return -1;
            }
            else {
                ps = conn.prepareStatement("SELECT Book_pages FROM Titles WHERE Book_title=?");
                ps.setString(1, book);
                rs = ps.executeQuery();

                if (rs.next()) return rs.getInt("Book_pages");
                else return -1;
            }
        } catch (IOException | SQLException e) {
            Log.log("Could not find book: " + book);
        }
        return -1;
    }

    protected int getPageNum(int bookID) {

        try {
            connect();
            PreparedStatement ps = conn.prepareStatement("SELECT Book_pages FROM Titles WHERE Book_id=?");
            ps.setInt(1, bookID);
            rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("Book_pages");
            else return -1;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    protected ArrayList<String> getTitle(boolean info) {

        try {
            connect();
            if (info) {
                statement = conn.createStatement();
                rs = statement.executeQuery("SELECT * FROM Titles");
            } else {
                statement = conn.createStatement();
                rs = statement.executeQuery("SELECT Book_id, Book_title, Book_pages FROM Titles");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> titles = new ArrayList<>();

        try {
            if (info) while (rs.next()) titles.add(rs.getString("Book_title"));
            else while (rs.next()) titles.add("Book id: " + rs.getInt("Book_id") + " Title: " + rs.getString("Book_title") + " Page count: " + rs.getInt("Book_pages"));

        } catch (SQLException e) {
            Log.log("No books in database");
            e.printStackTrace();
        }
        return titles;
    }

    protected boolean checkBookID(int bookID) {
        try {
            connect();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Titles WHERE Book_id=?");
            ps.setInt(1, bookID);
            rs = ps.executeQuery();
            if (rs.next()) return true;
            else return false;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
