import java.io.*;
import java.net.*;
import java.util.*;

public class GBNServer {
    static final int WINDOW_SIZE = 5;
    static final int LOSS_PROBABILITY = 30;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(6500)) {
            System.out.println("Server listening on port 6500...");
            try (Socket client = server.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                System.out.println("TCP Connection Established.");
                Random rand = new Random();
                int totalFrames = Integer.parseInt(in.readLine());
                int expectedFrame = 1;
                while (expectedFrame <= totalFrames) {
                    String received = in.readLine();
                    if (received == null)
                        continue;
                    int frameNum = Integer.parseInt(received);
                    if (frameNum == expectedFrame) {
                        if (rand.nextInt(100) < LOSS_PROBABILITY) {
                            out.println("TimeOut");
                            System.out.println("Frame " + frameNum + " lost! Resend from Frame " + expectedFrame);
                        } else {
                            System.out.println("Frame " + frameNum + " Received");
                            out.println(frameNum);
                            expectedFrame++;
                        }
                    } else {
                        System.out.println("Frame " + frameNum + " discarded (Expecting Frame " + expectedFrame + ")");
                    }
                }
                out.println("END");
                System.out.println("All frames received. Closing server connection.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}