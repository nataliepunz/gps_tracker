package at.jku.se.gps_tracker.model;

import at.jku.se.gps_tracker.controller.ErrorPopUpController;
import at.jku.se.gps_tracker.data.TracksDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

	public DataModel() {
		this.trackList = FXCollections.observableArrayList();
		this.directoryFolders = FXCollections.observableArrayList();
		conn = new TracksDB();
	}

	public void setDirectory(String currentDirectory) {
		if(currentDirectory == null) {
			return;
		}
		this.directory = currentDirectory;
		this.adjustDirectoryFolders();
	}
	
	private void adjustDirectoryFolders() {
		this.setDirectoryFolders();
		this.changeModel();
	}
	
	public void setDirectoryFolders() {
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
		if(conn!=null){
			conn.establishConnection(FilenameUtils.concat(directory, DATABASE_NAME));
			conn.setDirectory(directory);
		}
	}

	public void setDirectoryFolder(String directoryFolder) {
		this.directoryFolder = directoryFolder;
		if(!checkFolderExistence()) {
			this.directoryFolder=null;
			return;
		}
		updateModel();
	}

	public void updateModel() {
		trackList.clear();
		if(!checkFolderExistence()) {
			return;
		}
		if(directoryFolder!=null && directory!=null) {
			long start = System.nanoTime();
			conn.updateDataBase(directory, directoryFolder, EXTENSIONS);
			trackList.addAll(conn.getTracks(directoryFolder));
			System.out.println("Zeit fürs Einlesen von "+ trackList.size() +" GPS-Dateien: "+(double) (System.nanoTime()-start)/1000000);
		}
	}
	
	private boolean checkFolderExistence() {
		if((this.directory==null || this.directoryFolder==null)) {
			showErrorPopUp("Directory does not exist anymore! Remember to update after every change in the directory!");
			return false;
		} else if (!new File(FilenameUtils.concat(directory,directoryFolder)).exists()) {
			showErrorPopUp("Directory does not exist anymore! Remember to update after every change in the directory!");
			setDirectoryFolders();
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
	
	public void closeConnection() {
		conn.closeConnection();
	}
	
	public List<TrackPoint> getTrackPoints(Track track){
		if(track.getTrackPoints()!=null) {
			return track.getTrackPoints();
		} else {
			track.setTrackPoints(conn.getTrackPoints(track));
			return track.getTrackPoints();
		}
	}
}
