package network;

import java.net.ServerSocket;
import java.net.Socket;

public interface ServerSocketThreadListener {

    void serverSocketThreadStart(ServerSocketThread sst);
    void serverSocketThreadStop(ServerSocketThread sst);
    void serverSocketTreadCreate(ServerSocketThread sst, ServerSocket server);
    void serverSocketThreadAcceptTimeout(ServerSocketThread sst, ServerSocket server);
    void serverSocketAccepted(ServerSocketThread sst, Socket socket);
    void serverSocketThreadException(ServerSocketThread sst, Exception e);

}
