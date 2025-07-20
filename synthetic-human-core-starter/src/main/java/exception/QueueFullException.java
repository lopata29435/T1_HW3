package exception;

public class QueueFullException extends CommandException {
    
    public QueueFullException() {
        super("Command queue is full. Cannot accept more commands.", "QUEUE_FULL");
    }
    
    public QueueFullException(String message) {
        super(message, "QUEUE_FULL");
    }
} 