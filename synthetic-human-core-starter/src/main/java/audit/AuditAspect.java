package audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

//TODO добавить фильтрацию чувствительных данных
@Aspect
@Component
public class AuditAspect {
    
    private final AuditService auditService;
    
    @Autowired
    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }
    
    @Around("@annotation(audit.WeylandWatchingYou)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        WeylandWatchingYou annotation = method.getAnnotation(WeylandWatchingYou.class);
        
        String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        String description = annotation.description().isEmpty() ? methodName : annotation.description();
        
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            Object[] parameters = annotation.logParameters() ? joinPoint.getArgs() : new Object[0];
            Object auditResult = annotation.logResult() ? result : null;
            
            if (exception != null) {
                auditResult = "EXCEPTION: " + exception.getMessage();
            }
            
            auditService.auditMethodCall(methodName, parameters, auditResult, description, executionTime);
        }
    }
} 