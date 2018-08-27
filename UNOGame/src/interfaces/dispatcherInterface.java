package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.SignatureException;

public interface dispatcherInterface extends Remote {
	//user methods
	public int getPort() throws RemoteException;
	
	//server methods
	public void updateInfo(int serverPort, int load) throws RemoteException;
	    	
}
