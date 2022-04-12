package at.jku.se.gps_tracker.model;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
	private List<Track> tracks;
	String currentDirectory;
	String[] directoryFolders;
	String currentDirectoryFolder;
	
	public DataModel() {
		tracks = new ArrayList<>();
	}
	
	public void setCurrrentDirectory(String currentDirectory) {
		this.currentDirectory = currentDirectory;
	}
	
	public void setCurrentDirectoryFolder(int index) {
		this.currentDirectoryFolder = directoryFolders[index];
	}
}
