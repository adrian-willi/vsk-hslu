package ch.hslu.vsk.logger.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import ch.hslu.vsk.logger.common.Callback;
import ch.hslu.vsk.logger.common.LogViewerService;

public final class LogViewerClient extends JFrame implements ActionListener {

    private static final long serialVersionUID = 2L;
    private JTextField serverIp = new JTextField();
    private JTextField serverPort = new JTextField();
    private final JTextPane txtpnServerIp = new JTextPane();
    private final JTextPane txtpnPort = new JTextPane();
    private final JButton connectBtn = new JButton("Connect");
    private final JButton disconnectBtn = new JButton("Disconnect");
    private final JButton clearLogs = new JButton("Clear Logs");
    private JTextArea logField = new JTextArea();
    private JScrollPane scroll = new JScrollPane(logField, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private final JTextField status = new JTextField();
    private final JTextPane txtpnConnectionStatus = new JTextPane();
    private boolean connected = false;
    private LogViewerService service;
    private Callback callback;

    /**
     * Create the basic Frame.
     */
    private LogViewerClient() {
        setTitle("VSK FS20 g04 (very) simple Logger Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 400);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.NORTH);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        txtpnServerIp.setBackground(SystemColor.menu);
        txtpnServerIp.setEditable(false);
        txtpnServerIp.setText("Server hostname / IP");
        panel.add(txtpnServerIp);

        serverIp.setText("localhost");
        serverIp.setHorizontalAlignment(SwingConstants.CENTER);
        serverIp.setToolTipText("set hostname or IPv4 Address");
        serverIp.setColumns(10);
        panel.add(serverIp);

        txtpnPort.setBackground(SystemColor.control);
        txtpnPort.setText("Port");
        panel.add(txtpnPort);

        serverPort.setToolTipText("Server RMI TCP Port");
        serverPort.setHorizontalAlignment(SwingConstants.CENTER);
        serverPort.setText("5099");
        serverPort.setColumns(3);
        panel.add(serverPort);

        connectBtn.addActionListener(this);
        panel.add(connectBtn);

        disconnectBtn.addActionListener(this);
        disconnectBtn.setEnabled(false);
        panel.add(disconnectBtn);

        txtpnConnectionStatus.setBackground(SystemColor.control);
        txtpnConnectionStatus.setText("     Connection Status");
        panel.add(txtpnConnectionStatus);

        status.setBackground(new Color(255, 182, 193));
        status.setText(" NOK");
        status.setColumns(3);
        panel.add(status);

        logField.setTabSize(4);
        logField.setLineWrap(true);
        logField.setEditable(false);
        getContentPane().add(scroll, BorderLayout.CENTER);
        try {
            this.callback = new CallbackHandler(logField);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        clearLogs.addActionListener(this);
        getContentPane().add(clearLogs, BorderLayout.SOUTH);

    }

    /**
     * Logger Viwer to display remotely logged logs.
     *
     * This beautiful piece of work will be running on its own and remain here in
     * the client component.
     *
     * @param args
     * @throws MalformedURLException
     * @throws RemoteException
     * @throws NotBoundException
     */
    public static void main(final String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LogViewerClient frame = new LogViewerClient();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        if (event.getSource() == connectBtn && !connected) {
            try {
                service = (LogViewerService) Naming
                        .lookup("rmi://" + serverIp.getText() + ":" + serverPort.getText() + "/logger");
                service.register(callback);
                // addText(servcice.echo("Hey Server are you there?"));

                this.connected = true;
                disconnectBtn.setEnabled(true);
                connectBtn.setEnabled(false);
                status.setBackground(new Color(152, 251, 152));
                status.setText("  OK");
            } catch (MalformedURLException | RemoteException | NotBoundException e) {
                addText("Could not establish Connection!\n " + e.getCause());
                e.printStackTrace();
            }
        }
        if (event.getSource() == disconnectBtn && connected) {
            try {
                service.deregister(callback);

                this.connected = false;
                disconnectBtn.setEnabled(false);
                connectBtn.setEnabled(true);

                status.setBackground(new Color(255, 182, 193));
                status.setText(" NOK");
                addText("... disconnected");
            } catch (RemoteException e) {
                addText("Could not deregister!\n " + e.getCause());
                e.printStackTrace();
            }
        }
        if (event.getSource() == clearLogs) {
            logField.setText("");
        }
    }

    public void addText(final String text) {
        logField.append(text + "\n");
        logField.setCaretPosition(logField.getDocument().getLength() - 1);
    }

    public JTextArea getLogFiled() {
        return logField;
    }
}
