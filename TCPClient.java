import java.io.*;
import java.net.*;
import java.util.Scanner;
public class TCPClient {
    public static void main(String[] args){
        try{
            Socket socket=new Socket("localhost",5000);

            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out=new PrintWriter(socket.getOutputStream(),true);

            Scanner sc=new Scanner(System.in);
            String clientMsg,serverMsg;

            while(true){
                System.out.println("Enter the msg: ");
                clientMsg=sc.nextLine();
                out.println(clientMsg);

                if (clientMsg.equalsIgnoreCase("Exit")){
                    break;
                }

                serverMsg=in.readLine();
                if (serverMsg==null || serverMsg.equalsIgnoreCase("Exit")){
                    System.out.println("Server disconnected");
                    break;
                } 
                System.out.println("Server:"+serverMsg);

            }
            socket.close();
            sc.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
