package ch.hslu.vsk.logger.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaces for the win.
 *
 * This will be Implemented in the g04-loggercommon part as Interface for
 * LogMessage... I guess?
 *
 * This is used on Classes which should be Visible through RMI
 *
 * @author Maurizio Hostettler
 */

public interface LogViewerService extends Remote {

    /**
     * Register to get a log stream from the logger server.
     *
     * @param callback the object has to implement the Callback interface. It's a
     *                 kind of a subscriber information for the latest logs.
     * @throws RemoteException Throws an exception in case a remote is inaccessible.
     */
    void register(Callback callback) throws RemoteException;

    /**
     * deregister from the logger server.
     *
     * @param callback the object has to implement the Callback interface. It's a
     *                 kind of a subscriber information for the latest logs.
     * @throws RemoteException Throws an exception in case a remote is inaccessible.
     */
    void deregister(Callback callback) throws RemoteException;

    /**
     * Sends the latest logs to all registered clients.
     *
     * @param logString It's a log message :-).
     * @throws RemoteException Throws en exception in case a remote is inaccessible.
     */
    void notifyClients(String logString) throws RemoteException;

}
