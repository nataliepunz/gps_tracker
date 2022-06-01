package at.jku.se.gps_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.xml.stream.XMLStreamException;

import at.jku.se.gps_tracker.data.TrackParser;
import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;

public class TrackParserTest {

	TrackParser tParser = new TrackParser();
	
	@Test
	void correctGPXTrackTest() throws URISyntaxException, XMLStreamException, IOException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Koglerau_regular.gpx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		assertEquals("Koglerau", track.getName());
		assertEquals(Duration.ofSeconds(5), track.getDuration());
		assertEquals(6.159764868347754, track.getDistance());
		assertEquals(0, track.getAverageBPM());
		assertEquals(0, track.getMaximumBPM());
		assertEquals(2.873324, track.getElevation());
		assertEquals(Duration.ofSeconds(811), track.getPace());
		assertEquals((6.159764868347754d/5l)*3.6, track.getSpeed());
		assertEquals(LocalDate.of(2021, 04, 01), track.getDate());
		assertEquals(LocalTime.of(19, 31, 51), track.getStartTime());
		assertEquals("Koglerau_regular.gpx", track.getFileName());
		assertEquals("GPXTracks", track.getParentDirectory());
		assertTrue(track.getTrackPoints().size()==2);
		double distanceSumTrackPoint = 0;
		Duration durationSumTrackPoint = Duration.ofSeconds(0);
		double elevationSumTrackPoint = 0;
		for(TrackPoint t : track.getTrackPoints()) {
			distanceSumTrackPoint+= t.getDistance();
			durationSumTrackPoint = durationSumTrackPoint.plus(t.getDuration());
			elevationSumTrackPoint = t.getElevation();
		}
		assertTrue(track.getDistance()==distanceSumTrackPoint);
		assertTrue(track.getDuration().equals(durationSumTrackPoint));
		assertTrue(track.getElevation()==elevationSumTrackPoint);
	}
	
	@Test
	void noElevationGPXTrackTest() throws URISyntaxException, XMLStreamException, IOException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Koglerau_no_elevation.gpx").toURI()).toFile().getAbsolutePath());
		assertEquals("Koglerau", track.getName());
		assertEquals(Duration.ofSeconds(5), track.getDuration());
		assertEquals(5.448551406048683, track.getDistance());
		assertEquals(0, track.getAverageBPM());
		assertEquals(0, track.getMaximumBPM());
		assertEquals(0, track.getElevation());
		assertEquals(Duration.ofSeconds(917), track.getPace());
		assertEquals((5.448551406048683/5l)*3.6, track.getSpeed());
		assertEquals(LocalDate.of(2021, 04, 01), track.getDate());
		assertEquals(LocalTime.of(19, 31, 51), track.getStartTime());
		assertEquals("Koglerau_no_elevation.gpx", track.getFileName());
		assertEquals("GPXTracks", track.getParentDirectory());
		assertTrue(track.getTrackPoints().size()==2);
		double distanceSumTrackPoint = 0;
		Duration durationSumTrackPoint = Duration.ofSeconds(0);
		double elevationSumTrackPoint = 0;
		for(TrackPoint t : track.getTrackPoints()) {
			distanceSumTrackPoint+= t.getDistance();
			durationSumTrackPoint = durationSumTrackPoint.plus(t.getDuration());
			elevationSumTrackPoint = t.getElevation();
		}
		assertTrue(track.getDistance()==distanceSumTrackPoint);
		assertTrue(track.getDuration().equals(durationSumTrackPoint));
		assertTrue(track.getElevation()==elevationSumTrackPoint);
	}
	
	@Test
	void twoNameTagGPXTrackTest() throws URISyntaxException, XMLStreamException, IOException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Koglerau_two_trackNames.gpx").toURI()).toFile().getAbsolutePath());
		assertEquals("Koglerau_first_tag", track.getName());
		assertEquals(Duration.ofSeconds(5), track.getDuration());
		assertEquals(6.159764868347754, track.getDistance());
		assertEquals(0, track.getAverageBPM());
		assertEquals(0, track.getMaximumBPM());
		assertEquals(2.873324, track.getElevation());
		assertEquals(Duration.ofSeconds(811), track.getPace());
		assertEquals((6.159764868347754d/5l)*3.6, track.getSpeed());
		assertEquals(LocalDate.of(2021, 04, 01), track.getDate());
		assertEquals(LocalTime.of(19, 31, 51), track.getStartTime());
		assertEquals("Koglerau_two_trackNames.gpx", track.getFileName());
		assertEquals("GPXTracks", track.getParentDirectory());
		assertTrue(track.getTrackPoints().size()==2);
		double distanceSumTrackPoint = 0;
		Duration durationSumTrackPoint = Duration.ofSeconds(0);
		double elevationSumTrackPoint = 0;
		for(TrackPoint t : track.getTrackPoints()) {
			distanceSumTrackPoint+= t.getDistance();
			durationSumTrackPoint = durationSumTrackPoint.plus(t.getDuration());
			elevationSumTrackPoint = t.getElevation();
		}
		assertTrue(track.getDistance()==distanceSumTrackPoint);
		assertTrue(track.getDuration().equals(durationSumTrackPoint));
		assertTrue(track.getElevation()==elevationSumTrackPoint);
	}
	
	@Test
	void noNameTagGPXTrackTest() throws URISyntaxException, XMLStreamException, IOException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Koglerau_no_trackName.gpx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		assertEquals("Koglerau_no_trackName.gpx", track.getName());
		assertEquals(Duration.ofSeconds(5), track.getDuration());
		assertEquals(6.159764868347754, track.getDistance());
		assertEquals(0, track.getAverageBPM());
		assertEquals(0, track.getMaximumBPM());
		assertEquals(2.873324, track.getElevation());
		assertEquals(Duration.ofSeconds(811), track.getPace());
		assertEquals((6.159764868347754d/5l)*3.6, track.getSpeed());
		assertEquals(LocalDate.of(2021, 04, 01), track.getDate());
		assertEquals(LocalTime.of(19, 31, 51), track.getStartTime());
		assertEquals("Koglerau_no_trackName.gpx", track.getFileName());
		assertEquals("GPXTracks", track.getParentDirectory());
		assertTrue(track.getTrackPoints().size()==2);
		double distanceSumTrackPoint = 0;
		Duration durationSumTrackPoint = Duration.ofSeconds(0);
		double elevationSumTrackPoint = 0;
		for(TrackPoint t : track.getTrackPoints()) {
			distanceSumTrackPoint+= t.getDistance();
			durationSumTrackPoint = durationSumTrackPoint.plus(t.getDuration());
			elevationSumTrackPoint = t.getElevation();
		}
		assertTrue(track.getDistance()==distanceSumTrackPoint);
		assertTrue(track.getDuration().equals(durationSumTrackPoint));
		assertTrue(track.getElevation()==elevationSumTrackPoint);
	}
	
	@Test
	void noTimeTagGPXTrackTest() throws URISyntaxException, XMLStreamException, IOException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Koglerau_no_time.gpx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		assertEquals("Koglerau", track.getName());
		assertEquals(Duration.ofSeconds(0), track.getDuration());
		assertEquals(6.159764868347754, track.getDistance());
		assertEquals(0, track.getAverageBPM());
		assertEquals(0, track.getMaximumBPM());
		assertEquals(2.873324, track.getElevation());
		assertEquals(Duration.ofSeconds(0), track.getPace());
		assertEquals(0, track.getSpeed());
		assertEquals(LocalDate.now(), track.getDate());
		assertEquals(LocalTime.now().truncatedTo(ChronoUnit.SECONDS), track.getStartTime());
		assertEquals("Koglerau_no_time.gpx", track.getFileName());
		assertEquals("GPXTracks", track.getParentDirectory());
		assertTrue(track.getTrackPoints().size()==2);
		double distanceSumTrackPoint = 0;
		Duration durationSumTrackPoint = Duration.ofSeconds(0);
		double elevationSumTrackPoint = 0;
		for(TrackPoint t : track.getTrackPoints()) {
			distanceSumTrackPoint+= t.getDistance();
			durationSumTrackPoint = durationSumTrackPoint.plus(t.getDuration());
			elevationSumTrackPoint = t.getElevation();
		}
		assertTrue(track.getDistance()==distanceSumTrackPoint);
		assertTrue(track.getDuration().equals(durationSumTrackPoint));
		assertTrue(track.getElevation()==elevationSumTrackPoint);
	}
	
	@Test
	void noTrackPointTagsGPXTrackTest() throws URISyntaxException, XMLStreamException, IOException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Koglerau_no_trackPoints.gpx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		assertEquals("Koglerau", track.getName());
		assertEquals(Duration.ofSeconds(0), track.getDuration());
		assertEquals(0, track.getDistance());
		assertEquals(0, track.getAverageBPM());
		assertEquals(0, track.getMaximumBPM());
		assertEquals(0, track.getElevation());
		assertEquals(Duration.ofSeconds(0), track.getPace());
		assertEquals(0, track.getSpeed());
		assertEquals("Koglerau_no_trackPoints.gpx", track.getFileName());
		assertEquals("GPXTracks", track.getParentDirectory());
		assertEquals(LocalDate.now(), track.getDate());
		assertEquals(LocalTime.now().truncatedTo(ChronoUnit.SECONDS),track.getStartTime());
		assertTrue(track.getTrackPoints().isEmpty());
		double distanceSumTrackPoint = 0;
		Duration durationSumTrackPoint = Duration.ofSeconds(0);
		double elevationSumTrackPoint = 0;
		for(TrackPoint t : track.getTrackPoints()) {
			distanceSumTrackPoint+= t.getDistance();
			durationSumTrackPoint = durationSumTrackPoint.plus(t.getDuration());
			elevationSumTrackPoint = t.getElevation();
		}
		assertTrue(track.getDistance()==distanceSumTrackPoint);
		assertTrue(track.getDuration().equals(durationSumTrackPoint));
		assertTrue(track.getElevation()==elevationSumTrackPoint);
	}
	
	@Test
	void checkEqualValuesFullGPXTrackTest() throws URISyntaxException, XMLStreamException, IOException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Koglerau_full.gpx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		double distanceSumTrackPoint = 0;
		Duration durationSumTrackPoint = Duration.ofSeconds(0);
		double elevationSumTrackPoint = 0;
		for(TrackPoint t : track.getTrackPoints()) {
			distanceSumTrackPoint+= t.getDistance();
			durationSumTrackPoint = durationSumTrackPoint.plus(t.getDuration());
			elevationSumTrackPoint += t.getElevation();
		}
		assertTrue(track.getDistance()==distanceSumTrackPoint);
		assertTrue(track.getDuration().equals(durationSumTrackPoint));
		assertTrue(track.getElevation()==elevationSumTrackPoint);
		System.out.println();
		
		track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Stoupa_full.gpx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		distanceSumTrackPoint = 0;
		durationSumTrackPoint = Duration.ofSeconds(0);
		elevationSumTrackPoint = 0;
		for(TrackPoint t : track.getTrackPoints()) {
			distanceSumTrackPoint+= t.getDistance();
			durationSumTrackPoint = durationSumTrackPoint.plus(t.getDuration());
			elevationSumTrackPoint += t.getElevation();
		}
		assertTrue(track.getDistance()==distanceSumTrackPoint);
		assertTrue(track.getDuration().equals(durationSumTrackPoint));
		assertTrue(track.getElevation()==elevationSumTrackPoint);
		System.out.println();
	}
		
	@Test
	void correctTCXTrackTest() throws URISyntaxException, XMLStreamException, IOException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("TCXTracks/River_regular.tcx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		assertEquals("River", track.getName());
		assertEquals(Duration.ofSeconds((long) 233.9200000+ (long) 100.6000000), track.getDuration());
		assertEquals(495.2299805+334.1000061, track.getDistance());
		assertEquals((int) ((95+122)/2), track.getAverageBPM());
		assertEquals(130, track.getMaximumBPM());
		assertEquals(2.2, track.getElevation());
		assertEquals(Duration.ofSeconds(401), track.getPace());
		assertEquals(((495.2299805d+334.1000061d)/333l)*3.6, track.getSpeed());
		assertEquals(LocalDate.of(2021, 06, 07), track.getDate());
		assertEquals(LocalTime.of(16, 27, 56), track.getStartTime());
		assertEquals("River_regular.tcx", track.getFileName());
		assertEquals("TCXTracks", track.getParentDirectory());
		assertTrue(track.getTrackPoints().size()==5);
	}
	
	@Test
	void twoNameTagTCXTrackTest() throws URISyntaxException, XMLStreamException, IOException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("TCXTracks/River_two_trackNames.tcx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		assertEquals("River_first_tag", track.getName());
		assertEquals(Duration.ofSeconds((long) 233.9200000+ (long) 100.6000000), track.getDuration());
		assertEquals(495.2299805+334.1000061, track.getDistance());
		assertEquals((int) ((95+122)/2), track.getAverageBPM());
		assertEquals(130, track.getMaximumBPM());
		assertEquals(2.2, track.getElevation());
		assertEquals(Duration.ofSeconds(401), track.getPace());
		assertEquals(((495.2299805d+334.1000061d)/333l)*3.6, track.getSpeed());
		assertEquals(LocalDate.of(2021, 06, 07), track.getDate());
		assertEquals(LocalTime.of(16, 27, 56), track.getStartTime());
		assertEquals("River_two_trackNames.tcx", track.getFileName());
		assertEquals("TCXTracks", track.getParentDirectory());
		assertTrue(track.getTrackPoints().size()==5);
	}
	
	@Test
	void noNameTagTCXTrackTest() throws URISyntaxException, XMLStreamException, IOException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("TCXTracks/River_no_trackName.tcx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		assertEquals(Duration.ofSeconds((long) 233.9200000+ (long) 100.6000000), track.getDuration());
		assertEquals(495.2299805+334.1000061, track.getDistance());
		assertEquals((int) ((95+122)/2), track.getAverageBPM());
		assertEquals(130, track.getMaximumBPM());
		assertEquals(2.2, track.getElevation());
		assertEquals(Duration.ofSeconds(401), track.getPace());
		assertEquals(((495.2299805d+334.1000061d)/333l)*3.6, track.getSpeed());
		assertEquals(LocalDate.of(2021, 06, 07), track.getDate());
		assertEquals(LocalTime.of(16, 27, 56), track.getStartTime());
		assertEquals("River_no_trackName.tcx", track.getFileName());
		assertEquals("TCXTracks", track.getParentDirectory());
		assertTrue(track.getTrackPoints().size()==5);
	}
	
	@Test
	void noBPMTagsTCXTrackTest() throws URISyntaxException, XMLStreamException, IOException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("TCXTracks/River_no_BPM.tcx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		assertEquals(Duration.ofSeconds((long) 233.9200000+ (long) 100.6000000), track.getDuration());
		assertEquals(495.2299805+334.1000061, track.getDistance());
		assertEquals(0, track.getAverageBPM());
		assertEquals(0, track.getMaximumBPM());
		assertEquals(2.2, track.getElevation());
		assertEquals(Duration.ofSeconds(401), track.getPace());
		assertEquals(((495.2299805d+334.1000061d)/333l)*3.6, track.getSpeed());
		assertEquals(LocalDate.of(2021, 06, 07), track.getDate());
		assertEquals(LocalTime.of(16, 27, 56), track.getStartTime());
		assertEquals("River_no_BPM.tcx", track.getFileName());
		assertEquals("TCXTracks", track.getParentDirectory());
		assertTrue(track.getTrackPoints().size()==5);
	}
		
	@Test
	void foundNoFileTest() {
		assertThrows(FileNotFoundException.class, () -> tParser.getTrack("UNAVAILABLE FILE TEST"));
	}
	
	@Test
	void getTrackPointsTest() throws XMLStreamException, URISyntaxException, IOException {
		assertEquals(2, tParser.getTrackPoints(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Koglerau_regular.gpx").toURI()).toFile().getAbsolutePath()).size());
	}
}
