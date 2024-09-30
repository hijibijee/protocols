import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPBroadcaster {
    private final DatagramSocket socket;
    private final InetAddress broadcastAddress;

    UDPBroadcaster(InetAddress broadcastAddress) throws SocketException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        this.broadcastAddress = broadcastAddress;
    }

    public void broadcast(String message, int port) throws IOException {
        byte [] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, port);
        socket.send(packet);
    }

    public void close() {
        socket.close();
    }
}
