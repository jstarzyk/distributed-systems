package pl.edu.agh.student.jastarzyk.orthopedicunit.message;

public class Request extends Examination {

    private String routingKey;

    public Request(Type type, String patientName, String routingKey) {
        super(type, patientName);
        this.routingKey = routingKey;
    }

    @Override
    public String toString() {
        return type.toString() + " " + patientName;
    }

    public String getRoutingKey() {
        return routingKey;
    }

}
