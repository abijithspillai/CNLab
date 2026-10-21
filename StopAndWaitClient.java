import java.io.*;
import java.net.*;
import java.util.*;

public class StopAndWaitClient {
    public static void main(String[] args) {
        try (Socket sock = new Socket("127.0.0.1", 6500);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                Scanner sc = new Scanner(System.in)) {
            System.out.println("TCP Connection Established");
            System.out.println("Enter total number of frames:");
            int totalFrames = sc.nextInt();
            out.println(totalFrames);
            int frame = 1;
            while (frame <= totalFrames) {
                System.out.println("Sending Frame " + frame);
                out.println(frame);
                String response = in.readLine();
                if (response.equalsIgnoreCase("ACK")) {
                    System.out.println("ACK Received for frame " + frame);
                    frame++;
                } else if (response.equalsIgnoreCase("TimeOut")) {
                    System.out.println("Timeout! Resending frame " + frame);
                }
            }
            System.out.println("All frames sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
