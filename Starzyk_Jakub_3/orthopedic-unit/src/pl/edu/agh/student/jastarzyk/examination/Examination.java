package pl.edu.agh.student.jastarzyk.examination;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Examination implements Serializable {

//    int id;
    Type type;
    String patientName;
    String date;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

    public Examination() {
        this.date = dateFormat.format(new Date());
    }

    public Examination(Type type, String patientName) {
        this.type = type;
        this.patientName = patientName;
        this.date = dateFormat.format(new Date());
    }

}
