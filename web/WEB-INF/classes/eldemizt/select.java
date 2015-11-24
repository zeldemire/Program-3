package eldemizt;

import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zach Eldemire on 10/26/15.
 * Select.java
 * This class will show the available stories to the user, and pass their decision onto the reader class
 */
public class select extends HttpServlet{

    String file = "/tmp/servlet2.log";
    Log Log = new Log(file);

    /**
     * doGet function that gets the printwriter that will communicate with the client.
     * @param request used to get the parameters from the user
     * @param response used to write a response to the user
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response, Configuration configuration) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            this.createPage(out,configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Gets the form data from template and checks if the username and password match what is in the database.
     * @param req used to get the parameters from the user
     * @param resp used to write a response to the user
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, Configuration configuration) throws ServletException, IOException {
        String password = req.getParameter("password");
        String username = req.getParameter("user");
        Login login = new Login(password, username);

        if(login.testPassword(login.generateHash())) {
            Log.log(username + " successfully logged in. IP: " + req.getRemoteAddr());
            new reader().doGet(req,resp,configuration);
        }
        else
            resp.sendRedirect("select");
    }

    /**
     * This method will generate the page that the user is going to see.
     * @param writer used to communicate with freemarker template
     * @throws Exception
     */
    protected void createPage(PrintWriter writer, Configuration configuration) throws Exception{

        //freemarker hashmap
        Map<String, Object> root = new HashMap<>();
        String title = "HTTP APP V2";
        root.put("TITLE", title);

        //available stories that the user can read.
        List stories = new ArrayList();
        getStory gt = new getStory();
        stories = gt.getTitle(true);

        //If no stories
        if (stories == null) stories.add("No sotries");

        root.put("STORY", stories);

        //load the template
        Template template = configuration.getTemplate("select_template.ftl");
        template.process(root, writer);
    }
}
