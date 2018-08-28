package client;

import java.awt.geom.Rectangle2D;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class GUIMain extends Application {

	private static String token;

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/controller/Login.fxml"));

		stage.setTitle("FXML Welcome");
		stage.setScene(new Scene(root, 800, 450));
		stage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
