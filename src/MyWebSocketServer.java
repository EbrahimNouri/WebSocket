import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.util.*;

public class MyWebSocketServer extends WebSocketServer {
  private final Set<WebSocket> connections;
  private static final String logFilePath = "C:\\json.js";
  private static String previousLogContent = "";

  public MyWebSocketServer(InetSocketAddress address) {
    super(address);
    connections = new HashSet<>();
  }

  public static void main(String[] args) throws IOException {
    // Start WebSocket server
    MyWebSocketServer server = new MyWebSocketServer(new InetSocketAddress(8080));
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

      // Process only the latest modification event
      for (WatchEvent<?> event : key.pollEvents()) {
        WatchEvent.Kind<?> kind = event.kind();
        if (kind == StandardWatchEventKinds.OVERFLOW) {
          continue;
        }

        // Handle file modification event
        Path modifiedFile = (Path) event.context();
        if (modifiedFile.equals(logPath.getFileName())) {
          System.out.println("Log file modified: " + modifiedFile);
        }
      }

      String currentLogContent = readLogFile();
      if (currentLogContent != null && !currentLogContent.equals(previousLogContent)) {
        server.broadcast(currentLogContent);
        previousLogContent = currentLogContent;
      } else {
        System.out.println("No change in log file content. Skipping broadcast.");
      }

      // Reset the key
      boolean valid = key.reset();
      if (!valid) {
        break;
      }
    }
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    System.out.println("New connection: " + conn.getRemoteSocketAddress());
    connections.add(conn);
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    System.out.println("Connection closed: " + conn.getRemoteSocketAddress());
    connections.remove(conn);
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    System.out.println("Message received from " + conn.getRemoteSocketAddress() + ": " + message);
    String fileContent = readLogFile();
    assert fileContent != null;
    String[] lines = fileContent.split("\\r?\\n");
    List<String> result = new ArrayList<>();
    for (String line : lines) {
      if (line.contains(message))
        result.add(line);
    }
    broadcast(result.toString());
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    System.err.println("Error: ");
    ex.printStackTrace();
  }

  @Override
  public void onStart() {
    System.out.println("WebSocket server started");
  }

  public void broadcast(String message) {
    for (WebSocket client : connections) {
      client.send(message);
    }
  }

  // Read the contents of the log file
  protected static String readLogFile() {
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
