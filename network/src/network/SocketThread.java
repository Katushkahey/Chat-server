package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketThread extends Thread {

    private final SocketThreadListener LISTENER;
    private final Socket SOCKET;
    private DataOutputStream out;

    public SocketThread(SocketThreadListener listener, String name, Socket socket){
        super(name);
        LISTENER = listener;
        SOCKET = socket;
        this.start();
    }

    @Override
    public void run() {
        try {
            LISTENER.socketThreadStart(this, SOCKET);
            DataInputStream in = new DataInputStream(SOCKET.getInputStream());
            out = new DataOutputStream(SOCKET.getOutputStream());
            LISTENER.socketThreadReady(this,SOCKET);
            while (!isInterrupted()) {
                String msg = in.readUTF();
                LISTENER.receiveString(this, SOCKET, msg);
            }
        }catch (IOException e) {
            LISTENER.socketThreadException(this, e);
        }finally {
            try {
                SOCKET.close();
            } catch (IOException e) {
                LISTENER.socketThreadException(this, e);
            }
            LISTENER.socketThreadStop(this);
        }
    }

    public synchronized boolean sendMessage(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
            return true;
        }catch (IOException e) {
            LISTENER.socketThreadException(this, e);
            close();
            return false;
        }
    }

    public synchronized void close() {
        interrupt();
        try {
            SOCKET.close();
        } catch (IOException e) {
            LISTENER.socketThreadException(this, e);
        }
    }
}
