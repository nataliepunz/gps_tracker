package at.jku.se.gps_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import at.jku.se.gps_tracker.model.DataModel;

class DataModelNormalTest {

	private DataModel model;
	private String directoryPath;
	private String tracksFileLocation;
	
	@BeforeEach
	void setUp() throws URISyntaxException {
		model = new DataModel();
		model.setDirectory(Paths.get(getClass().getClassLoader().getResource("DataModelandTracksDB/help_for_testing.txt").toURI()).toFile().getParentFile().getAbsolutePath());
		model.updateModel();
		directoryPath = Paths.get(getClass().getClassLoader().getResource("DataModelandTracksDB/help_for_testing.txt").toURI()).toFile().getParentFile().getAbsolutePath();
		tracksFileLocation = FilenameUtils.concat(directoryPath, DataModel.DATABASE_NAME);
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
		model.setDirectory(directoryPath);
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
		model.adjustDirectoryFolder("FilterNotallowed");
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
	
	@Test
	void adjustDirectoryFolderAndFilterTest() {
		model.adjustDirectoryFolder("Filter2");
		model.updateModel();
		List<String> trackNames = model.getTrackList().stream().map(track -> track.getName()).collect(Collectors.toList());
		assertTrue(trackNames.contains("Bad Zell"));
		assertTrue(trackNames.contains("Barcelona"));
		assertTrue(trackNames.contains("Kreta"));	
	}
	
	@Test
	void deletionOfTrackTest() {
		assertEquals(3, model.getTrackList().size());
		String folderFilterOne = FilenameUtils.concat(directoryPath, "Filter1");
		String folderUnderFilterOne = FilenameUtils.concat(folderFilterOne, "move_to_simulate_deletion");
		File trackLaPaloma = new File(FilenameUtils.concat(folderFilterOne, "halbmarathon.tcx"));
		trackLaPaloma.renameTo(new File(FilenameUtils.concat(folderUnderFilterOne, "halbmarathon.tcx")));
		trackLaPaloma.delete();
		model.updateModel();
		assertEquals(2, model.getTrackList().size());
		File trackLaPalomaCopyBack = new File(FilenameUtils.concat(folderUnderFilterOne, "halbmarathon.tcx"));
		trackLaPalomaCopyBack.renameTo(new File(FilenameUtils.concat(folderFilterOne, "halbmarathon.tcx")));
		trackLaPalomaCopyBack.delete();
	}
	
	@Test
	void deleteFilterFolderDuringRunTimeTest() throws IOException {
		model.adjustDirectoryFolder("FilterFolderDeleteSimulation");
		model.updateModel();
		String toBeDeletedFolder = FilenameUtils.concat(directoryPath, "FilterFolderDeleteSimulation");
		String destinationFolderForSimulation = FilenameUtils.concat(directoryPath, "z_Delete_Simulator_For_FilterFolderDelete");
		FileUtils.moveDirectoryToDirectory(new File(toBeDeletedFolder), new File(destinationFolderForSimulation), false);
		model.updateModel();
		assertEquals("Filter1",model.getDirectoryFolder());
		FileUtils.moveDirectoryToDirectory(new File(FilenameUtils.concat(destinationFolderForSimulation, "FilterFolderDeleteSimulation")), new File(directoryPath), false);
	}
	
}
