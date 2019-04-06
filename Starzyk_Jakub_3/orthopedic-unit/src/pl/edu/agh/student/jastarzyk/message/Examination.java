package pl.edu.agh.student.jastarzyk.message;

public class Examination extends Message {

//    int id;
    Type type;
    String patientName;
//    String date;
//    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

//    public Examination() {
//        super();
//        this.date = dateFormat.format(new Date());
//    }

    public Examination(Type type, String patientName) {
        super();
        this.type = type;
        this.patientName = patientName;
//        this.date = dateFormat.format(new Date());
    }

    public enum Type {
        HIP,
        KNEE,
        ELBOW
    }
}
