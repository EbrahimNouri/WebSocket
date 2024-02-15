import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;

public class FileMonitor {
    private static MyWebSocketServer server;
    private static String logFilePath = "C:\\Users\\ebrahim nouri\\Desktop\\json.js"; // Specify the path to your log file

    public static void main(String[] args) throws IOException, InterruptedException {
        // Start WebSocket server
        server = new MyWebSocketServer(8080);
        server.start();

        // Monitor the log file for changes
        Path logPath = Paths.get(logFilePath);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        logPath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        // Your file monitoring code
        while (true) {
            Thread.sleep(10000);
            WatchKey key;
            try {
                key = watchService.take(); // Wait for a file modification event
            } catch (InterruptedException e) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // Handle file modification event
                Path modifiedFile = (Path) event.context();
                if (modifiedFile.equals(logPath.getFileName())) {
                    System.out.println("Log file modified: " + modifiedFile);
                    broadcast(readLogFile());
                }
            }

            // Reset the key
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    // Read the contents of the log file
    private static String readLogFile() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    // Call this method when you want to broadcast a message to all WebSocket clients
    private static void broadcast(String message) {
        server.broadcast(message);
    }
}
