package at.jku.se.gps_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import at.jku.se.gps_tracker.model.DataModel;

class DataModelNormalTest {

	private DataModel model;
	private String tracksFileLocation;
	
	@BeforeEach
	void setUp() throws URISyntaxException {
		model = new DataModel();
		model.setDirectory(Paths.get(getClass().getClassLoader().getResource("DataModelandTracksDB/help_for_testing.txt").toURI()).toFile().getParentFile().getAbsolutePath());
		model.updateModel();
		tracksFileLocation = Paths.get(getClass().getClassLoader().getResource("DataModelandTracksDB/help_for_testing.txt").toURI()).toFile().getParentFile().getAbsolutePath()+"\\"+DataModel.DATABASE_NAME;
	}
	
	@AfterEach
	void teardown() {
		model.closeConnection();
		File track = new File(tracksFileLocation);
		track.delete();
	}
	
	@Test
	void getDirectoryTest() throws URISyntaxException {
		assertEquals("Filter1", model.getDirectoryFolder());
	}
	
	@Test
	void setDirectoryTest() throws URISyntaxException {
		model.setDirectory(null);
		assertEquals("Filter1", model.getDirectoryFolder());
	}
	
	@Test
	void setAndGetDirectoryTest() throws URISyntaxException {
		model.adjustDirectoryFolder("Filter2");
		assertEquals("Filter2", model.getDirectoryFolder());
		model.adjustDirectoryFolder("Filter3");
		assertEquals("Filter3", model.getDirectoryFolder());
		model.adjustDirectoryFolder("Filter1");
		assertEquals("Filter1", model.getDirectoryFolder());
		model.adjustDirectoryFolder("Filter4");
		assertEquals("Filter1", model.getDirectoryFolder());
	}
	
	@Test
	void getTrackListTest() throws URISyntaxException {
		model.updateModel();
		assertEquals(3, model.getTrackList().size());
	}
	
	@Test
	void getTrackListAllTracksTest() throws URISyntaxException {
		model.adjustDirectoryFolder(DataModel.ALL_TRACK_KEYWORD);
		model.updateModel();
		assertEquals(9, model.getTrackList().size());
	}
	
	@Test
	void getTrackPointsTest() {
		assertNotNull(model.getTrackPoints(model.getTrackList().get(0)));
	}
	
	@Test
	void getDirectoryFoldersTest() {
		List<String> directories = model.getDirectoryFolders();
		assertTrue(directories.contains("Filter1"));
		assertTrue(directories.contains("Filter2"));
		assertTrue(directories.contains("Filter3"));
		assertFalse(directories.contains("Filter4"));
	}
	
}
