package at.jku.se.gps_tracker.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public interface ImportExport {
	
	default List<Track> updateTracks(List<File> files){
		List<Track> toBeReturnedTracks = new ArrayList<>();
		for(File file : files) {
			try {
				toBeReturnedTracks.add(readGPXTrack(file));
			} catch (FileNotFoundException | XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toBeReturnedTracks;
	}
	
	default Track readGPXTrack(File file) throws FileNotFoundException, XMLStreamException {
		
		long start = System.nanoTime();
		
		// for Track itself
		List<TrackPoint> helpList = new ArrayList<>();
		LocalDate trackDate = null;
		LocalTime trackTime = null;
		
		// for TrackPoints themselves
		int trackPointNr = 1;
		LocalTime prevTime = null;
		double elevation = 0;
		double latitude = 0;
		double longtitude = 0;
		double prevElevation = 0;
		double prevLatitude = 0;
		double prevLongtitude = 0;
		String helpTime = null;
		Duration time = null;
 	
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in = new FileInputStream(file.getAbsolutePath());
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);
		streamReader.nextTag(); // Advance over "xml" element
		streamReader.nextTag(); // Advance over "gpx" element
   
		while (streamReader.hasNext()) {
			if (streamReader.isStartElement()) {
				switch (streamReader.getLocalName()) {
					case "trkpt": {
						latitude = Double.valueOf(streamReader.getAttributeValue(null, "lat"));
						longtitude = Double.valueOf(streamReader.getAttributeValue(null, "lon"));
						while((streamReader.hasNext())) {
							streamReader.next();
							if (streamReader.isStartElement()) {
								switch (streamReader.getLocalName()) {
									case "ele":{
										double helpElevation = Double.valueOf(streamReader.getElementText());
										if(prevElevation==0) prevElevation = helpElevation;
										if(helpElevation>prevElevation){
											elevation = helpElevation-prevElevation;
										} else {
											elevation = 0;
										};
										prevElevation = helpElevation;
										break;
									}
									case "time":{
										helpTime = streamReader.getElementText();
										if(trackDate==null) trackDate = LocalDate.ofInstant(Instant.parse(helpTime), ZoneId.systemDefault());
										if(trackTime==null) trackTime = LocalTime.ofInstant(Instant.parse(helpTime), ZoneId.systemDefault());
										if(prevTime==null) prevTime = LocalTime.ofInstant(Instant.parse(helpTime), ZoneId.systemDefault());
										time = Duration.between(prevTime, LocalTime.ofInstant(Instant.parse(helpTime), ZoneId.systemDefault()));
										prevTime = LocalTime.ofInstant(Instant.parse(helpTime), ZoneId.systemDefault());
										break;
									}
								}
							}
							if(streamReader.isEndElement() && "trkpt".equals(streamReader.getLocalName())) {
								if(time==null) {
									helpList.add(new TrackPoint(trackPointNr, distance(latitude, longtitude, elevation, prevLatitude, prevLongtitude, prevElevation), elevation));
								} else {
									helpList.add(new TrackPoint(trackPointNr, distance(latitude, longtitude, elevation, prevLatitude, prevLongtitude, prevElevation), time, elevation));
								}
								trackPointNr++;
								break;
							}
						}
						break;
					}
				}
			}
			
			streamReader.next();
		}
				
		streamReader.close();
		
		long end = System.nanoTime();
	    
		System.out.println("Filename: "+FilenameUtils.getBaseName(file.getAbsolutePath()));
		System.out.println("Zeit fürs Parsen: "+(double) (end-start)/1000000);
		System.out.println("Geparse Trackpoints: "+(trackPointNr-1));
		System.out.println();
		
		return new Track("Running", file.getAbsolutePath(), trackDate, trackTime, prevLongtitude, null, prevLongtitude, prevLongtitude, trackPointNr, helpList);
	}
		
	public static double distance(double lat1, double lat2, double lon1,
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
