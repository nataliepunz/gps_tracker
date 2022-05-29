package at.jku.se.gps_tracker.data;

import java.io.File;
import java.math.BigDecimal;
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
/**
 * class for parsing of TCX files
 * @author Ozan
 *
 */
final class TCXParser extends TrackParser {	
	/**
	 * save the averageBPM on track level
	 * @author Ozan
	 */
	private static int averageBPM;
	
	/**
	 * count how many averageBPM have been added on track level
	 * multiple activity lap in a file lead to potentially multiple averageBPM
	 * @author Ozan
	 */
	private static int averageBPMCount;
	
	/**
	 * save the maximumBPM on track level
	 * @author Ozan
	 */
	private static int maximumBPM;
	
	/**
	 * check for if position has been set on current trackPoint
	 * @author Ozan
	 */
	private static boolean positionSet;
	
	/**
	 * variable for distance meters inside trackPoints
	 * @author Ozan
	 */
	private static double trackPointDistanceMeters;
	
	/**
	 * check for if distance meters inside trackPoints has been set
	 * @author Ozan
	 */
	private static boolean trackPointDistanceMetersSet;
	
	/**
	 * average BPM per trackpPoint
	 * @author Ozan
	 */
	private static int averageBPMTrackPoint;
	
	/**
	 * the distance meters set of the previous trackPoint
	 * @author Ozan
	 */
	private static double prevDistance;
	
	/**
	 * reset the fields of the TCX Parser specific attributes but also the shared ones
	 * @author Ozan
	 */
	private static void resetTCXFields(){
		averageBPM = 0;
		averageBPMCount = 0;
		maximumBPM = 0;
		prevDistance = 0;
		resetFields();
	}
	
	/**
	 * method for start of parsing process
	 * @author Ozan
	 * @param file given file path of track
	 * @param streamReader the streamReader instance of the file of the track
	 * @return Track based on parsed information
	 * @throws XMLStreamException
	 */
	static Track readTCXTrack (String file, XMLStreamReader streamReader) throws XMLStreamException {
		resetTCXFields();
		readTrack(streamReader);		
		if(averageBPMCount!=0) {
			averageBPM = averageBPM/averageBPMCount;
		}
		return createTCXTrack(file);
	}
	
	/**
	 * parses the Activity tags and calls method to handle the content
	 * @author Ozan
	 * @param streamReader the streamReader instance of the file of the track
	 * @throws XMLStreamException
	 */
	private static void readTrack(XMLStreamReader streamReader) throws XMLStreamException {
		while (streamReader.hasNext()) {
			if (streamReader.isStartElement() && streamReader.getLocalName().equals("Activity")) {
					manageTCXActivityElement(streamReader);
				}
			streamReader.next();
		}
		streamReader.close();
	}
	
	/**
	 * manages the parsing of the activity element
	 * assigns the name of the track (if present)
	 * if Lap tag is parsed it calls its management method
	 * @author Ozan
	 * @param streamReader the streamReader instance of the file of the track
	 * @throws NumberFormatException
	 * @throws XMLStreamException
	 */
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
	/**
	 * manages the parsing of the TCX lap elemnts
	 * sets the trackTime as the time from first parsed lap element
	 * calculates the totaltime, distance meters, averageBPM, maximumBPM and calls management method of trackpoint if encountered
	 * @author Ozan
	 * @param streamReader the streamReader instance of the file of the track
	 * @throws NumberFormatException
	 * @throws XMLStreamException 
	 */
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
	
	/**
	 * resets the values for the current trackPoint
	 * calls for / calculates necessary information of trackPoint
	 * creates a trackPoint Element if end tag is reached
	 * @author Ozan
	 * @param streamReader the streamReader instance of the file of the track
	 * @throws NumberFormatException
	 * @throws XMLStreamException
	 */
	private static void manageTCXTrackPointElement(XMLStreamReader streamReader) throws NumberFormatException, XMLStreamException {
		trackPointElevation = new BigDecimal(0);
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
	
	/**
	 * calculates the duration between this trackpoint and the previous one
	 * @author Ozan
	 * @param streamReader streamReader the streamReader instance of the file of the track
	 * @throws XMLStreamException
	 */
	private static void calculateTCXTrackPointTime(XMLStreamReader streamReader) throws XMLStreamException {
		trackPointTimePoint = Instant.parse(streamReader.getElementText());
		if(prevTrackPointTime==null) {
			prevTrackPointTime = trackPointTimePoint;
		}
		trackPointDuration = Duration.between(prevTrackPointTime, trackPointTimePoint);
		prevTrackPointTime = trackPointTimePoint;
	}
	
	/**
	 * calculates the elevation and elevation gains and assigns them accordingly
	 * @author Ozan
	 * @param streamReader streamReader the streamReader instance of the file of the track
	 * @throws XMLStreamException
	 */
	private static void calculateTCXTrackPointElevation(XMLStreamReader streamReader) throws XMLStreamException {
		trackPointElevation = new BigDecimal(streamReader.getElementText());
		trackPointElevationSet = true;
		if(!prevTrackPointElevationSet) {
			prevTrackPointElevation = trackPointElevation;
			prevTrackPointElevationSet=true;
		}
		if(trackPointElevation.doubleValue()>prevTrackPointElevation.doubleValue()) {
			trackPointElevationChange = trackPointElevation.subtract(prevTrackPointElevation).doubleValue();
			totalElevation += trackPointElevationChange;
		}
	}
	
	/**
	 * creates the trackpoint based on the parsed information
	 * takes into account several possible configurations of files (which elements are present and which not)
	 * @author Ozan
	 */
	private static void createTCXTrackPoint() {
		if(!trackPointElevationSet){
			trackPointElevation = prevTrackPointElevation;
		}
		if(!trackPointDistanceMetersSet && positionSet && prevTrackPointCoordinatesSet) {
			trackPointDistanceMeters = distance(trackPointLatitude, prevTrackPointLatitude, trackPointLongtitude, prevTrackPointLongtitude, trackPointElevation.doubleValue(), prevTrackPointElevation.doubleValue());
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
	
	/**
	 * creates the Track based on the parsed information
	 * if no time has been parsed set today as day
	 * if no name has been parsed set file name as name of track
	 * @author Ozan
	 * @param file given file path of track
	 * @return Track based on parsed information
	 */
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
