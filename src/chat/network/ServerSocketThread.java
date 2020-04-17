package chat.network;

public class ServerSocketThread extends Thread {
    public int port;

    public ServerSocketThread(String name, int port) {
        super(name);
        this.port = port;
        start();
    }

    @Override
    public void run() {
        while (isInterrupted()) {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
