package at.jku.se.gps_tracker.data;

import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;

public class TracksDB {

	private final String[] extensions;
	private Connection conn;
	private String directory;
	private TrackParser parser;
	
	public TracksDB(String... extensions) {
		this.extensions=extensions;
		parser = new TrackParser();
	}
	
	public void establishConnection(String directory, String dataBaseName) {
		if(this.directory!=null && this.directory.equals(directory)) {
			return;
		}
		String dataBaseLocation = FilenameUtils.concat(directory, dataBaseName);
		boolean setUpNecessary = new File(dataBaseLocation).exists();
		String url = "jdbc:sqlite:"+dataBaseLocation;
		this.directory=directory;
		try {
			conn = DriverManager.getConnection(url);
			conn.setAutoCommit(false);
			if(!setUpNecessary) {
				setUpTables();
			}
		} catch (SQLException e) {
			this.directory=null;
			try {
				conn.rollback();
			} catch (SQLException e1) {
				
			}
		}
	}
	
	private void setUpTables() {		
		try(Statement stmt = conn.createStatement()){
			stmt.execute("CREATE TABLE tracks (name TEXT NOT NULL, fileName TEXT NOT NULL, folder TEXT NOT NULL, date TEXT NOT NULL, time TEXT NOT NULL, distance REAL NOT NULL, duration REAL NOT NULL, averageBPM INTEGER NOT NULL, maximumBPM INTEGER NOT NULL, elevation REAL NOT NULL, PRIMARY KEY(fileName,folder));");
            conn.commit();
        } catch (SQLException e) {  
            try {
				conn.rollback();
			} catch (SQLException e1) {
				
			}
        }  
	}
	
	private void addTrackToDataBase(Track track) {
		if(track==null) {
			return;
		}
		try(PreparedStatement stmt = conn.prepareStatement("INSERT INTO tracks VALUES(?,?,?,?,?,?,?,?,?,?)")){
			stmt.setString(1, track.getName());
			stmt.setString(2, track.getFileName());
			stmt.setString(3, track.getParentDirectory());
			stmt.setString(4, track.getDate().toString());
			stmt.setString(5, track.getStartTime().toString());
			stmt.setDouble(6, track.getDistance());
			stmt.setDouble(7, track.getDuration().toSeconds());
			stmt.setInt(8, track.getAverageBPM());
			stmt.setInt(9, track.getMaximumBPM());
			stmt.setDouble(10, track.getElevation());
			stmt.execute();
			
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				
			}
		}
	}
	
	public List<Track> getTracks(String directoryFolder){
		List<Track> trackHelpList = new ArrayList<>();
		try(PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tracks WHERE folder=?")){
			stmt.setString(1, directoryFolder);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				trackHelpList.add(new Track.TrackBuilder(rs.getString("folder"), rs.getString("fileName"), rs.getString("name"), LocalDate.parse(rs.getString("date")), LocalTime.parse(rs.getString("time")))
						.distance(rs.getDouble("distance"))
						.duration(Duration.ofSeconds((long) rs.getDouble("duration")))
						.averageBPM(rs.getInt("averageBPM"))
						.maximumBPM(rs.getInt("maximumBPM"))
						.elevation(rs.getDouble("elevation"))
						.build()
				);
			}
		} catch (SQLException e) {
			
		}
		return trackHelpList;
	}
	
	public List<TrackPoint> getTrackPoints(Track track) {
		String trackFileString = getTrackPath(track.getParentDirectory(),track.getFileName());
		if(new File(trackFileString).exists()) {
			try {
				return parser.getTrackPoints(trackFileString);
			} catch (XMLStreamException | FileNotFoundException e) {
				
			}
			return new ArrayList<>();
		} else {
			return new ArrayList<>();
		}
	}
	
	private String getTrackPath(String parentDirectory, String fileName) {
		return FilenameUtils.concat(FilenameUtils.concat(directory, parentDirectory),fileName);
	}
	
	private List<String> returnTracksInFolder(String directoryFolder) {
		List<File> tracksInFolder = (List<File>) FileUtils.listFiles(new File(directory,directoryFolder), extensions, false);
		return tracksInFolder
						.stream()
						.map(f -> FilenameUtils.getName(f.getAbsolutePath()))
						.collect(Collectors.toList());
	}
	
	public List<List<String>> toBeRemovedTracks(String directoryFolder){
		List<String> dataBaseTracks = returnDifferenceDriveAndDB(false, directoryFolder);
		
		List<List<String>> toBeRemovedTracksDetails = new ArrayList<>();
		try(PreparedStatement stmt = conn.prepareStatement("DELETE FROM tracks WHERE fileName=? AND folder=?")){
			for(String track : dataBaseTracks) {
				toBeRemovedTracksDetails.add(new ArrayList<>(Arrays.asList(track, directoryFolder)));
				stmt.setString(1, track);
				stmt.setString(2, directoryFolder);
				stmt.execute();
			}
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				
			}
		}
		return toBeRemovedTracksDetails;
	}

	public List<Track> toBeAddedTracks(String directoryFolder) {
		List<String> driveTracks = returnDifferenceDriveAndDB(true, directoryFolder);
		
		List<Track> toBeAddedTracks = new ArrayList<>();
		Track t = null;
		for(String track : driveTracks) {
			try {
				t = parser.getTrack(getTrackPath(directoryFolder,track));
			} catch (XMLStreamException | FileNotFoundException e) {
				t=null;
			}
			if(t!=null) {
				toBeAddedTracks.add(t);
				addTrackToDataBase(t);
			}
		}
		return toBeAddedTracks;
	}
	
	private List<String> returnDifferenceDriveAndDB(boolean getTracksFromDrive, String directoryFolder){
		List<String> driveTracks = returnTracksInFolder(directoryFolder);
		
		List<String> dataBaseTracks = new ArrayList<>();
		try(PreparedStatement stmt = conn.prepareStatement("SELECT fileName FROM tracks WHERE folder=?")){
			stmt.setString(1, directoryFolder);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				dataBaseTracks.add(rs.getString("fileName"));
			}
		} catch (SQLException e) {
		}
		
		if(getTracksFromDrive) {
			driveTracks.removeAll(dataBaseTracks);
			return driveTracks;
		} else {
			dataBaseTracks.removeAll(driveTracks);
			return dataBaseTracks;
		}
	}
	
	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			
		}
	}
		
	public String getDirectory() {
		return directory;
	}
	
	public boolean checkConnection(String modelDirectory) {
		try {
			if(conn==null || !modelDirectory.equals(this.directory) ) {
				return false;
			}
			return conn.isValid(0);
		} catch (SQLException e) {
			return false;
		}
	}
}
