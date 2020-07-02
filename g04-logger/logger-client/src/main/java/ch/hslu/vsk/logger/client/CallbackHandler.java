package ch.hslu.vsk.logger.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.JTextArea;
import ch.hslu.vsk.logger.common.Callback;

public final class CallbackHandler extends UnicastRemoteObject implements Callback {

    private JTextArea logField;
    private static final long serialVersionUID = 3L;

    protected CallbackHandler(final JTextArea jTextArea) throws RemoteException {
        super();
        this.logField = jTextArea;
    }

    @Override
    public void update(final String logString) throws RemoteException {
        logField.append(logString + "\n");
        logField.setCaretPosition(logField.getDocument().getLength() - 1);
    }

}
