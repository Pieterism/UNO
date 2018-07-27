package GUI;

import java.rmi.RemoteException;
import java.util.Observable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import serverInterfaces.serverInterface;

public class PopupNewGameController {
	private String username;
	private serverInterface server;
	
	public PopupNewGameController(String username, serverInterface server) {
		this.username = username;
		this.server = server;
	}
	
	@FXML
	TextField name;
	
	@FXML 
	TextField description;
	
	@FXML
	TextField playerAmount;
	
	@FXML
	Button btn_start, btn_cancel;

	
	@FXML
	public void startGame() throws RemoteException {
		String gameName= name.getText();
		String gameDescription = description.getText();
		int aantal;
		if (playerAmount.getText().equals("")) {
			aantal = 2;
		}
		else {
			aantal = Integer.parseInt(playerAmount.getText());
			if (aantal >= 4) aantal = 4;
			if (aantal <= 2) aantal = 2;
		}
		
		if (gameName.equals("")) {
			gameName = username + "'s game"; 
		}
		else {
			
			if (description.getText() == null) {
				gameDescription = "A game made by " + username;
			}
			else {
				server.startNewGame(gameName, gameDescription, aantal);
				exit();
			}

			
		}
	}
	
	@FXML
	public void cancel() {
		exit();
	}
	
	public void exit() {
        Stage stage = (Stage) btn_cancel.getScene().getWindow();
        stage.close();
	}
}
