# WebSocket
Real-time file monitoring and broadcasting system using WebSocket technology in Java.

# Real-time File Monitor and Broadcaster

This project is a Java application that monitors changes to a specific file and broadcasts those changes in real-time using WebSocket technology.

## Features

- Monitors changes to a specified file.
- Utilizes Java's WatchService API for efficient file monitoring.
- Broadcasts file changes to connected WebSocket clients.
- Simple and lightweight implementation.

## Usage

1. Clone or download the repository.
2. Compile the Java source files (`FileMonitor.java` and `MyWebSocketServer.java`).
3. Run the compiled `FileMonitor` class to start the WebSocket server and file monitoring.

## Dependencies

- Java Development Kit (JDK) 8 or higher

## Getting Started

To get started with this project:

1. Clone the repository: `git clone https://github.com/your-username/real-time-file-monitor.git`
2. Compile the Java source files: `javac FileMonitor.java MyWebSocketServer.java`
3. Run the compiled `FileMonitor` class: `java FileMonitor`

## Configuration

You can configure the file to be monitored by modifying the `logFilePath` variable in the `FileMonitor.java` file.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
