import java.net.*;
import java.io.*;

public class ChecksumServer {

    static int find_checksum(String msg) {
        int sum = 0;

        for (int i = 0; i < msg.length(); i++) {
            sum += msg.charAt(i);
        }

        return sum % 256;
    }

    public static void main(String args[]) throws Exception {

        ServerSocket ss = new ServerSocket(5000);

        System.out.println("******* Server Started - CHECKSUM ******");
        Socket s = ss.accept();
        System.out.println("Client Connected");

        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

        while (true) {

            String msg = in.readLine();
            if (msg == null || msg.equals("exit")) {
                System.out.println("Exit & Client Disconnected");
                break;
            }

            int rec_Checksum = Integer.parseInt(in.readLine());
            int calc_Checksum = find_checksum(msg);

            System.out.println("\n Message : " + msg);
            System.out.println("Received Checksum : " + rec_Checksum);
            System.out.println("Calculated Checksum : " + calc_Checksum);

            if (rec_Checksum == calc_Checksum) {
                System.out.println("Status:    No Error , Transmission Success");
                out.println("ACK");
            } else {
                System.out.println("Status : Error Detected");
                out.println("NACK");
            }
        }

        s.close();
        ss.close();
    }
}