package chat;

import chat.network.ServerSocketThread;

class ChatServer {

    private ServerSocketThread serverSocketThread;

    void start(int port) {
        if (serverSocketThread != null || serverSocketThread.isAlive()) {
            System.out.println("Server is already alive!");
        } else {
            serverSocketThread = new ServerSocketThread("Chat server", port);
        }
    }

    void stop() {
        if (serverSocketThread != null || serverSocketThread.isAlive()) {
            serverSocketThread.interrupt();
        } else {
            System.out.println("Server isn't running");
        }
    }
}
