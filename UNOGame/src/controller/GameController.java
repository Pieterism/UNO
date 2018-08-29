package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import interfaces.gameControllerInterface;
import interfaces.serverInterface;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uno.Card;
import uno.DrawCard;
import uno.ReverseCard;
import uno.SkipCard;
import uno.WildCard;

public class GameController extends UnicastRemoteObject implements gameControllerInterface {

	private String path = "D:\\Google Drive\\School\\2017-2018\\1e Semester\\Gedistribueerde Systemen\\Opdracht UNO\\GIT_UNO\\UNOGame\\src\\pictures\\";

	// private String path =
	// "C:\\Users\\wouter\\Documents\\School\\geavanceerde\\UNOGame\\src\\pictures\\";

	// class variables
	private String username;
	private serverInterface server;
	private ObservableList data = FXCollections.observableArrayList();
	private List<ImageView> cards = new ArrayList<>();

	// game variables
	private int gameTheme;
	private String nextPlayer;
	private String currentPlayer;
	private Card topCard;
	private ListView scoreBoard;
	private List<Card> cardsList;
	private int gameID;
	private String gameName;
	private List<Opponent> opponents;
	private boolean yourTurn, readyToStart, boolDrawCard, colourSelected;

	private int selectedColor;

	// back-image
	private Image backImage;
	private Card selectedCard;

	// fxml variables
	@FXML
	private Label title;

	@FXML
	private Button btn_red, btn_green, btn_blue, btn_yellow, btn_exit;

	@FXML
	private TextField opponent1, opponent2, opponent3;

	@FXML
	private ImageView image_lastcard;

	@FXML
	private VBox opponent2Box, opponent3Box;

	@FXML
	private HBox opponent1Box, userBox;

	@FXML
	private ImageView btn_drawCard;

	// chat variables
	@FXML
	private TextField chat_input;

	@FXML
	private TextArea chat_output, text_scoreboard;

	public GameController(String username, serverInterface server, int gameID, String gameName, int gameTheme)
			throws RemoteException, FileNotFoundException {
		this.username = username;
		this.server = server;
		cardsList = new ArrayList<>();
		opponents = new ArrayList<>();
		this.gameID = gameID;
		this.gameName = gameName;
		this.gameTheme = gameTheme;
		backImage = new Image(new FileInputStream(path + gameTheme + "\\" + "UNO-Back.png"));

	}

	public GameController(String username, int gameTheme) throws RemoteException, FileNotFoundException {
		this.username = username;
		cardsList = new ArrayList<>();
		opponents = new ArrayList<>();
		this.gameTheme = gameTheme;

		backImage = new Image(new FileInputStream(path + gameTheme + "\\" + "UNO-Back.png"));

	}

