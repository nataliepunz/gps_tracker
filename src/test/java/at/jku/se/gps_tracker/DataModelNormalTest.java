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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import at.jku.se.gps_tracker.data.SQLOperationException;
import at.jku.se.gps_tracker.data.SQLRollbackException;
import at.jku.se.gps_tracker.model.DataModel;
/**
 * tests a valid DataModel object
 * @author Ozan
 *
 */
class DataModelNormalTest {
	/**
	 * variable to hold a DataModel object
	 * @author Ozan
	 */
	
	private DataModel model;
	/**
	 * varible to hold the directory for the IO Access tests
	 * @author Ozan
	 */
	
	private String directoryPath;
	
	/**
	 * variable to hold the location of the choosen directory
	 * @author Ozan
	 */
	private String tracksFileLocation;
	
	/**
	 * set up the test enviroment before each test
	 * @throws URISyntaxException
	 * @throws XMLStreamException
	 * @throws SQLRollbackException 
	 * @throws SQLOperationException 
	 * @throws IOException 
	 * @throws NullPointerException 
	 */
	@BeforeEach
	void setUp() throws URISyntaxException, XMLStreamException, SQLOperationException, SQLRollbackException, NullPointerException, IOException {
		model = new DataModel();
		model.setDirectory(Paths.get(getClass().getClassLoader().getResource("DataModelandTracksDB/help_for_testing.txt").toURI()).toFile().getParentFile().getAbsolutePath());
		model.setDirectoryFolders();
		model.changeModel();
    	model.updateTrackListFromDB();
    	syncTracks();
    	directoryPath = Paths.get(getClass().getClassLoader().getResource("DataModelandTracksDB/help_for_testing.txt").toURI()).toFile().getParentFile().getAbsolutePath();
		tracksFileLocation = FilenameUtils.concat(directoryPath, DataModel.DATABASE_NAME);
	}
	
	/**
	 * teardown of the test set up
	 * close the connection and delete the tracks file
	 * @author Ozan
	 * @throws SQLOperationException 
	 */
	@AfterEach
	void teardown() throws SQLOperationException {
		model.closeConnection();
		File track = new File(tracksFileLocation);
		track.delete();
	}
	
	/**
	 * check if the right directory gets returned
	 * @author Ozan
	 */
	@Test
	void getDirectoryTest() {
		assertEquals("Filter1", model.getDirectoryFolder());
	}
	
	/**
	 * set the a new directory and check if the directory folder is set correctly
	 * @author Ozan
	 * @throws SQLRollbackException 
	 * @throws SQLOperationException 
	 */
	@Test
	void setDirectoryTest() throws SQLOperationException, SQLRollbackException {
		model.setDirectory(FilenameUtils.concat(directoryPath, "z_Alternative_for_set_directory"));
		model.setDirectoryFolders();
		model.changeModel();
    	model.updateTrackListFromDB();
		assertEquals("A_Filter1", model.getDirectoryFolder());
	}
	
	/**
	 * set a new directory folder and check if valid
	 * @author Ozan
	 */
	@Test
	void setAndGetDirectoryFolderTest() {
		model.setDirectoryFolder("Filter2");
		assertEquals("Filter2", model.getDirectoryFolder());
		model.setDirectoryFolder("Filter3");
		assertEquals("Filter3", model.getDirectoryFolder());
		model.setDirectoryFolder("Filter1");
		assertEquals("Filter1", model.getDirectoryFolder());
		model.setDirectoryFolder("FilterNotallowed");
		assertEquals("Filter1", model.getDirectoryFolder());
	}
	
	/**
	 * 
	 * check if the number of files are read correctly. 3 is the desired result
	 * @author Ozan
	 */
	@Test
	void getTrackListTest() throws URISyntaxException, FileNotFoundException, XMLStreamException {
		assertEquals(3, model.getTrackList().size());
	}
	
