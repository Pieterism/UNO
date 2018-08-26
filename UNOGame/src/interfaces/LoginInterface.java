package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.SignatureException;

public interface LoginInterface extends Remote {

	String getToken(String username, String password)throws RemoteException, InvalidKeyException, SignatureException;

	boolean loginToken(String token)throws RemoteException;
}
