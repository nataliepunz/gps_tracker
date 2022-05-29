package at.jku.se.gps_tracker.model;

import at.jku.se.gps_tracker.data.TracksDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;
/**
 * class for managing the data model
 * this includes the directories, tracks and operations with the TrackDB-class
 * @author Ozan
 */
public class DataModel {
	
	/**
	 * static variable to save the keyword for desire to get all tracks
	 * @author Ozan 
	 */
	public static final String ALL_TRACK_KEYWORD = "All/Tracks";
	
	/**
	 * static variable to save the name of relevant database objects
	 * necessary for connection/creating the database object
	 * @author Ozan
	 */
	public static final String DATABASE_NAME = "track.db";
	
	/**
	 * static variable to save the parser supported data types
	 * necesary for distincting files from eachother and only getting supported ones
	 *  @author Ozan
	 */
	private static final String[] EXTENSIONS = new String[] { "gpx", "tcx" };
	
	/**
	 * the list of all the current tracks loaded into the program
	 * @author Ozan
	 */
	private ObservableList<Track> trackList;
	
	/**
	 * the current directory in which the database and files for tracks are
	 * @author Ozan
	 */
	private String directory;
	
	/**
	 * the directory folders inside the directory folder
	 * basis for the filtering options
	 * @author Ozan
	 */
	private ObservableList<String> directoryFolders;
	
	/**
	 * the current selected active directory folder
	 * @author Ozan
	 */
	private String directoryFolder;
	
	/**
	 * a TrackDB object for interacting with the associated database
	 * @author Ozan
	 */
	private TracksDB conn;

	/**
	 * constructor for a DataModel object
	 * instantiate the trackList and directoryFolders and creates TracksDB object
	 * @author Ozan
	 */
	public DataModel() {
		trackList = FXCollections.observableArrayList();
		directoryFolders = FXCollections.observableArrayList();
		conn = new TracksDB();
	}

	/**
	 * sets the directory in which the track database file and subfolders with their files are located
	 * @author Ozan
	 * @param directory
	 */
	public void setDirectory(String directory) {
		if(directory != null) {
			this.directory = directory;
		}
	}
	
	/**
	 * reads the folders inside the current directory and sets them inside a array
	 * @author Ozan
	 */
	public void setDirectoryFolders() {
		if(directory!=null) {
			directoryFolder=null;
			directoryFolders.clear();
			directoryFolders.addAll(new File(directory).list((dir, name) -> new File(dir, name).isDirectory()));
		}
	}
	
	/**
	 * checks if there are subfolders
	 * if yes a connection with the databsae is established and the first entry of the folders is set as the active one
	 * if no the tracklist gets cleared
	 * @author Ozan
	 */
	public void changeModel() {
		if(!directoryFolders.isEmpty()){
			conn.establishConnection(directory, DATABASE_NAME);
			setDirectoryFolder(directoryFolders.get(0));
		} else {
			trackList.clear();
		}
	}
	
	/**
	 * the current directory folder gets set
	 * the folder has to be part of the subfolders or the special keyword for all Tracks
	 * @author Ozan
	 * @param directoryFolder
	 */
	public void setDirectoryFolder(String directoryFolder) {
		if(directoryFolders.contains(directoryFolder) || directoryFolder.equals(ALL_TRACK_KEYWORD)) {
			this.directoryFolder = directoryFolder;
		}
	}
	
	/**
	 * returns the current directory
	 * @return directory as String
	 */
	public String getDirectoryFolder() {
		return this.directoryFolder;
	}

