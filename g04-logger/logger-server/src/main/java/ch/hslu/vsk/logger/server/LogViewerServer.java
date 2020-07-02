package ch.hslu.vsk.logger.server;

import ch.hslu.vsk.logger.common.LogViewerService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Server Part, creates a Registry and exposes a logViewerServant Object on it.
 *
 * Will be Implemented and running in the g04-loggerserver component!
 */
public final class LogViewerServer implements Runnable {

    private LogViewerService logViewerServant;
    //LogViewerService

    /**
     * protected in order to start it from loggerserver.
     *
     */
    protected LogViewerServer() {
        try {
            //this.LogViewerService
            this.logViewerServant = new LogViewerServant();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Registry registry = LocateRegistry.createRegistry(5099);
            registry.rebind("logger", logViewerServant);
            System.out.println("RMI Server started... binding \"logger\" exposed");
        } catch (RemoteException e) {
            System.err.println("could not start RMI Server: " + e);
        }
    }

    public LogViewerService getLogViewerServant() {
        return logViewerServant;
    }
}
