package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private final JTextArea log = new JTextArea();
    private final JPanel panelTop = new JPanel(new GridLayout(2,3));
    private final JTextField tfIPAddress = new JTextField("127.0.0.1");
    private final JTextField tfPort = new JTextField("8189");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("Always on top");
    private final JTextField tfLogin = new JTextField("Kate");
    private final JTextField tfPassword = new JTextField("Kate1");
    private final JButton login = new JButton("Log in");
    private final JPanel panelBottom = new JPanel(new BorderLayout());
    private final JButton bnDisconnect = new JButton("Disconnect");
    private final JTextField tfMessage = new JTextField();
    private final JButton btnSend = new JButton("Send");

    private JList<String> users = new JList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });
    }

    private ClientGUI(){
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setTitle("ChatClient");
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
        add(panelTop, BorderLayout.NORTH);
        add(scrollLog, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
        add(scrollUsers, BorderLayout.EAST);
        cbAlwaysOnTop.addActionListener(this);
        btnSend.addActionListener(this);
        tfMessage.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cbAlwaysOnTop) {
            setAlwaysOnTop(cbAlwaysOnTop.isSelected());
        } else if (e.getSource() == btnSend || e.getSource() == tfMessage) {
            if (!tfMessage.getText().trim().isEmpty()) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String msg = dtf.format(now) + " " + tfLogin.getText() + ": " + tfMessage.getText().trim() + "\n";
                putToLog(msg);
                writeToLod(msg);
            }
            tfMessage.setText(null);
            tfMessage.requestFocusInWindow();
        }
    }

    private void writeToLod (String msg) {
        try (FileWriter logFile = new FileWriter("log.txt", true)) {
            logFile.write(msg + "\n");
            logFile.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e, "EXCEPTION", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void putToLog (String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg);
            }
        });
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
