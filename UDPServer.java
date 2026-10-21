import java.net.*;
import java.util.Scanner;

public class UDPServer {

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(5000);
        Scanner sc = new Scanner(System.in);

        System.out.println("Server Started...");

        while (true) {
            byte[] receiveData = new byte[1024];

            DatagramPacket receivePacket =
                new DatagramPacket(receiveData, receiveData.length);

            socket.receive(receivePacket);

            String clientMsg = new String(
                receivePacket.getData(),
                0,
                receivePacket.getLength()
            );

            System.out.println("Client: " + clientMsg);

            if (clientMsg.equalsIgnoreCase("exit")) {
                break;
            }

            System.out.print("Server: ");
            String serverMsg = sc.nextLine();

            byte[] sendData = serverMsg.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(
                sendData,
                sendData.length,
                receivePacket.getAddress(),
                receivePacket.getPort()
            );

            socket.send(sendPacket);

            if (serverMsg.equalsIgnoreCase("exit")) {
                break;
            }
        }

        sc.close();
        socket.close();
    }
}