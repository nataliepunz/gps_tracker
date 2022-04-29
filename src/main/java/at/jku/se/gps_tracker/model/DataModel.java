package at.jku.se.gps_tracker.model;

import at.jku.se.gps_tracker.data.DataBaseOperations;
import at.jku.se.gps_tracker.data.TrackParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DataModel {
	
	private final String DATABASE_NAME = "track.db";
	
	private ObservableList<AbstractTrack> trackList;
	private String currentDirectory;
	private ObservableList<String> directoryFolders;
	private String currentDirectoryFolder;
	private String[] extensions;
	private HashSet<File> readFiles;
	private DataBaseOperations conn;

	public DataModel() {
		trackList = FXCollections.observableArrayList();
		extensions = new String[] { "gpx", "tcx" };
		conn = new DataBaseOperations(this);
	}

	public void setCurrrentDirectory(String currentDirectory) {
		if(currentDirectory==null) {
			return;
		}
		this.currentDirectory = currentDirectory;
		this.directoryFolders = FXCollections.observableArrayList(new File(this.currentDirectory).list((dir, name) -> new File(dir, name).isDirectory()));
		establishDBConnection();
		if(!directoryFolders.isEmpty()){
			readFiles = new HashSet<>();
			setCurrentDirectoryFolder(0);
		}
	}
	
	private void establishDBConnection() {
		if(conn!=null){
			conn.establishConnection(FilenameUtils.concat(currentDirectory, DATABASE_NAME));
		}
	}

	public void setCurrentDirectoryFolder(int index) {
		this.currentDirectoryFolder = directoryFolders.get(index);
		updateModel();
	}

	public void updateModel() {
		long start = System.nanoTime();
		List<File> files = (List<File>) FileUtils.listFiles(new File(currentDirectory,currentDirectoryFolder), extensions, true);
		conn.removeTracks(files, currentDirectoryFolder);
		conn.addTracks(files, currentDirectoryFolder);
		trackList.clear();
		trackList.addAll(conn.getTracks(currentDirectoryFolder));
		System.out.println("Zeit fürs Einlesen von "+ trackList.size() +" GPS-Dateien: "+(double) (System.nanoTime()-start)/1000000);
	}

	public ObservableList<AbstractTrack> getTrackList(){
		return this.trackList;
	}
	
	public ObservableList<String> getDirectoryFolders() {
		return this.directoryFolders;
	}	
}