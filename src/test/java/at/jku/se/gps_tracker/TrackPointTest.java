package at.jku.se.gps_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import at.jku.se.gps_tracker.model.TrackPoint;

public class TrackPointTest {
	
	private TrackPoint trackPoint;
	
	@BeforeEach
	void setUp() {
		trackPoint = new TrackPoint("1", 123, Duration.ofSeconds(100), 100, 150, 50);
	}
	
	@Test
	void constructorTest() {
		assertTrue(trackPoint!=null);
	}
	
	@Test
	void getNameTest() {
		assertEquals("1", trackPoint.getName());
	}
	
	@Test
	void getDistanceTest() {
		assertEquals(123, trackPoint.getDistance());
	}
	
	@Test
	void getDurationTest() {
		assertEquals(Duration.ofSeconds(100), trackPoint.getDuration());
	}
	
	@Test
	void getAverageBPMTest() {
		assertEquals(100, trackPoint.getAverageBPM());
	}
	
	@Test
	void getMaximumBPMTest() {
		assertEquals(150, trackPoint.getMaximumBPM());
	}
	
	@Test
	void getElevationTest() {
		assertEquals(50, trackPoint.getElevation());
	}
	
	@Test
	void getPaceTest() {
		assertEquals(Duration.ofSeconds(813), trackPoint.getPace());
	}
	
	@Test
	void getSpeedTest() {
		assertEquals((123d/100l)*3.6, trackPoint.getSpeed());
	}
	
	@Test
	void getDistancePropertyTest() {
		assertEquals(123, trackPoint.getDistanceProperty().getValue());
	}
	
	@Test
	void getDurationPropertyTest() {
		assertEquals("0:01:40", trackPoint.getDurationProperty().getValue());
	}
	
	@Test
	void getPacePropertyTest() {
		assertEquals("0:13:33", trackPoint.getPaceProperty().getValue());
	}
	
	@Test
	void getSpeedPropertyTest() {
		assertEquals((double) Math.round(((123d/100l)*3.6)*100)/100, trackPoint.getSpeedProperty().getValue());
	}
	
	@Test
	void getElevationPropertyTest() {
		assertEquals(50, trackPoint.getElevationProperty().getValue());
	}
	
}
