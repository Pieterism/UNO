package clientInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import dispatcherInterfaces.dispatcherInterface;

public interface clientInterface extends Remote {

    public void tell(String s) throws RemoteException;

    public void askColour() throws RemoteException;

    public void giveInterface(dispatcherInterface server) throws RemoteException;

}
