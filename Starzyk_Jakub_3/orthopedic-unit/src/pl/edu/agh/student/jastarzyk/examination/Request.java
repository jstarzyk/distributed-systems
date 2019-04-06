package pl.edu.agh.student.jastarzyk.examination;

public class Request extends Examination {

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    private String routingKey;

    public Request(Type type, String patientName) {
        super(type, patientName);
        this.routingKey = null;
    }

    @Override
    public String toString() {
        return type.toString() + " " + patientName;
    }
}
