package at.jku.se.gps_tracker.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;


public interface ImportExport {
	
	default void updateTracks(List<Track> trackList, List<File> files, HashSet<String> readFiles){
		for(File file : files) {
			if(FilenameUtils.getExtension(file.getAbsolutePath()).equals("gpx")) {
				try {
					trackList.add(readGPXTrack(file.getAbsolutePath()));
				} catch (IOException | XMLStreamException | ParserConfigurationException | TransformerFactoryConfigurationError | TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (FilenameUtils.getExtension(file.getAbsolutePath()).equals("tcx")) {
				try {
					trackList.add(readTCXTrack(file.getAbsolutePath()));
				} catch (IOException | XMLStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	default Track readGPXTrack(String file) throws XMLStreamException, ParserConfigurationException, IOException, TransformerFactoryConfigurationError, TransformerException {
		// for Track itself
		List<TrackPoint> helpList = new ArrayList<>();
		LocalDate trackDate = null;
		LocalTime trackTime = null;
		boolean trackDetailSet = false;
		double trackDistance = 0;
		int totalElevation = 0;
		Duration totalDuration = Duration.ofSeconds(0);
		
		// for TrackPoints themselves
		int trackPointNr = 1;
		
		//data per trackPoint
		double elevation;
		double elevationChange;
		double latitude;
		double longtitude;
		Instant timeRecorded;
		Duration timeNeeded;
		LocalTime intermediateTime;
		
		//data about previous trackPoint
		LocalTime prevTime = null;
		double prevElevation = 0;
		boolean prevElevationSet = false;
		double prevLatitude = 0;
		double prevLongtitude = 0;
		boolean prevCoordinatesSet = false;
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in = new FileInputStream(file);
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);
		
		while (streamReader.hasNext()) {
			if (streamReader.isStartElement()) {
				switch (streamReader.getLocalName()) {
					/*
					case "gpx":{
						if(streamReader.getAttributeValue(null,"activity")!=null) activity = streamReader.getAttributeValue(null,"activity");
						break;
					}
					*/
					case "trkpt": {
						elevation = 0;
						elevationChange = 0;
						timeRecorded = null;
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
										elevation = Double.parseDouble(streamReader.getElementText());
										if(!prevElevationSet) {
											prevElevation = elevation;
											prevElevationSet=true;
										}
										if(elevation>prevElevation) {
											elevationChange = elevation - prevElevation;
											totalElevation += elevationChange;
										}
										break;
									}
									case "time":{
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
										break;
									}
								}
							}
							
							if(streamReader.isEndElement() && "trkpt".equals(streamReader.getLocalName())) {
								double distance = distance(latitude, prevLatitude, longtitude, prevLongtitude, elevation, prevElevation);
								trackDistance += distance;
								if(timeNeeded==null) {
									helpList.add(new TrackPoint(trackPointNr,distance, Duration.ofSeconds(0), elevationChange));
								} else {
									helpList.add(new TrackPoint(trackPointNr,distance, timeNeeded, elevationChange));
								}
								trackPointNr++;
								prevLatitude = latitude;
								prevLongtitude = longtitude;
								prevElevation = elevation;
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
		
		//if (activity==null) activity = chooseAndWriteActivity(file);
		
		return new Track(/* activity, */ FilenameUtils.getBaseName(file), trackDate, trackTime, trackDistance, totalDuration, totalElevation, helpList);
	}
	
	
	//from here: https://stackoverflow.com/a/16794680
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
	/*
	public static String chooseAndWriteActivity(String file) throws XMLStreamException, ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		Stage chooser = new Stage();
		chooser.setTitle("Wählen Sie eine Aktivität aus für diesen GPS-Track");
		BorderPane layout = new BorderPane();
		TextField choosenActivity = new TextField();
		choosenActivity.setText("Running");
		Text text = new Text("Geben Sie die Aktivität an! Falls Sie dass Fenster schließen wird die letzte Eingabe als Aktivität verwendet!");
		Button button = new Button("Bestätigen");
		button.setOnAction(e -> chooser.close());
		VBox vbox = new VBox(text,choosenActivity,button);
		vbox.setSpacing(5);
		layout.setCenter(vbox);
		Scene chooserScene = new Scene(layout,450,90);
		chooser.setScene(chooserScene);
		chooser.showAndWait();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new FileInputStream(new File(file)));
		
		Element element = (Element) doc.getElementsByTagName("gpx").item(0);
		
		element.setAttribute("activity", choosenActivity.getText());
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
		
		return choosenActivity.getText();
	}
	*/
	
	default Track readTCXTrack (String file) throws XMLStreamException, FileNotFoundException {
		
		List<TrackPoint> helpList = new ArrayList<TrackPoint>();
		Instant startTime = null;
		LocalDate trackDate = null;
		LocalTime trackTime = null;
		boolean trackDetailSet = false;
		double trackDistance = 0;
		int totalElevation = 0;
		Duration totalDuration = Duration.ofSeconds(0);
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in = new FileInputStream(file);
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);
		
		while (streamReader.hasNext()) {
			if (streamReader.isStartElement()) {
				System.out.println(streamReader.getLocalName());
				switch (streamReader.getLocalName()) {
					case "Lap":{
						if(startTime==null) {
							startTime = Instant.parse(streamReader.getAttributeValue(null, "StartTime"));
							trackTime = LocalTime.ofInstant(startTime, ZoneId.systemDefault());
							trackDate = LocalDate.ofInstant(startTime, ZoneId.systemDefault());
						}
						while(streamReader.hasNext()) {
							streamReader.next();
							if (streamReader.isStartElement()) {
								System.out.println(streamReader.getLocalName());
								switch (streamReader.getLocalName()) {
									case "TotalTimeSeconds":{
										totalDuration = totalDuration.plusSeconds((long) Double.parseDouble(streamReader.getElementText()));
										break;
									}
									case "DistanceMeters":{
										trackDistance+= Double.parseDouble(streamReader.getElementText());
										break;
									}
								}
							}
							
							if(streamReader.isEndElement() && "Lap".equals(streamReader.getLocalName())) {
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
		return new Track(FilenameUtils.getBaseName(file), null, null, 0, null, 0, 0, 0, null);
	}
}
