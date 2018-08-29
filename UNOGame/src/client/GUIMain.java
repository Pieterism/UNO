package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class GUIMain extends Application {

	private static String token;

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/controller/Login.fxml"));

		stage.setTitle("FXML Welcome");
		stage.setScene(new Scene(root, 800, 450));
		stage.show();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
