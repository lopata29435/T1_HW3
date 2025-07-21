package config;

import audit.AuditAspect;
import audit.AuditService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(AuditService.class)
public class WeylandAuditAutoConfiguration {
    @Bean
    public AuditService auditService() {
        return new AuditService();
    }

    @Bean
    public AuditAspect auditAspect(AuditService auditService) {
        return new AuditAspect(auditService);
    }
} 