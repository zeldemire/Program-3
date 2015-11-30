package eldemizt.model;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Zach Eldemire on 11/10/15.
 * Ths class handles all of the interactions with the database.
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

    /**
     * This function gets the connection to the database.
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
     * This function will return the page information from the database with the given bookID.
     * @param page used to find what page to return.
     * @param bookID used to find what book to get the page from.
     * @return returns the page from the database without the <PAGE></PAGE>
     */
    public String getText(int page, int bookID) {
        String content = null;
        try {
            connect();
            Statement stmt = conn.createStatement();
            String sql = "SELECT Content FROM Book WHERE Book_id="+bookID+" AND Page_num="+page+"";
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

    /**
     * Helper function for the getText method.
     * @param page page contents from database
     * @return page without the <PAGE></PAGE> tags.
     */
    public String getPage(String page) {
        page = page.replace("<PAGE>", "");
        page = page.replace("</PAGE>", "");
        return page;
    }

    /**
     * Used to get either the bookID or the number of pages in the book.
     * @param book title of the book that the information is needed from.
     * @param info true if the bookID is needed, false if the number of pages is needed.
     * @return either the bookID or the number of pages, depending on the info variable.
     */
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

    /**
     * Used to get the number of pages in the book if only the bookID is known.
     * @param bookID used to identify what book to search for.
     * @return number of pages.
     */
    public int getPageNum(int bookID) {

        try {
            connect();
            PreparedStatement ps = conn.prepareStatement("SELECT Book_pages FROM Titles WHERE Book_id=?");
            ps.setInt(1, bookID);
            rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("Book_pages");
            else return -1;
        } catch (IOException | SQLException e) {
            Log.log(bookID + " is not in the database");
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Gets the titles of all of the books in the database. Or to get all of the information from the Titles table.
     * (Book_id, Book_title, Book_pages)
     * @param info used to determine if just the title is going to be returned or all of the info.
     * @return either the title or all of the info from the Titles database.
     */
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

    /**
     * Used to edit the contents of a story.
     * @param bookID the story to edit.
     * @param content the contents of the page.
     * @param page the page number to edit.
     */
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

    /**
     * Adds a page to the book corresponding to the bookID. And adds the contents of the new page to the book.
     * @param bookID the story to edit.
     * @param content page to insert into book.
     * @param page page number wher the contents should go.
     */
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

    /**
     * Used to add a title to the Titles table.
     * @param title title to be added.
     */
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

    /**
     * Used to get the bookID to the given title.
     * @param title title to look for int the Titles table.
     * @return the bookID for that title
     */
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

    /**
     * Used to update the title of a book.
     * @param title new title of the book.
     * @param bookID used to identify which book to update.
     */
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

    /**
     * Used to see if the given bookID is present in the database.
     * @param bookID bookID to search for in the database.
     * @return true if bookID is present, flase if not
     */
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

    /**
     * Used to delete the story from the database. This function will delete it frmo both the Book table and the Titles
     * table
     * @param bookID bookID used to find what book to delete.
     */
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
