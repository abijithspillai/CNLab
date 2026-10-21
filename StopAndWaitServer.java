import java.io.*;
import java.net.*;
import java.util.*;

public class StopAndWaitServer {
    static final int LOSS_PROBABILITY = 30;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(6500)) {
            System.out.println("Server started...");
            Socket client = server.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            Random rand = new Random();
            int totalFrames = Integer.parseInt(in.readLine());
            int expected = 1;
            while (expected <= totalFrames) {
                int frame = Integer.parseInt(in.readLine());
                if (rand.nextInt(100) < LOSS_PROBABILITY) {
                    System.out.println("Frame " + frame + " Lost");
                    out.println("TimeOut");
                } else {
                    System.out.println("Frame " + frame + " received");
                    out.println("ACK");
                    expected++;
                }
            }
            System.out.println("Transmission Completed");
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
