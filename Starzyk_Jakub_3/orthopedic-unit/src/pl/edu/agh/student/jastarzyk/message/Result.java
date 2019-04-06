package pl.edu.agh.student.jastarzyk.message;

public class Result extends Examination {

    private String result;

    public Result(Request request, String result) {
        super(request.type, request.patientName);
        this.result = result;
    }

    @Override
    public String toString() {
        return type.toString() + " " + patientName + " done";
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
