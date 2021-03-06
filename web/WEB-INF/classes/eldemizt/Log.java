package eldemizt;

/**
 * Created by Zach Eldemire on 10/27/15.
 */
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class Log {
    BufferedWriter bw;

    // open log
    public Log(String name) {
        bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(name, true));
        } catch (FileNotFoundException err) {
            System.err.println("Error creating log");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // write log message
    public void log(String msg) {
        try {
            bw.write(dateTime() + " "+  msg);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) {
            }
        }
    }


    //get current date time
    private String dateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
