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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Zach Eldemire on 11/12/15.
 */
public class Json extends HttpServlet{
    String restLog = "/tmp/rest.log";
    Log log = new Log(restLog);

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

    private void getPageNum( HttpServletResponse response, int book) throws IOException {
        int pageNum = new getStory().getPageNum(book);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("PG", pageNum);
        PrintWriter printWriter = response.getWriter();
        response.setContentType("text/json");
        printWriter.print(jsonObject.toString());
    }

    private String fixUrl(String url) {
        return url.replaceAll("%20", " ");
    }

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

    protected void editTitleandAuthor(HttpServletRequest request, HttpServletResponse response) {
        String title = fixUrl(getParts(request.getRequestURI(),5));
        int bookID = Integer.parseInt(getParts(request.getRequestURI(), 4));

        new getStory().updateTitle(title, bookID);

        response.setStatus(200);
        response.setContentType("application/json");
    }
    protected void editStory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader br = request.getReader();
        StringBuffer sb = new StringBuffer();
        String l;

        while ((l = br.readLine()) != null) {
            sb.append(l);
        }

        //4 = page, 5 = bookid
        new getStory().editStory(Integer.parseInt(getParts(request.getRequestURI(),5)), sb.toString(),
                Integer.parseInt(getParts(request.getRequestURI(),4)));

        response.setStatus(200);
        response.setContentType("application/json");
    }

    protected void addPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader br = request.getReader();
        StringBuilder sb = new StringBuilder();
        String l;

        while ((l = br.readLine()) != null) sb.append(l);

        log.log("" + getParts(request.getRequestURI(),3));
        //3 = page, 4 = bookid
        new getStory().addPage(Integer.parseInt(getParts(request.getRequestURI(),5)),sb.toString(), Integer.parseInt(getParts(request.getRequestURI(),4)));

        response.setStatus(200);
        response.setContentType("application/json");
    }

    protected void addBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        getStory gt = new getStory();
        String title = fixUrl(getParts(request.getRequestURI(), 4));
        String[] content = separatePages(getParts(request.getRequestURI(),5));

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

    protected void deleteStory(HttpServletRequest request, HttpServletResponse response) {
        int bookid = Integer.parseInt(getParts(request.getRequestURI(), 3));

        new getStory().deleteStory(bookid);

        response.setStatus(200);
        response.setContentType("application/json");
    }
    private String[] separatePages(String book) {
        String[] pages = book.split("~PAGE");
        for (int i = 0; i < pages.length; i++) {
            pages[i] = fixPage(pages[i]);
        }
        return pages;
    }
    private String  getParts(String uri, int index) {
        String[] parts = uri.split("/");
        return parts[index];
    }

    private String fixPage(String page) {
        return page.replaceAll("PAGE", "").replaceAll("~PAGE"," ").replaceAll("%20"," ").replaceAll("%60","");
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
//        Json json = new Json();
//        System.out.println(json.fixPage("%60PAGE%60HELO%60~PAGE%60%60PAGE%60THERE%60~PAGE%60"));

        String content[] = { "Mars", "", "Saturn", "", "Mars" };

        List<String> list = new ArrayList<String>(Arrays.asList(content));
        list.removeAll(Collections.singleton(null));
        String ne[] = list.toArray(new String[list.size()]);

        for (String aNe : ne) System.out.print(aNe);
    }
}
