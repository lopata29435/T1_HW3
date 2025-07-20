package command;

import audit.WeylandWatchingYou;
import exception.QueueFullException;
import metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class CommandService {
    private static final Logger logger = LoggerFactory.getLogger(CommandService.class);

    private final int maxQueueSize;
    private final BlockingQueue<Command> commandQueue;
    private final Map<String, AtomicInteger> authorStats = new ConcurrentHashMap<>();
    private final AtomicInteger completedCommands = new AtomicInteger(0);
    private final AtomicInteger failedCommands = new AtomicInteger(0);
    private final MetricsService metricsService;

    @Autowired
    public CommandService(@Lazy MetricsService metricsService, @Value("${weyland.command.queue.max-size:1000}") int maxQueueSize) {
        this.metricsService = metricsService;
        this.maxQueueSize = maxQueueSize;
        this.commandQueue = new LinkedBlockingQueue<>(maxQueueSize);
    }

    @WeylandWatchingYou(description = "Submit new command for execution")
    public Command submitCommand(Command command) {
        command.setId(UUID.randomUUID().toString());
        authorStats.computeIfAbsent(command.getAuthor(), k -> new AtomicInteger(0));
        metricsService.incrementCommandsSubmitted();
        if (command.getPriority() == CommandPriority.CRITICAL) {
            executeCommand(command);
        } else {
            if (commandQueue.size() >= maxQueueSize) {
                throw new QueueFullException();
            }
            try {
                commandQueue.put(command);
                logger.info("Command added to queue: {}", command.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Failed to add command to queue", e);
            }
        }
        return command;
    }

    @WeylandWatchingYou(description = "Execute individual command")
    public void executeCommand(Command command) {
        logger.info("Executing command: {}", command);
        command.setStatus(CommandStatus.EXECUTING);
        try {
            Thread.sleep(1000); // увеличено до 1 секунды
            command.setStatus(CommandStatus.COMPLETED);
            completedCommands.incrementAndGet();
            authorStats.get(command.getAuthor()).incrementAndGet();
            metricsService.incrementCommandsCompleted();
            metricsService.incrementAuthorCommands(command.getAuthor());
            logger.info("Command completed successfully: {}", command.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            command.setStatus(CommandStatus.FAILED);
            failedCommands.incrementAndGet();
            metricsService.incrementCommandsFailed();
            logger.error("Command execution interrupted: {}", command.getId(), e);
        } catch (Exception e) {
            command.setStatus(CommandStatus.FAILED);
            failedCommands.incrementAndGet();
            metricsService.incrementCommandsFailed();
            logger.error("Command execution failed: {}", command.getId(), e);
        }
    }

    @WeylandWatchingYou(description = "Process command queue")
    public void processQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Command command = commandQueue.take();
                executeCommand(command);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @WeylandWatchingYou(description = "Get current queue size")
    public int getQueueSize() {
        return commandQueue.size();
    }

    @WeylandWatchingYou(description = "Get completed commands count")
    public int getCompletedCommands() {
        return completedCommands.get();
    }

    @WeylandWatchingYou(description = "Get failed commands count")
    public int getFailedCommands() {
        return failedCommands.get();
    }

    @WeylandWatchingYou(description = "Get author statistics")
    public Map<String, Integer> getAuthorStats() {
        Map<String, Integer> stats = new ConcurrentHashMap<>();
        authorStats.forEach((author, count) -> stats.put(author, count.get()));
        return stats;
    }

    @WeylandWatchingYou(description = "Check if queue is full")
    public boolean isQueueFull() {
        return commandQueue.size() >= maxQueueSize;
    }
} 