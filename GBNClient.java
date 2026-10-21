import java.io.*;
import java.net.*;
import java.util.*;

public class GBNClient {
    static final int WINDOW_SIZE = 5;

    public static void main(String[] args) {
        try (Socket sock = new Socket("127.0.0.1", 6500);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                Scanner sc = new Scanner(System.in)) {
            System.out.println("TCP Connection Established.");
            System.out.print("Enter total number of frames: ");
            int totalFrames = sc.nextInt();
            out.println(totalFrames);
            int base = 1;
            int nextFrame = 1;
            while (true) {
                while (nextFrame < base + WINDOW_SIZE && nextFrame <= totalFrames) {
                    System.out.println("Sending Frame " + nextFrame);
                    out.println(nextFrame);
                    nextFrame++;
                }
                String ack = in.readLine();
                if (ack == null)
                    continue;
                if (ack.equalsIgnoreCase("END"))
                    break;
                if (ack.equalsIgnoreCase("TimeOut")) {
                    System.out.println("Timeout! Resending frames from Frame " + base);
                    nextFrame = base;
                } else {
                    int ackNum = Integer.parseInt(ack);
                    System.out.println("Frame " + ackNum + " Acknowledged");
                    base++;
                }
            }
            System.out.println("All frames sent and acknowledged. Closing client connection.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}