import java.io.*;
import java.net.*;
import java.util.Scanner;

public class FTPClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5000);
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter filename:");
        String filename = sc.nextLine();
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File not found");
            socket.close();
            return;
        }
        PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
        pw.println(file.getName());
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);

        OutputStream out = socket.getOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = bis.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
        bis.close();
        fis.close();
        socket.close();
        System.out.println("File Sent successfully");
    }
}