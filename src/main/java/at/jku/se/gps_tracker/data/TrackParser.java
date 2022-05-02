package at.jku.se.gps_tracker.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
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
	
	//TrackData
	List<TrackPoint> helpList;
	LocalDate trackDate;
	LocalTime trackTime;
	boolean trackDetailSet;
	double trackDistance;
	double totalElevation;
	Duration totalDuration;
	
	// for TrackPoints themselves
	int trackPointNr;
	double elevation;
	double elevationChange;
	double latitude;
	double longtitude;
	Instant timeRecorded;
	Duration timeNeeded;
	LocalTime intermediateTime;
	
	//data about previous trackPoint
	LocalTime prevTime;
	double prevElevation;
	boolean prevElevationSet;
	double prevLatitude;
	double prevLongtitude;
	boolean prevCoordinatesSet;
	
	private XMLInputFactory inputFactory;
	private XMLStreamReader streamReader;
	private InputStream in;
	
	private GPXParser gpx;
	private TCXParser tcx;
	
	public TrackParser() {
		inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
	}
	
	public void createParsers() {
		gpx = new GPXParser();
		tcx = new TCXParser();
	}
		
	public Track getTrack(String file){
		Track track = null;
		try {
			setUpTrackParser(file);
			if(FilenameUtils.getExtension(file).equals("gpx")) {
				track = gpx.readGPXTrack(file, streamReader);
			} else {
				track = tcx.readTCXTrack(file, streamReader);
			}
			in.close();
		} catch (Exception e) {
			showErrorPopUp("The GPS-Track "+FilenameUtils.getName(file)+" could not be read! The following Problem was encountered: "+e.getMessage());
		}
		return track;
	}
	
	public List<TrackPoint> getTrackPoints(String file){
		List<TrackPoint> points = new ArrayList<>();
		try {
			setUpTrackParser(file);
			if(FilenameUtils.getExtension(file).equals("gpx")) {
				points = new GPXParser().readGPXTrackPoints(file, streamReader);
			} else {
				points = new TCXParser().readTCXTrackPoints(file, streamReader);
			}
			in.close();
		} catch (Exception e) {
			showErrorPopUp("The GPS-Track "+FilenameUtils.getName(file)+" could not be read! The following Problem was encountered: "+e.getMessage());
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
