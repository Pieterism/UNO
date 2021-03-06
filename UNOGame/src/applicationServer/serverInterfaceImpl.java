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
import java.util.List;

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

/**
 * @author Pieter
 *
 */
public class serverInterfaceImpl extends UnicastRemoteObject implements serverInterface {

	private dbInterface db;
	private List<UnoGame> games;
	private int gameCounter, portnumber;
	private List<clientInterface> clients;
	private List<lobbyInterface> lobbies;
	public String secret;
	private dispatcherInterface dispatcher;
	private boolean first = true;

	/**
	 * @param dbPortnumber
	 * @param portnumber
	 * @throws RemoteException
	 */
	public serverInterfaceImpl(int dbPortnumber, int portnumber) throws RemoteException {
		games = new ArrayList<>();
		clients = new ArrayList<>();
		lobbies = new ArrayList<>();
		gameCounter = 0;
		this.portnumber = portnumber;
		
		if (secret == null) {
			secret = generateApiSecret(50);
		}
		System.out.println("server has started and pnmbr= " + dbPortnumber);
		setdb(dbPortnumber);
	}

	/* (non-Javadoc)
	 * @see interfaces.AuthenticationInterface#register(java.lang.String, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see interfaces.AuthenticationInterface#login(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean login(String username, String password)
			throws RemoteException, InvalidKeyException, SignatureException, SQLException {
		System.out.println("login is executed on server");
		if (!db.checkUsername(username)) {
			System.out.println("checkUsername");
			return false;
		} else {
			if (db.loginUser(username, password)) {
				System.out.println("true returned");
				return true;
			} else {
				System.out.println("false returned");
				return false;
			}
		}
	}

	/**
	 * @param msg
	 * @throws RemoteException
	 */
	public void tellClients(String msg) throws RemoteException {
		for (clientInterface client : clients) {
			client.tell(msg);
		}

	}

	/**
	 * @param dbPortnumber
	 */
	public void setdb(int dbPortnumber) {
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry("localhost", dbPortnumber);
			this.db = (dbInterface) registry.lookup("UNOdatabase"+ dbPortnumber);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see interfaces.serverInterface#startNewGame(java.lang.String, int, int)
	 */
	@Override
	public void startNewGame(String name, int gameTheme, int aantalSpelers) throws RemoteException {
		UnoGame uno = new UnoGame(aantalSpelers, name, gameTheme, db);
		games.add(uno);
		while (dispatcher==null) {
			getDispatcher();
		}
		uno.setGameId(db.addGame(gameCounter, name, aantalSpelers, this.portnumber, gameTheme));
		dispatcher.updateInfo(portnumber, gameCounter);
		System.out.println("dispatcher was notified with the new info " + gameCounter);
		gameCounter++;

	}
	
	/**
	 * 
	 */
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

	/* (non-Javadoc)
	 * @see interfaces.serverInterface#getGames()
	 */
	@Override
	public List<String> getGames() throws RemoteException {
		
		List<String> gamesList = new ArrayList<>();
		try {
			gamesList = db.getActiveGames();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (UnoGame game : games) {
			gamesList.add(new String(game.getId() + "\t" + game.getName() + "\t" + game.connectedPlayers() + "/"
					+ game.getPlayerCount() + "\t" + game.getTheme()));
		}
		return gamesList;
	}

	/* (non-Javadoc)
	 * @see interfaces.serverInterface#send(java.lang.String, java.lang.String)
	 */
	@Override
	public void send(String s, String username) throws RemoteException {
		String message;
		System.out.println(s);
		message = username + ": " + s;
		for (lobbyInterface temp : lobbies) {
			temp.setMsg(message);
		}

	}

	/* (non-Javadoc)
	 * @see interfaces.serverInterface#giveLobby(interfaces.lobbyInterface)
	 */
	@Override
	public void giveLobby(lobbyInterface lobbyController) throws RemoteException {
		lobbies.add(lobbyController);
	}

	/* (non-Javadoc)
	 * @see interfaces.serverInterface#exit(interfaces.lobbyInterface)
	 */
	@Override
	public void exit(lobbyInterface lobbyController) throws RemoteException {
		lobbies.remove(lobbyController);
	}

	/* (non-Javadoc)
	 * @see interfaces.serverInterface#joinGame(interfaces.gameControllerInterface, int, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see interfaces.serverInterface#sendGameMsg(java.lang.String, int, java.lang.String)
	 */
	@Override
	public void sendGameMsg(String msg, int gameID, String username) throws RemoteException {
		games.get(gameID).sendMsg(username + ": " + msg);
	}

	/* (non-Javadoc)
	 * @see interfaces.serverInterface#readyToStart(int, java.lang.String, int)
	 */
	@Override
	public void readyToStart(int gameId, String username, int gametheme) throws RemoteException {
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
			if (first) {
				first = false;
				ArrayList<String> info = new ArrayList<>();
				for (Player player : games.get(gameId).getPlayers()) {
					info.add(player.getName());
				}
				List<gameControllerInterface> controllers = new ArrayList<>();
				for (Player player : games.get(gameId).getPlayers()) {
					player.getGameController().sendPlayerInfo(info);
					controllers.add(player.getGameController());
				}
				db.addUsersToGame(games.get(gameId).getName(), info);
			}
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						db.createPlayerHandTabel(games.get(gameId).getId());
						games.get(gameId).play();
						for (Player player : games.get(gameId).getPlayers()) {
							player.setReady(false);
							player.getGameController().setReady(false);
							StringBuilder sb = new StringBuilder();
							games.get(gameId).setGameId(games.get(gameId).getId() + "0");
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

	/* (non-Javadoc)
	 * @see interfaces.serverInterface#getCards(java.lang.String, int)
	 */
	@Override
	public List<Card> getCards(String username, int gameID) {
		for (Player player : games.get(gameID).getPlayers()) {
			if (player.getName().equals(username)) {
				return player.getCards();
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public dbInterface getDb() {
		return db;
	}

	/**
	 * @return
	 */
	public List<clientInterface> getClients() {
		return clients;
	}

	/* (non-Javadoc)
	 * @see interfaces.serverInterface#closeGC(interfaces.gameControllerInterface)
	 */
	@Override
	public void closeGC(gameControllerInterface gci) throws RemoteException {
		for (UnoGame game : games) {
			for (Player player : game.getPlayers()) {
				if (gci.hashCode()==player.getGameController().hashCode()) {
					game.endGame();
				}
			}
		}
		
	}

	@Override
	public String getLoginToken(String usernamed) throws RemoteException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}