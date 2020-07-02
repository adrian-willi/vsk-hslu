package ch.hslu.vsk.logger.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.hslu.vsk.logger.common.Callback;
import ch.hslu.vsk.logger.common.LogViewerService;

public class LogViewerServant extends UnicastRemoteObject implements LogViewerService {

    /**
     * This will be the exposed Object AKA the LogMessage :-).
     *
     * Will be Implemented in the g04-loggerserver component!
     */
    private static final long serialVersionUID = 1L;
    private final List<Callback> subscribers;

    protected LogViewerServant() throws RemoteException {
        super();
        subscribers = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(final Callback callback) throws RemoteException {
        subscribers.add(callback);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyClients(final String logString) throws RemoteException {
        System.out.print("\nLog Message read to send to Subscribers : " + logString);
        final Iterator<Callback> iterator = subscribers.iterator();
        while (iterator.hasNext()) {
            Callback subscriber = iterator.next();
            subscriber.update(logString);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deregister(Callback callback) throws RemoteException {
        subscribers.remove(callback);
    }

}
