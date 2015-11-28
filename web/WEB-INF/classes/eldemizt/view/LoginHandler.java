package eldemizt.view;

import eldemizt.model.Log;
import eldemizt.model.Login;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zach Eldemire on 11/26/15.
 * Program 3
 * CSE 383
 */
public class LoginHandler extends HttpServlet{

    String file = "/tmp/servlet2.log";
    eldemizt.model.Log Log = new Log(file);

    protected void doGet( HttpServletResponse resp, Configuration configuration) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        try {
            this.createPage(out,configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will generate the page that the user is going to see.
     * @param writer used to communicate with freemarker template
     * @throws Exception
     */
    protected void createPage(PrintWriter writer, Configuration configuration) throws Exception{

        //freemarker hash map
        Map<String, Object> root = new HashMap<>();
        String title = "HTTP APP V2";
        root.put("TITLE", title);

        //load the template
        Template template = configuration.getTemplate("login_template.ftl");
        template.process(root, writer);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp, Configuration configuration) throws ServletException, IOException {

        String password = req.getParameter("password");
        String username = req.getParameter("user");
        Login login = new Login(password, username);

        if(login.testPassword(login.generateHash())) {
            if (login.isAdmin()) {
                new select().doGet(resp,configuration,true);
                Log.log(username + " successfully logged in to admin. IP: " + req.getRemoteAddr());
            }
            else {
                new select().doGet(resp, configuration,false);
                Log.log(username + " successfully logged in. IP: " + req.getRemoteAddr());
            }
        }
        else {
            Log.log(req.getRemoteAddr() + " Failed to login with username: " + username);
            resp.sendRedirect("LoginHandler");
        }

    }
}
