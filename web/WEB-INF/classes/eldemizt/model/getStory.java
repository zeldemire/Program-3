package eldemizt.model;

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
    eldemizt.model.Log Log = new Log(file);

    public getStory(){}

    public void connect() throws IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL,user,pwd);

        } catch (Exception err) {
            throw new IOException(err);
        }
    }

    public String getText(int page, int bookID) {
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

    public String getPage(String page) {
        page = page.replace("<PAGE>", "");
        page = page.replace("</PAGE>", "");
        return page;
    }

    public int getBookInfo(String book , boolean info) {
        PreparedStatement ps;
        try {
            connect();
            if (info) {
                ps = conn.prepareStatement("SELECT Book_id FROM Titles WHERE Book_title=?");
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

    public int getPageNum(int bookID) {

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

    public ArrayList<String> getTitle(boolean info) {

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

    public void editStory(int bookID, String content, int page) {
        try {
            connect();
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE Book SET Content=? WHERE Book_id=? AND Page_num=?");
            preparedStatement.setString(1, content);
            preparedStatement.setInt(2, bookID);
            preparedStatement.setInt(3, page);
            preparedStatement.executeUpdate();
            Log.log("BookID " + bookID + " has been updated on page: " +page);
        } catch (IOException | SQLException e) {
            Log.log("Failed to edit story.");
        }
    }

    public void addPage(int bookID, String content, int page) {
        try {
            connect();
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE Titles SET Book_pages=Book_pages+1 WHERE Book_id=?");
            preparedStatement.setInt(1, bookID);
            preparedStatement.executeUpdate();
            preparedStatement = conn.prepareStatement("INSERT INTO Book (Book_id, Page_num, Content) VALUES (?,?,?);");
            preparedStatement.setInt(1, bookID);
            preparedStatement.setInt(2, page);
            preparedStatement.setString(3, content);
            preparedStatement.executeUpdate();
            editStory(bookID,content,page);
            Log.log("Added page to: " + bookID);
        } catch (IOException | SQLException e) {
            Log.log("Failed to add page.");
        }
    }

    public void addTitle(String title) {
        try {
            connect();
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO Titles (Book_title) VALUES (?);");
            preparedStatement.setString(1, title);
            preparedStatement.executeUpdate();
            Log.log("Added book with title: " + title);
        } catch (IOException | SQLException e) {
            Log.log("Failed to add title.");
        }
    }

    public int getBookID(String title) {
        try {
            connect();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT Book_id FROM Titles WHERE Book_title=?;");
            preparedStatement.setString(1, title);
            rs = preparedStatement.executeQuery();
            if (rs.next()) return rs.getInt("Book_id");
            else return -1;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateTitle(String title, int bookID) {
        try {
            connect();
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE Titles SET Book_title=? WHERE Book_id=?");
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, bookID);
            preparedStatement.executeUpdate();
            Log.log("Updated story with id: " + bookID + "changed title to: " + title);
        } catch (IOException | SQLException e) {
            Log.log("Failed to update story.");
        }
    }
    public boolean checkBookID(int bookID) {
        try {
            connect();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Titles WHERE Book_id=?");
            ps.setInt(1, bookID);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteStory(int bookID) {
        try {
            connect();
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM Book WHERE Book_id=?");
            preparedStatement.setInt(1,bookID);
            preparedStatement.execute();
            preparedStatement = conn.prepareStatement("DELETE FROM Titles WHERE Book_id=?");
            preparedStatement.setInt(1, bookID);
            preparedStatement.execute();
            Log.log("Deleted story with id: " + bookID);
        } catch (IOException | SQLException e) {
            Log.log("Failed to delete story.");
        }

    }
}
