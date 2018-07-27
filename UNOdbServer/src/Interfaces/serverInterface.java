package Interfaces;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface serverInterface extends Remote {

    public String JWT() throws RemoteException;
}
