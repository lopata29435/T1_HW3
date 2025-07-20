package exception;

public class CommandException extends RuntimeException {
    
    private final String errorCode;
    
    public CommandException(String message) {
        super(message);
        this.errorCode = "COMMAND_ERROR";
    }
    
    public CommandException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public CommandException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "COMMAND_ERROR";
    }
    
    public CommandException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
} 