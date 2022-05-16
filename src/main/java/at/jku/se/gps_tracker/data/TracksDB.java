package at.jku.se.gps_tracker.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import at.jku.se.gps_tracker.controller.ErrorPopUpController;
import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;

public class TracksDB implements ErrorPopUpController {

	private static final String ERROR_ROLLBACK_MESSAGE = "ERROR! COULD NOT CONNECT TO DATABASE! ";
	private final String[] extensions;
	private Connection conn;
	private String directory;
	
	public TracksDB(String... extensions) {
		this.extensions=extensions;
	}
	
	public void establishConnection(String dataBaseLocation, String dataBaseFilePath) {
		boolean setUpNecessary = new File(dataBaseLocation).exists();
		String url = "jdbc:sqlite:"+dataBaseLocation;
		this.directory=dataBaseFilePath;
		try {
			if(checkConnection()) {
				closeConnection();
			}
			conn = DriverManager.getConnection(url);
			conn.setAutoCommit(false);
			if(!setUpNecessary) {
				setUpTables();
			}
		} catch (SQLException e) {  
			showErrorPopUp(ERROR_ROLLBACK_MESSAGE+e.getMessage());
		}
	}
	
	private void setUpTables() {		
		try(Statement stmt = conn.createStatement()){
			stmt.execute("CREATE TABLE tracks (name TEXT NOT NULL, fileName TEXT NOT NULL, folder TEXT NOT NULL, date TEXT NOT NULL, time TEXT NOT NULL, distance REAL NOT NULL, duration REAL NOT NULL, averageBPM INTEGER NOT NULL, maximumBPM INTEGER NOT NULL, elevation REAL NOT NULL, PRIMARY KEY(fileName,folder));");
            conn.commit();
        } catch (SQLException e) {  
        	showErrorPopUp("ERROR! COULD NOT CREATE TABLES! "+e.getMessage());
            try {
				conn.rollback();
			} catch (SQLException e1) {
				showErrorPopUp(ERROR_ROLLBACK_MESSAGE+e1.getMessage());
			}
        }  
	}
	
	public void addTrackToDataBase(String file, Track track) {
		if(file==null || track==null) return;
		try(PreparedStatement stmt = conn.prepareStatement("INSERT INTO tracks VALUES(?,?,?,?,?,?,?,?,?,?)")){
			stmt.setString(1, track.getName());
			stmt.setString(2, track.getFileName());
			stmt.setString(3, new File(file).getParentFile().getName());
			stmt.setString(4, track.getDate().toString());
			stmt.setString(5, track.getStartTime().toString());
			stmt.setDouble(6, track.getDistance());
			stmt.setDouble(7, track.getDurationNormal().toSeconds());
			stmt.setInt(8, track.getAverageBPM());
			stmt.setInt(9, track.getMaximumBPM());
			stmt.setDouble(10, track.getElevation());
			stmt.execute();
			
			conn.commit();
		} catch (SQLException e) {
			showErrorPopUp("ERROR! COULD NOT ADD TRACK! "+e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				showErrorPopUp(ERROR_ROLLBACK_MESSAGE+e1.getMessage());
			}
		}
	}
	
	public List<Track> getTracks(String directoryFolder){
		List<Track> trackHelpList = new ArrayList<>();
		try(PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tracks WHERE folder=?")){
			stmt.setString(1, directoryFolder);
			ResultSet rs = stmt.executeQuery();
			Track t = null;
			while(rs.next()) {
				t = new Track.TrackBuilder(rs.getString("folder"), rs.getString("fileName"), rs.getString("name"), LocalDate.parse(rs.getString("date")), LocalTime.parse(rs.getString("time")))
						.distance(rs.getDouble("distance"))
						.duration(Duration.ofSeconds((long) rs.getDouble("duration")))
						.averageBPM(rs.getInt("averageBPM"))
						.maximumBPM(rs.getInt("maximumBPM"))
						.elevation(rs.getDouble("elevation"))
						.build();
				trackHelpList.add(t);
			}
		} catch (SQLException e) {
			showErrorPopUp("ERROR! COULD NOT GET TRACKS! "+e.getMessage());
		}
		return trackHelpList;
	}
	
