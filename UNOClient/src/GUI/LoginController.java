package GUI;


import dispatcherInterfaces.dispatcherInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import clientInterfaces.clientInterfaceImpl;
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
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverInterfaces.serverInterface;

public class LoginController {
	
    dispatcherInterface dispatcher;
    serverInterface server = null;
    clientInterfaceImpl client;
    String username= null;
    
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

    //singup naar voorgrond brengen
    @FXML
    private void SignUpUp() {
        pn_Login.toBack();
        pn_Register.toFront();
    }

    //signin naar voorgrond brengen
    @FXML
    private void SignInUp() {
        pn_Register.toBack();
        pn_Login.toFront();
    }
    
    //init, connectie naar dispatcher opzetten, connectie met vrije server starten
    public void initialize() throws RemoteException, NotBoundException {
    	Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        dispatcher = (dispatcherInterface) registry.lookup("UNOdispatcher");
        
        int port = dispatcher.getPort();
        System.out.println(port);
        
    	registry = LocateRegistry.getRegistry("localhost", port);
    	server = (serverInterface) registry.lookup("UNOserver");
    	
    	if(server.ping()) {
    		System.out.println("connection established!");
    	}
    }

    //login in account
    @FXML
    private void Login() throws RemoteException, NotBoundException {
        if (server.login(loginUsername.getText(), loginPassword.getText())!=null) {
        	username = loginUsername.getText();
            startLobby();
            Stage stage = (Stage) btn_Login.getScene().getWindow();
            stage.close();
        } else {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("The password of username was incorrect!");
            errorAlert.showAndWait();
        }
    }

    //register new account
    @FXML
    private void Register() throws RemoteException, NotBoundException {
        if (password1.getText().toCharArray().length <= 6) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("The size of password must be at least 6 characters long.");
            errorAlert.showAndWait();
        } else {

            if (password1.getText().equals(password2.getText())) {
                username = registerUsername.getText();
                if (server.register(registerUsername.getText(), password1.getText())!= null) {
                    startLobby();
                    Stage stage = (Stage) btn_Login.getScene().getWindow();
                    stage.close();
                } else {
                    Alert errorAlert = new Alert(AlertType.ERROR);
                    errorAlert.setHeaderText("Input not valid");
                    errorAlert.setContentText("The username is already in use, please login or use a new username");
                    errorAlert.showAndWait();
                }
            } else {
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setHeaderText("Input not valid");
                errorAlert.setContentText("The passwords do not match");
                errorAlert.showAndWait();
            }
        }
    }

    //start lobby met fmxl pagina
    private void startLobby() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Lobby.fxml"));
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
