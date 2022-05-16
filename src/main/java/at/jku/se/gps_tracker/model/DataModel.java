package at.jku.se.gps_tracker.model;

import at.jku.se.gps_tracker.controller.ErrorPopUpController;
import at.jku.se.gps_tracker.data.TracksDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataModel implements ErrorPopUpController {
	
	private static final String DATABASE_NAME = "track.db";
	private static final String[] EXTENSIONS = new String[] { "gpx", "tcx" };
	private static final String ALL_TRACK_KEYWORD = "All/Tracks";
	
	private ObservableList<Track> trackList;
	private String directory;
	private ObservableList<String> directoryFolders;
	private String directoryFolder;
	private TracksDB conn;
	private Stage stage;

	public DataModel(Stage primaryStage) {
		this.trackList = FXCollections.observableArrayList();
		this.directoryFolders = FXCollections.observableArrayList();
		conn = new TracksDB(EXTENSIONS);
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
		this.directoryFolder=null;
		this.directoryFolders.clear();
		this.directoryFolders.addAll(new File(this.directory).list((dir, name) -> new File(dir, name).isDirectory()));
	}
	
	public void changeModel() {
		if(!directoryFolders.isEmpty()){
			establishDBConnection();
			adjustDirectoryFolder(directoryFolders.get(0));
		} else {
			stage.setTitle("TrackStar - "+directory);
			trackList.clear();
		}
	}
			
	private void establishDBConnection() {
		if(!this.directory.equals(conn.getDirectory())){
			conn.establishConnection(FilenameUtils.concat(directory, DATABASE_NAME), directory);
		}
	}
	
	public void adjustDirectoryFolder(String directoryFolder) {
		setDirectoryFolder(directoryFolder);
		updateTrackListFromDB();
	}

	private void setDirectoryFolder(String directoryFolder) {
		this.directoryFolder = directoryFolder;
		if(directoryFolder.equals(ALL_TRACK_KEYWORD)) {
			stage.setTitle("TrackStar - "+directory+" - ALL TRACKS!");
		} else {
			stage.setTitle("TrackStar - "+FilenameUtils.concat(directory, directoryFolder));
		}
	}

	private void updateTrackListFromDB(){
		if(conn.checkConnection()) {
			trackList.clear();
			if(this.directoryFolder.equals(ALL_TRACK_KEYWORD)) {
				for(String directoryFolderFromLoop : directoryFolders) {
					if (!new File(FilenameUtils.concat(directory,directoryFolderFromLoop)).exists()) {
						continue;
					}
					trackList.addAll(conn.getTracks(directoryFolderFromLoop));
				}
			} else {
				this.trackList.addAll(conn.getTracks(directoryFolder));
			}
		}
	}
	
	public void updateModel() {
		long start = System.nanoTime();
		if(this.directoryFolder!=null && this.directoryFolder.equals(ALL_TRACK_KEYWORD)) {
			updateModelAllTracks();
		} else {
			updateModelOneFolder();
		}
		System.out.println("Zeit fürs Einlesen von "+ trackList.size() +" GPS-Dateien: "+(double) (System.nanoTime()-start)/1000000);
	}
	
	private void updateModelAllTracks() {
		for(String directoryFolderFromLoop : directoryFolders) {
			if (!new File(FilenameUtils.concat(directory,directoryFolderFromLoop)).exists()) {
				continue;
			}
			trackList.removeAll(removeTracks(conn.toBeRemovedTracks(directoryFolderFromLoop)));
			trackList.addAll(conn.toBeAddedTracks(directoryFolderFromLoop));
		}
	}
		
	private void updateModelOneFolder() {
		if(!checkDirectoryExistence()) {
			return;
		}
		trackList.removeAll(removeTracks(conn.toBeRemovedTracks(directoryFolder)));
		trackList.addAll(conn.toBeAddedTracks(directoryFolder));
	}
	
	private List<Track> removeTracks(List<List<String>> trackDetails) {
		List<Track> toBeRemovedTracks = new ArrayList<>();
		for(List<String> trackDetail : trackDetails) {
			toBeRemovedTracks.add(trackList.stream()
				.filter(t -> t.getFileName().equals(trackDetail.get(0)) && t.getParentDirectory().equals(trackDetail.get(1)))
				.findAny()
				.orElse(null)
			);
		}		
		return toBeRemovedTracks;
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

	public ObservableList<Track> getTrackList(){
		return this.trackList;
	}
	
	public ObservableList<String> getDirectoryFolders() {
		return this.directoryFolders;
	}
	
	public String getDirectoryFolder() {
		return this.directoryFolder;
	}
	
	public void closeConnection() {
		if(conn.checkConnection()) {
			conn.closeConnection();
		}
	}
	
	public boolean checkConnection() {
		return this.conn.checkConnection();
	}
	
	public List<TrackPoint> getTrackPoints(Track track){
		if(track==null) {
			return new ArrayList<>();
		} else if(track.getTrackPoints()!=null) {
			return track.getTrackPoints();
		} else {
			track.setTrackPoints(conn.getTrackPoints(track));
			return track.getTrackPoints();
		}
	}
}
