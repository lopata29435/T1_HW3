package org.main.bishop.bishopprototype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {
        "org.main.bishop.bishopprototype",
        "command",
        "metrics",
        "audit",
        "exception"
})
public class BishopPrototypeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BishopPrototypeApplication.class, args);
    }
}
