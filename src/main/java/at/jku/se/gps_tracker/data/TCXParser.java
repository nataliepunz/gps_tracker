package at.jku.se.gps_tracker.data;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FilenameUtils;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;

public class TCXParser extends TrackParser {
	
	public Track readTCXTrack (String file, XMLStreamReader streamReader) throws XMLStreamException {
		
		helpList = new ArrayList<>();
		startTime = null;
		trackDate = null;
		trackTime = null;
		averageBPM = 0;
		averageBPMCount = 0;
		maximumBPM = 0;
		trackDistance = 0;
		totalElevation = 0;
		totalDuration = Duration.ofSeconds(0);
		
		trackPointNr = 1;
		timeNeeded = null;
		
		prevTime = null;
		prevElevation = 0;
		prevElevationSet = false;
		prevLatitude = 0;
		prevLongtitude = 0;
		prevDistance = 0;
		prevCoordinatesSet = false;
		
		while (streamReader.hasNext()) {
			if (streamReader.isStartElement() && streamReader.getLocalName().equals("Activity")) {
					manageTCXActivityElement(streamReader);
				}
			streamReader.next();
		}
		
		streamReader.close();
		
		if(averageBPMCount!=0) averageBPM = averageBPM/averageBPMCount;
		
		return createTCXTrack(file);
	}
	
	private void manageTCXActivityElement(XMLStreamReader streamReader) throws NumberFormatException, XMLStreamException {
		while(streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement() && streamReader.getLocalName().equals("Lap")) {
				manageTCXLapElement(streamReader);
			}
			if(streamReader.isEndElement() && "Activity".equals(streamReader.getLocalName())) {
				break;
			}
		}
	}
	
	private void manageTCXLapElement(XMLStreamReader streamReader) throws NumberFormatException, XMLStreamException {
		if(startTime==null) {
			startTime = Instant.parse(streamReader.getAttributeValue(null, "StartTime"));
			trackTime = LocalTime.ofInstant(startTime, ZoneId.systemDefault());
			trackDate = LocalDate.ofInstant(startTime, ZoneId.systemDefault());
		}
		while(streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				switch (streamReader.getLocalName()) {
					case "TotalTimeSeconds":{
						totalDuration = totalDuration.plusSeconds((long) Double.parseDouble(streamReader.getElementText()));
						break;
					}
					case "DistanceMeters":{
						trackDistance+= Double.parseDouble(streamReader.getElementText());
						break;
					}
					case "AverageHeartRateBpm":{
						streamReader.nextTag();
						averageBPM += Integer.parseInt(streamReader.getElementText());
						averageBPMCount++;
						break;
					}
					case "MaximumHeartRateBpm":{
						streamReader.nextTag();
						helpMaxBPM = Integer.parseInt(streamReader.getElementText());
						if(helpMaxBPM>maximumBPM) maximumBPM=helpMaxBPM; 
						break;
					}
					case "Trackpoint":{
						manageTCXTrackPointElement(streamReader);
						break;
					}
					default:{
						break;
					}
				}
			}
			if(streamReader.isEndElement() && "Lap".equals(streamReader.getLocalName())) {
				break;
			}
		}
	}
	
	private void manageTCXTrackPointElement(XMLStreamReader streamReader) throws NumberFormatException, XMLStreamException {
		elevation = 0;
		elevationChange = 0;
		timeNeeded = null;
		latitude = 0;
		longtitude = 0;
		averageBPMPoint = 0;
		distanceMeters = 0;
		distanceMetersSet=false;
		positionSet = false;
		elevationSet = false;
		while(streamReader.hasNext()) {
			if (streamReader.isStartElement()) {
				switch (streamReader.getLocalName()) {
					case "Time":{
						calculateTCXTrackPointTime(streamReader);
						break;
					}
					case "LatitudeDegrees":{
						latitude = Double.parseDouble(streamReader.getElementText());
						positionSet = true;
						break;
					}
					case "LongtitudeDegrees":{
						longtitude = Double.parseDouble(streamReader.getElementText());
						break;
					}
					case "AltitudeMeters":{
						calculateTCXTrackPointElevation(streamReader);
						break;
					}
					case "DistanceMeters":{
						helpDistance = Double.parseDouble(streamReader.getElementText());
						distanceMeters = helpDistance-prevDistance;
						distanceMetersSet = true;
						prevDistance = helpDistance;
						break;
					}
					case "HeartRateBpm":{
						streamReader.nextTag();
						averageBPMPoint = Integer.parseInt(streamReader.getElementText());
						break;
					}
					default:{
						break;
					}
				}
			}
			
			if(streamReader.isEndElement()&&"Trackpoint".equals(streamReader.getLocalName())){
				createTCXTrackPoint();
				break;
			}
			streamReader.next();
		}
	}
	
	private void calculateTCXTrackPointTime(XMLStreamReader streamReader) throws XMLStreamException {
		timeRecorded = Instant.parse(streamReader.getElementText());
		intermediateTime = LocalTime.ofInstant(timeRecorded, ZoneId.systemDefault());
		if(prevTime==null) prevTime = intermediateTime;
		timeNeeded = Duration.between(prevTime, intermediateTime);
		prevTime = intermediateTime;
	}
	
	private void calculateTCXTrackPointElevation(XMLStreamReader streamReader) throws XMLStreamException {
		elevation = Double.parseDouble(streamReader.getElementText());
		elevationSet = true;
		if(!prevElevationSet) {
			prevElevation = elevation;
			prevElevationSet=true;
		}
		if(elevation>prevElevation) {
			elevationChange = elevation - prevElevation;
			totalElevation += elevationChange;
		}
	}
		
	private void createTCXTrackPoint() {
		if(!distanceMetersSet && positionSet && prevCoordinatesSet) {
			distanceMeters = distance(latitude, prevLatitude, longtitude, prevLongtitude, elevation, prevElevation);
		}
		if(timeNeeded==null || timeNeeded.getSeconds()==0 || distanceMeters==0) {
			helpList.add(new TrackPoint(String.valueOf(trackPointNr), distanceMeters, timeNeeded, Duration.ofSeconds(0), 0, averageBPMPoint, averageBPMPoint, elevationChange));
		} else {
			helpList.add(new TrackPoint(String.valueOf(trackPointNr), distanceMeters, timeNeeded, Duration.ofSeconds((long) (timeNeeded.getSeconds()/distanceMeters)), distanceMeters/timeNeeded.getSeconds(), averageBPMPoint, averageBPMPoint, elevationChange));
		}
		trackPointNr++;
		if(positionSet) {
			prevLatitude = latitude;
			prevLongtitude = longtitude;
			prevCoordinatesSet = true;
		}
		if(elevationSet) prevElevation = elevation;
	}
	
	private Track createTCXTrack(String file) {
		if(totalDuration.getSeconds()==0 || trackDistance==0) {
			return new Track(FilenameUtils.getName(file), trackDate, trackTime, trackDistance, totalDuration,Duration.ofSeconds(0),0, averageBPM, maximumBPM, totalElevation, helpList);
		} else {
			return new Track(FilenameUtils.getName(file), trackDate, trackTime, trackDistance, totalDuration,Duration.ofSeconds((long) (totalDuration.getSeconds()/trackDistance)),trackDistance/totalDuration.getSeconds(), averageBPM, maximumBPM, totalElevation, helpList);
		}
	}
}