	/**
	 * if a active connection with the database is set then the tracks from the database are read
	 * eiter all tracks or only the ones inside the current set directoryFolder
	 * @author Ozan
	 */
	public void updateTrackListFromDB(){
		if(checkConnection()) {
			trackList.clear();
			if(ALL_TRACK_KEYWORD.equals(directoryFolder)) {
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
	

	/**
	 * checks and returns (based on the boolean value) either the difference between drive and database or the difference between database and drive in a specified folder
	 * @author Ozan
	 * @param boolean getTracksFromDrive
	 * @return if getTracksFromDrive true return the tracks in folder that are not in the DataBase; if false return tracks in DataBase that are not in the DataBase
	 * @throws FileNotFoundException if the folder requested from is not found
	 * @throws NullPointerException if there is no connection with the database or the directory/directoryfolder is null 
	 */
	
	public List<String> getDifferenceDriveAndDB(boolean getTracksFromDrive, String directoryFolder) throws NullPointerException, FileNotFoundException{
		if(directory==null || directoryFolder==null || !checkConnection()) {
			throw new NullPointerException();
		} else if (!checkFolderExists(directoryFolder)){
			throw new FileNotFoundException();
		}
		
		List<String> driveTracks = getTracksInFolder(directoryFolder);
		
		List<String> dataBaseTracks = conn.getTracksDBFileName(directoryFolder);
				
		if(getTracksFromDrive) {
			driveTracks.removeAll(dataBaseTracks);
			return driveTracks.stream()
							.map(s -> getTrackPath(s, directoryFolder))
							.collect(Collectors.toList());
		} else {
			dataBaseTracks.removeAll(driveTracks);
			return dataBaseTracks;
		}
	}
	
	/**
	 * add a given Track to the the database and to the tracklist
	 * @author Ozan
	 * @param trackFilePath
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public void addTrack(String trackFilePath) throws FileNotFoundException, XMLStreamException {
		Track t = conn.parseTrack(trackFilePath);
		trackList.add(t);
		conn.addTrackToDataBase(t);
	}
	
	/**
	 * remove a given FileName from the tracklist and the database
	 * @author Ozan
	 * @param trackFilePath
	 * @param directoryFolder
	 */
	public void removeTrack(String fileName, String directoryFolder) {
		conn.removeTrackFromDataBase(fileName, directoryFolder);
		removeTrackFromTrackList(new ArrayList<>(Arrays.asList(fileName, directoryFolder)));
	}
	
	/**
	 * implementation of the remove logic for the tracklist
	 * @author Ozan
	 * @param trackDetail
	 */
	private void removeTrackFromTrackList(List<String> trackDetail) {
		trackList.remove(trackList.stream()
			.filter(t -> t.getFileName().equals(trackDetail.get(0)) && t.getParentDirectory().equals(trackDetail.get(1)))
			.findAny()
			.orElse(null)
		);
	}
	
	/**
	 * get the trackpoints for a given track
	 * either returns the trackPoints already loaded into the program or reads them from the associated file
	 * @author Ozan
	 * @param track
	 * @return TrackPoints as List
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public List<TrackPoint> getTrackPoints(Track track) throws FileNotFoundException, XMLStreamException{
		if(track==null) {
			return new ArrayList<>();
		} else if(track.getTrackPoints()!=null && !track.getTrackPoints().isEmpty()) {
			return track.getTrackPoints();
		} else {
			track.setTrackPoints(this.conn.getTrackPoints(getTrackPath(track.getFileName(), track.getParentDirectory())));
			return track.getTrackPoints();
		}
	}
	
	/**
	 * returns the tracks inside of a given folder
	 * @author Ozan
	 * @param directoryFolder
	 * @return the filePaths of all track inside a given folder
	 */
	private List<String> getTracksInFolder(String directoryFolder) {
		List<File> tracksInFolder = (List<File>) FileUtils.listFiles(new File(directory,directoryFolder), EXTENSIONS, false);
		return tracksInFolder
						.stream()
						.map(f -> FilenameUtils.getName(f.getAbsolutePath()))
						.collect(Collectors.toList());
	}
	
	/**
	 * builds a filePath out of given directoryFolder and filename
	 * @author Ozan
	 * @param fileName of the track
	 * @param directoryFolder in which track is
	 * @return the full FilePath to the associated file of the track
	 */
	private String getTrackPath(String fileName, String directoryFolder) {
		return FilenameUtils.concat(FilenameUtils.concat(directory, directoryFolder),fileName);
	}
	
	/**
	 * returns the list of tracks
	 * @author Ozan
	 * @return Tracks as List
	 */
	public ObservableList<Track> getTrackList(){
		return trackList;
	}
	
	/**
	 * returns the list of directory subfolders
	 * @author Ozan
	 * @return subfolders as a list
	 */
	public ObservableList<String> getDirectoryFolders() {
		return directoryFolders;
	}
	
	/**
	 * checks if the current connection with the database is valid
	 * @author Ozan
	 * @return true if valid connection to database
	 */
	public boolean checkConnection() {
		return this.conn.checkConnection(this.directory);
	}
	
	/**
	 * checks and then closes the connection to the database
	 * @author Ozan
	 */
	public void closeConnection() {
		if(checkConnection()) {
			conn.closeConnection();
		}
	}
	
	/**
	 * checks if a given directory subfolder exists inside the current directory
	 * @param directoryFolder
	 * @return true if subfolder exists
	 */
	public boolean checkFolderExists(String directoryFolder) {
		return new File(FilenameUtils.concat(directory,directoryFolder)).exists();
	}
	
	
}
