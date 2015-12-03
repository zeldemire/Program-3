package eldemizt.json;

import eldemizt.model.APIKeys;
import eldemizt.model.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Zach Eldemire on 11/21/15.
 * Program 3
 * CSE 383
 * This class handles all of the rest calls to the servlet.
 */
public class Rest extends HttpServlet {

    String restLog = "/tmp/rest.log";
    Log log = new Log(restLog);

    /**
     * This function will handle all of the doget requests to the server. It only deals with json requests.
     * @param req Used to get information from client, parameters.
     * @param resp Used to se the response back from the server to the client.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        URL url = new URL(req.getRequestURL().toString());
        String urlPath = url.getPath();
        String parts[] = urlPath.split("/");

        log.log(req.getRemoteAddr() + " connected");
        new Json().handleRequest(req, resp, parts);
    }

    /**
     * This function will handle the doDelete requests from the website.
     * @param req used to get URI.
     * @param resp used to set status.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        URL url = new URL(req.getRequestURL().toString());
        String urlPath = url.getPath();
        String parts[] = urlPath.split("/");
        String key = parts[3];

        if (!keyCheck(key)) {
            new Json().deleteStory(req,resp);
        }
        else {
            resp.setStatus(400);
            resp.setContentType("application/json");
        }
    }

    /**
     * This function will handle doPut calls from website.
     * @param req used to get URI.
     * @param resp used to set the status.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        URL url = new URL(req.getRequestURL().toString());
        String urlPath = url.getPath();
        String parts[] = urlPath.split("/");

        if (!keyCheck(parts[4])) {
            if (parts[3].equals("addbook")) new Json().addBook(req, resp);
            else if (parts[3].equals("addpage")) new Json().addPage(req, resp);
        }
        else {
            resp.setStatus(400);
            resp.setContentType("application/json");
        }
    }

    /**
     * Used to handle doPost call from website.
     * @param req used to get URI.
     * @param resp used to interact with the client.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        URL url = new URL(req.getRequestURL().toString());
        String urlPath = url.getPath();
        String parts[] = urlPath.split("/");

        if (!keyCheck(parts[4])) {
            if (parts[3].equals("editStory")) new Json().editStory(req, resp);
            else if (parts[3].equals("editTitle")) new Json().editTitle(req, resp);
        }
        else {
            resp.setStatus(400);
            resp.setContentType("application/json");
        }
    }

    /**
     * Used to check if the key is in the database.
     * @param key key to be checked.
     * @return true if the key is not in the database, false if it is.
     */
    private boolean keyCheck(String key) {
        APIKeys apiKeys = new APIKeys();
        return apiKeys.keyCheckNoUser(key);
    }
}


