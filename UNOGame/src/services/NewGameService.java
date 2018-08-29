package services;

import interfaces.serverInterface;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class NewGameService extends Service<Void> {

	private String gameName;
	private int aantal, gameTheme;
	private serverInterface server;

	public NewGameService(String gameName, int gameTheme, Integer aantal, serverInterface server) {
		this.gameName = gameName;
		this.gameTheme = gameTheme;
		this.server = server;
		this.aantal = aantal;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				server.startNewGame(gameName, gameTheme, aantal);
				return null;
			}
		};
	}

}
