package client;

import controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestGame extends Application{

	@Override
    public void start(Stage stage) throws Exception {
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/GUI/Game.fxml"));
        GameController controller = new GameController("Wouter", 1);

        fxmlLoader.setController(controller);
        Parent root = (Parent) fxmlLoader.load();
        stage.setTitle("Game");
        stage.setScene(new Scene(root));
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
