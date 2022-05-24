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
import java.util.List;

import at.jku.se.gps_tracker.model.DataModel;

class DataModelUnusualTest {

	private DataModel model;
	
	@BeforeEach
	void setUp() throws URISyntaxException {
		model = new DataModel();
	}
		
	@Test
	void getDirectoryTest() throws URISyntaxException {
		assertEquals(null, model.getDirectoryFolder());
	}
	
	@Test
	void setDirectoryTest() throws URISyntaxException {
		model.setDirectory(null);
		assertEquals(null, model.getDirectoryFolder());
	}
	
	@Test
	void setAndGetDirectoryTest() throws URISyntaxException {
		model.adjustDirectoryFolder("Filter2");
		assertEquals(null, model.getDirectoryFolder());
	}
	
	@Test
	void getTrackListTest() throws URISyntaxException {
		model.updateModel();
		assertEquals(0, model.getTrackList().size());
	}
	
	@Test
	void getTrackListAllTracksTest() throws URISyntaxException {
		model.adjustDirectoryFolder(DataModel.ALL_TRACK_KEYWORD);
		model.updateModel();
		assertEquals(0, model.getTrackList().size());
	}
	
	@Test
	void getTrackPointsTest() {
		assertNotNull(model.getTrackPoints(null));
	}
	
	@Test
	void getDirectoryFoldersTest() {
		List<String> directories = model.getDirectoryFolders();
		assertFalse(directories.contains("Filter1"));
		assertFalse(directories.contains("Filter2"));
		assertFalse(directories.contains("Filter3"));
		assertFalse(directories.contains("Filter4"));
		assertEquals(0,directories.size());
	}
	
	
	
}
