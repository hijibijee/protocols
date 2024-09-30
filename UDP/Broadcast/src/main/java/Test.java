import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        UDPClient client1 = new UDPClient(InetAddress.getByName("192.168.0.105"), 8090, "CLIENT-1"); // run ipconfig and copy the ipv4 address of the machine

        client1.broadcast("Hi from client 1", InetAddress.getByName("255.255.255.255"), 8090); // 255.255.255.255 is the broadcast channel for local network
        client1.broadcast("Second message from client 1", InetAddress.getByName("255.255.255.255"), 8090);
        client1.broadcast("Third message from client 1", InetAddress.getByName("255.255.255.255"), 8090);
        client1.broadcast("Fourth message from client 1", InetAddress.getByName("255.255.255.255"), 8090);

        Thread.sleep(10000);
        client1.close();
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
