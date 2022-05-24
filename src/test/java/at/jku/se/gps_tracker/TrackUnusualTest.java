package at.jku.se.gps_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;

class TrackUnusualTest {

	private Track track;
	
	@BeforeEach
	void setUp() {
		track = new Track();
	}
	
	@Test
	void getParentDirectoryTest() {
		assertEquals(null, track.getParentDirectory());
	}
	
	@Test
	void getFileNameTest() {
		assertEquals(null, track.getFileName());
	}
	
	@Test
	void getNameTest() {
		assertEquals(null, track.getName());
	}
	
	@Test
	void getDateTest() {
		assertEquals(null,track.getDate());
	}
	
	@Test
	void getStartTimeTest() {
		assertEquals(null,track.getStartTime());
	}
	
	@Test
	void getDistanceTest() {
		assertEquals(0, track.getDistance());
	}
	
	@Test
	void getDurationTest() {
		assertEquals(Duration.ofSeconds(0), track.getDuration());
	}
	
	@Test
	void getAverageBPMTest() {
		assertEquals(0, track.getAverageBPM());
	}
	
	@Test
	void getMaximumBPMTest() {
		assertEquals(0, track.getMaximumBPM());
	}
	
	@Test
	void getElevationTest() {
		assertEquals(0, track.getElevation());
	}
	
	@Test
	void getPaceTest() {
		assertEquals(Duration.ofSeconds(0),track.getPace());
	}
	
	@Test
	void getSpeedTest() {
		assertEquals(0,track.getSpeed());
	}
	
	@Test
	void getTrackPointsTest() {
		assertEquals(null,track.getTrackPoints());
	}
	
	@Test
	void setTrackPointsTest() {
		track.setTrackPoints(Arrays.asList(new TrackPoint("2", 0, null, 0)));
		assertFalse(track.getTrackPoints().isEmpty());
		assertTrue(track.getTrackPoints().get(0).getName().equals("2"));
	}
	
	@Test
	void getDistancePropertyTest() {
		assertEquals(0, track.getDistanceProperty().getValue());
	}
	
	@Test
	void getDurationPropertyTest() {
		assertEquals("0:00:00", track.getDurationProperty().getValue());
	}
	
	@Test
	void getPacePropertyTest() {
		assertEquals("0:00:00", track.getPaceProperty().getValue());
	}
	
	@Test
	void getSpeedPropertyTest() {
		assertEquals(0, track.getSpeedProperty().getValue());
	}
	
	@Test
	void getElevationPropertyTest() {
		assertEquals(0, track.getElevationProperty().getValue());
	}
	
	@Test
	void calculatePaceOnlyOneZero() {
		track = new Track.TrackBuilder("testParentDirectory", "testFileName", "testName", LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.SECONDS))
				.distance(12345)
				.duration(Duration.ofSeconds(0))
				.averageBPM(100)
				.maximumBPM(600)
				.elevation(123)
				.trackPoints(Arrays.asList(new TrackPoint("1", 0, null, 0)))
				.build();
		assertEquals(Duration.ofSeconds(0), track.getPace());
	}
	
	@Test
	void calculateSpeedOnlyOneZero() {
		track = new Track.TrackBuilder("testParentDirectory", "testFileName", "testName", LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.SECONDS))
				.distance(12345)
				.duration(Duration.ofSeconds(0))
				.averageBPM(100)
				.maximumBPM(600)
				.elevation(123)
				.trackPoints(Arrays.asList(new TrackPoint("1", 0, null, 0)))
				.build();
		assertEquals(0, track.getSpeed());
	}
	
	@Test
	void formatNegativeDuration() {
		track = new Track.TrackBuilder("testParentDirectory", "testFileName", "testName", LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.SECONDS))
				.distance(12345)
				.duration(Duration.ofSeconds(-10))
				.averageBPM(100)
				.maximumBPM(600)
				.elevation(123)
				.trackPoints(Arrays.asList(new TrackPoint("1", 0, null, 0)))
				.build();
		assertEquals("-0:00:10", track.getDurationProperty().getValue());
	}
}
