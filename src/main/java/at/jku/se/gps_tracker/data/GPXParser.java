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

class GPXParser extends TrackParser{
				
	static Track readGPXTrack(String file, XMLStreamReader streamReader) throws XMLStreamException {
		resetFields();
		readTrack(streamReader);
		return createGPXTrack(file);
	}
	
	private static void readTrack(XMLStreamReader streamReader) throws XMLStreamException {
		while (streamReader.hasNext()) {
			if (streamReader.isStartElement() && streamReader.getLocalName().equals("trk")) {
				manageGPXTracks(streamReader);
			}
			streamReader.next();
		}
		streamReader.close();
	}
	
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
	
	private static void manageGPXTrackPointElement(XMLStreamReader streamReader) throws XMLStreamException {
		trackPointElevation = 0;
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

	private static void calculateGPXElevation(XMLStreamReader streamReader) throws XMLStreamException {
		trackPointElevation = Double.parseDouble(streamReader.getElementText());
		trackPointElevationSet = true;
		if(!prevTrackPointElevationSet) {
			prevTrackPointElevation = trackPointElevation;
		}
		if(trackPointElevation>prevTrackPointElevation) {
			trackPointElevationChange = trackPointElevation - prevTrackPointElevation;
			totalElevation += trackPointElevationChange;
		}
	}
	
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
	
	private static void createGPXTrackPoint() {
		if(!trackPointElevationSet){
			trackPointElevation = prevTrackPointElevation;
		}
		double distance = distance(trackPointLatitude, prevTrackPointLatitude, trackPointLongtitude, prevTrackPointLongtitude, trackPointElevation, prevTrackPointElevation);
		totalDistance += distance;
		trackPointsList.add(new TrackPoint(String.valueOf(trackPointNr), distance, trackPointDuration, trackPointElevationChange));
		trackPointNr++;
		prevTrackPointLatitude = trackPointLatitude;
		prevTrackPointLongtitude = trackPointLongtitude;
		if(trackPointElevationSet) {
			prevTrackPointElevation = trackPointElevation;
		}
	}
	
	private static Track createGPXTrack(String file) {
		if(trackTimeDate==null) {
			trackTimeDate = Instant.now();
		}
		if(trackName==null) {
			trackName=FilenameUtils.getName(file);
		}
		return new Track.TrackBuilder(new File(file).getParentFile().getName(),FilenameUtils.getName(file),trackName, LocalDate.ofInstant(trackTimeDate, ZoneId.systemDefault()), LocalTime.parse(LocalTime.ofInstant(trackTimeDate, ZoneId.systemDefault()).format(dtf)))
					.distance(totalDistance)
					.duration(totalDuration)
					.elevation(totalElevation)
					.trackPoints(trackPointsList)
					.build();
	}	
}
