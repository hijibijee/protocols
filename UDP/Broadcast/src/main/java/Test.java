import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Run this program on multiple machines in your network
 * Change the InetAddress for client according to each machine
 * Set different name for the client in each machine for fun
 * See the effect of broadcasting
 */
public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        UDPClient client = new UDPClient(InetAddress.getByName("192.168.0.105"), 8090, "CLIENT-1"); // run ipconfig and copy the ipv4 address of the machine
        UDPBroadcaster broadcaster = new UDPBroadcaster(InetAddress.getByName("255.255.255.255")); // 255.255.255.255 is the broadcast address for local network
        Scanner scanner = new Scanner(System.in);

        Thread broadcasterThread = new Thread(() -> {
            System.out.println("Just type in your message and press enter to broadcast it to all devices connected to the network and listening to port 8090");
            System.out.println("Type STOP to stop broadcasting");
            while (true) {
                String message = scanner.nextLine();
                if (message.equals("STOP")) {
                    break;
                }
                try {
                    broadcaster.broadcast(message, 8090);
                } catch (IOException e) {
                    System.out.println("Failed to broadcast message: " + message);
                }
            }
            broadcaster.close();
            client.close();
        });

        while (true) {
            System.out.println("Choose your role: broadcaster/listener/both");
            String answer = scanner.nextLine();
            boolean wrongAnswer = false;

            switch (answer) {
                case "both" -> {
                    broadcasterThread.start();
                    client.start();
                }
                case "listener" -> {
                    System.out.println("Type STOP to close program");
                    client.start();
                    while (true) {
                        String stop = scanner.nextLine();
                        if (stop.equals("STOP")) {
                            break;
                        }
                    }
                    client.close();
                }
                case "broadcaster" -> broadcasterThread.start();
                default -> wrongAnswer = true;
            }

            if (!wrongAnswer) break;
        }
    }

    public static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
            = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                .map(a -> a.getBroadcast())
                .filter(Objects::nonNull)
                .forEach(broadcastList::add);
        }
        return broadcastList;
    }
}
