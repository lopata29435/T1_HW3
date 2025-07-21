package audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    @Value("${weyland.audit.mode:CONSOLE}")
    private AuditMode auditMode;
    
    @Value("${weyland.audit.kafka.topic:weyland-audit}")
    private String kafkaTopic;
    
    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final Map<String, Integer> methodCallCount = new ConcurrentHashMap<>();
    
    public void auditMethodCall(String methodName, Object[] parameters, Object result, 
                               String description, long executionTime) {
        AuditEvent event = new AuditEvent(
            methodName,
            parameters,
            result,
            description,
            executionTime,
            LocalDateTime.now()
        );
        
        methodCallCount.merge(methodName, 1, Integer::sum);
        
        switch (auditMode) {
            case KAFKA:
                sendToKafka(event);
                break;
            case CONSOLE:
            default:
                logToConsole(event);
                break;
        }
    }
    
    private void sendToKafka(AuditEvent event) {
        if (kafkaTemplate != null) {
            try {
                String eventJson = objectMapper.writeValueAsString(event);
                kafkaTemplate.send(kafkaTopic, eventJson);
                logger.debug("Audit event sent to Kafka: {}", event.getMethodName());
            } catch (Exception e) {
                logger.error("Failed to send audit event to Kafka", e);
                logToConsole(event);
            }
        } else {
            logger.warn("Kafka template not available, falling back to console logging");
            logToConsole(event);
        }
    }
    
    private void logToConsole(AuditEvent event) {
        logger.info("AUDIT: Method={}, Description={}, ExecutionTime={}ms, Timestamp={}, Parameters={}, Result={}",
            event.getMethodName(),
            event.getDescription(),
            event.getExecutionTime(),
            event.getTimestamp(),
            event.getParameters(),
            event.getResult()
        );
    }
    
    public Map<String, Integer> getMethodCallStats() {
        return new ConcurrentHashMap<>(methodCallCount);
    }
    
    public enum AuditMode {
        CONSOLE,
        KAFKA
    }
} 