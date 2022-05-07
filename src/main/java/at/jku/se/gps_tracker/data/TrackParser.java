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
	
	//data per track
	List<TrackPoint> trackPointsList;
	Instant trackTimeDate;
	double totalDistance;
	Duration totalDuration;
	double totalElevation;
	
	// data per trackPoint
	int trackPointNr;
	double trackPointElevation;
	double trackPointElevationChange;
	boolean trackPointElevationSet;
	double trackPointLatitude;
	double trackPointLongtitude;
	Instant trackPointTimePoint;
	Duration trackPointDuration;
	
	//data about previous trackPoint
	Instant prevTrackPointTime;
	double prevTrackPointElevation;
	boolean prevTrackPointElevationSet;
	double prevTrackPointLatitude;
	double prevTrackPointLongtitude;
	boolean prevTrackPointCoordinatesSet;
	
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
				track = new GPXParser().readGPXTrack(file, streamReader);
			} else {
				track = new TCXParser().readTCXTrack(file, streamReader);
			}
		} catch (FileNotFoundException e) {
			showErrorPopUpNoWait("ERROR! File "+FilenameUtils.getName(file)+" could not be read! Not possible to access file on disk! Please update the tracks to try again!");
		} catch (XMLStreamException e) {
			showErrorPopUpNoWait("ERROR! File "+FilenameUtils.getName(file)+" could not be read! Please ensure that the file has been made correctly and try again!");
		} catch (Exception e) {
			showErrorPopUpNoWait("ERROR! File "+FilenameUtils.getName(file)+" could not be read! An error has been encountered! Please ensure file is correct and try again!");
		}
		try {
			in.close();
		} catch (IOException e) {
			showErrorPopUp("ERROR! The file could not be closed correctly. Please restart the application to ensure correct working!");
		}
		return track;
	}
	
	public List<TrackPoint> getTrackPoints(String file){
		List<TrackPoint> points = new ArrayList<>();
		try {
			setUpTrackParser(file);
			if(FilenameUtils.getExtension(file).equals("gpx")) {
				points = new GPXParser().readGPXTrackPoints(streamReader);
			} else {
				points = new TCXParser().readTCXTrackPoints(streamReader);
			}
		} catch (FileNotFoundException e) {
			showErrorPopUpNoWait("ERROR! File "+FilenameUtils.getName(file)+" could not be read! Not possible to access file on disk! Please update the tracks to try again!");
		} catch (XMLStreamException e) {
			showErrorPopUpNoWait("ERROR! File "+FilenameUtils.getName(file)+" could not be read! Please ensure that the file has been made correctly and try again!");
		} catch (Exception e) {
			showErrorPopUpNoWait("ERROR! File "+FilenameUtils.getName(file)+" could not be read! An error has been encountered! Please ensure file is correct and try again!");
		}
		try {
			in.close();
		} catch (IOException e) {
			showErrorPopUp("ERROR! The file could not be closed correctly. Please restart the application to ensure correct working!");
		}
		return points;
	}
	
	private void setUpTrackParser(String file) throws XMLStreamException, FileNotFoundException  {
		in = new BufferedInputStream(new FileInputStream(file));
		streamReader = inputFactory.createXMLStreamReader(in);
	}
		
	//from here: https://stackoverflow.com/a/16794680
	protected double distance(double lat1, double lat2, double lon1,
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
}
