package services;

import applicationServer.serverInterfaceImpl;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LoginService extends Service<Boolean> {

	private String username, password;
	private serverInterfaceImpl server;

	/**
	 * @param username
	 * @param password
	 */
	public LoginService(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				return server.login(username, password);
			}

		};
	}
//	@Override
//	protected Task<Boolean> createTask() {
//		return new Task<Boolean>() {
//			@Override
//			protected Boolean call() throws Exception {
//				 try {
//					 //TODO: Main aanpassen (eventueel ook nog jwt) 
//	                    Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());
//	                    //LOGGER.log(Level.INFO, "Registry retrieved: {0}", myRegistry);
//
//	                     serverInterface loginService = (serverInterface) myRegistry.lookup("LoginService");
//	                    //LOGGER.log(Level.INFO, "loginService retrieved: {0}", loginService);
//
//	                    boolean succesfulLogin = false;
//
//	                    if (Main.token != null) {
//	                        succesfulLogin = loginService.loginWithToken(Main.token);
//	                        System.out.println("1: " + succesfulLogin);
//	                    }
//
//	                    if(!succesfulLogin) {
//
//	                        String token = loginService.getLoginToken(username, password);
//
//	                        succesfulLogin = token != null;
//
//	                        if (succesfulLogin) {
//	                            Main.token = token;
//	                        }
//	                    }
//
//	                    return succesfulLogin;
//	                } catch (Exception e) {
//	                    e.printStackTrace();
//	                }
//	                return false;
//	            }
//		};
//	}

}
