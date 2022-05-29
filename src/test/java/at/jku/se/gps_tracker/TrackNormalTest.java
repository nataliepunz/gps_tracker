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
/**
 * tests a valid Track object
 * @author Ozan
 *
 */
class TrackNormalTest {
	/**
	 * variable to hold the track object
	 * @author Ozan
	 */
	private Track track;
	
	/**
	 * performed before each test to set up track
	 * @author Ozan
	 */
	@BeforeEach
	void setUp() {
		track = new Track.TrackBuilder("testParentDirectory", "testFileName", "testName", LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.SECONDS))
				.distance(12345)
				.duration(Duration.ofSeconds(100))
				.averageBPM(100)
				.maximumBPM(600)
				.elevation(123)
				.trackPoints(Arrays.asList(new TrackPoint("1", 0, null, 0)))
				.build();
	}
	
	/**
	 * checks if constructor returns a not null value
	 * @author Ozan
	 */
	@Test
	void normalConstructorTest() {
		assertTrue(track!=null);
	}
	
	/**
	 * tests the getParentDirectory method
	 * @author Ozan
	 */
	@Test
	void getParentDirectoryTest() {
		assertEquals("testParentDirectory", track.getParentDirectory());
	}
	
	/**
	 * tests the getFileName method
	 * @author Ozan
	 */
	@Test
	void geFileNameTest() {
		assertEquals("testFileName", track.getFileName());
	}
	
	/**
	 * tests the getName method
	 * @author Ozan
	 */
	@Test
	void getNameTest() {
		assertEquals("testName", track.getName());
	}
	
	/**
	 * tests the getDate method
	 * @author Ozan
	 */
	@Test
	void getDateTest() {
		assertEquals(LocalDate.now(),track.getDate());
	}
	
	/**
	 * tests the getStartTime method
	 * @author Ozan
	 */
	@Test
	void getStartTimeTest() {
		assertEquals(LocalTime.now().truncatedTo(ChronoUnit.SECONDS),track.getStartTime());
	}
	
	/**
	 * tests the getDistance method
	 * @author Ozan
	 */
	@Test
	void getDistanceTest() {
		assertEquals(12345, track.getDistance());
	}
	
	/**
	 * tests the getDuration method
	 * @author Ozan
	 */
	@Test
	void getDurationTest() {
		assertEquals(Duration.ofSeconds(100), track.getDuration());
	}
	
	/**
	 * tests the getAverageBPM method
	 * @author Ozan
	 */
	@Test
	void getAverageBPMTest() {
		assertEquals(100, track.getAverageBPM());
	}
	
	/**
	 * tests the getMaximumBPM method
	 * @author Ozan
	 */
	@Test
	void getMaximumBPMTest() {
		assertEquals(600, track.getMaximumBPM());
	}
	
	/**
	 * tests the getElevation method
	 * @author Ozan
	 */
	@Test
	void getElevationTest() {
		assertEquals(123, track.getElevation());
	}
	
	/**
	 * tests the getPace method
	 * @author Ozan
	 */
	@Test
	void getPaceTest() {
		assertEquals(Duration.ofSeconds(8),track.getPace());
	}
	
	/**
	 * tests the getSpeed method
	 * @author Ozan
	 */
	@Test
	void getSpeedTest() {
		assertEquals((12345d/100l)*3.6,track.getSpeed());
	}
	
	/**
	 * tests the getTrackPoints method
	 * @author Ozan
	 */
	@Test
	void getTrackPointsTest() {
		assertFalse(track.getTrackPoints().isEmpty());
		assertTrue(track.getTrackPoints().get(0).getName().equals("1"));
	}
	
	/**
	 * tests the setTrackPoints method
	 * @author Ozan
	 */
	@Test
	void setTrackPointsTest() {
		track.setTrackPoints(Arrays.asList(new TrackPoint("2", 0, null, 0)));
		assertFalse(track.getTrackPoints().isEmpty());
		assertTrue(track.getTrackPoints().get(0).getName().equals("2"));
	}
	
	/**
	 * tests the getDistance method
	 * @author Ozan
	 */
	@Test
	void getDistancePropertyTest() {
		assertEquals(12345, track.getDistanceProperty().getValue());
	}
	
	/**
	 * tests the getDurationProperty method for TableView
	 * @author Ozan
	 */
	@Test
	void getDurationPropertyTest() {
		assertEquals("0:01:40", track.getDurationProperty().getValue());
	}
	
	/**
	 * tests the getPacenProperty method for TableView
	 * @author Ozan
	 */
	@Test
	void getPacePropertyTest() {
		assertEquals("0:00:08", track.getPaceProperty().getValue());
	}
	
	/**
	 * tests the getPropertyProperty method for TableView
	 * @author Ozan
	 */
	@Test
	void getSpeedPropertyTest() {
		assertEquals((double) Math.round(((12345d/100l)*3.6)*100)/100, track.getSpeedProperty().getValue());
	}
	
	/**
	 * tests the getElevationnProperty method for TableView
	 * @author Ozan
	 */
	@Test
	void getElevationPropertyTest() {
		assertEquals(123, track.getElevationProperty().getValue());
	}
}
