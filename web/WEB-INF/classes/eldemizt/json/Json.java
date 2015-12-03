package eldemizt.json;

import eldemizt.model.APIKeys;
import eldemizt.model.Log;
import eldemizt.model.getStory;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Zach Zach Eldemire on 11/12/15.
 * Program 3
 * CSE 383
 * This class handles all of the json.
 */
public class Json extends HttpServlet{
    String restLog = "/tmp/rest.log";
    Log log = new Log(restLog);

    /**
     * Checks the URL to see which function to pass the request to. This function handles getkey, storyList, storyTitles,
     * getStoryID, story, and getPageNum.
     * @param request used to get the parameters.
     * @param response used to communicate with client.
     * @param URL URL to check and see what function to be passed to.
     * @throws ServletException
     * @throws IOException
     */
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, String[] URL) throws ServletException, IOException {
        if (URL.length <= 3 || URL.length > 7) {
            error(response, "No API selected");
            log.log("Incorrect URL from: " + request.getRemoteAddr());
        }
        else if (URL[4].equals("getkey") && URL.length == 7) getAPIKey(request,response,URL);
        else if (URL[4].equals("storyList")) getStoryList(request,response, URL[3],false);
        else if (URL[4].equals("storyTitles")) getStoryList(request,response, URL[3],true);
        else if (URL[4].equals("getStoryID")) getStoryID(request,response,fixUrl(URL[5]),URL[3]);
        else if (URL[4].equals("story")) getStory(request,response, URL[5], URL[3], URL[6]);
        else if (URL[4].equals("getPageNum")) getPageNum( response, Integer.parseInt(URL[5]));
    }

    /**
     * Returns the number of pages in the given book.
     * @param response used to communicate with client.
     * @param book book to get the number of pages for.
     * @throws IOException
     */
    private void getPageNum( HttpServletResponse response, int book) throws IOException {
        int pageNum = new getStory().getPageNum(book);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("PG", pageNum);
        PrintWriter printWriter = response.getWriter();
        response.setContentType("text/json");
        printWriter.print(jsonObject.toString());
    }

    /**
     * Removes the spaces from the URL.
     * @param url URL to fix.
     * @return the fixed URL.
     */
    private String fixUrl(String url) {
        return url.replaceAll("%20", " ");
    }

    /**
     * Returns the API key to the client.
     * @param request Used for logging the IP address.
     * @param response Used to communicate with the client.
     * @param URL Used to get the username and password.
     * @throws ServletException
     * @throws IOException
     */
    private void getAPIKey (HttpServletRequest request, HttpServletResponse response, String[] URL) throws ServletException, IOException {
        String key = new APIKeys().JSONGetAPIKey(URL[5], URL[6]);

        //check to see if key is empty
        if (key == null) {
            log.log("Could not create rest key for user: " + request.getRemoteAddr());
            error(response,"Could not create rest key");
            return;
        }

        log.log("Created rest key: " + key + " for user: " + request.getRemoteAddr());

        PrintWriter printWriter = response.getWriter();
        response.setContentType("text/json");
        printWriter.print(key);
    }

    /**
     * Returns the titles of the stories in the database.
     * @param request used for loggin the IP address.
     * @param response used to communicate with the client.
     * @param key API key.
     * @param title Used to se if the client either wants all of the book info or just the title.
     * @throws ServletException
     * @throws IOException
     */
    private void getStoryList (HttpServletRequest request, HttpServletResponse response, String key, boolean title) throws ServletException, IOException {
        ArrayList<String> titles = new getStory().getTitle(title);
        boolean keyCheck = new APIKeys().keyCheckNoUser(key);

        //check key
        if (keyCheck) {
            log.log("Incorrect key for user: " + request.getRemoteAddr());
            error(response, "Incorrect key.");
            return;
        }

        //check to see if there are any stories in the database
        if (titles == null) {
            log.log("No stories in database at current time." + request.getRemoteAddr());
            error(response, "No stories in database.");
            return;
        }
        //create array of stories
        JSONArray list = new JSONArray();

        for (String t : titles) {
            JSONObject storyEntry = new JSONObject();
            storyEntry.put("Book info",t);
            list.put(storyEntry);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("StoryList", list);

        PrintWriter printWriter = response.getWriter();
        response.setContentType("text/json");
        printWriter.print(jsonObject.toString());
        log.log("Returned storyList to user: " + request.getRemoteAddr());
    }

    /**
     * Returns the page from the book identified by the bookID to the client.
     * @param request used for logging IP address.
     * @param response used to communicate with client.
     * @param bookID used to get the correct book.
     * @param key API key.
     * @param page page to return to client.
     * @throws ServletException
     * @throws IOException
     */
    private void getStory (HttpServletRequest request, HttpServletResponse response, String bookID, String key, String page) throws ServletException, IOException {
        boolean keyCheck = new APIKeys().keyCheckNoUser(key);
        getStory gt = new getStory();

        //check key
        if (keyCheck) {
            log.log("Incorrect key for user: " + request.getRemoteAddr());
            error(response, "Incorrect key.");
            return;
        }

        if (!gt.checkBookID(Integer.parseInt(bookID))) {
            log.log("Incorrect bookID from user: " + request.getRemoteAddr());
            error(response, "Incorrect bookID.");
            return;
        }

        if (Integer.parseInt(page) > gt.getPageNum(Integer.parseInt(bookID)) || Integer.parseInt(page) < 1) {
            log.log("Incorrect page number from user: " + request.getRemoteAddr());
            error(response, "Invalid page number.");
            return;
        }


        String storyContent = new getStory().getText(Integer.parseInt(page), Integer.parseInt(bookID));

        //check to see if the story is empty
        if (storyContent == null) {
            error(response, "Story is empty");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Story", storyContent);

        PrintWriter printWriter = response.getWriter();
        response.setContentType("text/json");
        printWriter.print(jsonObject.toString());
        log.log("Page " + page + " from bookID: " + bookID + " sent to user: " + request.getRemoteAddr());
    }

    /**
     * Used to get the bookID of a book given the title.
     * @param request used for logging the IP address.
     * @param response used to communicate with client
     * @param book Title of the book to get the info from.
     * @param key API key.
     * @throws IOException
     */
    private void getStoryID(HttpServletRequest request, HttpServletResponse response, String book, String key) throws IOException {
        boolean keyCheck = new APIKeys().keyCheckNoUser(key);
        getStory gt = new getStory();

        int bookStatus = gt.getBookInfo(book, true);

        if (bookStatus == -1) {
            log.log("Incorrect book name from ajax.");
            return;
        }

        //check key
        if (keyCheck) {
            log.log("Incorrect key for user: " + request.getRemoteAddr());
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ID", bookStatus);

        PrintWriter printWriter = response.getWriter();
        response.setContentType("text/json");
        printWriter.print(jsonObject.toString());
        log.log("Returned story ID to ajax");
    }

    /**
     * Used to edit the title of a book in the database.
     * @param request used to get the new title and bookID.
     * @param response used to communicate with client
     */
    protected void editTitle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = "";

        if (br != null) {
            json = br.readLine();
        }

        JSONObject jsonObject = new JSONObject(json);

        String title = jsonObject.getString("title");
        int bookID = jsonObject.getInt("bookID");

        new getStory().updateTitle(title, bookID);

        response.setStatus(200);
        response.setContentType("application/json");
    }

    /**
     * Edits a story in the database.
     * @param request used to get reader for the edited page.
     * @param response used to communicate with client.
     * @throws IOException
     */
    protected void editStory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = "";

        if (br != null) {
            json = br.readLine();
        }

        JSONObject jsonObject = new JSONObject(json);

        new getStory().editStory(jsonObject.getInt("bookID"),jsonObject.getString("content"),jsonObject.getInt("newPage"));

        response.setStatus(200);
        response.setContentType("application/json");
    }

    /**
     * Adds a page to the given book.
     * @param request used to get reader for the page contents.
     * @param response used to communicate with client
     * @throws IOException
     */
    protected void addPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = "";

        if (br != null) {
            json = br.readLine();
        }

        JSONObject jsonObject = new JSONObject(json);


        new getStory().addPage(jsonObject.getInt("bookID"),jsonObject.getString("content"),jsonObject.getInt("newPage"));

        response.setStatus(200);
        response.setContentType("application/json");
    }

    /**
     * Adds story to the database.
     * @param request used to get URI, and page contents.
     * @param response used to communicate with client.
     * @throws IOException
     */
    protected void addBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        getStory gt = new getStory();
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = "";

        if (br != null) {
            json = br.readLine();
        }

        JSONObject jsonObject = new JSONObject(json);

        String title = jsonObject.getString("title");
        String[] content = separatePages(jsonObject.getString("page"));

        gt.addTitle(title);
        int storyID = gt.getBookID(title);

        List<String> list = new ArrayList<String>(Arrays.asList(content));
        list.removeAll(Collections.singleton(""));
        String[] newContent = list.toArray(new String[list.size()]);

        for (int i = 0; i < newContent.length; i++) {
            gt.addPage(storyID,fixPage(newContent[i]),i+1);
        }

        response.setStatus(200);
        response.setContentType("application/json");
    }

    /**
     * Deletes a story from the database.
     * @param request used to get the URI.
     * @param response used to communicate with client.
     */
    protected void deleteStory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = "";

        if (br != null) {
            json = br.readLine();
        }

        JSONObject jsonObject = new JSONObject(json);

        int bookid = jsonObject.getInt("bookID");

        new getStory().deleteStory(bookid);

        response.setStatus(200);
        response.setContentType("application/json");
    }

    /**
     * Separates the book by the `PAGE``~PAGE` tag.
     * @param book the book to edit.
     * @return the book separated into pages.
     */
    private String[] separatePages(String book) {
        String[] pages = book.split("</PAGE>");
        for (int i = 0; i < pages.length; i++) {
            pages[i] = fixPage(pages[i]);
        }
        return pages;
    }

    /**
     * Removes the `PAGE``~PAGE` tags from input.
     * @param page page to edit.
     * @return the page without the <PAGE></PAGE> tags.
     */
    private String fixPage(String page) {
        return page.replaceAll("<PAGE>", "").replaceAll("</PAGE>"," ");
    }

    /**
     * Function used to send error messages to the client.
     * @param response used to communicate with client.
     * @param msg message to send to client.
     * @throws IOException
     */
    private void error(HttpServletResponse response, String msg) throws IOException {
        PrintWriter out = response.getWriter();
        response.setStatus(400);
        response.setContentType("text/json");

        JSONObject json = new JSONObject();
        json.put("ERROR",msg);
        out.print(json.toString());
    }
}
