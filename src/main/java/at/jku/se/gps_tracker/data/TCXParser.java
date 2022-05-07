package at.jku.se.gps_tracker.data;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FilenameUtils;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;

class TCXParser extends TrackParser {
	private int averageBPM;
	private int averageBPMCount;
	private int maximumBPM;
	private boolean positionSet;
	private double trackPointDistanceMeters;
	private boolean trackPointDistanceMetersSet;
	private int averageBPMTrackPoint;
	private double prevDistance;
	
	TCXParser(){
		trackPointsList = new ArrayList<>();
		trackTimeDate = null;
		averageBPM = 0;
		averageBPMCount = 0;
		maximumBPM = 0;
		totalDistance = 0;
		totalElevation = 0;
		totalDuration = Duration.ofSeconds(0);
		
		trackPointNr = 1;
		
		prevTrackPointTime = null;
		prevTrackPointElevation = 0;
		prevTrackPointElevationSet = false;
		prevTrackPointLatitude = 0;
		prevTrackPointLongtitude = 0;
		prevDistance = 0;
		prevTrackPointCoordinatesSet = false;
	}
	
	List<TrackPoint> readTCXTrackPoints(XMLStreamReader streamReader) throws XMLStreamException {
		readTrack(streamReader);
		return trackPointsList;
	}
		
	Track readTCXTrack (String file, XMLStreamReader streamReader) throws XMLStreamException {
		readTrack(streamReader);		
		if(averageBPMCount!=0) averageBPM = averageBPM/averageBPMCount;
		return createTCXTrack(file);
	}
	
	private void readTrack(XMLStreamReader streamReader) throws XMLStreamException {
		while (streamReader.hasNext()) {
			if (streamReader.isStartElement() && streamReader.getLocalName().equals("Activity")) {
					manageTCXActivityElement(streamReader);
				}
			streamReader.next();
		}
		streamReader.close();
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
		if(trackTimeDate==null) {
			trackTimeDate = Instant.parse(streamReader.getAttributeValue(null, "StartTime"));
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
						totalDistance+= Double.parseDouble(streamReader.getElementText());
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
						int helpMaxBPM = Integer.parseInt(streamReader.getElementText());
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
		trackPointElevation = 0;
		trackPointElevationChange = 0;
		trackPointDuration = Duration.ofSeconds(0);
		trackPointLatitude = 0;
		trackPointLongtitude = 0;
		averageBPMTrackPoint = 0;
		trackPointDistanceMeters = 0;
		trackPointDistanceMetersSet = false;
		positionSet = false;
		trackPointElevationSet = false;
		while(streamReader.hasNext()) {
			if (streamReader.isStartElement()) {
				switch (streamReader.getLocalName()) {
					case "Time":{
						calculateTCXTrackPointTime(streamReader);
						break;
					}
					case "LatitudeDegrees":{
						trackPointLatitude = Double.parseDouble(streamReader.getElementText());
						break;
					}
					case "LongtitudeDegrees":{
						trackPointLongtitude = Double.parseDouble(streamReader.getElementText());
						positionSet = true;
						break;
					}
					case "AltitudeMeters":{
						calculateTCXTrackPointElevation(streamReader);
						break;
					}
					case "DistanceMeters":{
						double helpDistance = Double.parseDouble(streamReader.getElementText());
						trackPointDistanceMeters = helpDistance-prevDistance;
						trackPointDistanceMetersSet = true;
						prevDistance = helpDistance;
						break;
					}
					case "HeartRateBpm":{
						streamReader.nextTag();
						averageBPMTrackPoint = Integer.parseInt(streamReader.getElementText());
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
		trackPointTimePoint = Instant.parse(streamReader.getElementText());
		if(prevTrackPointTime==null) {
			prevTrackPointTime = trackPointTimePoint;
		}
		trackPointDuration = Duration.between(prevTrackPointTime, trackPointTimePoint);
		prevTrackPointTime = trackPointTimePoint;
	}
	
	private void calculateTCXTrackPointElevation(XMLStreamReader streamReader) throws XMLStreamException {
		trackPointElevation = Double.parseDouble(streamReader.getElementText());
		trackPointElevationSet = true;
		if(!prevTrackPointElevationSet) {
			prevTrackPointElevation = trackPointElevation;
			prevTrackPointElevationSet=true;
		}
		if(trackPointElevation>prevTrackPointElevation) {
			trackPointElevationChange = trackPointElevation - prevTrackPointElevation;
			totalElevation += trackPointElevationChange;
		}
	}
		
	private void createTCXTrackPoint() {
		if(!trackPointElevationSet){
			trackPointElevation = prevTrackPointElevation;
		}
		if(!trackPointDistanceMetersSet && positionSet && prevTrackPointCoordinatesSet) {
			trackPointDistanceMeters = distance(trackPointLatitude, prevTrackPointLatitude, trackPointLongtitude, prevTrackPointLongtitude, trackPointElevation, prevTrackPointElevation);
		}
		trackPointsList.add(new TrackPoint(String.valueOf(trackPointNr), trackPointDistanceMeters, trackPointDuration, averageBPMTrackPoint, averageBPMTrackPoint, trackPointElevationChange));
		trackPointNr++;
		if(positionSet) {
			prevTrackPointLatitude = trackPointLatitude;
			prevTrackPointLongtitude = trackPointLongtitude;
			prevTrackPointCoordinatesSet = true;
		}
		if(trackPointElevationSet) prevTrackPointElevation = trackPointElevation;
	}
	
	private Track createTCXTrack(String file) {
		if(trackTimeDate==null) {
			trackTimeDate = Instant.now();
		}
		return new Track.TrackBuilder(new File(file).getParentFile().getName(),FilenameUtils.getName(file), trackTimeDate)
				.distance(totalDistance)
				.duration(totalDuration)
				.averageBPM(averageBPM)
				.maximumBPM(maximumBPM)
				.elevation(totalElevation)
				.trackPoints(trackPointsList)
				.build();
	}

	
}
