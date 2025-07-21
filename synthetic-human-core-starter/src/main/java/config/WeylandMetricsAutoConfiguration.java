package config;

import audit.AuditService;
import command.CommandService;
import io.micrometer.core.instrument.MeterRegistry;
import metrics.MetricsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(MetricsService.class)
public class WeylandMetricsAutoConfiguration {
    @Bean
    public MetricsService metricsService(CommandService commandService, AuditService auditService, MeterRegistry meterRegistry) {
        return new MetricsService(commandService, auditService, meterRegistry);
    }
} 