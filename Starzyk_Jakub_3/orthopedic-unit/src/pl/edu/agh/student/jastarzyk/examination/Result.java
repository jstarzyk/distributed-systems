package pl.edu.agh.student.jastarzyk.examination;

public class Result extends Examination {

    private String result;

    public Result(Type type, String patientName) {
        super(type, patientName);
    }

    public Result(Request request) {
        super();
        this.patientName = request.patientName;
        this.type = request.type;
        this.result = null;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return type.toString() + " " + patientName + " done";
    }
}
