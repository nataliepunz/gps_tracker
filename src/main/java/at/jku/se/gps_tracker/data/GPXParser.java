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

class GPXParser extends TrackParser{
		
	GPXParser(){
		// per Track
		trackPointsList = new ArrayList<>();
		trackTimeDate = null;
		totalDistance = 0;
		totalElevation = 0;
		totalDuration = Duration.ofSeconds(0);
				
		// for TrackPoints themselves
		trackPointNr = 1;
		
		//data about previous trackPoint
		prevTrackPointTime = null;
		prevTrackPointElevation = 0;
		prevTrackPointElevationSet = false;
		prevTrackPointLatitude = 0;
		prevTrackPointLongtitude = 0;
		prevTrackPointCoordinatesSet = false;	
	}
	
	List<TrackPoint> readGPXTrackPoints(XMLStreamReader streamReader) throws XMLStreamException {
		readTrack(streamReader);
		return trackPointsList;
	}
	
	Track readGPXTrack(String file, XMLStreamReader streamReader) throws XMLStreamException {
		readTrack(streamReader);
		return createGPXTrack(file);
	}
	
	private void readTrack(XMLStreamReader streamReader) throws XMLStreamException {
		while (streamReader.hasNext()) {
			if (streamReader.isStartElement() && streamReader.getLocalName().equals("trkpt")) {
				manageGPXTrackPointElement(streamReader);
			}
			streamReader.next();
		}
		streamReader.close();
	}
	
	private void manageGPXTrackPointElement(XMLStreamReader streamReader) throws NumberFormatException, XMLStreamException {
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

	private void calculateGPXElevation(XMLStreamReader streamReader) throws NumberFormatException, XMLStreamException {
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
	
	private void calculateGPXTime(XMLStreamReader streamReader) throws XMLStreamException {
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
	
	private void createGPXTrackPoint() {
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
	
	private Track createGPXTrack(String file) {
		if(trackTimeDate==null) {
			trackTimeDate = Instant.now();
		}
		return new Track.TrackBuilder(new File(file).getParentFile().getName(),FilenameUtils.getName(file), trackTimeDate)
					.distance(totalDistance)
					.duration(totalDuration)
					.elevation(totalElevation)
					.trackPoints(trackPointsList)
					.build();
	}	
}
