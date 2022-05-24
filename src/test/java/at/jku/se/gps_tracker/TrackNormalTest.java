package at.jku.se.gps_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;

class TrackNormalTest {

	private Track track = new Track.TrackBuilder("testParentDirectory", "testFileName", "testName", LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.SECONDS))
									.distance(12345)
									.duration(Duration.ofSeconds(100))
									.averageBPM(100)
									.maximumBPM(600)
									.elevation(123)
									.trackPoints(Arrays.asList(new TrackPoint("1", 0, null, 0)))
									.build();
	
	@Test
	void normalConstructorTest() {
		assertTrue(track!=null);
	}
	
	@Test
	void getParentDirectoryTest() {
		assertEquals("testParentDirectory", track.getParentDirectory());
	}
	
	@Test
	void geFileNameTest() {
		assertEquals("testFileName", track.getFileName());
	}
	
	@Test
	void getNameTest() {
		assertEquals("testName", track.getName());
	}
	
	@Test
	void getDateTest() {
		assertEquals(LocalDate.now(),track.getDate());
	}
	
	@Test
	void getStartTimeTest() {
		assertEquals(LocalTime.now().truncatedTo(ChronoUnit.SECONDS),track.getStartTime());
	}
	
	@Test
	void getDistanceTest() {
		assertEquals(12345, track.getDistance());
	}
	
	@Test
	void getDurationTest() {
		assertEquals(Duration.ofSeconds(100), track.getDuration());
	}
	
	@Test
	void getAverageBPMTest() {
		assertEquals(100, track.getAverageBPM());
	}
	
	@Test
	void getMaximumBPMTest() {
		assertEquals(600, track.getMaximumBPM());
	}
	
	@Test
	void getElevationTest() {
		assertEquals(123, track.getElevation());
	}
	
	@Test
	void getPaceTest() {
		assertEquals(Duration.ofSeconds(8),track.getPace());
	}
	
	@Test
	void getSpeedTest() {
		assertEquals((12345d/100l)*3.6,track.getSpeed());
	}
	
	@Test
	void getTrackPointsTest() {
		assertFalse(track.getTrackPoints().isEmpty());
		assertTrue(track.getTrackPoints().get(0).getName().equals("1"));
	}
	
	@Test
	void setTrackPointsTest() {
		track.setTrackPoints(Arrays.asList(new TrackPoint("2", 0, null, 0)));
		assertFalse(track.getTrackPoints().isEmpty());
		assertTrue(track.getTrackPoints().get(0).getName().equals("2"));
	}
	
	@Test
	void getDistancePropertyTest() {
		assertEquals(12345, track.getDistanceProperty().getValue());
	}
	
	@Test
	void getDurationPropertyTest() {
		assertEquals("0:01:40", track.getDurationProperty().getValue());
	}
	
	@Test
	void getPacePropertyTest() {
		assertEquals("0:00:08", track.getPaceProperty().getValue());
	}
	
	@Test
	void getSpeedPropertyTest() {
		assertEquals((double) Math.round(((12345d/100l)*3.6)*100)/100, track.getSpeedProperty().getValue());
	}
	
	@Test
	void getElevationPropertyTest() {
		assertEquals(123, track.getElevationProperty().getValue());
	}
}
