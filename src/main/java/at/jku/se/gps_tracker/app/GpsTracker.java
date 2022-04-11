package at.jku.se.gps_tracker.app;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import at.jku.se.gps_tracker.model.ImportExport;
import at.jku.se.gps_tracker.model.Track;
import javafx.application.Application;
import javafx.stage.Stage;

public class GpsTracker extends Application implements ImportExport {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		// folder for complete testing = C:\\Users\\Ozan\\Desktop\\WINF-Studium\\4. Semester\\PR - Software Engineering\\Theorie\\test data
		File dir = new File("C:\\Users\\Ozan\\Desktop\\koglerau2.gpx");
		String[] extensions = new String[] { "gpx", "tcx" };
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		long start = System.nanoTime();
		List<Track> tracks = updateTracks(files);
		long end = System.nanoTime();
		System.out.println("Zeit fürs Parsen von "+ tracks.size() +" gpx-Dateien: "+(double) (end-start)/1000000);
		System.exit(1);
	}

}
