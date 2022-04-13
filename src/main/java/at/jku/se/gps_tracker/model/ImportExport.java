package at.jku.se.gps_tracker.model;

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
	
	default void updateTracks(List<Track> trackList, HashSet<String> files, HashSet<String> readFiles){
		HashSet<String> copyFiles = new HashSet<String>(files);
		HashSet<String> copyReadFiles = new HashSet<String>(readFiles);
		files.removeAll(readFiles);
		for(String file : files) {
			if(FilenameUtils.getExtension(file).equals("gpx")) {
				try {
					trackList.add(readGPXTrack(file));
				} catch (IOException | XMLStreamException | ParserConfigurationException | TransformerFactoryConfigurationError | TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (FilenameUtils.getExtension(file).equals("tcx")) {
				try {
					trackList.add(readTCXTrack(file));
				} catch (IOException | XMLStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			readFiles.add(file);
		}
		copyReadFiles.removeAll(copyFiles);
		for(String s : readFiles) {
			removeTrack(s);
		}
		readFiles.removeAll(copyReadFiles);
	}
	
	default Track readGPXTrack(String file) throws XMLStreamException, ParserConfigurationException, IOException, TransformerFactoryConfigurationError, TransformerException {
		
		// per Track
		List<TrackPoint> helpList = new ArrayList<>();
		LocalDate trackDate = null;
		LocalTime trackTime = null;
		boolean trackDetailSet = false;
		double trackDistance = 0;
		double totalElevation = 0;
		Duration totalDuration = Duration.ofSeconds(0);
		
		// for TrackPoints themselves
		int trackPointNr = 1;
		
		//data this trackPoint
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
								if(timeNeeded==null || timeNeeded.getSeconds()==0 || distance==0) {
									helpList.add(new TrackPoint(trackPointNr,distance, Duration.ofSeconds(0),0,0, elevationChange));
								} else {
									helpList.add(new TrackPoint(trackPointNr,distance, timeNeeded, timeNeeded.getSeconds()/distance, distance/timeNeeded.getSeconds(),elevationChange));
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
		
		if(totalDuration.getSeconds()==0 || trackDistance==0) {
			return new Track(/* activity, */ FilenameUtils.getName(file), trackDate, trackTime, trackDistance, totalDuration, 0, 0, totalElevation, helpList);
		} else {
			return new Track(/* activity, */ FilenameUtils.getName(file), trackDate, trackTime, trackDistance, totalDuration, totalDuration.getSeconds()/trackDistance, trackDistance/totalDuration.getSeconds(), totalElevation, helpList);
		}
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
		int averageBPM = 0;
		int averageBPMCount = 0;
		int maximumBPM = 0;
		double trackDistance = 0;
		double totalElevation = 0;
		Duration totalDuration = Duration.ofSeconds(0);
		
		
		//data per trackPoint
		int trackPointNr = 1;
		double elevation;
		boolean elevationSet;
		double elevationChange;
		double latitude;
		double longtitude;
		boolean positionSet;
		double distanceMeters;
		boolean distanceMetersSet;
		Instant timeRecorded;
		Duration timeNeeded;
		LocalTime intermediateTime;
		int averageBPMPoint;
		int helpMaxBPM;
		double helpDistance;
		
		//data about previous trackPoint
		LocalTime prevTime = null;
		double prevElevation = 0;
		boolean prevElevationSet = false;
		double prevLatitude = 0;
		double prevLongtitude = 0;
		double prevDistance = 0;
		boolean prevPositionSet = false;
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in = new FileInputStream(file);
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);
		
		while (streamReader.hasNext()) {
			if (streamReader.isStartElement()) {
				switch (streamReader.getLocalName()) {
					case "Activity":{
						while(streamReader.hasNext()) {
							streamReader.next();
							if (streamReader.isStartElement()) {
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
														elevation = 0;
														elevationChange = 0;
														timeRecorded = null;
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
																		timeRecorded = Instant.parse(streamReader.getElementText());
																		intermediateTime = LocalTime.ofInstant(timeRecorded, ZoneId.systemDefault());
																		if(prevTime==null) prevTime = intermediateTime;
																		timeNeeded = Duration.between(prevTime, intermediateTime);
																		prevTime = intermediateTime;
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
																}
															}
															
															if(streamReader.isEndElement()&&"Trackpoint".equals(streamReader.getLocalName())){
																//TODO create and add new trackpoint!
																if(!distanceMetersSet && positionSet && prevPositionSet) {
																	distanceMeters = distance(latitude, prevLatitude, longtitude, prevLongtitude, elevation, prevElevation);
																}
																if(timeNeeded==null || timeNeeded.getSeconds()==0 || distanceMeters==0) {
																	helpList.add(new TrackPoint(trackPointNr, distanceMeters, timeNeeded, 0, 0, averageBPMPoint, averageBPMPoint, elevationChange));
																} else {
																	helpList.add(new TrackPoint(trackPointNr, distanceMeters, timeNeeded, timeNeeded.getSeconds()/distanceMeters, distanceMeters/timeNeeded.getSeconds(), averageBPMPoint, averageBPMPoint, elevationChange));
																}
																trackPointNr++;
																if(positionSet) {
																	prevLatitude = latitude;
																	prevLongtitude = longtitude;
																	prevPositionSet = true;
																}
																if(elevationSet) prevElevation = elevation;
																break;
															}
															streamReader.next();
														}
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
							if(streamReader.isEndElement() && "Activity".equals(streamReader.getLocalName())) {
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
		if(averageBPMCount!=0) averageBPM = averageBPM/averageBPMCount;
		
		if(totalDuration.getSeconds()==0 || trackDistance==0) {
			return new Track(FilenameUtils.getName(file), trackDate, trackTime, trackDistance, totalDuration,0,0, averageBPM, maximumBPM, totalElevation, helpList);
		} else {
			return new Track(FilenameUtils.getName(file), trackDate, trackTime, trackDistance, totalDuration,totalDuration.getSeconds()/trackDistance,trackDistance/totalDuration.getSeconds(), averageBPM, maximumBPM, totalElevation, helpList);
		}
	}
	
	default void removeTrack(String track) {
		trackList.rem
	}
	
}
