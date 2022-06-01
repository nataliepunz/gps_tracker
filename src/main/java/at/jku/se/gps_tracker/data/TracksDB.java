package at.jku.se.gps_tracker.data;

import java.io.File;
import java.io.IOException;
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

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FilenameUtils;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;
/**
 * class to establish connection with the database file and manage tracks
 * @author Ozan
 *
 */
public class TracksDB {

	private static final String USER_DOCUMENTATION_MESSAGE = "PLEASE CONSULT USER DOCUMENTATION!";
	private static final String ROLLBACK_MESSAGE = "CAUTION ERROR! NOT ABLE TO REVERT CHANGES! "+USER_DOCUMENTATION_MESSAGE;
	private static final String INTERNAL_ERROR_MESSAGE = "CAUTION ERROR! INTERNAL WORK ERROR!"+USER_DOCUMENTATION_MESSAGE;
	
	/**
	 * variable to hold the connection with the database
	 * @author Ozan
	 */
	private Connection conn;
	
	/**
	 * variable to hold the directory with which the object works with
	 * @author Ozan
	 */
	private String directory;
	
	/**
	 * variable to hold an object of the TrackParser; enables parsing operations
	 * @author Ozan
	 */
	private final TrackParser parser;
	
	/**
	 * create and assigns the TrackParser object
	 * @author Ozan
	 */
	public TracksDB() {
		parser = new TrackParser();
	}
	
	/**
	 * checks if given directory invalid
	 * establish the connection with the database file. if there is none a new file will be created and populated with the necessary tables
	 * @author Ozan
	 * @param directory
	 * @param dataBaseName
	 * @throws SQLOperationException 
	 * @throws SQLRollbackException 
	 */
	public void establishConnection(String directory, String dataBaseName) throws SQLOperationException, SQLRollbackException {
		if(directory==null || directory.equals(this.directory)) {
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
				throw new SQLRollbackException(ROLLBACK_MESSAGE);
			}
            throw new SQLOperationException("CAUTION ERROR! NOT ABLE TO SET UP CONNECTION TO DATABASE! "+USER_DOCUMENTATION_MESSAGE);
		} catch (SQLRollbackException e) {
			throw new SQLRollbackException(ROLLBACK_MESSAGE);
		}
	}
	
	/**
	 * sets the necessary tables up for a newly created trackdatabase file
	 * @author Ozan
	 * @throws SQLOperationException 
	 * @throws SQLRollbackException 
	 */
	private void setUpTables() throws SQLOperationException, SQLRollbackException {		
		try(Statement stmt = conn.createStatement()){
			stmt.execute("CREATE TABLE tracks (name TEXT NOT NULL, fileName TEXT NOT NULL, folder TEXT NOT NULL, date TEXT NOT NULL, time TEXT NOT NULL, distance REAL NOT NULL, duration REAL NOT NULL, averageBPM INTEGER NOT NULL, maximumBPM INTEGER NOT NULL, elevation REAL NOT NULL, PRIMARY KEY(fileName,folder));");
            conn.commit();
        } catch (SQLException e) {  
            try {
				conn.rollback();
			} catch (SQLException e1) {
				throw new SQLRollbackException(ROLLBACK_MESSAGE);
			}
            throw new SQLOperationException("CAUTION ERROR! NOT ABLE TO CREATE NECESSARY SET UP! "+USER_DOCUMENTATION_MESSAGE);
        }  
	}
	
	/**
	 * adds a given non-null track into the database
	 * @author Ozan
	 * @param track to add to database
	 * @throws SQLOperationException 
	 * @throws SQLRollbackException 
	 */
	public void addTrackToDataBase(Track track) throws SQLOperationException, SQLRollbackException {
		if(track!=null) {
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
					throw new SQLRollbackException(ROLLBACK_MESSAGE);
				}
				throw new SQLOperationException("CAUTION ERROR! NOT ABLE TO ADD TRACK TO DATABASE: "+track.getFileName()+" in "+track.getFileName()+"! "+USER_DOCUMENTATION_MESSAGE);
			}
		}
	}
	
	/**
	 * removes given track from database based on its fileName and directory folder
	 * @author Ozan
	 * @param fileName fileName of to be removed track
	 * @param directoryFolder in which directory folder is located
	 * @throws SQLOperationException 
	 * @throws SQLRollbackException 
	 */
	public void removeTrackFromDataBase(String fileName, String directoryFolder) throws SQLOperationException, SQLRollbackException {
		try(PreparedStatement stmt = conn.prepareStatement("DELETE FROM tracks WHERE fileName=? AND folder=?")){
			stmt.setString(1, fileName);
			stmt.setString(2, directoryFolder);
			stmt.execute();
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				throw new SQLRollbackException(ROLLBACK_MESSAGE);
			}
			throw new SQLOperationException("CAUTION ERROR! NOT ABLE TO REMOVE TRACK FROM DATABASE: "+fileName+" in "+directoryFolder+"! "+USER_DOCUMENTATION_MESSAGE);
		}
	}
	
	/**
	 * returns the saved tracks in database based on their directoryfolder
	 * @author Ozan
	 * @param directoryFolder
	 * @return tracks in given directoryFolder as List
	 * @throws SQLOperationException 
	 */
	public List<Track> getTracks(String directoryFolder) throws SQLOperationException {
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
			throw new SQLOperationException("CAUTION ERROR! NOT ABLE TO GET TRACKS FROM DATABASE FOR "+directoryFolder+"! "+USER_DOCUMENTATION_MESSAGE);
		}
		return trackHelpList;
	}
	
	/**
	 * parses the track file and returns its trackPoints
	 * @author Ozan
	 * @param trackFileString filePath of track
	 * @return TrackPoints associated track
	 * @throws XMLStreamException
	 * @throws IOException 
	 */
	public List<TrackPoint> getTrackPoints(String trackFileString) throws XMLStreamException, IOException {
		return parser.getTrackPoints(trackFileString);
	}
	
	/**
	 * parses and returns track given its filePath
	 * @author Ozan
	 * @param trackPath filePath of track
	 * @return Track
	 * @throws XMLStreamException
	 * @throws IOException 
	 */
	public Track parseTrack(String trackPath) throws XMLStreamException, IOException {
		return parser.getTrack(trackPath);
	}
	
	/**
	 * returns the filenames of tracks based on their directoryFolder
	 * @author Ozan
	 * @param directoryFolder
	 * @return fileNames of saved tracks in database based on directoryFolder
	 * @throws SQLOperationException 
	 */
	public List<String> getTracksDBFileName(String directoryFolder) throws SQLOperationException{
		List<String> dataBaseTracks = new ArrayList<>();
		try(PreparedStatement stmt = conn.prepareStatement("SELECT fileName FROM tracks WHERE folder=?")){
			stmt.setString(1, directoryFolder);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				dataBaseTracks.add(rs.getString("fileName"));
			}
		} catch (SQLException e) {
			throw new SQLOperationException(INTERNAL_ERROR_MESSAGE);
		}
		return dataBaseTracks;
	}
	
	/**
	 * closes the connection with the databse file
	 * @author Ozan
	 * @throws SQLOperationException 
	 */
	public void closeConnection() throws SQLOperationException {
		try {
			conn.close();
		} catch (SQLException e) {
			throw new SQLOperationException(INTERNAL_ERROR_MESSAGE);
		}
	}
	
	/**
	 * checks if a valid connection (not null and in sync with given Directory) is given
	 * @author Ozan
	 * @param modelDirectory
	 * @return true if connection is valid
	 */
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
