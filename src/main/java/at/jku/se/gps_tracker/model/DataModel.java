package at.jku.se.gps_tracker.model;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataModel implements ImportExport {
	private ObservableList<Track> trackList;
	private String currentDirectory;
	private ObservableList<String> directoryFolders;
	private String currentDirectoryFolder;
	private String[] extensions;
	
	public DataModel() {
		trackList = FXCollections.observableArrayList();
		extensions = new String[] { "gpx", "tcx" };
	}
	
	public void setCurrrentDirectory(String currentDirectory) {
		if(this.currentDirectory==currentDirectory) return;
		this.currentDirectory = currentDirectory;
		this.directoryFolders = FXCollections.observableArrayList(new File(this.currentDirectory).list((dir, name) -> new File(dir, name).isDirectory()));
		if(!directoryFolders.isEmpty()){
			setCurrentDirectoryFolder(0);
		}
	}
	
	public void setCurrentDirectoryFolder(int index) {
		this.currentDirectoryFolder = directoryFolders.get(index);
		updateModel();
	}
	
	public void updateModel() {
		List<File> files = (List<File>) FileUtils.listFiles(new File(currentDirectory+"\\"+currentDirectoryFolder), extensions, true);
		long start = System.nanoTime();
		updateTracks(trackList,files);
		long end = System.nanoTime();
		System.out.println("Zeit fürs Parsen von "+ trackList.size() +" gpx-Dateien: "+(double) (end-start)/1000000);
	}
	
	public ObservableList<Track> getTracks(){
		return this.trackList;
	}
	
	public ObservableList<String> getDirectoryFolders() {
		return this.directoryFolders;
	}
}
