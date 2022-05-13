package at.jku.se.gps_tracker.model;

import at.jku.se.gps_tracker.controller.ErrorPopUpController;
import at.jku.se.gps_tracker.data.TracksDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.List;

public class DataModel implements ErrorPopUpController {
	
	private static final String DATABASE_NAME = "track.db";
	private static final String[] EXTENSIONS = new String[] { "gpx", "tcx" };
	
	private ObservableList<AbstractTrack> trackList;
	private String directory;
	private ObservableList<String> directoryFolders;
	private String directoryFolder;
	private TracksDB conn;
	private Stage stage;

	public DataModel(Stage primaryStage) {
		this.trackList = FXCollections.observableArrayList();
		this.directoryFolders = FXCollections.observableArrayList();
		conn = new TracksDB();
		this.stage = primaryStage;
	}

	public void setDirectory(String currentDirectory) {
		if(currentDirectory == null) {
			return;
		}
		this.directory = currentDirectory;
		this.adjustDirectoryFolders();
	}
	
	public void adjustDirectoryFolders() {
		this.setDirectoryFolders();
		this.changeModel();
	}
	
	private void setDirectoryFolders() {
		if(directory==null) {
			return;
		}
		this.directoryFolders.clear();
		this.directoryFolders.addAll(new File(this.directory).list((dir, name) -> new File(dir, name).isDirectory()));
	}
	
	private void changeModel() {
		if(!directoryFolders.isEmpty()){
			establishDBConnection();
			setDirectoryFolder(directoryFolders.get(0));
		} else {
			directoryFolder = null;
			trackList.clear();
		}
	}
		
	private void establishDBConnection() {
		if(!this.directory.equals(conn.getDirectory())){
			if(conn.checkConnection()) {
				conn.closeConnection();
			}
			conn.establishConnection(FilenameUtils.concat(directory, DATABASE_NAME), directory);
		}
	}

	public void setDirectoryFolder(String directoryFolder) {
		this.directoryFolder = directoryFolder;
		if(directoryFolder.equals("All/Tracks")) {
			stage.setTitle("TrackStar");
		} else {
			stage.setTitle("TrackStar - "+FilenameUtils.concat(directory, directoryFolder));
		}	
	}

	public void updateModel() {
		long start = System.nanoTime();
		if(this.directoryFolder.equals("All/Tracks")) {
			updateModelAllTracks();
		} else {
			updateModelOneFolder();
		}
		System.out.println("Zeit fürs Einlesen von "+ trackList.size() +" GPS-Dateien: "+(double) (System.nanoTime()-start)/1000000);
		
	}
	
	private void updateModelAllTracks() {
		trackList.clear();
		for(String s : directoryFolders) {
			if (!new File(FilenameUtils.concat(directory,s)).exists()) {
				continue;
			}
			conn.updateDataBase(directory, s, EXTENSIONS);
			trackList.addAll(conn.getTracks(s));
		}
	}
	
	private void updateModelOneFolder() {
		trackList.clear();
		if(!checkDirectoryExistence()) {
			return;
		}
		conn.updateDataBase(directory, directoryFolder, EXTENSIONS);
		trackList.addAll(conn.getTracks(directoryFolder));
		
	}
	
	private boolean checkDirectoryExistence() {
		if(this.directoryFolder==null || this.directory==null) {
			showErrorPopUpNoWait("Ensure that a valid directory has been choosen! Otherwise update the directory!");
			stage.setTitle("TrackStar");
			return false;
		} else if (!new File(FilenameUtils.concat(directory,directoryFolder)).exists()) {
			showErrorPopUpNoWait("Directory does not exist anymore! Remember to update after every change in the directory!");
			adjustDirectoryFolders();
			return false;
		} else {
			return true;
		}
	}

	public ObservableList<AbstractTrack> getTrackList(){
		return this.trackList;
	}
	
	public ObservableList<String> getDirectoryFolders() {
		return this.directoryFolders;
	}
	
	public String getDirectoryFolder() {
		return this.directoryFolder;
	}
	
	public void closeConnection() {
		conn.closeConnection();
	}
	
	public boolean checkConnection() {
		return this.conn.checkConnection();
	}
	
	public List<TrackPoint> getTrackPoints(Track track){
		if(track.getTrackPoints()!=null) {
			return track.getTrackPoints();
		} else {
			track.setTrackPoints(conn.getTrackPoints(this.directory, track));
			return track.getTrackPoints();
		}
	}
}