	public void initialize() throws RemoteException {
		yourTurn = false;
		this.readyToStart = false;
		title.setText(gameName);
		btn_drawCard.setImage(backImage);
		btn_drawCard.setFocusTraversable(true);
		btn_drawCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			try {
				System.out.println("pressed!");
				if (!readyToStart) {
					server.readyToStart(gameID, username, gameTheme);
					this.readyToStart = true;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			event.consume();
		});

		btn_red.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			this.colourSelected = true;
			this.selectedColor = Card.COLOUR_RED;
			event.consume();
		});

		btn_blue.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			this.colourSelected = true;
			this.selectedColor = Card.COLOUR_BLUE;
			event.consume();
		});

		btn_yellow.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			this.colourSelected = true;
			this.selectedColor = Card.COLOUR_YELLOW;
			event.consume();
		});

		btn_green.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			this.colourSelected = true;
			this.selectedColor = Card.COLOUR_GREEN;
			event.consume();
		});

		btn_drawCard.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			boolDrawCard = true;
			event.consume();
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
		text_scoreboard.clear();
		for (String string : scoreboard) {
			text_scoreboard.setText(string + "\n");
		}

	}

	// add player in de lijst van spelers in het spel
	public void addPlayer(String username, int aantal) {
		Opponent opponent = new Opponent(username, aantal, 0);
		opponents.add(opponent);
		switch (opponents.size()) {
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
		default:
			break;
		}
	}

	private void setOpponentCards(Opponent opponent) {
		if (opponent.id == 1) {
			opponent1Box.getChildren().clear();
			ImageView imageView;
			for (int i = 0; i < opponent.getAmountCards(); i++) {
				imageView = new ImageView(backImage);
				imageView.setFitHeight(100);
				imageView.setPreserveRatio(true);
				imageView.setRotate(180);
				opponent1Box.getChildren().add(imageView);
			}
		}
		if (opponent.id == 2) {
			opponent2Box.getChildren().clear();
			ImageView imageView;
			for (int i = 0; i < opponent.getAmountCards(); i++) {
				imageView = new ImageView(backImage);
				imageView.setFitHeight(50);
				imageView.setPreserveRatio(true);
				imageView.setRotate(90);
				opponent2Box.getChildren().add(imageView);
			}
		}
		if (opponent.id == 3) {
			opponent3Box.getChildren().clear();
			ImageView imageView;
			for (int i = 0; i < opponent.getAmountCards(); i++) {
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

		Image image1 = new Image(new FileInputStream(path + gameTheme + "\\" + card1.cardName));
		Image image2 = new Image(new FileInputStream(path + gameTheme + "\\" + card2.cardName));
		Image image3 = new Image(new FileInputStream(path + gameTheme + "\\" + card3.cardName));
		Image image4 = new Image(new FileInputStream(path + gameTheme + "\\" + card4.cardName));

		ImageView imageView1 = new ImageView(image1);
		ImageView imageView2 = new ImageView(image2);
		ImageView imageView3 = new ImageView(image3);
		ImageView imageView4 = new ImageView(image4);

		imageView1.setFitHeight(100);
		imageView1.setPreserveRatio(true);

		imageView2.setFitHeight(100);
		imageView2.setPreserveRatio(true);

		imageView3.setFitHeight(100);
		imageView3.setPreserveRatio(true);

		imageView4.setFitHeight(100);
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

	private void setMyCards() throws FileNotFoundException {
		ImageView imageView;
		Image image;
		userBox.getChildren().clear();
		try {
			for (Card card : cardsList) {

				image = new Image(new FileInputStream(path + gameTheme + "\\" + card.cardName), 0, 100, true, true);
				imageView = new ImageView(image);
				imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
					for (int i = 0; i < userBox.getChildren().size(); i++) {
						if (userBox.getChildren().get(i) == event.getTarget()) {
							selectedCard = cardsList.get(i);
							break;
						}
					}
					event.consume();
				});
				imageView.setFocusTraversable(true);
				imageView.setFitHeight(125);
				imageView.setPreserveRatio(true);
				userBox.getChildren().add(imageView);
				System.out.println(card.cardName);

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ConcurrentModificationException cme) {

		}

	}

	// add cards in lijst van kaarten van de speler
	@Override
	public void addCards(List<Card> cards) throws RemoteException {
		cardsList.addAll(cards);

		// run on UI thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					setMyCards();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// return the selected card in GUI
	@Override
	public Card getCard() throws RemoteException {
		this.setMsg("It is your turn, play a card!");

		// logic to select a card
		Card temp = getCardRec();
		if (temp == null) {
			return null;
		}
		Iterator<Card> iterator = cardsList.iterator();
		while (iterator.hasNext()) {
			Card card = iterator.next();
			if (card.cardName.equals(temp.cardName)) {
				cardsList.remove(temp);
				break;
			}
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// view update
				try {
					setMyCards();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		return temp;
	}

	public Card getCardRec() {
		selectedCard = null;
		boolDrawCard = false;
		while (selectedCard == null && !boolDrawCard) {
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (selectedCard == null) {
			return null;
		}
		if (!selectedCard.canPlayOn(topCard)) {
			return getCardRec();
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
			image_lastcard.setImage(new Image(new FileInputStream(path + gameTheme + "\\" + card.cardName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCardAmountPlayer(String username, int amount) throws RemoteException {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (Opponent opponent : opponents) {
					if (opponent.getName().equals(username)) {
						opponent.setAmountCards(amount);
						setOpponentCards(opponent);
					}
				}
			}
		});
	}

	@Override
	public void setReady(boolean b) throws RemoteException {
		this.readyToStart = b;
		this.cardsList.clear();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				userBox.getChildren().clear();
				try {
					btn_drawCard.setImage(new Image(new FileInputStream(path + gameTheme + "\\" + "UNO-Back.png")));
					if (!b) {
						opponent1Box.getChildren().clear();
						opponent2Box.getChildren().clear();
						opponent3Box.getChildren().clear();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	@Override
	public int askColor() throws RemoteException {
		setMsg("Chose a color!");
		btn_blue.setOpacity(1);
		btn_green.setOpacity(1);
		btn_red.setOpacity(1);
		btn_yellow.setOpacity(1);
		this.selectedColor = Card.COLOUR_BLUE;
		this.colourSelected = false;
		while (!colourSelected) {
			try {
				Thread.sleep(100L);
			} catch (Exception e) {
			}
		}

		btn_blue.setOpacity(0.7);
		btn_green.setOpacity(0.7);
		btn_red.setOpacity(0.7);
		btn_yellow.setOpacity(0.7);

		return selectedColor;
	}

	@Override
	public void sendPlayerInfo(ArrayList<String> info) throws RemoteException {
		if (opponents.isEmpty()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					for (String string : info) {
						if (!string.equals(username)) {
							addPlayer(string, 0);
						}
					}
				}
			});
		}
	}
}

class Opponent {
	String name;
	int amountCards;
	int score;
	public int id;

	public Opponent(String name, int amountCards, int score) {
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
