package command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import metrics.MetricsService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandServiceTest {
    
    @Mock
    private MetricsService metricsService;
    
    private CommandService commandService;
    private final int maxQueueSize = 1000;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandService = new CommandService(metricsService, maxQueueSize);
    }
    
    @Test
    void testSubmitCriticalCommand() {
        Command command = new Command(
            "Test critical command",
            CommandPriority.CRITICAL,
            "Test Author",
            "2024-01-01T12:00:00"
        );
        
        Command result = commandService.submitCommand(command);
        
        assertNotNull(result.getId());
        assertEquals(CommandStatus.COMPLETED, result.getStatus());
        verify(metricsService).incrementCommandsSubmitted();
        verify(metricsService).incrementCommandsCompleted();
        verify(metricsService).incrementAuthorCommands("Test Author");
    }
    
    @Test
    void testSubmitCommonCommand() {
        Command command = new Command(
            "Test common command",
            CommandPriority.COMMON,
            "Test Author",
            "2024-01-01T12:00:00"
        );
        
        Command result = commandService.submitCommand(command);
        
        assertNotNull(result.getId());
        assertEquals(CommandStatus.PENDING, result.getStatus());
        assertEquals(1, commandService.getQueueSize());
        verify(metricsService).incrementCommandsSubmitted();
    }
    
    @Test
    void testGetAuthorStats() {
        Command command1 = new Command("Test 1", CommandPriority.CRITICAL, "Author1", "2024-01-01T12:00:00");
        Command command2 = new Command("Test 2", CommandPriority.CRITICAL, "Author2", "2024-01-01T12:00:00");
        
        commandService.submitCommand(command1);
        commandService.submitCommand(command2);
        
        var stats = commandService.getAuthorStats();
        assertEquals(2, stats.size());
        assertTrue(stats.containsKey("Author1"));
        assertTrue(stats.containsKey("Author2"));
    }
} 