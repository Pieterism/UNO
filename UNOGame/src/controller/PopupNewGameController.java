package controller;

import java.rmi.RemoteException;

import interfaces.serverInterface;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.NewGameService;

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
		int aantal = 2;
		if (playerAmount!=null) {
			try {
				aantal = Integer.parseInt(playerAmount.getText());
				if (aantal >= 4) aantal = 4;
				if (aantal <= 2) aantal = 2;
			} catch (NumberFormatException nfe) {
			}
		}
		if (gameName == null) {
			gameName = username + "'s game"; 
		}
		
		if (gameDescription == null) {
			gameDescription = "A game made by " + username;
		}
		
		NewGameService newGameService = new NewGameService(gameName, gameDescription, aantal, server);
		newGameService.setOnSucceeded(Success ->{
			System.out.println("new game started!");
			
		});
		newGameService.start();
		exit();	
	}
    
	private void popUpAlert(String string) {
    	Alert errorAlert = new Alert(AlertType.ERROR);
    	errorAlert.setHeaderText("Input not valid");
    	errorAlert.setContentText(string);
    	errorAlert.showAndWait();		
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
