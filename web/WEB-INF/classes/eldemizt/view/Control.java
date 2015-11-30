package eldemizt.view;

import eldemizt.model.Log;
import eldemizt.view.reader;
import eldemizt.view.select;
import freemarker.template.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Zach Eldemire on 11/10/15.
 * This is the dispatcher program responsible for selecting the correct servlet.
 */
public class Control extends HttpServlet {
    String file = "/tmp/servlet2.log";
    eldemizt.model.Log Log = new Log(file);

    /**
     * Dispatcher for the servlets. Will get the url and call the corresponding servlet depending on the input.
     * If the url doesn't have a servlet it is sent to the default servlet, LoginHandler.
     * @param req used to get parameters from client
     * @param resp used to communicate with the client
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        URL url = new URL(req.getRequestURL().toString());
        String servlet = url.getPath();
        String parts[] = servlet.split("/");
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setServletContextForTemplateLoading(getServletContext(), "WEB-INF/templates/");
        configuration.setDefaultEncoding("UTF-8");

        Log.log(req.getRemoteAddr() + " connected.");
        if (parts.length > 2) {
            if ("reader".equals(parts[3])) new reader().doGet(req,resp, configuration);
            else new LoginHandler().doGet(resp,configuration);
        }
        else new LoginHandler().doGet(resp,configuration);
    }

    /**
     * This will redirect all doPost calls from the website forms to the corresponding servlet to handle.
     * @param req used to get the url
     * @param resp passed onto the corresponding servlet
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        URL url = new URL(req.getRequestURL().toString());
        String servlet = url.getPath();
        String parts[] = servlet.split("/");

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setServletContextForTemplateLoading(getServletContext(), "WEB-INF/templates/");
        configuration.setDefaultEncoding("UTF-8");

        if ("select".equals(parts[3])) new select().doPost(req,resp,configuration);
        else if ("LoginHandler".equals(parts[3])) new LoginHandler().doPost(req,resp,configuration);
    }

}
