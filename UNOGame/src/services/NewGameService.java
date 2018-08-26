package services;

import interfaces.serverInterface;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class NewGameService extends Service<Void>{

	private String gameName, gameDescription;
	private int aantal;
	private serverInterface server;
	
	public NewGameService(String gameName, String gameDescription, Integer aantal, serverInterface server) {
		this.gameName = gameName;
		this.gameDescription = gameDescription;
		this.server = server;
		this.aantal = aantal;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				server.startNewGame(gameName, gameDescription, aantal);
				return null;
			}
		};
	}
	

}
