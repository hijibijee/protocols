import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPClient extends Thread {
    private final int PORT;
    private final InetAddress ADDRESS;
    private final DatagramSocket serverSocket;
    private Thread listenerThread;

    UDPClient(InetAddress address, int port, String name) throws SocketException {
        this.ADDRESS = address;
        this.PORT = port;
        setName(name);
        serverSocket = new DatagramSocket(PORT, ADDRESS);
        listenerThread = new Thread(() -> {
            while (!this.isInterrupted()) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    serverSocket.receive(packet);
                    System.out.println(getName() + " received: " + new String(packet.getData(), 0, packet.getLength()));
                } catch (IOException e) {
                    if (!(e instanceof SocketException && "Socket closed".equals(e.getMessage()))) {
                        System.out.println(getName() + " - Error on receive");
                    }
                }
            }
        });
    }

    @Override
    public void run() {
        listenerThread.start();
    }

    public void close() {
        serverSocket.close();
        System.out.println(getName() + " closed");
        listenerThread.interrupt();
        this.interrupt();
    }
}
