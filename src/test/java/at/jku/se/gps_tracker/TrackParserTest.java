package at.jku.se.gps_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import at.jku.se.gps_tracker.data.TrackParser;
import at.jku.se.gps_tracker.model.Track;

public class TrackParserTest {

	TrackParser tParser = new TrackParser();
	
	@Test
	void correctGPXTrackTest() throws URISyntaxException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Koglerau_regular.gpx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		assertEquals("Koglerau", track.getName());
		assertEquals("GPXTracks", track.getParentDirectory());
	}
	
	@Test
	void noElevationGPXTrackTest() throws URISyntaxException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Koglerau_no_elevation.gpx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		assertEquals("Koglerau", track.getName());
		assertEquals("GPXTracks", track.getParentDirectory());
		assertEquals(0, track.getElevation());
	}
	
	@Test
	void twoNameTagGPXTrackTest() throws URISyntaxException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Koglerau_two_trackNames.gpx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		assertEquals("Koglerau_first_tag", track.getName());
		assertEquals("GPXTracks", track.getParentDirectory());
	}
	
	@Test
	void noNameTagGPXTrackTest() throws URISyntaxException {
		Track track = tParser.getTrack(Paths.get(getClass().getClassLoader().getResource("GPXTracks/Koglerau_no_trackName.gpx").toURI()).toFile().getAbsolutePath());
		assertTrue(track!=null);
		assertEquals("Koglerau_no_trackName.gpx", track.getName());
		assertEquals("GPXTracks", track.getParentDirectory());
	}
	
}
