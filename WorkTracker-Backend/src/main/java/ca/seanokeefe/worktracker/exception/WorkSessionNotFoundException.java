package ca.seanokeefe.worktracker.exception;

public class WorkSessionNotFoundException extends RuntimeException {

    public WorkSessionNotFoundException() {
        super("Work session not found.");
    }

    public WorkSessionNotFoundException(String message) {
        super(message);
    }
}
