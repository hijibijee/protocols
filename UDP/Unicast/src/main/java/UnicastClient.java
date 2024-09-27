import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.net.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class UnicastClient extends Thread {
    private final int PORT;
    private DatagramSocket socket;
    private byte [] buffer;
    private final int bufferSize;
    private Thread messageSendThread;
    private Thread listenerThread;

    Queue<Message> pendingMessages = new ArrayDeque<>();

    UnicastClient(int port, int bufferSize, String name) {
        PORT = port;
        this.bufferSize = bufferSize;
        buffer = new byte[bufferSize];
        this.setName(name);
    }

    public void run() {
        try {
            socket = new DatagramSocket(PORT);
            System.out.println(getName() + " started on port: " + socket.getLocalPort());

            messageSendThread = new Thread(() -> {
                while (!this.isInterrupted()) {
                    Message message = pendingMessages.poll();
                    if (message != null) {
                        executeMessageSend(message.getMessage(), message.getServerPort());
                    }
                }
            });

            listenerThread = new Thread(() -> {
               while (!this.isInterrupted()) {
                    listenToNextServerResponse();
               }
            });

            messageSendThread.start();
            listenerThread.start();
        } catch (SocketException e) {
            System.out.println("Failed to start client at PORT: " + PORT);
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message, int serverPort) {
        pendingMessages.add(Message.builder()
            .message(message)
            .serverPort(serverPort)
            .build());
    }

    private void executeMessageSend(String message, int serverPort) {
        try {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            byte [] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, serverPort);
            socket.send(packet);
        } catch (IOException e) {
            if (!(e instanceof SocketException && "Socket closed".equals(e.getMessage()))) {
                throw new RuntimeException(e);
            }
        }
    }

    private void listenToNextServerResponse() {
        try {
            Arrays.fill(buffer, (byte) 0);
            DatagramPacket receive = new DatagramPacket(buffer, bufferSize);
            socket.receive(receive);
            System.out.println("[" + getName() + "][" + receive.getPort() + "] received: " + new String(receive.getData(), 0, receive.getLength()));
        } catch (IOException e) {
            if (!(e instanceof SocketException && "Socket closed".equals(e.getMessage()))) {
                throw new RuntimeException(e);
            }
        }
    }

    public void shutdown() {
        if (socket != null) {
            this.socket.close();
            System.out.println(this.getName() + " shutdown");
            messageSendThread.interrupt();
            listenerThread.interrupt();
            this.interrupt();
        }
    }

    @Builder
    @Data
    static class Message {
        private String message;
        private int serverPort;
    }
}
