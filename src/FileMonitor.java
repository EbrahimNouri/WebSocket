import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.*;

public class FileMonitor {
    private static MyWebSocketServer server;
    private static String logFilePath = "C:\\json.js";

    public static void main(String[] args) throws IOException {
        // Start WebSocket server
        server = new MyWebSocketServer(new InetSocketAddress(8080));
        server.start();
        System.out.println("WebSocket server started on port 8080");

        // Monitor the log file for changes
        Path logPath = Paths.get(logFilePath);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        logPath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        System.out.println("File monitoring started");

        // Your file monitoring code
        while (true) {
            WatchKey key;
            try {
                key = watchService.take(); // Wait for a file modification event
            } catch (InterruptedException e) {
                e.printStackTrace();
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
                    String logContent = readLogFile();
                    if (logContent != null) {
                        server.broadcast(logContent);
                    } else {
                        System.err.println("Failed to read log file content");
                    }
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
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
