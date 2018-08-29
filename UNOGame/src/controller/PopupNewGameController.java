package controller;

import java.rmi.RemoteException;
import interfaces.serverInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.NewGameService;

public class PopupNewGameController {
	private String username;
	private serverInterface server;
	ObservableList<Integer> themeList = FXCollections.observableArrayList(0, 1);
	ObservableList<Integer> numberOfPlayerList = FXCollections.observableArrayList(1, 2, 3, 4);

	public PopupNewGameController(String username, serverInterface server) {
		this.username = username;
		this.server = server;
		themePicker.setItems(themeList);
		numberOfPlayersnew.setItems(numberOfPlayerList);

	}

	@FXML
	TextField name;

	@FXML
	ChoiceBox<Integer> themePicker = new ChoiceBox<>(themeList);

	@FXML
	ChoiceBox<Integer> numberOfPlayersnew = new ChoiceBox<>(numberOfPlayerList);

	@FXML
	TextField playerAmount;

	@FXML
	Button btn_start, btn_cancel;

	@FXML
	public void initialize() {
		themePicker.setItems(themeList);
		numberOfPlayersnew.setItems(numberOfPlayerList);
	}

	@FXML
	public void startGame() throws RemoteException {
		int aantal = numberOfPlayersnew.getValue();
		String gameName = name.getText();

		if (gameName == "") {
			gameName = username + "'s game";
		}

		NewGameService newGameService = new NewGameService(gameName, themePicker.getValue(),
				numberOfPlayersnew.getValue(), server);
		newGameService.setOnSucceeded(Success -> {
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
