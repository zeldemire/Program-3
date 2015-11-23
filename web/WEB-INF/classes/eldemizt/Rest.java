package eldemizt;

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
 */
public class Rest extends HttpServlet {

    String restLog = "/tmp/rest.log";
    Log log = new Log(restLog);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        URL url = new URL(req.getRequestURL().toString());
        String urlPath = url.getPath();
        String parts[] = urlPath.split("/");

        log.log(req.getRemoteAddr() + " connected");
        new Json().handleRequest(req, resp, parts);
    }
}
