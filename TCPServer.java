import java.io.*;
import java.net.*;
import java.util.Scanner;
public class TCPServer {
    public static void main(String[] args){
        try{
            ServerSocket serverSocket=new ServerSocket(5000);
            System.out.println("Server is waiting for client.");

            Socket socket=serverSocket.accept();
            System.out.println("Client connected.");

            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
            Scanner sc=new Scanner(System.in);
            String clientMsg,serverMsg;
            while(true){
                clientMsg=in.readLine();
                if (clientMsg==null || clientMsg.equalsIgnoreCase("Exit")){
                    System.out.println("Client disconnected");
                    break;
                } 
                System.out.println("Client:"+clientMsg);
                System.out.println("Enter the msg: ");
                serverMsg=sc.nextLine();
                out.println(serverMsg);
                if (serverMsg.equalsIgnoreCase("Exit")){
                    break;
                }
            }
            socket.close();
            serverSocket.close();
            sc.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
