package at.jku.se.gps_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import at.jku.se.gps_tracker.model.TrackPoint;
/**
 * test for valid TrackPoint object
 * @author Ozan
 *
 */
public class TrackPointTest {
	/**
	 * variable to hold a trackPoint
	 * @author Ozan
	 */
	private TrackPoint trackPoint;
	
	/**
	 * setUp of a new TrackPoint object
	 * @author Ozan
	 */
	@BeforeEach
	void setUp() {
		trackPoint = new TrackPoint("1", 123, Duration.ofSeconds(100), 100, 150, 50);
	}
	
	/**
	 * check if the constructor returns a not null value
	 * @author Ozan
	 */
	@Test
	void constructorTest() {
		assertTrue(trackPoint!=null);
	}
	
	/**
	 * tests the getName method
	 * @author Ozan
	 */
	@Test
	void getNameTest() {
		assertEquals("1", trackPoint.getName());
	}
	
	/**
	 * tests the getDistance method
	 * @author Ozan
	 */
	@Test
	void getDistanceTest() {
		assertEquals(123, trackPoint.getDistance());
	}
	
	/**
	 * tests the getDuration method
	 * @author Ozan
	 */
	@Test
	void getDurationTest() {
		assertEquals(Duration.ofSeconds(100), trackPoint.getDuration());
	}
	
	/**
	 * tests the getAverageBPM method
	 * @author Ozan
	 */
	@Test
	void getAverageBPMTest() {
		assertEquals(100, trackPoint.getAverageBPM());
	}
	
	/**
	 * tests the getMaximumBPM method
	 * @author Ozan
	 */
	@Test
	void getMaximumBPMTest() {
		assertEquals(150, trackPoint.getMaximumBPM());
	}
	
	/**
	 * tests the getElevation method
	 * @author Ozan
	 */
	@Test
	void getElevationTest() {
		assertEquals(50, trackPoint.getElevation());
	}
	
	/**
	 * tests the getPace method
	 * @author Ozan
	 */
	@Test
	void getPaceTest() {
		assertEquals(Duration.ofSeconds(813), trackPoint.getPace());
	}
	
	/**
	 * tests the getSpeed method
	 * @author Ozan
	 */
	@Test
	void getSpeedTest() {
		assertEquals((123d/100l)*3.6, trackPoint.getSpeed());
	}
	
	/**
	 * tests the getDistanceProperty method for TableViews
	 * @author Ozan
	 */
	@Test
	void getDistancePropertyTest() {
		assertEquals(123, trackPoint.getDistanceProperty().getValue());
	}
	
	/**
	 * tests the getDurationProperty method for TableViews
	 * @author Ozan
	 */
	@Test
	void getDurationPropertyTest() {
		assertEquals("0:01:40", trackPoint.getDurationProperty().getValue());
	}
	
	/**
	 * tests the getPaceProperty method for TableViews
	 * @author Ozan
	 */
	@Test
	void getPacePropertyTest() {
		assertEquals("0:13:33", trackPoint.getPaceProperty().getValue());
	}
	
	/**
	 * tests the getSpeedProperty method for TableViews
	 * @author Ozan
	 */
	@Test
	void getSpeedPropertyTest() {
		assertEquals((double) Math.round(((123d/100l)*3.6)*100)/100, trackPoint.getSpeedProperty().getValue());
	}
	
	/**
	 * tests the getElevationProperty method for TableViews
	 * @author Ozan
	 */
	@Test
	void getElevationPropertyTest() {
		assertEquals(50, trackPoint.getElevationProperty().getValue());
	}
	
}
