package at.jku.se.gps_tracker.model;

import at.jku.se.gps_tracker.data.TrackParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;

public class DataModel {
	private ObservableList<AbstractTrack> trackList;
	private ObservableList<TrackPoint> trackPoints;
	private String currentDirectory;
	private ObservableList<String> directoryFolders;
	private String currentDirectoryFolder;
	private String[] extensions;
	private HashSet<String> readFiles;

	public DataModel() {
		trackList = FXCollections.observableArrayList();
		extensions = new String[] { "gpx", "tcx" };
	}

	public void setCurrrentDirectory(String currentDirectory) {
		if(this.currentDirectory!=null && this.currentDirectory.equals(currentDirectory)) {
			return;
		}
		this.currentDirectory = currentDirectory;
		this.directoryFolders = FXCollections.observableArrayList(new File(this.currentDirectory).list((dir, name) -> new File(dir, name).isDirectory()));
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
		List<File> filesAsFile = (List<File>) FileUtils.listFiles(new File(currentDirectory,currentDirectoryFolder), extensions, true);
		HashSet<String> files = new HashSet<>();
		filesAsFile.forEach(f -> files.add(f.getAbsolutePath()));
		long start = System.nanoTime();
		TrackParser parser = new TrackParser();
		trackList.clear(); // to make sure changeCategory doesnt simply add new lists but clears old selection
		trackList.addAll(parser.addTracks(files, readFiles));
		long end = System.nanoTime();
		parser.removeTracks(trackList,files,readFiles);
		System.out.println("Zeit fürs Parsen von "+ trackList.size() +" GPS-Dateien: "+(double) (end-start)/1000000);
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
