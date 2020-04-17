package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler {

    private static final int POS_X = 1000;
    private static final int POS_Y = 550;
    private static final int WIDTH = 200;
    private static final int HEIGHT = 100;

    private final ChatServer chatServer = new ChatServer();
    private final JButton start = new JButton("Start");
    private final JButton stop = new JButton("Stop");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServerGUI();
            }
        });
    }

    private ServerGUI() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(POS_X, POS_Y, WIDTH, HEIGHT);
        setResizable(false);
        setAlwaysOnTop(true);
        setTitle("ChatServer");
        setLayout(new GridLayout(1, 2));
        add(start);
        add(stop);
        setVisible(true);
        start.addActionListener(this);
        stop.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
            chatServer.start(8189);
        } else if (e.getSource() == stop) {
            chatServer.stop();
        } else {
            throw new RuntimeException("Unknown sourse: " + e.getSource());
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        String msg;
        StackTraceElement[] ste = e.getStackTrace();
        if (ste.length == 0) {
            msg = "Empty Stack Trace";
        } else {
            msg = e.getClass().getCanonicalName() + ": " + e.getMessage() + "/n/t at " +
                    ste[0];
            JOptionPane.showMessageDialog(this, msg, "EXCEPTION", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
