package at.jku.se.gps_tracker.app;

import at.jku.se.gps_tracker.model.DataModel;
import at.jku.se.gps_tracker.controller.TrackManagerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
/**
 * class starts the application by loading in the associated fxml File and showing the GUI
 * @author Ozan
 *
 */
public class GpsTracker extends Application {
	
	/**
	 * launching of the Java-FX by also calling the start method
	 * @author Ozan
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * steps to be taken when the application gets started
	 * includes the creation of the datamodel and TrackManagerController objects and their loading
	 * @author Ozan
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {		
		primaryStage.setTitle("TrackStar");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TrackManager.fxml"));
		TrackManagerController start = new TrackManagerController(new DataModel());
				 
		loader.setController(start);
		Parent root = loader.load();
		
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon/icon.png")));
		primaryStage.setScene(new Scene(root,800,600));
		primaryStage.show();
		primaryStage.setOnCloseRequest(c -> System.exit(1));
	}
}