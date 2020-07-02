package ch.hslu.vsk.logger.server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import ch.hslu.vsk.logger.common.LogMessage;
import ch.hslu.vsk.logger.common.LogPersistor;

/**
 * @author adiwi
 */

public final class LogMessageHandler implements Runnable {

    private final Socket client;
    private ObjectInputStream inputStream;
    private final LogPersistor stringPersistorAdapter;
    private List<PropertyChangeListener> subscribers = new ArrayList<PropertyChangeListener>();

    public LogMessageHandler(final Socket client, final LogPersistor stringPersistorAdapter)
            throws IOException {
        this.client = client;
        this.stringPersistorAdapter = stringPersistorAdapter;
        this.inputStream = new ObjectInputStream(this.client.getInputStream());
    }

    @Override
    public void run() {
        Object receivedLog = null;
        while (true) {
            try {
                receivedLog = this.inputStream.readObject();
            } catch (EOFException ex) {
                // EOF is a predicted good state :p //wird geworfen, wenn das Ende beim Lesen des Streams erreicht wurde
                return; // to the while loop!
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            if (receivedLog != null) {
                LogMessage receivedMessage = (LogMessage) receivedLog;
                LogMessage confirmedMessage = new LogMessage(receivedMessage.getMessage(),
                        receivedMessage.getLoglevel(), receivedMessage.getApplicationId(),
                        receivedMessage.getTimeOfLog(), Instant.now());
                System.out.println(confirmedMessage);
                this.stringPersistorAdapter.save(confirmedMessage);
                this.notifySubscribers(new PropertyChangeEvent(this, "LogMessage", null, confirmedMessage));
                receivedLog = null;
            }
        }
    }

    public void subscribe(final PropertyChangeListener subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(final PropertyChangeListener subscriber) {
        subscribers.remove(subscriber);
    }

    private void notifySubscribers(final PropertyChangeEvent notification) {
        for (final PropertyChangeListener subscriber : subscribers) {
            subscriber.propertyChange(notification);
        }

    }
}
