import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import java.util.*;

public class MyWebSocketServer extends WebSocketServer {
    private Set<WebSocket> connections;

    public MyWebSocketServer(InetSocketAddress address) {
        super(address);
        connections = new HashSet<>();
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
        String fileContent = FileMonitor.readLogFile();
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

}
