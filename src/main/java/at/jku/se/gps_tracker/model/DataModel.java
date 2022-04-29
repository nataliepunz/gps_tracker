package at.jku.se.gps_tracker.model;

import at.jku.se.gps_tracker.data.DataBaseOperations;
import at.jku.se.gps_tracker.data.TrackParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;

public class DataModel {
	
	private final String DATABASE_NAME = "track.db";
	
	private ObservableList<AbstractTrack> trackList;
	private ObservableList<TrackPoint> trackPoints;
	private String currentDirectory;
	private ObservableList<String> directoryFolders;
	private String currentDirectoryFolder;
	private String[] extensions;
	private HashSet<String> readFiles;
	private DataBaseOperations conn;

	public DataModel() {
		trackList = FXCollections.observableArrayList();
		extensions = new String[] { "gpx", "tcx" };
		conn = new DataBaseOperations();
	}

	public void setCurrrentDirectory(String currentDirectory) {
		if(this.currentDirectory!=null) {
			return;
		}
		this.currentDirectory = currentDirectory;
		this.directoryFolders = FXCollections.observableArrayList(new File(this.currentDirectory).list((dir, name) -> new File(dir, name).isDirectory()));
		conn.establishConnection(FilenameUtils.concat(currentDirectory, DATABASE_NAME));
		if(!directoryFolders.isEmpty()){
			readFiles = new HashSet<>();
			setCurrentDirectoryFolder(0);
		}
	}

	public void setCurrentDirectoryFolder(int index) {
		this.currentDirectoryFolder = directoryFolders.get(index);
		updateModel();
	}

	public void updateModel() {
		long start = System.nanoTime();
		/*
		List<File> filesAsFile = (List<File>) FileUtils.listFiles(new File(currentDirectory,currentDirectoryFolder), extensions, true);
		HashSet<String> files = new HashSet<>();
		filesAsFile.forEach(f -> files.add(f.getAbsolutePath()));
		TrackParser parser = new TrackParser(conn);
		trackList.clear();
		trackList.addAll(parser.addTracks(files, readFiles));
		parser.removeTracks(trackList,files,readFiles);
		*/
		trackList.addAll(conn.getTracks());
		System.out.println("Zeit fürs Einlesen von "+ trackList.size() +" GPS-Dateien: "+(double) (System.nanoTime()-start)/1000000);
	}

	public ObservableList<AbstractTrack> getTrackList(){
		return this.trackList;
	}

	public ObservableList<TrackPoint> getTrackPoints(){
		return this.trackPoints;
	}

	public ObservableList<String> getDirectoryFolders() {
		return this.directoryFolders;
	}
}
