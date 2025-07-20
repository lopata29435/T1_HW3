package org.main.bishop.bishopprototype;

import metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    private final MetricsService metricsService;

    @Autowired
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentMetrics() {
        return ResponseEntity.ok(metricsService.getCurrentMetrics());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> getHealth() {
        return ResponseEntity.ok(Map.of(
                "status", "OPERATIONAL",
                "service", "Bishop Prototype",
                "version", "1.0.0"
        ));
    }
} 