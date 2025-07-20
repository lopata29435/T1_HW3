package audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class AuditEvent {
    private String methodName;
    private Object[] parameters;
    private Object result;
    private String description;
    private long executionTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    public AuditEvent() {}
    
    public AuditEvent(String methodName, Object[] parameters, Object result, 
                     String description, long executionTime, LocalDateTime timestamp) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.result = result;
        this.description = description;
        this.executionTime = executionTime;
        this.timestamp = timestamp;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public Object[] getParameters() {
        return parameters;
    }
    
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public long getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "AuditEvent{" +
                "methodName='" + methodName + '\'' +
                ", description='" + description + '\'' +
                ", executionTime=" + executionTime +
                ", timestamp=" + timestamp +
                '}';
    }
} 