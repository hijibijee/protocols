import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UnicastServer extends Thread {
    private static final int MAX_PACKET_SIZE_LIMIT = 65535;
    private static final int UDP_HEADER_SIZE = 8;

    private final int SERVER_PORT;
    private final int BUFFER_SIZE;
    private final byte [] buffer;

    private DatagramSocket socket;

    UnicastServer(int port, int bufferLength, String name) {
        SERVER_PORT = port;
        if (bufferLength > MAX_PACKET_SIZE_LIMIT - UDP_HEADER_SIZE) {
            throw new RuntimeException("UDP packet size limit is " + MAX_PACKET_SIZE_LIMIT + " bytes");
        }
        BUFFER_SIZE = bufferLength;
        buffer = new byte[BUFFER_SIZE];
        this.setName(name);
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(SERVER_PORT);
            System.out.println(getName() + " started on port: " + socket.getLocalPort());

            while (!this.isInterrupted()) {
                DatagramPacket receivedPacket = new DatagramPacket(buffer, BUFFER_SIZE);

                socket.receive(receivedPacket);

                InetAddress senderAddress = receivedPacket.getAddress();
                int senderPort = receivedPacket.getPort();
                String data = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                System.out.println("[" + getName() + "][" + senderPort + "] received: " + data);

                byte [] acknowledgement = (getName() + " - OK! - " + data).getBytes();
                DatagramPacket packetToSend = new DatagramPacket(acknowledgement, acknowledgement.length, senderAddress, senderPort);
                socket.send(packetToSend);
            }
        } catch (IOException e) {
            if (!(e instanceof SocketException && "Socket closed".equals(e.getMessage()))) {
                throw new RuntimeException(e);
            }
        }
    }

    public void shutdown() {
        if (socket != null) {
            this.socket.close();
            System.out.println(getName() + " shutdown");
            this.interrupt();
        }
    }
}
