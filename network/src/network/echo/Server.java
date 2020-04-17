package network.echo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8189);
             Socket socket = serverSocket.accept()) {
            System.out.println("We have a connection with a client");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            while (true) {
                String b = in.readUTF();
                out.writeUTF("echo " + b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
