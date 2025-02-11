package manager.exception;

public class ManagerTaskCrossingException extends Exception {
    public ManagerTaskCrossingException() {
        super();
    }

    public ManagerTaskCrossingException(String message) {
        super(message);
    }
}