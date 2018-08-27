package controller;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import client.clientInterfaceImpl;
import interfaces.dispatcherInterface;
import interfaces.serverInterface;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.LoginService2;
import services.RegisterService;

public class LoginController {

	dispatcherInterface dispatcher;
	serverInterface server = null;
	clientInterfaceImpl client;
	String username = null;
	private int port;

	@FXML
	private Button btn_SignIn, btn_SignUp, btn_Login, btn_Register;

	@FXML
	private Pane pn_Login, pn_Register;

	@FXML
	private TextField registerUsername, loginUsername;

	@FXML
	private PasswordField password1, password2, loginPassword;

	@FXML
	private DatePicker birthdate;

	// singup naar voorgrond brengen
	@FXML
	private void SignUpUp() {
		pn_Login.toBack();
		pn_Register.toFront();
	}

	// signin naar voorgrond brengen
	@FXML
	private void SignInUp() {
		pn_Register.toBack();
		pn_Login.toFront();
	}

	// init, connectie naar dispatcher opzetten, connectie met vrije server starten
	public void initialize() throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry("localhost", 1099);
		dispatcher = (dispatcherInterface) registry.lookup("UNOdispatcher");

		port = dispatcher.getPort();
		System.out.println(port);
		registry = LocateRegistry.getRegistry("localhost", port);
		server = (serverInterface) registry.lookup("UNOserver");
	}

	// login in account
	@FXML
	private void Login() throws RemoteException, NotBoundException {
		if (loginUsername.getText().length() != 0 && loginPassword.getText().length() != 0) {
			LoginService2 loginService = new LoginService2(loginUsername.getText(), loginPassword.getText());
			loginService.setOnSucceeded(Success -> {
				boolean succes = Success.getSource().getValue() != null;
				if (succes) {
					this.username = loginUsername.getText();
					startLobby();
					Stage stage = (Stage) btn_Login.getScene().getWindow();
					stage.close();
				} else {
					popUpAlert("Password of username was incorrect!");
				}
			});
			loginService.start();
		} else {
			popUpAlert("Not all fields were filled out!");
		}
	}

	private void popUpAlert(String string) {
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setHeaderText("Input not valid");
		errorAlert.setContentText(string);
		errorAlert.showAndWait();
	}

	// register new account
	@FXML
	private void Register() throws RemoteException, NotBoundException {
		if (password1.getText().toCharArray().length <= 6) {
			popUpAlert("The size of password must be at least 6 characters long.");
		} else {

			if (password1.getText().equals(password2.getText())) {
				username = registerUsername.getText();
				if (username.length() <= 6) {
					popUpAlert("The size of the username must be at least 6 characters long.");
				} else {
					RegisterService registerService = new RegisterService(username, password1.getText(), server);
					registerService.setOnSucceeded(Success -> {
						boolean succes = Success.getSource().getValue() != null;
						if (succes) {
							startLobby();
							Stage stage = (Stage) btn_Login.getScene().getWindow();
							stage.close();
						} else {
							popUpAlert("Registration failed");
						}
					});
					registerService.start();

				}
			} else {
				popUpAlert("The passwords do not match");
			}
		}
	}

	// start lobby met fmxl pagina
	private void startLobby() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/controller/Lobby.fxml"));
			System.out.println(username);
			LobbyController controller = new LobbyController(username, client, server);
			fxmlLoader.setController(controller);

			Parent root1 = (Parent) fxmlLoader.load();

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("ABC");
			stage.setScene(new Scene(root1));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
