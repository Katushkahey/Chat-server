package network;

import java.net.Socket;

public interface SocketThreadListener {

    void socketThreadStart(SocketThread st, Socket socket);
    void socketThreadReady(SocketThread st, Socket socket);
    void receiveString(SocketThread st, Socket socket, String str);
    void socketThreadStop(SocketThread st);
    void socketThreadException(SocketThread st, Exception e);
}
