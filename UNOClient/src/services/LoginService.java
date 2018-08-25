package services;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import serverInterfaces.serverInterface;

public class LoginService extends Service<Boolean>{

	private String username, password;
	private serverInterface server;
	
	public LoginService(String username, String password, serverInterface server) {
		this.username = username;
		this.password = password;
		this.server = server;
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				 try {
	                    Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
	                    //LOGGER.log(Level.INFO, "Registry retrieved: {0}", myRegistry);

	                    LoginStub loginService = (LoginStub) myRegistry.lookup("LoginService");
	                    //LOGGER.log(Level.INFO, "loginService retrieved: {0}", loginService);

	                    boolean succesfulLogin = false;

	                    if (Main.token != null) {
	                        succesfulLogin = loginService.loginWithToken(Main.token);
	                        System.out.println("1: " + succesfulLogin);
	                    }

	                    if(!succesfulLogin) {

	                        String token = loginService.getLoginToken(username, password);

	                        succesfulLogin = token != null;

	                        if (succesfulLogin) {
	                            Main.token = token;
	                        }
	                    }

	                    LOGGER.log(Level.INFO, "Loginserver retrieve token succesful=", succesfulLogin);

	                    return succesfulLogin;
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                return false;
	            }
			}
		};
	}
	

}
