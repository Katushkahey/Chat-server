package server.core;

import common.Library;
import network.ServerSocketThread;
import network.ServerSocketThreadListener;
import network.SocketThread;
import network.SocketThreadListener;
import network.echo.Client;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer implements ServerSocketThreadListener, SocketThreadListener {

    private ServerSocketThread serverSocketThread;
    public static Vector<SocketThread> clients = new Vector<>();
    private final ChatServerListener LISTENER;

    public ChatServer(ChatServerListener listener) {
        LISTENER = listener;
    }

    public void start(int port) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            System.out.println("Server is already alive!");
        } else {
            serverSocketThread = new ServerSocketThread(this,"Chat server", port, 1000);
        }
    }

    public void stop() {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            serverSocketThread.interrupt();
        } else {
            System.out.println("Server isn't running");
        }
    }

    private void putLog (String msg) {
        LISTENER.chatServerMessage(msg);
    }

    /**
     * ServerSocketThread methods
     **/

    @Override
    public void serverSocketThreadStart(ServerSocketThread sst) {
        SqlClient.connect();
        putLog("ServerSocketThread started");
    }

    @Override
    public void serverSocketThreadStop(ServerSocketThread sst) {
        SqlClient.disconnect();
        putLog("ServerSocketThread stopped");
    }

    @Override
    public void serverSocketTreadCreate(ServerSocketThread sst, ServerSocket server) {
        putLog("ServerSocketThread created");
    }

    @Override
    public void serverSocketThreadAcceptTimeout(ServerSocketThread sst, ServerSocket server) {

    }

    @Override
    public void serverSocketAccepted(ServerSocketThread sst, Socket socket) {
        String name = "SocketThread " + socket.getInetAddress() + socket.getInetAddress();
        new ClientThread(this, name, socket);
    }

    @Override
    public void serverSocketThreadException(ServerSocketThread sst, Exception e) {
        putLog("ServerSocketThread exception");
    }

    /**
     * SocketThread methods
     **/

    @Override
    public synchronized void socketThreadStart(SocketThread st, Socket socket) {
        putLog("SocketThread start");
    }

    @Override
    public synchronized void socketThreadReady(SocketThread st, Socket socket) {
        clients.add(st);
    }

    @Override
    public synchronized void receiveString(SocketThread st, Socket socket, String str) {
        ClientThread client = (ClientThread) st;
        if (client.isAuthorized()) {
            handleAuthorizedMessage(client, str);
        } else {
            handleNonAuthorizedMessage(client, str);
        }
    }

    @Override
    public synchronized void socketThreadStop(SocketThread st) {
        clients.remove(st);
    }

    @Override
    public synchronized void socketThreadException(SocketThread st, Exception e) {
        putLog("SocketThread exception");
    }

    private void sendToAuthorizedClients(String str) {
        for (int i = 0; i < clients.size() ; i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) {
                continue;
            }
            client.sendMessage(str);
        }
    }

    private void handleAuthorizedMessage(ClientThread ct, String str) {
        sendToAuthorizedClients(str);
    }

    private void handleNonAuthorizedMessage(ClientThread ct, String str) {
        String[] arr = str.split(Library.DELIMITER);
        if (arr.length != 3 || !arr[0].equals(Library.AUTH_REQUEST)) {
            ct.msgFormatError(str);
            return;
        }
        String login = arr[1];
        String password = arr[2];
        String nickname = SqlClient.getNickname(login, password);
        if (nickname == null) {
            putLog("Invalid login attempt");
            ct.authFailed();
            return;
        }
        ct.authAccept(nickname);
        sendToAuthorizedClients(Library.getTypeBroadcast("Server", nickname + "connected!"));
    }
}
