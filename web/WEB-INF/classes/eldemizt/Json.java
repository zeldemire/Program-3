package eldemizt;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Zach Eldemire on 11/12/15.
 */
public class Json extends HttpServlet{
    String restLog = "/tmp/rest.log";
    Log log = new Log(restLog);

    public void handleRequest(HttpServletRequest request, HttpServletResponse response, String[] URL) throws ServletException, IOException {
        if (URL[4].equals("getkey") && URL.length == 7) getAPIKey(request,response,URL);
        else if (URL[4].equals("storyList")) getStoryList(request,response, URL[3]);
        else if (URL[4].equals("story")) getStory(request,response, URL[5], URL[3], URL[6]);
        else if (URL.length <= 2 || URL.length > 7) error(response, "No API selected");
    }

    private void getAPIKey (HttpServletRequest request, HttpServletResponse response, String[] URL) throws ServletException, IOException {
        String key = new APIKeys().JSONGetAPIKey(URL[5], URL[6]);

        //check to see if key is empty
        if (key == null) {
            log.log("Could not create rest key");
            error(response,"Could not create rest key");
            return;
        }

        log.log("Created rest key: " + key + " for user: " + request.getRemoteAddr());

        PrintWriter printWriter = response.getWriter();
        response.setContentType("text/json");
        printWriter.print(key);
    }

    private void getStoryList (HttpServletRequest request, HttpServletResponse response, String key) throws ServletException, IOException {
        ArrayList<String> titles = new getStory().getTitle(false);
        boolean keyCheck = new APIKeys().keyCheckNoUser(key);

        //check key
        if (keyCheck) {
            log.log("Incorrect key for user: " + request.getRemoteAddr());
            error(response, "Incorrect key.");
            return;
        }

        //check to see if there are any stories in the database
        if (titles == null) {
            log.log("No stories in database at current time.");
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

        PrintWriter printWriter = response.getWriter();
        response.setContentType("text/json");
        printWriter.print(storyContent);
        log.log("Page " + page + " from bookID: " + bookID + " sent to user: " + request.getRemoteAddr());
    }

    private void error(HttpServletResponse response, String msg) throws IOException {
        PrintWriter out = response.getWriter();
        response.setStatus(400);
        response.setContentType("text/json");

        JSONObject json = new JSONObject();
        json.put("ERROR",msg);
        out.print(json.toString());
    }

}
