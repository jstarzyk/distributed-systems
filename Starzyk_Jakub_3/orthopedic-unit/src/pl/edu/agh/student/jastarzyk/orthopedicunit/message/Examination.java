package pl.edu.agh.student.jastarzyk.orthopedicunit.message;

public class Examination extends Message {

//    int id;
    Type type;
    String patientName;

    public Examination(Type type, String patientName) {
        super();
        this.type = type;
        this.patientName = patientName;
    }

    public enum Type {
        HIP,
        KNEE,
        ELBOW
    }

}
