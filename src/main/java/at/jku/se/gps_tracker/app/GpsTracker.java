package at.jku.se.gps_tracker.app;

import at.jku.se.gps_tracker.model.DataModel;
import at.jku.se.gps_tracker.controller.TrackManagerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GpsTracker extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {		
		DataModel model = new DataModel();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TrackManager.fxml"));
		TrackManagerController start = new TrackManagerController(model);
				 
		loader.setController(start);
		 
		Parent root = loader.load();
		
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon/icon.png")));
		primaryStage.setScene(new Scene(root,800,600));
		primaryStage.show();
		primaryStage.setOnCloseRequest(c -> System.exit(1));
	}
}