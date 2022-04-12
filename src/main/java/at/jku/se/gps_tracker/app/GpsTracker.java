package at.jku.se.gps_tracker.app;

import at.jku.se.gps_tracker.model.DataModel;
import at.jku.se.gps_tracker.model.ImportExport;
import at.jku.se.gps_tracker.controller.TrackManagerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GpsTracker extends Application implements ImportExport {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		// folder for complete testing = C:\\Users\\Ozan\\Desktop\\WINF-Studium\\4. Semester\\PR - Software Engineering\\Theorie\\test data
		
		DataModel model = new DataModel();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TrackManager.fxml"));
		TrackManagerController start = new TrackManagerController(model);
		 
		loader.setController(start);
		 
		Parent root = loader.load();
		 
		primaryStage.setTitle("TrackStar");
		primaryStage.setScene(new Scene(root,800,600));
		primaryStage.show();
		primaryStage.setOnCloseRequest(c -> System.exit(1));
	}
}


/*
File dir = new File("C:\\Users\\Ozan\\Desktop\\gpx_test");
String[] extensions = new String[] { "gpx", "tcx" };
List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
long start = System.nanoTime();
List<Track> tracks = updateTracks(files);
long end = System.nanoTime();
System.out.println("Zeit fürs Parsen von "+ tracks.size() +" gpx-Dateien: "+(double) (end-start)/1000000);
System.exit(1);
*/