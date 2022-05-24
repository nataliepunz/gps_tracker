package at.jku.se.gps_tracker.data;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FilenameUtils;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;

class TCXParser extends TrackParser {
	private static int averageBPM;
	private static int averageBPMCount;
	private static int maximumBPM;
	private static boolean positionSet;
	private static double trackPointDistanceMeters;
	private static boolean trackPointDistanceMetersSet;
	private static int averageBPMTrackPoint;
	private static double prevDistance;
	
	private static void resetTCXFields(){
		averageBPM = 0;
		averageBPMCount = 0;
		maximumBPM = 0;
		prevDistance = 0;
		resetFields();
	}
			
	static Track readTCXTrack (String file, XMLStreamReader streamReader) throws XMLStreamException {
		resetTCXFields();
		readTrack(streamReader);		
		if(averageBPMCount!=0) {
			averageBPM = averageBPM/averageBPMCount;
		}
		return createTCXTrack(file);
	}
	
	private static void readTrack(XMLStreamReader streamReader) throws XMLStreamException {
		while (streamReader.hasNext()) {
			if (streamReader.isStartElement() && streamReader.getLocalName().equals("Activity")) {
					manageTCXActivityElement(streamReader);
				}
			streamReader.next();
		}
		streamReader.close();
	}
	
	private static void manageTCXActivityElement(XMLStreamReader streamReader) throws NumberFormatException, XMLStreamException {
		while(streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				switch(streamReader.getLocalName()) {
					case "Id" :{
						if(trackName==null) {
							trackName = streamReader.getElementText();
						}
						break;
					}
					case "Lap" :{
						manageTCXLapElement(streamReader);
						break;
					}
					default :{
						break;
					}
				}
			}
			if(streamReader.isEndElement() && "Activity".equals(streamReader.getLocalName())) {
				break;
			}
		}
	}
	
	private static void manageTCXLapElement(XMLStreamReader streamReader) throws NumberFormatException, XMLStreamException {
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
	
	private static void manageTCXTrackPointElement(XMLStreamReader streamReader) throws NumberFormatException, XMLStreamException {
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
	
	private static void calculateTCXTrackPointTime(XMLStreamReader streamReader) throws XMLStreamException {
		trackPointTimePoint = Instant.parse(streamReader.getElementText());
		if(prevTrackPointTime==null) {
			prevTrackPointTime = trackPointTimePoint;
		}
		trackPointDuration = Duration.between(prevTrackPointTime, trackPointTimePoint);
		prevTrackPointTime = trackPointTimePoint;
	}
	
	private static void calculateTCXTrackPointElevation(XMLStreamReader streamReader) throws XMLStreamException {
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
		
	private static void createTCXTrackPoint() {
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
		if(trackPointElevationSet) {
			prevTrackPointElevation = trackPointElevation;
		}
	}
	
	private static Track createTCXTrack(String file) {
		if(trackTimeDate==null) {
			trackTimeDate = Instant.now();
		}
		if(trackName==null) {
			trackName = FilenameUtils.getName(file);
		}
		return new Track.TrackBuilder(new File(file).getParentFile().getName(),FilenameUtils.getName(file), trackName, LocalDate.ofInstant(trackTimeDate, ZoneId.systemDefault()), LocalTime.parse(LocalTime.ofInstant(trackTimeDate, ZoneId.systemDefault()).format(dtf)))
				.distance(totalDistance)
				.duration(totalDuration)
				.averageBPM(averageBPM)
				.maximumBPM(maximumBPM)
				.elevation(totalElevation)
				.trackPoints(trackPointsList)
				.build();
	}

	
}
