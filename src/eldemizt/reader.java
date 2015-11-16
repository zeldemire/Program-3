package eldemizt;

/**
 * Created by Zach Eldemire on 10/26/15.
 * Reader class
 * This class will read the book from the file and send it to the freemarker template
 */
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class reader extends HttpServlet{
    String file = "/tmp/servlet2.log";
    Log Log = new Log(file);



    /**
     * This function will get the form data from the url and Log the users name, email, book, remoteIP, and time.
     * It will also read the book from the hard drive and send it to the freemarker template. This method is the method
     * that communicates with the freemarker template.
     * @param request used to get the parameters from the user
     * @param response used to write a response to the user
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response, Configuration configuration) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();

        //Parameters from URL
        String user = request.getParameter("user");
        String email = request.getParameter("email");
        String book = request.getParameter("book");

        //Book pages
        List<String> page = new ArrayList<>();

        //Log input
        Log.log(user + " " + email + " " + request.getRemoteAddr() + " " + book + "In reader");

        //Test input
        if (book == null) book = "";

        if (user == null) user = "";

        if (email == null) email = "";

        //Hash map for the freemarker
        Map<String, Object> root = new HashMap<>();
        String title = "HTTP APP V2";
        root.put("TITLE", title);
        root.put("USER", user);
        root.put("EMAIL", email);
        root.put("PAGE", page);

        String books;
        String[] pageSplit;

        getStory gt = new getStory(book);

        books = gt.getText();
        books = books.replace("<PAGE>", "");
        pageSplit = books.split("</PAGE>");
        Collections.addAll(page, pageSplit);


        Template template = configuration.getTemplate("reader_template.ftl");
        try {
            template.process(root, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
            Log.log(e.getMessage());
        }
    }
}
