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
	private Connection conn;
	private String directory;
	
	public void establishConnection(String dataBaseLocation, String dataBaseFilePath) {
		boolean setUpNecessary = new File(dataBaseLocation).exists();
		String url = "jdbc:sqlite:"+dataBaseLocation;
		try {
			conn = DriverManager.getConnection(url);
			conn.setAutoCommit(false);
			if(!setUpNecessary) {
				setUpTables();
			}
			this.directory=dataBaseFilePath;
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
	
	public List<Track> getTracks(String currentDirectory){
		List<Track> trackHelpList = new ArrayList<>();
		try(PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tracks WHERE folder=?")){
			stmt.setString(1, currentDirectory);
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
	
	public List<TrackPoint> getTrackPoints(String directory, Track track){
		if(new File(FilenameUtils.concat(FilenameUtils.concat(directory, track.getParentDirectory()),track.getFileName())).exists()) {
			return new TrackParser().getTrackPoints(FilenameUtils.concat(FilenameUtils.concat(directory, track.getParentDirectory()),track.getFileName()));
		} else {
			showErrorPopUp("ERROR! REMEMBER TO UPDATE THE PROGRAM AFTER EVERY CHANGE!");
			return new ArrayList<>();
		}
	}
	
	public void updateDataBase(String currentDirectory, String currentDirectoryFolder, String... extensions) {
		List<File> files = (List<File>) FileUtils.listFiles(new File(currentDirectory,currentDirectoryFolder), extensions, false);
		removeTracks(files, currentDirectoryFolder);
		addTracks(files, currentDirectoryFolder);
	}
	
	private void removeTracks(List<File> files, String currentDirectoryFolder){
		HashSet<String> driveFiles = new HashSet<>();
		files.forEach(f -> driveFiles.add(FilenameUtils.getName(f.getAbsolutePath())));
		
		HashSet<String> dataBaseFiles = new HashSet<>();
		try(PreparedStatement stmt = conn.prepareStatement("SELECT fileName FROM tracks WHERE folder=?")){
			stmt.setString(1, currentDirectoryFolder);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				dataBaseFiles.add(rs.getString("fileName"));
			}
		} catch (SQLException e) {
			showErrorPopUp("ERROR! COULD NOT GET TRACKS TO CHECK! "+e.getMessage());
		}
		
		dataBaseFiles.removeAll(driveFiles);
		if(driveFiles.isEmpty()) {
			return;
		}
		
		try(PreparedStatement stmt = conn.prepareStatement("DELETE FROM tracks WHERE fileName=? AND folder=?")){
			for(String s : dataBaseFiles) {
				stmt.setString(1, s);
				stmt.setString(2, currentDirectoryFolder);
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
	}

	private void addTracks(List<File> files, String currentDirectoryFolder) {
		HashMap<String, File> mapping = new HashMap<>();
		HashSet<String> driveFiles = new HashSet<>();
		files.forEach(f -> {
			driveFiles.add(FilenameUtils.getName(f.getAbsolutePath()));
			mapping.put(FilenameUtils.getName(f.getAbsolutePath()), f);
		});
		
		HashSet<String> dataBaseFiles = new HashSet<>();
		try(PreparedStatement stmt = conn.prepareStatement("SELECT fileName FROM tracks WHERE folder=?")){
			stmt.setString(1, currentDirectoryFolder);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				dataBaseFiles.add(rs.getString("fileName"));
			}
		} catch (SQLException e) {
			showErrorPopUp("ERROR! COULD NOT GET TRACKS TO CHECK! "+e.getMessage());
		}
		
		driveFiles.removeAll(dataBaseFiles);
		
		if(driveFiles.isEmpty()) {
			return;
		}
		
		TrackParser parser = new TrackParser();
		for(String s : driveFiles) {
			addTrackToDataBase(mapping.get(s).getAbsolutePath(), parser.getTrack(mapping.get(s).getAbsolutePath()));
		}
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
