import java.net.*;
import java.io.*;

public class ChecksumClient {

    static int find_checksum(String msg) {
        int sum = 0;
        for (int i = 0; i < msg.length(); i++)
            sum += msg.charAt(i);
        return sum % 256;
    }

    public static void main(String args[]) throws Exception {

        Socket s = new Socket("localhost", 5000);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(s.getInputStream()));

        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

        String[] testMessages = {
                "Hello World",
                "Checksum Test",
                "Network Protocol",
                "Data Transmission",
                "Error Detection"
        };

        int[] testCases = { 1, 2, 3, 4, 1 };

        for (int i = 0; i < testMessages.length; i++) {
            String msg = testMessages[i];
            int check = find_checksum(msg);
            int choice = testCases[i];

            System.out.println("\n--- Test Case " + (i + 1) + " ---");
            System.out.println("Original Message: " + msg);
            System.out.println("Original Checksum: " + check);
            System.out.println("Case Selection: " + choice);

            switch (choice) {
                case 1:
                    System.out.println("Test: Normal transmission (no error)");
                    break;

                case 2:
                    msg = msg + "X";
                    System.out.println("Test: Message with error");
                    break;

                case 3:
                    check = check + 10;
                    System.out.println("Test: Checksum error");
                    break;

                case 4:
                    msg = msg + "X";
                    check = check + 10;
                    System.out.println("Test: Both message and checksum error");
                    break;
            }

            out.println(msg);
            out.println(check);

            String response = in.readLine();
            System.out.println("Modified Message: " + msg);
            System.out.println("Modified Checksum: " + check);
            System.out.println("Server Response: " + response);
            if (response.equals("ACK"))
                System.out.println("Result: Transmission Successful ✓");
            else
                System.out.println("Result: Error Detected - Please Resend ✗");
        }

        System.out.println("\n--- All test cases completed ---");
        s.close();
    }
}