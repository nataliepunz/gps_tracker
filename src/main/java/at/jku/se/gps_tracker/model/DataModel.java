package at.jku.se.gps_tracker.model;

import at.jku.se.gps_tracker.data.TracksDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataModel {
	
	public static final String ALL_TRACK_KEYWORD = "All/Tracks";
	public static final String DATABASE_NAME = "track.db";
	private static final String[] EXTENSIONS = new String[] { "gpx", "tcx" };
		
	private ObservableList<Track> trackList;
	private String directory;
	private ObservableList<String> directoryFolders;
	private String directoryFolder;
	private TracksDB conn;

	public DataModel() {
		trackList = FXCollections.observableArrayList();
		directoryFolders = FXCollections.observableArrayList();
		conn = new TracksDB(EXTENSIONS);
	}

	public void setDirectory(String directory) {
		if(directory == null) {
			return;
		}
		this.directory = directory;
		adjustDirectoryFolders();
	}
	
	public void adjustDirectoryFolders() {
		setDirectoryFolders();
		changeModel();
	}
	
	private void setDirectoryFolders() {
		if(directory==null) {
			return;
		}
		directoryFolder=null;
		directoryFolders.clear();
		directoryFolders.addAll(new File(directory).list((dir, name) -> new File(dir, name).isDirectory()));
	}
	
	private void changeModel() {
		if(!directoryFolders.isEmpty()){
			conn.establishConnection(directory, DATABASE_NAME);
			adjustDirectoryFolder(directoryFolders.get(0));
		} else {
			trackList.clear();
		}
	}
	
	public void adjustDirectoryFolder(String directoryFolder) {
		if(directoryFolders.contains(directoryFolder) || directoryFolder.equals(ALL_TRACK_KEYWORD)) {
			this.directoryFolder = directoryFolder;
			updateTrackListFromDB();
		}
	}

	private void updateTrackListFromDB(){
		if(checkConnection()) {
			trackList.clear();
			if(directoryFolder.equals(ALL_TRACK_KEYWORD)) {
				for(String directoryFolderFromLoop : directoryFolders) {
					if (!new File(FilenameUtils.concat(directory,directoryFolderFromLoop)).exists()) {
						continue;
					}
					trackList.addAll(conn.getTracks(directoryFolderFromLoop));
				}
			} else {
				trackList.addAll(conn.getTracks(directoryFolder));
			}
		}
	}
	
	public void updateModel() {
		long start = System.nanoTime();
		if(checkConnection()) {
			if(directoryFolder!=null && directoryFolder.equals(ALL_TRACK_KEYWORD)) {
				updateModelAllTracks();
			} else {
				updateModelOneFolder();
			}
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
		if(directoryFolder==null || directory==null) {
			return false;
		} else if (!new File(FilenameUtils.concat(directory,directoryFolder)).exists()) {
			adjustDirectoryFolders();
			return false;
		} else {
			return true;
		}
	}

	public ObservableList<Track> getTrackList(){
		return trackList;
	}
	
	public ObservableList<String> getDirectoryFolders() {
		return directoryFolders;
	}
	
	public String getDirectoryFolder() {
		return this.directoryFolder;
	}
		
	public void closeConnection() {
		if(checkConnection()) {
			conn.closeConnection();
		}
	}
	
	private boolean checkConnection() {
		return this.conn.checkConnection(this.directory);
	}
	
	public List<TrackPoint> getTrackPoints(Track track){
		if(track==null) {
			return new ArrayList<>();
		} else if(track.getTrackPoints()!=null && !track.getTrackPoints().isEmpty()) {
			return track.getTrackPoints();
		} else {
			track.setTrackPoints(this.conn.getTrackPoints(track));
			return track.getTrackPoints();
		}
	}
}
