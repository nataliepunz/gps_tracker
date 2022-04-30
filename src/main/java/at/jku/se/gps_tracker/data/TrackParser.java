package at.jku.se.gps_tracker.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
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
	double trackDistance;
	double totalElevation;
	Duration totalDuration;
	
	//TCX
	Instant startTime;
	int averageBPM;
	int averageBPMCount;
	int maximumBPM;
	
	boolean trackDetailSet;
	
	// for TrackPoints themselves
	int trackPointNr;
	double elevation;
	double elevationChange;
	double latitude;
	double longtitude;
	Instant timeRecorded;
	Duration timeNeeded;
	LocalTime intermediateTime;
	
	//TCX
	boolean elevationSet;
	boolean positionSet;
	double distanceMeters;
	boolean distanceMetersSet;
	int averageBPMPoint;
	int helpMaxBPM;
	double helpDistance;
	
	//data about previous trackPoint
	LocalTime prevTime;
	double prevElevation;
	boolean prevElevationSet;
	double prevLatitude;
	double prevLongtitude;
	boolean prevCoordinatesSet;
	
	//TCX
	double prevDistance = 0;
	
	
	TrackParsingOperations conn;
	
	public TrackParser() {
		
	}
	
	public TrackParser(TrackParsingOperations conn) {
		this.conn = conn;
	}
	
	public Track getTrack(String file){
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		InputStream in;
		XMLStreamReader streamReader;
		Track track = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			streamReader = inputFactory.createXMLStreamReader(in);
			if(FilenameUtils.getExtension(file).equals("gpx")) {
				track = new GPXParser().readGPXTrack(file, streamReader);
			} else {
				track = new TCXParser().readTCXTrack(file, streamReader);
			}
			in.close();
		} catch (Exception e) {
			showErrorPopUp("The GPS-Track "+FilenameUtils.getName(file)+" could not be read! The following Problem was encountered: "+e.getMessage());
		}
		return track;
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
