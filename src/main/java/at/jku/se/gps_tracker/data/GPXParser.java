package at.jku.se.gps_tracker.data;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FilenameUtils;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;

public class GPXParser extends TrackParser{
		
	List<TrackPoint> readGPXTrackPoints(XMLStreamReader streamReader) throws XMLStreamException {
		readTrack(streamReader);
		return helpList;
	}
	
	Track readGPXTrack(String file, XMLStreamReader streamReader) throws XMLStreamException {
		readTrack(streamReader);
		return createGPXTrack(file);
	}
	
	private void readTrack(XMLStreamReader streamReader) throws XMLStreamException {
		resetFields();
		while (streamReader.hasNext()) {
			if (streamReader.isStartElement() && streamReader.getLocalName().equals("trkpt")) {
				manageGPXTrackPointElement(streamReader);
			}
			streamReader.next();
		}
		streamReader.close();
	}
	
	private void resetFields() {
		// per Track
		helpList = new ArrayList<>();
		trackDate = null;
		trackTime = null;
		trackDetailSet = false;
		trackDistance = 0;
		totalElevation = 0;
		totalDuration = Duration.ofSeconds(0);
		
		// for TrackPoints themselves
		trackPointNr = 1;
		timeNeeded = null;
		elevation = 0;
				
		//data about previous trackPoint
		prevTime = null;
		prevElevation = 0;
		prevElevationSet = false;
		prevLatitude = 0;
		prevLongtitude = 0;
		prevCoordinatesSet = false;			
	}
	
	private void manageGPXTrackPointElement(XMLStreamReader streamReader) throws NumberFormatException, XMLStreamException {
		elevation = 0;
		elevationChange = 0;
		timeNeeded = null;
		latitude = Double.parseDouble(streamReader.getAttributeValue(null, "lat"));
		longtitude = Double.parseDouble(streamReader.getAttributeValue(null, "lon"));
		if(!prevCoordinatesSet) {
			prevLatitude = latitude;
			prevLongtitude = longtitude;
			prevCoordinatesSet=true;
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
		elevation = Double.parseDouble(streamReader.getElementText());
		if(!prevElevationSet) {
			prevElevation = elevation;
			prevElevationSet=true;
		}
		if(elevation>prevElevation) {
			elevationChange = elevation - prevElevation;
			totalElevation += elevationChange;
		}
	}
	
	private void calculateGPXTime(XMLStreamReader streamReader) throws XMLStreamException {
		timeRecorded = Instant.parse(streamReader.getElementText());
		intermediateTime = LocalTime.ofInstant(timeRecorded, ZoneId.systemDefault());
		if(!trackDetailSet) {
			trackDate = LocalDate.ofInstant(timeRecorded, ZoneId.systemDefault());
			trackTime = intermediateTime;
			trackDetailSet=true;
		}
		if(prevTime==null) prevTime = intermediateTime;
		timeNeeded = Duration.between(prevTime, intermediateTime);
		totalDuration = totalDuration.plus(timeNeeded);
		prevTime = intermediateTime;
	}
	
	private void createGPXTrackPoint() {
		double distance = distance(latitude, prevLatitude, longtitude, prevLongtitude, elevation, prevElevation);
		trackDistance += distance;
		if(timeNeeded==null || timeNeeded.getSeconds()==0 || distance==0) {
			helpList.add(new TrackPoint(String.valueOf(trackPointNr),distance, Duration.ofSeconds(0),Duration.ofSeconds(0), 0, elevationChange));
		} else {
			helpList.add(new TrackPoint(String.valueOf(trackPointNr),distance, timeNeeded, Duration.ofSeconds((long) (timeNeeded.getSeconds()/distance)), distance/timeNeeded.getSeconds(),elevationChange));
		}
		trackPointNr++;
		prevLatitude = latitude;
		prevLongtitude = longtitude;
		prevElevation = elevation;
	}
	
	private Track createGPXTrack(String file) {
		if(totalDuration.getSeconds()==0 || trackDistance==0) {
			return new Track(new File(file).getParentFile().getName(),FilenameUtils.getName(file), trackDate, trackTime, trackDistance, totalDuration, Duration.ofSeconds(0), 0, totalElevation, helpList);
		} else {
			return new Track(new File(file).getParentFile().getName(),FilenameUtils.getName(file), trackDate, trackTime, trackDistance, totalDuration, Duration.ofSeconds((long) (totalDuration.getSeconds()/trackDistance)), trackDistance/totalDuration.getSeconds(), totalElevation, helpList);
		}
	}

	
}
