package applicationServer;

import static security.JWTUtils.generateApiSecret;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import interfaces.AuthenticationInterface;
import interfaces.clientInterface;
import interfaces.dbInterface;
import interfaces.dispatcherInterface;
import interfaces.gameControllerInterface;
import interfaces.lobbyInterface;
import interfaces.serverInterface;
import security.BCrypt;
import uno.Card;
import uno.Player;
import uno.UnoGame;

public class serverInterfaceImpl extends UnicastRemoteObject implements serverInterface {

	private dbInterface db;
	private List<UnoGame> games;
	private int gameCounter, portnumber;
	private List<clientInterface> clients;
	private List<lobbyInterface> lobbies;
	public String secret;
	private dispatcherInterface dispatcher;

	public serverInterfaceImpl(int dbPortnumber, int portnumber) throws RemoteException {
		games = new ArrayList<>();
		clients = new ArrayList<>();
		lobbies = new ArrayList<>();
		gameCounter = 0;
		this.portnumber = portnumber;
		
		if (secret == null) {
			secret = generateApiSecret(50);
		}
		System.out.println("server has started");
		setdb(dbPortnumber);
	}

	@Override
	public boolean register(String username, String password)
			throws RemoteException, InvalidKeyException, SignatureException {
		if (!db.checkUsername(username)) {
			return false;
		} else {
			db.addUser(username, BCrypt.hashpw(password, BCrypt.gensalt()));
			tellClients(username + " has succesfully connected to UNO room and your id is: ");
			return true;
		}
	}

	@Override
	public boolean login(String username, String password)
			throws RemoteException, InvalidKeyException, SignatureException {
		if (!db.checkUsername(username)) {
			return false;
		} else {
			if (db.loginUser(username, password)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public void tellClients(String msg) throws RemoteException {
		for (clientInterface client : clients) {
			client.tell(msg);
		}

	}

	public void setdb(int dbPortnumber) {
		Registry registry;
		dbInterface db;
		try {
			registry = LocateRegistry.getRegistry("localhost", dbPortnumber);
			db = (dbInterface) registry.lookup("UNOdatabase");
			this.db = db;
		} catch (Exception e) {
		}

	}

	@Override
	public void startNewGame(String name, String description, int aantalSpelers) throws RemoteException {
		games.add(new UnoGame(aantalSpelers, gameCounter, name, description));
		gameCounter++;
		while (dispatcher==null) {
			getDispatcher();
		}
		dispatcher.updateInfo(portnumber, games.size());
	}
	
	public void getDispatcher() {
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry("localhost", 1099);
			dispatcher = (dispatcherInterface) registry.lookup("UNOdispatcher");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getGames() throws RemoteException {
		List<String> gamesList = new ArrayList<>();
		for (UnoGame game : games) {
			gamesList.add(new String(game.getId() + "\t" + game.getName() + "\t" + game.connectedPlayers() + "/"
					+ game.getPlayerCount() + "\t" + game.getBeschrijving()));
		}
		return gamesList;
	}

	@Override
	public void send(String s, String username) throws RemoteException {
		String message;
		System.out.println(s);
		message = username + ": " + s;
		for (lobbyInterface temp : lobbies) {
			temp.setMsg(message);
		}

	}

	@Override
	public void giveLobby(lobbyInterface lobbyController) throws RemoteException {
		lobbies.add(lobbyController);
	}

	@Override
	public void exit(lobbyInterface lobbyController) throws RemoteException {
		lobbies.remove(lobbyController);
	}

	@Override
	public void joinGame(gameControllerInterface gameController, int gameID, String username)
			throws RemoteException, IllegalStateException {
		UnoGame game = games.get(gameID);
		try {
			game.addPlayer(username, gameController);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("an unexpected exception has occured @addPlayer");
		}
	}

	@Override
	public void sendGameMsg(String msg, int gameID, String username) throws RemoteException {
		games.get(gameID).sendMsg(username + ": " + msg);
	}

	@Override
	public void readyToStart(int gameId, String username) throws RemoteException {
		System.out.println("ready to start executed!");
		boolean start = true;
		for (Player player : games.get(gameId).getPlayers()) {
			if (player.getName().contains(username) && !player.getReady()) {
				player.setReady(true);
				sendGameMsg("is ready to play!", gameId, player.getName());
			}
			if (!player.getReady() && games.get(gameId).getPlayerCount() == games.get(gameId).getPlayers().size()) {
				start = false;
			}
		}
		if (start) {
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						ArrayList<String> info = new ArrayList<>();
						for (Player player : games.get(gameId).getPlayers()) {
							info.add(player.getName());
						}
						for (Player player : games.get(gameId).getPlayers()) {
							player.getGameController().sendPlayerInfo(info);
						}

						games.get(gameId).play();
						for (Player player : games.get(gameId).getPlayers()) {
							player.setReady(false);
							player.getGameController().setReady(false);
						}
					} catch (RemoteException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
		}
	}

	@Override
	public List<Card> getCards(String username, int gameID) {
		for (Player player : games.get(gameID).getPlayers()) {
			if (player.getName().equals(username)) {
				return player.getCards();
			}
		}
		return null;
	}

	public dbInterface getDb() {
		return db;
	}

	public List<clientInterface> getClients() {
		return clients;
	}

	@Override
	public String getLoginToken(String username, String password) throws RemoteException, SQLException {
		db.getToken(username);
		return null;
	}
}