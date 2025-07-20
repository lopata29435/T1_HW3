    package command;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.CommandLineRunner;
    import org.springframework.stereotype.Component;
    import java.util.concurrent.ExecutorService;
    import java.util.concurrent.Executors;

    @Component
    public class CommandQueueProcessor implements CommandLineRunner {

        private final CommandService commandService;
        private final ExecutorService executorService;

        @Autowired
        public CommandQueueProcessor(CommandService commandService) {
            this.commandService = commandService;
            this.executorService = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "CommandQueueProcessor");
                t.setDaemon(true);
                return t;
            });
        }

        @Override
        public void run(String... args) throws Exception {
            executorService.submit(() -> {
                try {
                    commandService.processQueue();
                } catch (Exception e) {
                    System.err.println("Error in command queue processor: " + e.getMessage());
                }
            });
        }
    }