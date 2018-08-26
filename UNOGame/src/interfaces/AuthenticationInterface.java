package interfaces;

import java.rmi.Remote;

public interface AuthenticationInterface extends Remote {

	public boolean register(String username, String password);

	public String getLoginToken(String username, String password);

	public boolean tokenLogin(String token);
}
