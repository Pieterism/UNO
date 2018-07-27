package GUI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.javafx.tk.Toolkit;
import com.sun.security.auth.callback.TextCallbackHandler;
import com.sun.swing.internal.plaf.synth.resources.synth;

import UNO.Card;
import UNO.DrawCard;
import UNO.ReverseCard;
import UNO.SkipCard;
import UNO.UnoGame;
import UNO.WildCard;
import clientInterfaces.gameControllerInterface;
import clientInterfaces.lobbyInterface;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import serverInterfaces.serverInterface;

public class GameController extends UnicastRemoteObject implements gameControllerInterface{
<<<<<<< HEAD
	private String path = "D:\\Google Drive\\School\\2017-2018\\1e Semester\\Gedistribueerde Systemen\\Opdracht UNO\\GIT_UNO\\UNOClient\\picture\\";
=======
	private String path = "C:\\Users\\wouter\\Documents\\school\\GedistribueerdeSystemen\\UNO-game\\UNOClient\\picture\\";
	
>>>>>>> cf47b343858ac6b5f28f011156fa0e65fe390586
	//class variables
	private String username;
	private serverInterface server;
    private ObservableList data = FXCollections.observableArrayList();
    
    //game variables
    private String nextPlayer;
    private String currentPlayer;
    private Card topCard;
    private ListView scoreBoard;
    private List<Card> cardsList;
    private int gameID;
    private String gameName;
    private List<Opponent> opponents;
    private boolean yourTurn, readyToStart;
    
    //back-image
    private Image backImage;
    private Card selectedCard;
    
    //fxml variables
    @FXML
    private Label title;
    
    @FXML
    private TextField opponent1, opponent2, opponent3;
    
    @FXML
    private ImageView image_lastcard;
    
    @FXML
    private VBox opponent2Box, opponent3Box;
    
    @FXML
    private HBox opponent1Box, userBox;
    
    @FXML
    private ImageView itopCard;
    
    //chat variables
    @FXML
    private TextField chat_input;
    
    @FXML
    private TextArea chat_output;
    
	public GameController(String username, serverInterface server, int gameID, String gameName) throws RemoteException, FileNotFoundException {
		this.username = username;
		this.server = server;
		cardsList = new ArrayList<>();
		opponents = new ArrayList<>();
		this.gameID = gameID;
		this.gameName = gameName;
		backImage = new Image(new FileInputStream(path + "UNO-Back.png"));

	}
	
	public GameController(String username) throws RemoteException, FileNotFoundException{
		this.username = username;
		cardsList = new ArrayList<>();
		opponents = new ArrayList<>();
		
		backImage = new Image(new FileInputStream(path + "UNO-Back.png"));

	}
	
