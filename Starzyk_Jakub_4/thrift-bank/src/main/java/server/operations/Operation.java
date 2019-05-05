package server.operations;

import java.util.Date;

public abstract class Operation {

    private Date submissionDate;
    private Date executionDate;
    private boolean executed;

    Operation() {
        this.submissionDate = new Date();
        this.executed = false;
    }

    public void execute() {
        this.executionDate = new Date();
        this.executed = true;
    }

}
