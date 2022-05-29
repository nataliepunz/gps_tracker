package at.jku.se.gps_tracker.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FilenameUtils;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;
/**
 * class to handle Parsers
 * @author Ozan
 */
public class TrackParser {
	
	/**
	 * the format specification for Time instance
	 * @author Ozan
	 */
	protected static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	/**
	 * Data per Track object
	 * @author Ozan
	 */
	
	/**
	 * list of parsed trackPoints
	 * @author Ozan 
	 */
	static List<TrackPoint> trackPointsList;
	
	/**
	 * the name of the parsed track
	 * @author Ozan
	 */
	static String trackName;
	
	/**
	 * the startTime and Date of the track
	 * @author Ozan
	 */
	static Instant trackTimeDate;
	
	/**
	 * the total distance of the track
	 * @author Ozan
	 */
	static double totalDistance;
	
	/**
	 * the total duration of the track
	 * @author Ozan
	 */
	static Duration totalDuration;
	
	/**
	 * the total elevation gains of the track
	 * @author Ozan
	 */
	static double totalElevation;
	
	/**
	 * data per TrackPoit object
	 */
	
	/**
	 * the number of the trackpoint inside the track
	 * @author Ozan
	 */
	static int trackPointNr;
	
	/**
	 * the elevation of the trackpoint
	 * @author Ozan
	 */
	static BigDecimal trackPointElevation;
	
	/**
	 * the elevation gains of the trackpoint
	 * @author Ozan
	 */
	static double trackPointElevationChange;
	
	/**
	 * check if a elevation has been detected and set accordingly in the track file
	 * @author Ozan
	 */
	static boolean trackPointElevationSet;
	
	/**
	 * the latitude of the trackPoint
	 * @author Ozan
	 */
	static double trackPointLatitude;
	
	/**
	 * the longtitude of the trackPoint
	 * @author Ozan
	 */
	static double trackPointLongtitude;
	
	/**
	 * the startTime and Date of the trackPoint
	 * necessary to calculate duration
	 * @author Ozan
	 */
	static Instant trackPointTimePoint;
	
	/**
	 * the duration of the trackPoint
	 * @author Ozan
	 */
	static Duration trackPointDuration;
	
	/**
	 * data about the previous parsed trackPoint
	 */
	
	/**
	 * the startTime and date of the previous trackPoint
	 * @author Ozan
	 */
	static Instant prevTrackPointTime;
	
	/**
	 * the elevation of the previous trackPoint
	 * @author Ozan
	 */
	static BigDecimal prevTrackPointElevation;
	
	/**
	 * if the elevation had been set in the previous trackPoint
	 * @author Ozan
	 */
	static boolean prevTrackPointElevationSet;
	
	/**
	 * the latitude of the previous trackPoint
	 * @author Ozan
	 */
	static double prevTrackPointLatitude;
	
	/**
	 * the lontitude of the previous trackPoint
	 * @author Ozan
	 */
	static double prevTrackPointLongtitude;
	
	/**
	 * if the coordintes of the previous trackPoint had been set
	 * @author Ozan
	 */
	static boolean prevTrackPointCoordinatesSet;
	
	/**
	 * parsing necessities
	 */
	
	/**
	 * the inputFactory to read the given file
	 * @author Ozan
	 */
	private XMLInputFactory inputFactory;
	
	/**
	 * the streamReader instance to read the given xml file
	 * @author Ozan
	 */
	private XMLStreamReader streamReader;
	
	/**
	 * the InputStream with the given file
	 * @author Ozan
	 */
	private InputStream in;
	
	/**
	 * instantiate the inputFactory and setProperties for safety
	 * @author Ozan
	 */
	public TrackParser() {
		inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
	}
	
	/**
	 * parses and returns a given file (eiter GPX or TCX) as Track
	 * @author Ozan
	 * @param file filePath of track
	 * @return parsed Track
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 */
	public Track getTrack(String file) throws XMLStreamException, FileNotFoundException{
		Track track = null;
		cleanUp();
		setUpTrackParser(file);
		if(FilenameUtils.getExtension(file).equals("gpx")) {
			track = GPXParser.readGPXTrack(file, streamReader);
		} else {
			track = TCXParser.readTCXTrack(file, streamReader);
		}
		cleanUp();
		return track;
	}
	
	/**
	 * resets the fields and closes the InputStream
	 * @author Ozan
	 */
	public void cleanUp() {
		resetFields();
		if(in!=null) {
			try {
				in.close();
			} catch (IOException e) {
				in = null;
			}
		}
	}
	
	/**
	 * parses and returns the trackpoints of a given file path
	 * @author Ozan
	 * @param file filePath of track
	 * @return TrackPoints of given file as List
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 */
	public List<TrackPoint> getTrackPoints(String file) throws XMLStreamException, FileNotFoundException{
		return this.getTrack(file).getTrackPoints();
	}
	
	/**
	 * sets up the track parsing 
	 * @author Ozan
	 * @param file to be parsed file
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 */
	private void setUpTrackParser(String file) throws XMLStreamException, FileNotFoundException  {
		in = new BufferedInputStream(new FileInputStream(file));
		streamReader = inputFactory.createXMLStreamReader(in);
	}
		
	/**
	 * returns distance based on two coordinate sets
	 * see: https://stackoverflow.com/a/16794680
	 * @param lat1 latitude of first coordinate set
	 * @param lat2 latitude of second coordinate set
	 * @param lon1 longitude of first coordinate set
	 * @param lon2 longitude of second coordinate set
	 * @param el1 elevation of first coordinate set
	 * @param el2 elevation of second coordinate set
	 * @return distance between two coordinates
	 */
	protected static double distance(double lat1, double lat2, double lon1,
	        double lon2, double el1, double el2) {
		
	    final int R = 6371; // Radius of the earth

	    double latDistance = Math.toRadians(lat2 - lat1);
	    double lonDistance = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters

	    double height = el1 - el2;

	    distance = Math.pow(distance, 2) + Math.pow(height, 2);

	    return Math.sqrt(distance);
	}
	
	/**
	 * reset the fields to enable parsing of next track
	 * @author Ozan
	 */
	protected static final void resetFields() {
		trackName = null;
		trackPointsList = new ArrayList<>();
		trackTimeDate = null;
		totalDistance = 0;
		totalElevation = 0;
		totalDuration = Duration.ofSeconds(0);
				
		trackPointNr = 1;
		
		prevTrackPointTime = null;
		prevTrackPointElevation = new BigDecimal(0);
		prevTrackPointElevationSet = false;
		prevTrackPointLatitude = 0;
		prevTrackPointLongtitude = 0;
		prevTrackPointCoordinatesSet = false;
	}
}
