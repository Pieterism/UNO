package serverInterfaces;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import UNO.Card;
import UNO.Player;
import UNO.UnoGame;
import clientInterfaces.clientInterface;
import clientInterfaces.gameControllerInterface;
import clientInterfaces.lobbyInterface;
import dbInterfaces.dbInterface;
import javafx.application.Platform;

import static security.JWTUtils.generateApiSecret;

public class serverInterfaceImpl extends UnicastRemoteObject implements serverInterface {

    private dbInterface db;
    private List<UnoGame> games;
    private int gameCounter, playerCounter;
    private List<clientInterface> clients;
    private List<lobbyInterface> lobbies;
    private HashMap<String, clientInterface> map;
    public String secret;
    

    public serverInterfaceImpl(int dbPortnumber) throws RemoteException {
        games = new ArrayList<>();
        clients = new ArrayList<>();
        lobbies = new ArrayList<>();
        map = new HashMap<>();
        gameCounter = 0;
        playerCounter = 0;
//        try {
//            Registry registry = LocateRegistry.getRegistry("localhost", 1100);
//            this.db = (dbInterface) registry.lookup("UNO");
//            System.out.println("connected to db");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
        if (secret == null) {
        	secret = generateApiSecret(50);
        }
        System.out.println("server has started");
        setdb(dbPortnumber);
    }

    public void setdb(int dbPortnumber) {
    	Registry registry;
    	dbInterface db;
    	try {
        	registry = LocateRegistry.getRegistry("localhost", dbPortnumber);
        	db = (dbInterface) registry.lookup("UNOdatabase");
        	this.db = db;
    	}
    	catch (Exception e) {
		}
        
    }

    @Override
    public String register(String username, String password) throws RemoteException {
        if (!db.checkUsername(username)) {
            return null;
        } else {
            db.addUser(username, password);
            tellClients(username + " has succesfully connected to UNO room and your id is: ");
            return "ok";
        }
    }

    private void tellClients(String msg) throws RemoteException {
        for (clientInterface client : clients) {
            client.tell(msg);
        }

    }

    @Override
    public String login(String username, String password) throws RemoteException {
		if (db.checkUsername(username)) {
			return null;
		} else {
			if (db.loginUser(username, password)) {
				return "ok";
			} else {
				return null;
			}
		}
	}

    @Override
    public void startNewGame(String name, String description, int aantalSpelers) throws RemoteException {
        games.add(new UnoGame(aantalSpelers, gameCounter, name, description));
        gameCounter++;
    }

    @Override
    public List<String> getGames() throws RemoteException {
        List<String> gamesList = new ArrayList<>();
        for (UnoGame game : games) {
            gamesList.add(new String(game.getId() + "\t" + game.getName() + "\t" + game.connectedPlayers() + "/" + game.getPlayerCount() + "\t" + game.getBeschrijving()));
        }
        return gamesList;
    }

    @Override
    public void playCard(Card card) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<String> getUsers() throws RemoteException {
        return new ArrayList<>(map.keySet());
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
    public void giveClient(String s, clientInterface client) throws RemoteException {
        map.put(s, client);
        System.out.println(s);
        clients.add(client);     
    }

	@Override
	public void giveLobby(lobbyInterface lobbyController) throws RemoteException {
		lobbies.add(lobbyController);
	}

	@Override
	public void exit(clientInterface client, lobbyInterface lobbyController) throws RemoteException {
		lobbies.remove(lobbyController);
		map.remove(client);
		clients.remove(client);
	}

	@Override
	public void joinGame(gameControllerInterface gameController, int gameID, String username) throws RemoteException, IllegalStateException {
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
	public boolean ping() throws RemoteException {
		return true;
	}

	@Override
	public void giveGameController(gameControllerInterface gcInterface) throws RemoteException {
		
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
	
	@Override
	public void readyToStart(int gameId, String username ) throws RemoteException {
		System.out.println("ready to start executed!");
		boolean start = true;
		for (Player player : games.get(gameId).getPlayers()) {
			if(player.getName().contains(username)&& !player.getReady() ) {
				player.setReady(true);
				sendGameMsg("is ready to play!", gameId, player.getName());
			}
			if (!player.getReady()&& games.get(gameId).getPlayerCount() == games.get(gameId).getPlayers().size()) {
				start = false;
			}
		}
		if (start) {
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						games.get(gameId).play();
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
}
