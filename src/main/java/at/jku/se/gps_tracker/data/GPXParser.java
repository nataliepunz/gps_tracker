package at.jku.se.gps_tracker.data;

import java.io.File;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FilenameUtils;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;
/**
 * class to parse GPX files
 * @author Ozan
 */
final class GPXParser extends TrackParser{
	
	/**
	 * method for start of the parsing process
	 * @author Ozan
	 * @param file given file path of track
	 * @param streamReader the streamReader instance of the file of the track
	 * @return parsed Track
	 * @throws XMLStreamException
	 */
	static Track readGPXTrack(String file, XMLStreamReader streamReader) throws XMLStreamException {
		resetFields();
		readTrack(streamReader);
		return createGPXTrack(file);
	}
	
	/**
	 * finds trk Tags and reads underlying tags
	 * @author Ozan
	 * @param streamReader streamReader the streamReader instance of the file of the track
	 * @throws XMLStreamException
	 */
	private static void readTrack(XMLStreamReader streamReader) throws XMLStreamException {
		while (streamReader.hasNext()) {
			if (streamReader.isStartElement() && streamReader.getLocalName().equals("trk")) {
				manageGPXTracks(streamReader);
			}
			streamReader.next();
		}
		streamReader.close();
	}
	
	/**
	 * differentiates between name tag or trkpt (trackpoint) tag
	 * breaks the loop when end tag of track is found
	 * @author Ozan
	 * @param streamReader streamReader the streamReader instance of the file of the track
	 * @throws XMLStreamException
	 */
	private static void manageGPXTracks(XMLStreamReader streamReader) throws XMLStreamException {
		while (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				switch(streamReader.getLocalName()) {
					case "name" :{
						if(trackName==null) {
							trackName = streamReader.getElementText();
						}
						break;
					}
					case "trkpt" :{
						manageGPXTrackPointElement(streamReader);
						break;
					}
					default :{
						break;
					}
				}
			}
			if(streamReader.isEndElement() && "trk".equals(streamReader.getLocalName())) {
				break;
			}
		}
	}
	
	/**
	 * parses the TrackPoint elements and calls for creation a TrackPoint object at the end
	 * checks if elevation or a time is also included and calls apporpiate methods
	 * resets the values of the trackPoints
	 * @author Ozan
	 * @param streamReader streamReader the streamReader instance of the file of the track
	 * @throws XMLStreamException
	 */
	private static void manageGPXTrackPointElement(XMLStreamReader streamReader) throws XMLStreamException {
		trackPointElevation = new BigDecimal(0);
		trackPointElevationChange = 0;
		trackPointElevationSet = false;
		trackPointDuration = Duration.ofSeconds(0);
		trackPointLatitude = Double.parseDouble(streamReader.getAttributeValue(null, "lat"));
		trackPointLongtitude = Double.parseDouble(streamReader.getAttributeValue(null, "lon"));
		if(!prevTrackPointCoordinatesSet) {
			prevTrackPointLatitude = trackPointLatitude;
			prevTrackPointLongtitude = trackPointLongtitude;
			prevTrackPointCoordinatesSet=true;
		}
		while((streamReader.hasNext())) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				switch (streamReader.getLocalName()) {
					case "ele":{
						calculateGPXElevation(streamReader);
						break;
					}
					case "time":{
						calculateGPXTime(streamReader);
						break;
					}
					default:{
						break;
					}
				}
			}
			
			if(streamReader.isEndElement() && "trkpt".equals(streamReader.getLocalName())) {
				createGPXTrackPoint();
				break;
			}
		}
	}
	
	/**
	 * calculates the elevation and elevation gains and assigns them accordingly
	 * @author Ozan
	 * @param streamReader streamReader the streamReader instance of the file of the track
	 * @throws XMLStreamException
	 */
	private static void calculateGPXElevation(XMLStreamReader streamReader) throws XMLStreamException {
		trackPointElevation = new BigDecimal(streamReader.getElementText());
		trackPointElevationSet = true;
		if(!prevTrackPointElevationSet) {
			prevTrackPointElevation = trackPointElevation;
			prevTrackPointElevationSet = true;
		}
		if(trackPointElevation.doubleValue()>prevTrackPointElevation.doubleValue()) {
			trackPointElevationChange = trackPointElevation.subtract(prevTrackPointElevation).doubleValue();
			totalElevation += trackPointElevationChange;
		}
	}
	
	/**
	 * calculates the duration between this trackpoint and the previous one
	 * @author Ozan
	 * @param streamReader streamReader the streamReader instance of the file of the track
	 * @throws XMLStreamException
	 */
	private static void calculateGPXTime(XMLStreamReader streamReader) throws XMLStreamException {
		trackPointTimePoint = Instant.parse(streamReader.getElementText());
		if(trackTimeDate==null) {
			trackTimeDate = trackPointTimePoint;
		}
		if(prevTrackPointTime==null) {
			prevTrackPointTime = trackPointTimePoint;
		}
		trackPointDuration = Duration.between(prevTrackPointTime, trackPointTimePoint);
		totalDuration = totalDuration.plus(trackPointDuration);
		prevTrackPointTime = trackPointTimePoint;
	}
	
	/**
	 * creates the trackpoint based on the parsed information
	 * @author Ozan
	 */
	private static void createGPXTrackPoint() {
		if(!trackPointElevationSet){
			trackPointElevation = prevTrackPointElevation;
		}
		double distance = distance(trackPointLatitude, prevTrackPointLatitude, trackPointLongtitude, prevTrackPointLongtitude, trackPointElevation.doubleValue(), prevTrackPointElevation.doubleValue());
		totalDistance += distance;
		trackPointsList.add(new TrackPoint(String.valueOf(trackPointNr), distance, trackPointDuration, trackPointElevationChange));
		trackPointNr++;
		prevTrackPointLatitude = trackPointLatitude;
		prevTrackPointLongtitude = trackPointLongtitude;
		if(trackPointElevationSet) {
			prevTrackPointElevation = trackPointElevation;
		}
	}
	
	/**
	 * creates the Track based on the parsed information
	 * if no time has been parsed set today as day
	 * if no name has been parsed set file name as name of track
	 * @param file given file path of track
	 * @return Track based on parsed information
	 */
	private static Track createGPXTrack(String file) {
		if(trackTimeDate==null) {
			trackTimeDate = Instant.now();
		}
		if(trackName==null) {
			trackName=FilenameUtils.getName(file);
		}
		return new Track.TrackBuilder(new File(file).getParentFile().getName(),FilenameUtils.getName(file),trackName, LocalDate.ofInstant(trackTimeDate, ZoneId.systemDefault()), LocalTime.ofInstant(trackTimeDate, ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS))
					.distance(totalDistance)
					.duration(totalDuration)
					.elevation(totalElevation)
					.trackPoints(trackPointsList)
					.build();
	}	
}
