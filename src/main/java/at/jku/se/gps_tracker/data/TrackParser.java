package at.jku.se.gps_tracker.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FilenameUtils;

import at.jku.se.gps_tracker.controller.ErrorPopUpController;
import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;

public class TrackParser implements ErrorPopUpController {
	
	protected static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final String ERROR_TEXT = "ERROR! File could not be read! ";
	
	//data per track
	static List<TrackPoint> trackPointsList;
	static String trackName;
	static Instant trackTimeDate;
	static double totalDistance;
	static Duration totalDuration;
	static double totalElevation;
	
	// data per trackPoint
	static int trackPointNr;
	static double trackPointElevation;
	static double trackPointElevationChange;
	static boolean trackPointElevationSet;
	static double trackPointLatitude;
	static double trackPointLongtitude;
	static Instant trackPointTimePoint;
	static Duration trackPointDuration;
	
	//data about previous trackPoint
	static Instant prevTrackPointTime;
	static double prevTrackPointElevation;
	static boolean prevTrackPointElevationSet;
	static double prevTrackPointLatitude;
	static double prevTrackPointLongtitude;
	static boolean prevTrackPointCoordinatesSet;
	
	//parsing necessities
	private XMLInputFactory inputFactory;
	private XMLStreamReader streamReader;
	private InputStream in;
	
	public TrackParser() {
		inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
	}
			
	public Track getTrack(String file){
		Track track = null;
		try {
			setUpTrackParser(file);
			if(FilenameUtils.getExtension(file).equals("gpx")) {
				track = GPXParser.readGPXTrack(file, streamReader);
			} else {
				track = TCXParser.readTCXTrack(file, streamReader);
			}
			resetFields();
		} catch (FileNotFoundException e) {
			showErrorPopUpNoWait(ERROR_TEXT+"("+FilenameUtils.getName(file)+") Not possible to access file on disk! Please update the tracks to try again!");
		} catch (XMLStreamException e) {
			showErrorPopUpNoWait(ERROR_TEXT+"("+FilenameUtils.getName(file)+") Please ensure that the file has been made correctly and try again!");
		} catch (Exception e) {
			showErrorPopUpNoWait(ERROR_TEXT+"("+FilenameUtils.getName(file)+") An error has been encountered! Please ensure file is correct and try again! ");
		}
		try {
			in.close();
		} catch (IOException e) {
			showErrorPopUp(ERROR_TEXT+"("+FilenameUtils.getName(file)+") Please restart the application to ensure correct working!");
		}
		return track;
	}
	
	public List<TrackPoint> getTrackPoints(String file){
		return this.getTrack(file).getTrackPoints();
	}
	
	private void setUpTrackParser(String file) throws XMLStreamException, FileNotFoundException  {
		in = new BufferedInputStream(new FileInputStream(file));
		streamReader = inputFactory.createXMLStreamReader(in);
	}
		
	//from here: https://stackoverflow.com/a/16794680
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
	
	protected static final void resetFields() {
		trackName = null;
		trackPointsList = new ArrayList<>();
		trackTimeDate = null;
		totalDistance = 0;
		totalElevation = 0;
		totalDuration = Duration.ofSeconds(0);
				
		trackPointNr = 1;
		
		prevTrackPointTime = null;
		prevTrackPointElevation = 0;
		prevTrackPointElevationSet = false;
		prevTrackPointLatitude = 0;
		prevTrackPointLongtitude = 0;
		prevTrackPointCoordinatesSet = false;
	}
}
