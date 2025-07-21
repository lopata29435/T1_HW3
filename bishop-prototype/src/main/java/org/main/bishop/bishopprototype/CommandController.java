package org.main.bishop.bishopprototype;

import command.Command;
import command.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/commands")
public class CommandController {
    private final CommandService commandService;

    @Autowired
    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping
    public ResponseEntity<Command> submitCommand(@Valid @RequestBody Command command) {
        Command submittedCommand = commandService.submitCommand(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(submittedCommand);
    }

    @GetMapping("/queue/size")
    public ResponseEntity<Map<String, Integer>> getQueueSize() {
        Map<String, Integer> response = Map.of(
                "queueSize", commandService.getQueueSize(),
                "completedCommands", commandService.getCompletedCommands()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/authors")
    public ResponseEntity<Map<String, Integer>> getAuthorStats() {
        return ResponseEntity.ok(commandService.getAuthorStats());
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = Map.of(
                "queueSize", commandService.getQueueSize(),
                "completedCommands", commandService.getCompletedCommands(),
                "queueFull", commandService.isQueueFull(),
                "authorStats", commandService.getAuthorStats()
        );
        return ResponseEntity.ok(status);
    }
} 