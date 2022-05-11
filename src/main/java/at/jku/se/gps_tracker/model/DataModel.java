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
	private String currentDirectory;
	private ObservableList<String> directoryFolders;
	private String currentDirectoryFolder;
	private TracksDB conn;

	public DataModel() {
		this.trackList = FXCollections.observableArrayList();
		this.directoryFolders = FXCollections.observableArrayList();
		conn = new TracksDB();
	}

	public void setCurrrentDirectory(String currentDirectory) {
		if(currentDirectory==null) {
			return;
		}
		this.currentDirectory = currentDirectory;
		this.directoryFolders.clear();
		this.directoryFolders.addAll(new File(this.currentDirectory).list((dir, name) -> new File(dir, name).isDirectory()));            
		if(!directoryFolders.isEmpty()){
			establishDBConnection();
			setCurrentDirectoryFolder(directoryFolders.get(0));
		}
	}
		
	private void establishDBConnection() {
		if(conn!=null){
			conn.establishConnection(FilenameUtils.concat(currentDirectory, DATABASE_NAME));
			conn.setDirectory(currentDirectory);
		}
	}

	public void setCurrentDirectoryFolder(String directory) {
		if(currentDirectoryFolder!=null && !new File(FilenameUtils.concat(currentDirectory,directory)).exists()) {
			showErrorPopUp("Directory does not exist anymore! Remember to update afer every change in the directory!");
			this.directoryFolders = FXCollections.observableArrayList(new File(this.currentDirectory).list((dir, name) -> new File(dir, name).isDirectory()));
			return;
		}
		this.currentDirectoryFolder = directory;
		updateModel();
	}

	public void updateModel() {
		trackList.clear();
		if(currentDirectoryFolder!=null && new File(FilenameUtils.concat(currentDirectoryFolder,currentDirectoryFolder)).exists()) {
			showErrorPopUp("Directory does not exist anymore! Remember to update afer every change in the directory!");
			this.directoryFolders = FXCollections.observableArrayList(new File(this.currentDirectory).list((dir, name) -> new File(dir, name).isDirectory()));
			return;
		}
		if(currentDirectoryFolder!=null && currentDirectory!=null) {
			long start = System.nanoTime();
			conn.updateDataBase(currentDirectory, currentDirectoryFolder, EXTENSIONS);
			trackList.addAll(conn.getTracks(currentDirectoryFolder));
			System.out.println("Zeit fürs Einlesen von "+ trackList.size() +" GPS-Dateien: "+(double) (System.nanoTime()-start)/1000000);
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
