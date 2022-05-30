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
 * tests for an empty Track object
 * @author Ozan
 *
 */
class TrackUnusualTest {
	/**
	 * variable to hold Track object
	 * @author Ozan
	 */
	private Track track;
	
	/**
	 * set up the empty Track object before each test
	 */
	@BeforeEach
	void setUp() {
		track = new Track();
	}
	
	/**
	 * tests the getParentDirectory method
	 * @author Ozan
	 */
	@Test
	void getParentDirectoryTest() {
		assertEquals(null, track.getParentDirectory());
	}
	
	/**
	 * tests the getFile method
	 * @author Ozan
	 */
	@Test
	void getFileNameTest() {
		assertEquals(null, track.getFileName());
	}
	
	/**
	 * tests the getName method
	 * @author Ozan
	 */
	@Test
	void getNameTest() {
		assertEquals(null, track.getName());
	}
	
	/**
	 * tests the getDate method
	 * @author Ozan
	 */
	@Test
	void getDateTest() {
		assertEquals(null,track.getDate());
	}
	
	/**
	 * tests the getStartTime method
	 * @author Ozan
	 */
	@Test
	void getStartTimeTest() {
		assertEquals(null,track.getStartTime());
	}
	
	/**
	 * tests the getDistance method
	 * @author Ozan
	 */
	@Test
	void getDistanceTest() {
		assertEquals(0, track.getDistance());
	}
	
	/**
	 * tests the getDuration method
	 * @author Ozan 
	 */
	@Test
	void getDurationTest() {
		assertEquals(Duration.ofSeconds(0), track.getDuration());
	}
	
	/**
	 * tests the getAverageBPM method
	 * @author Ozan
	 */
	@Test
	void getAverageBPMTest() {
		assertEquals(0, track.getAverageBPM());
	}
	
	/**
	 * tests the getMaximumBPM method
	 * @author Ozan
	 */
	@Test
	void getMaximumBPMTest() {
		assertEquals(0, track.getMaximumBPM());
	}
	
	/**
	 * tests the getElevation method
	 * @author Ozan
	 */
	@Test
	void getElevationTest() {
		assertEquals(0, track.getElevation());
	}
	
	/**
	 * tests the getPace method
	 * @author Ozan
	 */
	@Test
	void getPaceTest() {
		assertEquals(Duration.ofSeconds(0),track.getPace());
	}
	
	/**
	 * tests the getSpeed method
	 * @author Ozan
	 */
	@Test
	void getSpeedTest() {
		assertEquals(0,track.getSpeed());
	}
	
	/**
	 * tests the getTrackPoints method
	 * @author Ozan
	 */
	@Test
	void getTrackPointsTest() {
		assertEquals(null,track.getTrackPoints());
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
	 * tests the getDistanceProperty method for TableView
	 * @author Ozan
	 */
	@Test
	void getDistancePropertyTest() {
		assertEquals(0, track.getDistanceProperty().getValue());
	}
	
	/**
	 * tests the getDurationeProperty method for TableView
	 * @author Ozan
	 */
	@Test
	void getDurationPropertyTest() {
		assertEquals("0:00:00", track.getDurationProperty().getValue());
	}
	
	/**
	 * tests the getPaceProperty method for TableView
	 * @author Ozan
	 */
	@Test
	void getPacePropertyTest() {
		assertEquals("0:00:00", track.getPaceProperty().getValue());
	}
	
	/**
	 * tests the getSpeedProperty method for TableView
	 * @author Ozan
	 */
	@Test
	void getSpeedPropertyTest() {
		assertEquals(0, track.getSpeedProperty().getValue());
	}
	
	/**
	 * tests the getElevationProperty method for TableView
	 * @author Ozan
	 */
	@Test
	void getElevationPropertyTest() {
		assertEquals(0, track.getElevationProperty().getValue());
	}
	
	/**
	 * tests if duration is zero then speed and pace are also zero
	 * @author Ozan
	 */
	@Test
	void calculateDurationOnlyOneZero() {
		track = new Track.TrackBuilder("testParentDirectory", "testFileName", "testName", LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.SECONDS))
				.distance(12345)
				.duration(Duration.ofSeconds(0))
				.averageBPM(100)
				.maximumBPM(600)
				.elevation(123)
				.trackPoints(Arrays.asList(new TrackPoint("1", 0, null, 0)))
				.build();
		assertEquals(Duration.ofSeconds(0), track.getPace());
		assertEquals(0, track.getSpeed());
	}
	
	/**
	 * tests if distance is zero then speed and pace are also zero
	 * @author Ozan
	 */
	@Test
	void calculateDistanceOnlyOneZero() {
		track = new Track.TrackBuilder("testParentDirectory", "testFileName", "testName", LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.SECONDS))
				.distance(0)
				.duration(Duration.ofSeconds(1100))
				.averageBPM(100)
				.maximumBPM(600)
				.elevation(123)
				.trackPoints(Arrays.asList(new TrackPoint("1", 0, null, 0)))
				.build();
		assertEquals(Duration.ofSeconds(0), track.getPace());
		assertEquals(0, track.getSpeed());
	}
}
