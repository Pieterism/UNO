package services;

import interfaces.serverInterface;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class RegisterService extends Service<Boolean> {

	private String username, password;
	private serverInterface server;

	/**
	 * @param username
	 * @param password
	 * @param server
	 */
	public RegisterService(String username, String password, serverInterface server) {
		this.username = username;
		this.password = password;
		this.server = server;
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				return server.register(username, password);
			}
		};
	}

}
