package at.jku.se.gps_tracker.controller;

import java.io.File;
import at.jku.se.gps_tracker.model.DataModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
/**
 * controller for choosing the directory 
 * @author Ozan
 *
 */
public class StartViewController {
	
	@FXML
	private Button buttonChoseDirectory;
	
	/**
	 * the associated datamodel
	 * @author Ozan
	 */
	private DataModel model;
	
	/**
	 * the stage that this controller was called from
	 * @author Ozan
	 */
	private Stage stage;
	
	/**
	 * constructor to set datamodel and parent stage
	 * @param model associated datamodel
	 * @param stage parent stage
	 */
	public StartViewController (DataModel model, Stage stage) {
		this.model = model;
		this.stage=stage;
	}
	
	/**
	 * opens the directory chooser window and sets the non-null value into the datamodel
	 * @author Ozan
	 * @param event JAVA-FX NECESSITY
	 */
	@FXML
	private void chooseDirectory(ActionEvent event) {
		stage.close();
		DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Desktop"));
        Stage window = new Stage();
        window.setMaxHeight(200);
        window.setMaxWidth(200);
        window.setAlwaysOnTop(true);
        window.setOnCloseRequest(c -> Platform.exit());
        File selectedDirectory = directoryChooser.showDialog(window);
        if(selectedDirectory!=null) {
        	model.setDirectory(selectedDirectory.getAbsolutePath());
        	model.setDirectoryFolders();
        	model.changeModel();
        	model.updateTrackListFromDB();
        }
	}
}
