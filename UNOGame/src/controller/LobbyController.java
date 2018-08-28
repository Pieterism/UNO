package controller;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;

import interfaces.lobbyInterface;
import interfaces.serverInterface;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import uno.Player;

public class LobbyController extends UnicastRemoteObject implements lobbyInterface{

    serverInterface server;
    private String textmsg;
    private ObservableList gameData = FXCollections.observableArrayList();
    private ObservableList userData = FXCollections.observableArrayList();
    private ListView games = new ListView(gameData);
    private ListView users = new ListView(gameData);
    private int gameID;
    private int gameTheme;
    private String gameName;
    String selectedUser = null;
    String username;
    
    @FXML
    private Button btn_join, btn_new, btn_spectate, btn_exit, btn_send, btn_reload;
    
    @FXML
    Label lbl_username;

    @FXML
    private ListView<Player> players;

    @FXML
    private TextField chat_input;

    @FXML
    private TextArea chat;

    @FXML
    private AnchorPane pn_input, pn_output;

    public LobbyController(String s, serverInterface server) throws RemoteException{
        this.username = s;
        this.server = server;
    }

    public void initialize() throws RemoteException, NotBoundException {
        setList();
        server.giveLobby(this);
        games.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				List<String> temp = Arrays.asList(newValue.split("\t"));
				gameID = Integer.parseInt(temp.get(0));
				gameName = temp.get(1);
				gameTheme = Integer.parseInt(temp.get(3));
				
			}
        });
        
        chat.setEditable(false);
        lbl_username.setText(username);
    }

    public void setList() throws RemoteException {
        List<String> gameslist = server.getGames();
        gameData.addAll(gameslist);
        games.setEditable(false);
        games.setItems(gameData);
        pn_input.getChildren().add(games);
        games.prefWidthProperty().bind(pn_input.widthProperty());
        games.prefHeightProperty().bind(pn_input.heightProperty());
    }

    @FXML
    public void createNewGame() throws RemoteException {
    	startPopupNewGame();
    	reload();
    }

    @FXML
    public void joinGame() throws RemoteException {
		startGame();
		exit();
	}
 

    @FXML
    public void exit() throws RemoteException {
    	server.exit(this);
        Stage stage = (Stage) btn_exit.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void send() throws RemoteException {
    	server.send(chat_input.getText(), username);
        chat_input.setText("");
    }
    
    @FXML
    public void reload() throws RemoteException {
        List<String> gameslist = server.getGames();
        gameData.clear();
        gameData.addAll(gameslist);
        games.setEditable(false);
        games.setItems(gameData);
    }

    public void startGame() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/controller/Game.fxml"));
            
            GameController controller = new GameController(username, server, gameID, gameName, gameTheme);

            fxmlLoader.setController(controller);
            System.out.println(controller);
            System.out.println(gameID);
            System.out.println(username);
            System.out.println(gameName);
            Parent root1 = (Parent) fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Game");
            stage.setScene(new Scene(root1));
            stage.show();
            server.joinGame(controller, gameID, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public void setMsg(String msg) throws RemoteException {
		String message = chat.getText() + msg + "\n";
        chat.setText(message);
	}
	
	public void startPopupNewGame(){
		try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/controller/PopupNewGame.fxml"));
            PopupNewGameController controller = new PopupNewGameController(username, server);
            fxmlLoader.setController(controller);

            Parent root1 = (Parent) fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Create new Game");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