	public List<TrackPoint> getTrackPoints(Track track){
		if(new File(FilenameUtils.concat(FilenameUtils.concat(directory, track.getParentDirectory()),track.getFileName())).exists()) {
			return new TrackParser().getTrackPoints(FilenameUtils.concat(FilenameUtils.concat(directory, track.getParentDirectory()),track.getFileName()));
		} else {
			showErrorPopUp("ERROR! REMEMBER TO UPDATE THE PROGRAM AFTER EVERY CHANGE!");
			return new ArrayList<>();
		}
	}
	
	public List<File> returnFilesInFolder(String currentDirectoryFolder) {
		return (List<File>) FileUtils.listFiles(new File(directory,currentDirectoryFolder), extensions, false);
	}
	
	public List<List<String>> toBeRemovedTracks(String directoryFolder){
		List<List<String>> toBeRemovedTracksDetails = new ArrayList<>();
		HashSet<String> driveFiles = new HashSet<>();
		returnFilesInFolder(directoryFolder).forEach(f -> driveFiles.add(FilenameUtils.getName(f.getAbsolutePath())));
		
		HashSet<String> dataBaseFiles = new HashSet<>();
		try(PreparedStatement stmt = conn.prepareStatement("SELECT fileName FROM tracks WHERE folder=?")){
			stmt.setString(1, directoryFolder);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				dataBaseFiles.add(rs.getString("fileName"));
			}
		} catch (SQLException e) {
			showErrorPopUp("ERROR! COULD NOT GET TRACKS TO CHECK! "+e.getMessage());
		}
		
		dataBaseFiles.removeAll(driveFiles);
		
		try(PreparedStatement stmt = conn.prepareStatement("DELETE FROM tracks WHERE fileName=? AND folder=?")){
			for(String s : dataBaseFiles) {
				toBeRemovedTracksDetails.add(new ArrayList<>(Arrays.asList(s, directoryFolder)));
				stmt.setString(1, s);
				stmt.setString(2, directoryFolder);
				stmt.execute();
			}
			conn.commit();
		} catch (SQLException e) {
			showErrorPopUp("ERROR! COULD NOT REMOVE TRACK! "+e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				showErrorPopUp(ERROR_ROLLBACK_MESSAGE+e1.getMessage());
			}
		}
		return toBeRemovedTracksDetails;
	}

	public List<Track> toBeAddedTracks(String directoryFolder) {
		List<Track> toBeAddedTracks = new ArrayList<>();
		HashMap<String, File> mapping = new HashMap<>();
		HashSet<String> folderFiles = new HashSet<>();
		returnFilesInFolder(directoryFolder).forEach(f -> {
			folderFiles.add(FilenameUtils.getName(f.getAbsolutePath()));
			mapping.put(FilenameUtils.getName(f.getAbsolutePath()), f);
		});
		
		HashSet<String> dataBaseFiles = new HashSet<>();
		try(PreparedStatement stmt = conn.prepareStatement("SELECT fileName FROM tracks WHERE folder=?")){
			stmt.setString(1, directoryFolder);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				dataBaseFiles.add(rs.getString("fileName"));
			}
		} catch (SQLException e) {
			showErrorPopUp("ERROR! COULD NOT GET TRACKS TO CHECK! "+e.getMessage());
		}
		
		folderFiles.removeAll(dataBaseFiles);
				
		TrackParser parser = new TrackParser();
		Track t;
		for(String s : folderFiles) {
			t = parser.getTrack(mapping.get(s).getAbsolutePath());
			toBeAddedTracks.add(t);
			addTrackToDataBase(mapping.get(s).getAbsolutePath(),t);
		}
		return toBeAddedTracks;
	}
		
	public void closeConnection() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			showErrorPopUp("COULD NOT CLOSE DATABASE! "+e.getMessage());
		}
	}
		
	public String getDirectory() {
		return this.directory;
	}
	
	public boolean checkConnection() {
		try {
			if(this.conn==null) {
				return false;
			}
			return this.conn.isValid(0);
		} catch (SQLException e) {
			showErrorPopUp("No connection to Database! Restart the Application please");
			return false;
		}
	}
}
