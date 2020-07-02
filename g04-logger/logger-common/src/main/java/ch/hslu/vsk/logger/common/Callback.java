package ch.hslu.vsk.logger.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Callback extends Remote {
    void update(String logString) throws RemoteException;
}
