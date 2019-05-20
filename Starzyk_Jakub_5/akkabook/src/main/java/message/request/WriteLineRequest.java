package message.request;

public class WriteLineRequest implements scala.Serializable {

    private final String line;

    public WriteLineRequest(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }
}
