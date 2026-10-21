
import java.io.*;
import java.net.*;

public class FTPServer {
    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server Started");
        System.out.println("Waiting for client");

        Socket socket = serverSocket.accept();
        System.out.println("Client connected");

        InputStream in = socket.getInputStream();

        StringBuilder name = new StringBuilder();
        int ch;

        while ((ch = in.read()) != -1 && ch != '\n') {
            if (ch != '\r') {
                name.append((char) ch);
            }
        }

        String filename = name.toString();

        FileOutputStream fos = new FileOutputStream("Received_" + filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        byte[] buffer = new byte[1024];
        int bytesRead;

        System.out.println("File Content");

        while ((bytesRead = in.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
            System.out.println(
                    new String(buffer, 0, bytesRead));
        }

        bos.close();
        fos.close();
        socket.close();
        serverSocket.close();

        System.out.println("File Received successfully");
        System.out.println("Saved as: Received_" + filename);
    }
}
