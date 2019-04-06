package pl.edu.agh.student.jastarzyk.orthopedicunit.message;

public class Info extends Message {

    private String message;

    public Info(String message) {
        super();
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

}
