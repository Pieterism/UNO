package services;

import interfaces.AuthenticationInterface;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class RegisterService extends Service<Boolean> {

	private String username, password;
	private AuthenticationInterface auth;

	public RegisterService(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				return auth.register(username, password);
			}
		};
	}

}