	@Test
	void getTrackListAllTracksTest() throws URISyntaxException, XMLStreamException, NullPointerException, SQLOperationException, SQLRollbackException, IOException {
		model.setDirectoryFolder(DataModel.ALL_TRACK_KEYWORD);
		syncTracks();
		assertEquals(9, model.getTrackList().size());
	}
	
	@Test
	void getTrackPointsTest() throws XMLStreamException, IOException {
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
	void setDirectoryFolderAndFilterTest() throws XMLStreamException, NullPointerException, SQLOperationException, SQLRollbackException, IOException {
		model.setDirectoryFolder("Filter2");
		syncTracks();
		List<String> trackNames = model.getTrackList().stream().map(track -> track.getName()).collect(Collectors.toList());
		assertTrue(trackNames.contains("Bad Zell"));
		assertTrue(trackNames.contains("Barcelona"));
		assertTrue(trackNames.contains("Kreta"));	
	}
	
	@Test
	void deletionOfTrackTest() throws XMLStreamException, NullPointerException, SQLOperationException, SQLRollbackException, IOException {
		assertEquals(3, model.getTrackList().size());
		String folderFilterOne = FilenameUtils.concat(directoryPath, "Filter1");
		String folderUnderFilterOne = FilenameUtils.concat(folderFilterOne, "move_to_simulate_deletion");
		File trackLaPaloma = new File(FilenameUtils.concat(folderFilterOne, "halbmarathon.tcx"));
		trackLaPaloma.renameTo(new File(FilenameUtils.concat(folderUnderFilterOne, "halbmarathon.tcx")));
		trackLaPaloma.delete();
		syncTracks();
		assertEquals(2, model.getTrackList().size());
		File trackLaPalomaCopyBack = new File(FilenameUtils.concat(folderUnderFilterOne, "halbmarathon.tcx"));
		trackLaPalomaCopyBack.renameTo(new File(FilenameUtils.concat(folderFilterOne, "halbmarathon.tcx")));
		trackLaPalomaCopyBack.delete();
	}
	
	@Test
	void deleteFilterFolderDuringRunTimeTest() throws IOException, XMLStreamException, SQLOperationException, SQLRollbackException {
		model.setDirectoryFolder("FilterFolderDeleteSimulation");
		model.changeModel();
    	model.updateTrackListFromDB();
		syncTracks();
		String toBeDeletedFolder = FilenameUtils.concat(directoryPath, "FilterFolderDeleteSimulation");
		String destinationFolderForSimulation = FilenameUtils.concat(directoryPath, "z_Delete_Simulator_For_FilterFolderDelete");
		FileUtils.moveDirectoryToDirectory(new File(toBeDeletedFolder), new File(destinationFolderForSimulation), false);
		syncTracks();
		assertEquals("Filter1",model.getDirectoryFolder());
		FileUtils.moveDirectoryToDirectory(new File(FilenameUtils.concat(destinationFolderForSimulation, "FilterFolderDeleteSimulation")), new File(directoryPath), false);
	}
	
	private void syncTracks() throws XMLStreamException, NullPointerException, SQLOperationException, SQLRollbackException, IOException {
		if(DataModel.ALL_TRACK_KEYWORD.equals(model.getDirectoryFolder())) {
			for(String folder : model.getDirectoryFolders()) {
				if(model.checkFolderExists(folder)) {
					syncTracks(folder);
				}
			}
		} else {
			syncTracks(model.getDirectoryFolder());
		}		
	}
	
	private void syncTracks(String directoryFolder) throws XMLStreamException, NullPointerException, SQLOperationException, SQLRollbackException, IOException {
		for(String s : model.getDifferenceDriveAndDB(true, directoryFolder)) {
				model.addTrack(s);
		}
		for(String s : model.getDifferenceDriveAndDB(false, directoryFolder)) {
			model.removeTrack(s, directoryFolder);
		}
	}
	
}
