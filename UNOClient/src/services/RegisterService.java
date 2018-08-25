package services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import serverInterfaces.serverInterface;

public class RegisterService extends Service<String>{

	private String username, password;
	private serverInterface server;
	
	public RegisterService(String username, String password, serverInterface server) {
		this.username = username;
		this.password = password;
		this.server = server;
	}

	@Override
	protected Task<String> createTask() {
		return new Task<String>() {
			@Override
			protected String call() throws Exception {
				return server.register(username, password);
			}
		};
	}
	

}
