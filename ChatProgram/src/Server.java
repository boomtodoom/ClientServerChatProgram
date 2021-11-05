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
    if (!ipAddress.isEmpty() && ipAddress != null) {
      this.servSock = new ServerSocket(port, 1, InetAddress.getByName(ipAddress));
      System.out.println(servSock.getInetAddress());
      System.out.println(servSock.getLocalPort());
      UPnP.openPortTCP(port);
    } else {
      this.servSock = new ServerSocket(0, 1, InetAddress.getLocalHost());
    }
  }

  public static void main(String[] args) throws IOException {
    System.out.println("Inp ip = " + args[0] + " Inp Port = " + args[1]);
    Server serv = new Server(args[0], Integer.valueOf(args[1]));

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
            PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String username;
            while (true) {
              pw.println("Please enter your username");
              username = in.readLine();
              username = username.replace(" ", "_");
              if (names.contains(username)) {
                pw.println("This username is currently in use");
              } else {
                names.add(username);
                writers.add(pw);
                for (int i = 0; i < writers.size(); i++) {
                  writers.get(i).println("Welcome to the chatroom " + username);
                }
                pw.println(
                    "The currently implemented server commands are: \n/w username message - sends a message to a specified user\n/f fileLocation - sends a file to the server");
                break;
              }
            }
            String message;
            while ((message = in.readLine()) != null) {
              System.out.println(message);
              if (message.startsWith("/w ")) {
                String[] splitMsg = message.split("\s*\s");
                System.out.println(splitMsg[0] + " " + splitMsg[1]);
                if (names.contains(splitMsg[1])) {
                  int nameIndex = names.indexOf(splitMsg[1]);
                  String msgTrim =
                      username + " ~ " + message.substring(message.indexOf(splitMsg[2]));
                  writers.get(nameIndex).println(msgTrim);
                  pw.println(msgTrim);
                } else {
                  pw.println("That is not a valid username");
                }
              } else {
                for (int i = 0; i < writers.size(); i++) {
                  writers.get(i).println(username + ": " + message);
                }
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
