package server.core;

import common.Library;
import network.SocketThread;
import network.SocketThreadListener;

import java.net.Socket;

 class ClientThread extends SocketThread {

    private String nickname;
    private boolean isReconnected;
    private boolean isAuthorized;

    ClientThread(SocketThreadListener listener, String name, Socket socket) {
        super(listener, name, socket);
    }

    boolean isAuthorized() {
        return isAuthorized;
    }

    void authAccept(String nickname) {
        isAuthorized = true;
        this.nickname = nickname;
        sendMessage(Library.getAuthAccept(nickname));
    }

    void authFailed() {
        sendMessage(Library.getAuthDenied());
        close();
    }

    void msgFormatError(String msg) {
        sendMessage(Library.getMsgFormatError(msg));
        close();
    }

    String getNickname() {
        return nickname;
    }

    void reconnect() {
        isReconnected = true;
        close();
    }

    boolean isReconnected() {
        return isReconnected;
    }
}
