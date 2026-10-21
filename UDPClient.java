import java.net.*;
import java.util.Scanner;

public class UDPClient {

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("localhost");
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Client: ");
            String clientMsg = sc.nextLine();

            byte[] sendData = clientMsg.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(
                sendData,
                sendData.length,
                serverAddress,
                5000
            );

            socket.send(sendPacket);

            if (clientMsg.equalsIgnoreCase("exit")) {
                break;
            }

            byte[] receiveData = new byte[1024];

            DatagramPacket receivePacket = new DatagramPacket(
                receiveData,
                receiveData.length
            );

            socket.receive(receivePacket);

            String serverMsg = new String(
                receivePacket.getData(),
                0,
                receivePacket.getLength()
            );

            System.out.println("Server: " + serverMsg);

            if (serverMsg.equalsIgnoreCase("exit")) {
                break;
            }
        }

        sc.close();
        socket.close();
    }
}