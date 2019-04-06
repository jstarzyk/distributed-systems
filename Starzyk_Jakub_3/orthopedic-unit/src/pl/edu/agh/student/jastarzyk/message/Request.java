package pl.edu.agh.student.jastarzyk.message;

public class Request extends Examination {

    public String getRoutingKey() {
        return routingKey;
    }
//
//    public void setRoutingKey(String routingKey) {
//        this.routingKey = routingKey;
//    }

    private String routingKey;

    public Request(Type type, String patientName, String routingKey) {
        super(type, patientName);
        this.routingKey = routingKey;
    }

    @Override
    public String toString() {
        return type.toString() + " " + patientName;
    }
}
