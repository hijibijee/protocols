public class Test {
    public static final int SERVER1_PORT = 8090;
    public static final int SERVER2_PORT = 8091;
    public static final int CLIENT1_PORT = 9080;
    public static final int CLIENT2_PORT = 9081;
    public static final int CLIENT3_PORT = 9082;

    public static void main(String[] args) throws InterruptedException {
        UnicastServer server1 = new UnicastServer(SERVER1_PORT, 250, "SERVER-1");
        server1.start();

        UnicastServer server2 = new UnicastServer(SERVER2_PORT, 150, "SERVER-2");
        server2.start();

        UnicastClient client1 = new UnicastClient(CLIENT1_PORT, 100, "CLIENT-1");
        client1.start();
        client1.sendMessage("Hi server 1! I am CLIENT-1.", SERVER1_PORT);
        client1.sendMessage("Hi server 2! I am CLIENT-1.", SERVER2_PORT);
        client1.sendMessage("Eat your snacks.", SERVER1_PORT);
        client1.sendMessage("Maintain your diet.", SERVER2_PORT);

        UnicastClient client2 = new UnicastClient(CLIENT2_PORT, 100, "CLIENT-2");
        client2.start();
        client2.sendMessage("Hi server 2! I am CLIENT-2.", SERVER2_PORT);
        client2.sendMessage("I will travel around the world.", SERVER2_PORT);

        UnicastClient client3 = new UnicastClient(CLIENT3_PORT, 100, "CLIENT-3");
        client3.start();
        client3.sendMessage("Hi server 1! I am CLIENT-3.", SERVER1_PORT);
        client3.sendMessage("I like Pizza.", SERVER1_PORT);
        client3.sendMessage("Hi server 2! I am CLIENT-3.", SERVER2_PORT);
        client3.sendMessage("I like Soccer.", SERVER2_PORT);

        Thread.sleep(10000);
        server1.shutdown();
        server2.shutdown();
        client1.shutdown();
        client2.shutdown();
        client3.shutdown();
    }
}
