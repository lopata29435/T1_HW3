package metrics;

import command.CommandService;
import audit.AuditService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsService {
    
    private final CommandService commandService;
    private final AuditService auditService;
    private final MeterRegistry meterRegistry;
    
    private final Counter commandsSubmittedCounter;
    private final Counter commandsCompletedCounter;
    private final Counter commandsFailedCounter;
    private final Gauge queueSizeGauge;
    private final Map<String, Counter> authorCounters = new ConcurrentHashMap<>();
    
    @Autowired
    public MetricsService(CommandService commandService, AuditService auditService, MeterRegistry meterRegistry) {
        this.commandService = commandService;
        this.auditService = auditService;
        this.meterRegistry = meterRegistry;
        
        this.commandsSubmittedCounter = Counter.builder("weyland.commands.submitted")
            .description("Total number of commands submitted")
            .register(meterRegistry);
            
        this.commandsCompletedCounter = Counter.builder("weyland.commands.completed")
            .description("Total number of commands completed")
            .register(meterRegistry);
            
        this.commandsFailedCounter = Counter.builder("weyland.commands.failed")
            .description("Total number of commands failed")
            .register(meterRegistry);

        this.queueSizeGauge = Gauge.builder("weyland.commands.queue.size", this, MetricsService::getQueueSize)
                .description("Current number of commands in queue")
                .register(meterRegistry);
    }
    
    public void incrementCommandsSubmitted() {
        commandsSubmittedCounter.increment();
    }
    
    public void incrementCommandsCompleted() {
        commandsCompletedCounter.increment();
    }
    
    public void incrementCommandsFailed() {
        commandsFailedCounter.increment();
    }
    
    public void incrementAuthorCommands(String author) {
        authorCounters.computeIfAbsent(author, 
            a -> Counter.builder("weyland.commands.by.author")
                .tag("author", a)
                .description("Commands by author")
                .register(meterRegistry))
            .increment();
    }
    
    public double getQueueSize() {
        return commandService.getQueueSize();
    }
    
    @Scheduled(fixedRate = 5000)
    public void updateMetrics() {
        Map<String, Integer> authorStats = commandService.getAuthorStats();
        authorStats.forEach((author, count) -> {
            if (!authorCounters.containsKey(author)) {
                authorCounters.put(author, 
                    Counter.builder("weyland.commands.by.author")
                        .tag("author", author)
                        .description("Commands by author")
                        .register(meterRegistry));
            }
        });
        
        Map<String, Integer> methodStats = auditService.getMethodCallStats();
        methodStats.forEach((method, count) -> {
            Counter methodCounter = meterRegistry.counter("weyland.method.calls", "method", method);
        });
    }
    
    public Map<String, Object> getCurrentMetrics() {
        return Map.of(
            "queueSize", commandService.getQueueSize(),
            "completedCommands", commandService.getCompletedCommands(),
            "authorStats", commandService.getAuthorStats(),
            "methodCallStats", auditService.getMethodCallStats()
        );
    }
} 