package pl.edu.agh.student.jastarzyk.examination;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Message implements Serializable {

    String date;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

    Message() {
        this.date = dateFormat.format(new Date());
    }

}
