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
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;

public class TrackParsingOperations {

	private Connection conn;
	
	public TrackParsingOperations() {
		conn = null;
	}
	
	public void establishConnection(String dataBaseLocation) {
		boolean setUpNecessary = new File(dataBaseLocation).exists();
		String url = "jdbc:sqlite:"+dataBaseLocation;
		try {
			conn = DriverManager.getConnection(url);
			conn.setAutoCommit(false);
			if(!setUpNecessary) {
				setUpTables();
			}
		} catch (SQLException e) {  
			System.out.println(e.getMessage());  
		}
	}
	
	private void setUpTables() {
		String sqlTrackTableStatement = "CREATE TABLE tracks (name TEXT NOT NULL, folder TEXT NOT NULL, date TEXT NOT NULL, time TEXT NOT NULL, distance REAL NOT NULL, duration REAL NOT NULL, pace REAL NOT NULL, speed REAL NOT NULL, averageBPM INTEGER NOT NULL, maximumBPM INTEGER NOT NULL, elevation REAL NOT NULL, PRIMARY KEY(name,folder));";
		
		String sqlTrackPointTableStatement = "CREATE TABLE trackpoints (name TEXT NOT NULL, folder TEXT NOT NULL, nr INTEGER NOT NULL, distance	REAL NOT NULL, duration	REAL NOT NULL, pace	REAL NOT NULL, speed REAL NOT NULL, averageBPM INTEGER NOT NULL, maximumBPM INTEGER NOT NULL, elevation REAL NOT NULL, FOREIGN KEY(name,folder) REFERENCES tracks(name,folder) ON DELETE CASCADE, PRIMARY KEY(name,folder,nr));";	
		
		try(Statement stmt = conn.createStatement()){
			stmt.execute(sqlTrackTableStatement);
            stmt.execute(sqlTrackPointTableStatement);  
            conn.commit();
        } catch (SQLException e) {  
            System.out.println(e.getMessage());
            try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        }  
	}
	
	public void addTrackToDataBase(String file, Track track) {
		String sqlAddTrack = "INSERT INTO tracks VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		String sqlAddTrackPoints = "INSERT INTO trackpoints VALUES(?,?,?,?,?,?,?,?,?,?)";
		String parentDirectory = new File(file).getParentFile().getName();
		try(PreparedStatement stmt = conn.prepareStatement(sqlAddTrack); PreparedStatement stmt2 = conn.prepareStatement(sqlAddTrackPoints)){
			
			stmt.setString(1, track.getName());
			stmt.setString(2, parentDirectory);
			stmt.setString(3, track.getDate().toString());
			stmt.setString(4, track.getStartTime().toString());
			stmt.setDouble(5, track.getDistance());
			stmt.setDouble(6, track.getDuration().toSeconds());
			stmt.setDouble(7, track.getPace().toSeconds());
			stmt.setDouble(8, track.getSpeed());
			stmt.setInt(9, track.getAverageBPM());
			stmt.setInt(10, track.getMaximumBPM());
			stmt.setDouble(11, track.getElevation());
			stmt.execute();
			
			for(TrackPoint t : track.getTrackPoints()) {
				stmt2.setString(1, track.getName());
				stmt2.setString(2, parentDirectory);
				stmt2.setString(3, t.getName());
				stmt2.setDouble(4, t.getDistance());
				stmt2.setDouble(5, t.getDuration().toSeconds());
				stmt2.setDouble(6, t.getPace().toSeconds());
				stmt2.setDouble(7, t.getSpeed());
				stmt2.setInt(8, t.getAverageBPM());
				stmt2.setInt(9, t.getMaximumBPM());
				stmt2.setDouble(10, t.getElevation());
				stmt2.execute();
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public List<Track> getTracks(String currentDirectory){
		List<Track> trackHelpList = new ArrayList<>();
		try(PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tracks WHERE folder=?")){
			stmt.setString(1, currentDirectory);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				trackHelpList.add(new Track(this, rs.getString("folder"), rs.getString("name"), LocalDate.parse(rs.getString("date")), LocalTime.parse(rs.getString("time")), rs.getDouble("distance"), Duration.ofSeconds((long) rs.getDouble("duration")), Duration.ofSeconds((long) rs.getDouble("pace")), rs.getDouble("speed"), rs.getInt("averageBPM"), rs.getInt("maximumBPM"), rs.getDouble("elevation")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return trackHelpList;
	}
	
	public List<TrackPoint> getTrackPoints(Track track){
		List<TrackPoint> trackPointHelpList = new ArrayList<>();
		try(PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM trackpoints WHERE name=? AND folder=?")){
			stmt2.setString(1, track.getName());
			stmt2.setString(2, track.getParentDirectory());
			ResultSet rs2 = stmt2.executeQuery();
			while(rs2.next()) {
				trackPointHelpList.add(new TrackPoint(rs2.getString("nr"),rs2.getDouble("distance"), Duration.ofSeconds((long) rs2.getDouble("duration")), Duration.ofSeconds((long) rs2.getDouble("pace")), rs2.getDouble("speed"), rs2.getInt("averageBPM"), rs2.getInt("maximumBPM"), rs2.getDouble("elevation")));              
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return trackPointHelpList;
	}
	
	public void removeTracks(List<File> files, String currentDirectoryFolder){
		HashSet<String> driveFiles = new HashSet<>();
		files.forEach(f -> driveFiles.add(FilenameUtils.getName(f.getAbsolutePath())));
		
		HashSet<String> dataBaseFiles = new HashSet<>();
		try(PreparedStatement stmt = conn.prepareStatement("SELECT name FROM tracks WHERE folder=?")){
			stmt.setString(1, currentDirectoryFolder);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				dataBaseFiles.add(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		dataBaseFiles.removeAll(driveFiles);
		
		try(PreparedStatement stmt = conn.prepareStatement("DELETE FROM tracks WHERE name=? AND folder=?")){
			for(String s : dataBaseFiles) {
				stmt.setString(1, s);
				stmt.setString(2, currentDirectoryFolder);
				stmt.execute();
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void addTracks(List<File> files, String currentDirectoryFolder) {
		HashMap<String, File> mapping = new HashMap<>();
		HashSet<String> driveFiles = new HashSet<>();
		files.forEach(f -> {
			driveFiles.add(FilenameUtils.getName(f.getAbsolutePath()));
			mapping.put(FilenameUtils.getName(f.getAbsolutePath()), f);
		});
		
		HashSet<String> dataBaseFiles = new HashSet<>();
		try(PreparedStatement stmt = conn.prepareStatement("SELECT name FROM tracks WHERE folder=?")){
			stmt.setString(1, currentDirectoryFolder);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				dataBaseFiles.add(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		driveFiles.removeAll(dataBaseFiles);
		
		HashMap<String, Track> toAddTracks = new HashMap<>();
		TrackParser parser = new TrackParser();
		for(String s : driveFiles) {
			toAddTracks.put(mapping.get(s).getAbsolutePath(), parser.getTrack(mapping.get(s).getAbsolutePath()));
		}
		for(Map.Entry<String, Track> s : toAddTracks.entrySet()) {
			addTrackToDataBase(s.getKey(), s.getValue());
		}
	}
}
