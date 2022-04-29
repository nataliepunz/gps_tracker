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
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;

public class DataBaseOperations {

	private Connection conn;
	
	public DataBaseOperations() {
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
		String sqlTrackTableStatement = "CREATE TABLE tracks (\n"
				+ "name	TEXT NOT NULL, \n"
				+ "folder TEXT NOT NULL, \n"
				+ "date TEXT NOT NULL, \n"
				+ "time TEXT NOT NULL, \n"
				+ "distance	REAL NOT NULL, \n"
				+ "duration	REAL NOT NULL, \n"
				+ "pace	REAL NOT NULL, \n"
				+ "speed REAL NOT NULL, \n"
				+ "averageBPM INTEGER NOT NULL, \n"
				+ "maximumBPM INTEGER NOT NULL, \n"
				+ "elevation REAL NOT NULL, \n"
				+ "PRIMARY KEY(name,folder) \n"
				+ ");";
		
		String sqlTrackPointTableStatement = "CREATE TABLE trackpoints (\n"
				+ "name	TEXT NOT NULL, \n"
				+ "folder TEXT NOT NULL, \n"
				+ "nr INTEGER NOT NULL, \n"
				+ "distance	REAL NOT NULL, \n"
				+ "duration	REAL NOT NULL, \n"
				+ "pace	REAL NOT NULL, \n"
				+ "speed REAL NOT NULL, \n"
				+ "averageBPM INTEGER NOT NULL, \n"
				+ "maximumBPM INTEGER NOT NULL, \n"
				+ "elevation REAL NOT NULL, \n"
				+ "FOREIGN KEY(name,folder) REFERENCES tracks(name,folder) ON DELETE CASCADE, \n"
				+ "PRIMARY KEY(name,folder,nr) \n"
				+ ");";
		
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
	
	public List<Track> getTracks(){
		List<Track> trackHelpList = new ArrayList<>();
		try(Statement stmt = conn.createStatement()){
			ResultSet rs = stmt.executeQuery("SELECT * FROM tracks");
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
}