	public void initialize() throws RemoteException {
		yourTurn = false;
		this.readyToStart = false;
		title.setText(gameName);
		itopCard.setImage(backImage);
		itopCard.setFocusTraversable(true);
		itopCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event->{
			try {
				System.out.println("pressed!");
				if(!readyToStart) {
					server.readyToStart(gameID, username);
					readyToStart = true;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
	}
	
	@FXML
	public void sendMsg() throws RemoteException {
		server.sendGameMsg(chat_input.getText(), gameID, username);
		chat_input.setText("");
	}

	@FXML
	public void enteredOpaque() {
		
	}
	
	@Override
	public void setMsg(String msg) throws RemoteException {
		String message = chat_output.getText() + msg + "\n";
		chat_output.setText(message);
	}

	@Override
	public void setScoreboard(List<String> scoreboard) throws RemoteException {
		data.clear();
		data.addAll(scoreboard);
		scoreBoard.setItems(data);
	}

	// add player in de lijst van spelers in het spel
	@Override
	public void addPlayer(String username, int aantal) throws RemoteException {
		Opponent opponent = new Opponent(username, aantal, 0);
		opponents.add(opponent);
		switch(opponents.size()) {
		case 1: {
			opponent1.setText(username);
			opponent.setId(1);
			break;
		}
		case 2: {
			opponent2.setText(username);
			opponent.setId(2);
			break;
		}
		case 3: {
			opponent3.setText(username);
			opponent.setId(3);
			break;
		}
		default: break;
		}
		setOpponentCards(opponent);
	}

	private void setOpponentCards(Opponent opponent) {
		if (opponent.id == 1 ) {
			ImageView imageView;
			for(int i = 0; i< opponent.getAmountCards(); i++) {
				imageView = new ImageView(backImage);
				imageView.setFitHeight(100);
				imageView.setPreserveRatio(true);
				imageView.setRotate(180);
				opponent1Box.getChildren().add(imageView);
			}
		}
		if (opponent.id == 2 ) {
			ImageView imageView;
			for(int i = 0; i< opponent.getAmountCards(); i++) {
				imageView = new ImageView(backImage);
				imageView.setFitHeight(50);
				imageView.setPreserveRatio(true);
				imageView.setRotate(90);
				opponent2Box.getChildren().add(imageView);
			}
		}
		if (opponent.id == 3 ) {
			ImageView imageView;
			for(int i = 0; i< opponent.getAmountCards(); i++) {
				imageView = new ImageView(backImage);
				imageView.setFitHeight(50);
				imageView.setPreserveRatio(true);
				imageView.setRotate(270);
				opponent3Box.getChildren().add(imageView);
			}
		}
	}
	
	private void setMyCardsTest() throws FileNotFoundException {
		Card card1 = new Card(2, 4);
		Card card2 = new Card(1, 3);
		Card card3 = new Card(4, 8);
		Card card4 = new Card(3, 0);
		
		Image image1 = new Image(new FileInputStream(path + card1.cardName));
		Image image2 = new Image(new FileInputStream(path + card2.cardName));
		Image image3 = new Image(new FileInputStream(path + card3.cardName));
		Image image4 = new Image(new FileInputStream(path + card4.cardName));

		ImageView imageView1 = new ImageView(image1);
		ImageView imageView2 = new ImageView(image2);
		ImageView imageView3 = new ImageView(image3);
		ImageView imageView4 = new ImageView(image4);
		
		imageView1.setFitHeight(125);
		imageView1.setPreserveRatio(true);
		
		imageView2.setFitHeight(125);
		imageView2.setPreserveRatio(true);
		
		imageView3.setFitHeight(125);
		imageView3.setPreserveRatio(true);
		
		imageView4.setFitHeight(125);
		imageView4.setPreserveRatio(true);
		
		
		userBox.getChildren().add(imageView1);
		userBox.getChildren().add(imageView2);
		userBox.getChildren().add(imageView3);
		userBox.getChildren().add(imageView4);
		
	}
	
	private void dummyTest() {
		Opponent dummy = new Opponent("Op1", 7, 0);
		opponents.add(dummy);
		dummy.setId(1);
		opponent1.setText(dummy.getName());
		setOpponentCards(dummy);
		
		Opponent dummy2 = new Opponent("Op2", 7, 0);
		opponents.add(dummy2);
		dummy2.setId(2);
		opponent2.setText(dummy2.getName());
		setOpponentCards(dummy2);
		
		Opponent dummy3 = new Opponent("Op3", 10, 0);
		opponents.add(dummy3);
		dummy3.setId(3);
		opponent3.setText(dummy3.getName());
		setOpponentCards(dummy3);
		
		cardsList.add(new Card(2, 5));
		cardsList.add(new Card(1, 0));
		cardsList.add(new Card(3, 5));
		cardsList.add(new Card(4, 7));
		cardsList.add(new Card(1, 8));
		cardsList.add(new Card(2, 5));
		cardsList.add(new WildCard());
		cardsList.add(new SkipCard(3, 1));
		cardsList.add(new DrawCard(1, 2));
		cardsList.add(new ReverseCard(2));
		
		try {
			setMyCards();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private synchronized void setMyCards() throws FileNotFoundException {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				ImageView imageView;
				Image image;
				for (Card card: cardsList) {
					try {
						image = new Image(new FileInputStream(path + card.cardName));
						imageView = new ImageView(image);
						imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
					         for(int i = 0; i<userBox.getChildren().size(); i++) {
					        	 if(userBox.getChildren().get(i)==event.getTarget()) {
					        		 selectedCard = cardsList.get(i);
					        		 break;
					        	 }
					         }
					         System.out.println(selectedCard.cardName);
					         event.consume();
					     });
						imageView.setFocusTraversable(true);
						imageView.setFitHeight(125);
						imageView.setPreserveRatio(true);
						userBox.getChildren().add(imageView);
						System.out.println(card.cardName);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					
				}
			}
		});
		
	}

	// add cards in lijst van kaarten van de speler
	@Override
	public void addCards(List<Card> cards) throws RemoteException {
		this.cardsList.addAll(cards);
		try {
			setMyCards();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//return the selected card in GUI
	@Override
	public synchronized Card getCard() throws RemoteException {
		selectedCard = null;
		while (selectedCard == null || !selectedCard.canPlayOn(topCard)) {

		}
		return selectedCard;
	}

	@Override
	public void setNextPlayer(String username) throws RemoteException {
		nextPlayer = username;
	}

	@Override
	public void addPile(Card card) throws RemoteException {
		System.out.println("Top card " + card.cardName);
		topCard = card;
		try {
			itopCard.setImage(new Image(new FileInputStream(path + card.cardName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCardAmountPlayer(String username, int amount) throws RemoteException {
		for(Opponent opponent : opponents) {
			if(opponent.getName()==username) {
				opponent.setAmountCards(amount);
				setOpponentCards(opponent);
			}
		}
	}
}

class Opponent {
	String name;
	int amountCards;
	int score;
	public int id;
	
	public Opponent (String name, int amountCards, int score) {
		this.name = name;
		this.amountCards = amountCards;
		this.score = score;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAmountCards() {
		return amountCards;
	}

	public void setAmountCards(int amountCards) {
		this.amountCards = amountCards;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
}
