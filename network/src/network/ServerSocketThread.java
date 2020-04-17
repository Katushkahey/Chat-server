package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketThread extends Thread {

    private ServerSocketThreadListener listener;
    private int port;
    private int timeout;

    public ServerSocketThread(ServerSocketThreadListener listener, String name, int port, int timeout) {
        super(name);
        this.port = port;
        this.timeout = timeout;
        this.listener = listener;
        start();
    }

    @Override
    public void run() {
        listener.serverSocketThreadStart(this);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(timeout);
            listener.serverSocketTreadCreate(this, serverSocket);
            while (!isInterrupted()) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    listener.serverSocketThreadAcceptTimeout(this, serverSocket);
                    continue;
                }
                listener.serverSocketAccepted(this, socket);
            }
        } catch (IOException e) {
            listener.serverSocketThreadException(this, e);
        } finally {
            listener.serverSocketThreadStop(this);
        }
    }
}
