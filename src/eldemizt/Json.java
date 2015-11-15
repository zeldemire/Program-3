package eldemizt;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Zach Eldemire on 11/12/15.
 */
public class Json extends HttpServlet{
    public Json () {}

    public void handleRequest(HttpServletResponse response, String[] URL) throws ServletException, IOException {
        //if (URL.length <= 2 ) error(response, "No API for: " + URL[3]);
        if (URL[4].equals("getkey") && URL.length == 7) getAPIKey(response,URL);
        else if (URL[4].equals("storyList")) getStoryList();
        //else if (URL[4].equals("story")) getStory(response, URL[5]);
    }

    private void getAPIKey (HttpServletResponse response, String[] URL) throws ServletException, IOException {
        String key = new APIKeys().JSONGetAPIKey(URL[5], URL[6]);
        if (key == null) {
            error(response,"Could not create rest key");
            return;
        }

        PrintWriter printWriter = response.getWriter();
        response.setContentType("text/json");
        printWriter.print(key);
    }

    private void getStoryList () throws ServletException, IOException {
        ArrayList<String> titles = new getStory().getTitle();
        //create array of stories
        JSONArray list = new JSONArray();

        for (String t : titles) {
            JSONObject storyEntry = new JSONObject();
            storyEntry.put("title",t);
            list.put(storyEntry);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("StoryList", list);

//        PrintWriter printWriter = response.getWriter();
//        response.setContentType("text/json");
//        printWriter.print(jsonObject.toString());

        System.out.println(jsonObject.toString());
    }

    private void getStory (String story) throws ServletException, IOException {
        String storyContent = new getStory(story).getText();
//        PrintWriter printWriter = response.getWriter();
//        response.setContentType("text/json");
//        printWriter.print(storyContent);
        System.out.println(storyContent);
    }

    private void error(HttpServletResponse response, String msg) throws IOException {
        PrintWriter out = response.getWriter();
        response.setStatus(400);
        response.setContentType("text/json");

        JSONObject json = new JSONObject();
        json.put("ERROR",msg);
        out.print(json.toString());
    }

    public static void main(String[] args) {
        Json j = new Json();
        try {
            j.getStory("book");
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
