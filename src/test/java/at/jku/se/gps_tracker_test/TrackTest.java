package at.jku.se.gps_tracker_test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import at.jku.se.gps_tracker.model.Track;

class TrackTest {

	private Track track = new Track.TrackBuilder("testParentDirectory", "testFileName", "testName", LocalDate.now(), LocalTime.now()).build();
	
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
		assertEquals(LocalTime.now(),track.getStartTime());
	}
	
}
