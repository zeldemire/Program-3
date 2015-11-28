package eldemizt.view;

import eldemizt.model.getStory;
import eldemizt.model.Login;
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

    /**
     * doGet function that gets the printwriter that will communicate with the client.
     * @param response used to write a response to the user
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet( HttpServletResponse response, Configuration configuration, boolean admin) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            if (admin) this.createAdminPage(out,configuration);
            else this.createPage(out,configuration);
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
        new reader().doGet(req,resp,configuration);
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
        eldemizt.model.getStory gt = new getStory();
        stories = gt.getTitle(true);

        //If no stories
        if (stories == null) stories.add("No sotries");

        root.put("STORY", stories);
        root.put("ADMIN", false);
        root.put("NUMOFPAGES", 0);

        //load the template
        Template template = configuration.getTemplate("select_template.ftl");
        template.process(root, writer);
    }

    /**
     * This method will generate the admin page that the user is going to see.
     * @param writer used to communicate with freemarker template
     * @throws Exception
     */
    protected void createAdminPage(PrintWriter writer, Configuration configuration) throws Exception{

        Login login = new Login("admin", "admin");
        String apiKey = login.generateAPIKey();

        //freemarker hashmap
        Map<String, Object> root = new HashMap<>();
        String title = "HTTP APP V2 Admin";
        root.put("TITLE", title);

        //available stories that the user can read.
        List stories = new ArrayList();
        eldemizt.model.getStory gt = new getStory();
        stories = gt.getTitle(true);

        root.put("STORY", stories);
        root.put("ADMIN", true);

        //load the template
        Template template = configuration.getTemplate("select_template.ftl");
        template.process(root, writer);
    }
}
