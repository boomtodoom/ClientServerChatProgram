import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private Scanner scan;

    public Client(String ipAddress, int port) throws IOException {
        this.clientSocket = new Socket(InetAddress.getByName(ipAddress),port);
        this.scan = new Scanner(System.in);
    }

    void run() throws IOException {
        String message=null;
        Thread clientThread = new Thread(){
            public void run() {
                try {
                    System.out.println("Client thread started");
                    Scanner in = new Scanner(clientSocket.getInputStream());
                    String servMessage;
                    while((servMessage=in.nextLine())!=null) {
                        System.out.println(servMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        clientThread.start();
        while(true){
            message = scan.nextLine();
            PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(),true);
            out.println(message);
            out.flush();
        }
    }
    public static void main(String[] args) throws IOException {
        Client client = new Client(args[0],Integer.valueOf(args[1]));
        client.run();

    }
}
