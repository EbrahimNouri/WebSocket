import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyWebSocketServer extends WebSocketServer {
    private Timer timer;
    private List<WebSocket> clients;

    public MyWebSocketServer(int port) {
        super(new InetSocketAddress(port));
        clients = new ArrayList<>();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
        clients.add(conn); // Add the newly connected client to the list
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress());
        clients.remove(conn); // Remove the disconnected client from the list
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message received from " + conn.getRemoteSocketAddress() + ": " + message);
        // Handle the received message here
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error: ");
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started");
        // Start the timer to send updates every second
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendTimeUpdate();
            }
        }, 0, 1000);
    }

    @Override
    public void broadcast(String message) {
        // Iterate over all connected clients and send the message
        for (WebSocket client : clients) {
            client.send(message);
        }
    }

    private void sendTimeUpdate() {
        String currentTime = String.valueOf(System.currentTimeMillis());
        broadcast("Current time: " + currentTime);
    }


    public static void main(String[] args) {
        int port = 8080; // Change this to the desired port number
        MyWebSocketServer server = new MyWebSocketServer(port);
        server.start();
        System.out.println("WebSocket server started on port " + port);
    }
}
