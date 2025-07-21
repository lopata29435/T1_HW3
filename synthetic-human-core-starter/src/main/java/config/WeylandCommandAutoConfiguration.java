package config;

import command.CommandQueueProcessor;
import command.CommandService;
import metrics.MetricsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(CommandService.class)
public class WeylandCommandAutoConfiguration {
    @Bean
    public CommandService commandService(@Value("${weyland.command.queue.max-size:1000}") int maxQueueSize) {
        return new CommandService(maxQueueSize);
    }

    @Bean
    public CommandQueueProcessor commandQueueProcessor(CommandService commandService) {
        return new CommandQueueProcessor(commandService);
    }
} 