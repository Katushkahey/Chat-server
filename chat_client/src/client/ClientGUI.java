package client;

import common.Library;
import network.SocketThread;
import network.SocketThreadListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler, SocketThreadListener {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;
    private final String TITLE = "ChatClient";

    private final JTextArea log = new JTextArea();
    private final JPanel panelTop = new JPanel(new GridLayout(2,3));
    private final JTextField tfIPAddress = new JTextField("127.0.0.1");
    private final JTextField tfPort = new JTextField("8189");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("Always on top");
    private final JTextField tfLogin = new JTextField("Katushkahey");
    private final JTextField tfPassword = new JTextField("123");
    private final JButton login = new JButton("Log in");
    private final JPanel panelBottom = new JPanel(new BorderLayout());
    private final JButton bnDisconnect = new JButton("Disconnect");
    private final JTextField tfMessage = new JTextField();
    private final JButton btnSend = new JButton("Send");
    private JList<String> users = new JList<>();
    private boolean shownIoErrors = false;
    private SocketThread socketThread;
    private final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });
    }

    private ClientGUI() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setTitle(TITLE);
        log.setEditable(false);
        log.setLineWrap(true);  //переход на новую строку в случае достижение конца textArea
        log.setWrapStyleWord(true); // перенос слов
        setVisible(true);
        JScrollPane scrollLog = new JScrollPane(log);
        JScrollPane scrollUsers = new JScrollPane(users);
        scrollUsers.setPreferredSize(new Dimension(100,0));
        panelTop.add(tfIPAddress);
        panelTop.add(tfPort);
        panelTop.add(cbAlwaysOnTop);
        panelTop.add(tfLogin);
        panelTop.add(tfPassword);
        panelTop.add(login);
        panelBottom.add(bnDisconnect, BorderLayout.WEST);
        panelBottom.add(tfMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);
        panelBottom.setVisible(false);
        add(panelTop, BorderLayout.NORTH);
        add(scrollLog, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
        add(scrollUsers, BorderLayout.EAST);
        cbAlwaysOnTop.addActionListener(this);
        btnSend.addActionListener(this);
        tfMessage.addActionListener(this);
        login.addActionListener(this);
        bnDisconnect.addActionListener(this);
    }

    private void connect() {
        Socket socket = null;
        try {
            socket = new Socket(tfIPAddress.getText(), Integer.parseInt(tfPort.getText()));
        } catch (IOException e) {
            writeToLog("Exception: " + e.getMessage());
        }
        socketThread = new SocketThread(this, "ClientSocketThread", socket);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == cbAlwaysOnTop) {
            setAlwaysOnTop(cbAlwaysOnTop.isSelected());
        } else if (source == btnSend || source == tfMessage) {
            sendMessage();
        } else if (source == login) {
            connect();
        } else if (source == bnDisconnect) {
            socketThread.close();
        } else {
            throw new RuntimeException("Unknown source" + source);
        }
    }

    private void sendMessage() {
        if (!tfMessage.getText().trim().isEmpty()) {
            String msg = tfMessage.getText().trim();
            socketThread.sendMessage(msg);
        }
        tfMessage.setText(null);
        tfMessage.requestFocusInWindow();
    }

    private void writeToLog (String msg) {
        try (FileWriter logFile = new FileWriter("log.txt", true)) {
            logFile.write(msg+ "\n");
            logFile.flush();
        } catch (IOException e) {
           if (!shownIoErrors) {
               shownIoErrors = true;
               showException(e);
           }
        }
    }

    private void putToLog (String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        showException(e);
        System.exit(1);
    }

    private void showException(Throwable e) {
        String msg;
        StackTraceElement[] ste = e.getStackTrace();
        if (ste.length == 0) {
            msg = "Empty Stack Trace";
        } else {
            msg = e.getClass().getCanonicalName() + ": " + e.getMessage() + "\n\t at " +
                    ste[0];
            JOptionPane.showMessageDialog(this, msg, "EXCEPTION", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void socketThreadStart(SocketThread st, Socket socket) {
        writeToLog("connected...");
    }

    @Override
    public void socketThreadReady(SocketThread st, Socket socket) {
        panelTop.setVisible(false);
        panelBottom.setVisible(true);
        String login = tfLogin.getText();
        String password = tfPassword.getText();
        st.sendMessage(Library.getAuthRequest(login, password));
    }

    @Override
    public void receiveString(SocketThread st, Socket socket, String str) {
        handleMessage(str);
        writeToLog(str);
    }

    @Override
    public void socketThreadStop(SocketThread st) {
        putToLog("connection lost");
        writeToLog("connection lost");
        panelTop.setVisible(true);
        panelBottom.setVisible(false);
        setTitle(TITLE);
        final String [] EMPTY = new String[0];
        users.setListData(EMPTY);
    }

    @Override
    public void socketThreadException(SocketThread st, Exception e) {
        e.printStackTrace();
        showException(e);
    }

    private void handleMessage(String msg) {
        String arr[] = msg.split(Library.DELIMITER);
        String msgType = arr[0];
        switch (msgType) {
            case Library.AUTH_ACCEPT:
                 setTitle(TITLE + ". " + arr[1]);
                break;
            case Library.AUTH_DENIED:
                break;
            case Library.MSG_FORMAT_ERROR:
                socketThread.close();
                break;
            case Library.TYPE_BROADCAST:
                msg = DTF.format(LocalDateTime.now()) + " " + arr[1] +
                        ": " + arr[2];
                putToLog(msg);
                break;
            case Library.USER_LIST:
                String userList = msg.substring(Library.USER_LIST.length() + Library.DELIMITER.length());
                String[] listOfUsers = userList.split(Library.DELIMITER);
                Arrays.sort(listOfUsers);
                users.setListData(listOfUsers);
                break;
            default:
                throw new RuntimeException("Unknown message format: " + msg);
        }
    }

}
