import com.dosse.upnp.UPnP;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private ServerSocket servSock;
    Thread servThread;
    ArrayList<PrintWriter> writers = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();

    public Server(String ipAddress, int port) throws IOException {
        if(!ipAddress.isEmpty()&&ipAddress!=null){
            this.servSock = new ServerSocket(port,1,InetAddress.getByName(ipAddress));
            System.out.println(servSock.getInetAddress());
            System.out.println(servSock.getLocalPort());
            UPnP.openPortTCP(port);
        } else {
            this.servSock = new ServerSocket(0,1,InetAddress.getLocalHost());
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Inp ip = "+args[0]+" Inp Port = "+args[1]);
        Server serv = new Server(args[0],Integer.valueOf(args[1]));

        System.out.println("SERVER STARTED");
        serv.run();

    }

    public void run() throws IOException {
        while (true) {
            Socket client = this.servSock.accept();
            Thread servThread = new Thread() {
                public void run() {
                    try {
                        System.out.println("Client connected");
                        PrintWriter pw =new PrintWriter(client.getOutputStream(),true);
                        writers.add(pw);
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String username;
                        while(true) {
                            pw.println("Please enter your username");
                            username = in.readLine();
                            if(names.contains(username)){
                                pw.println("This username is currently in use");
                            } else {
                                pw.println("Welcome to the chatroom "+username);
                                names.add(username);
                                break;
                            }
                        }
                        String message;
                        while ((message = in.readLine()) != null) {
                            System.out.println(message);
                            for(int i=0;i<writers.size();i++){
                                writers.get(i).println(username+" : "+message);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            servThread.start();
        }
    }
}
