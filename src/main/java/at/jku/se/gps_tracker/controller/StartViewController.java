package at.jku.se.gps_tracker.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import at.jku.se.gps_tracker.model.DataModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class StartViewController {
	@FXML
	private Button buttonChoseDirectory;
	
	private DataModel model;
	private Stage stage;
	private Stage primaryStage;
	
	public StartViewController (DataModel model, Stage stage, Stage primaryStage) {
		this.model = model;
		this.stage=stage;
		this.primaryStage = primaryStage;
	}
	
	@FXML
	private void chooseDirectory(ActionEvent event) {
		stage.close();
		DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Desktop"));
        Stage window = new Stage();
        window.setMaxHeight(200);
        window.setMaxWidth(200);
        window.setAlwaysOnTop(true);
        File selectedDirectory = directoryChooser.showDialog(window);
        this.model.setCurrrentDirectory(selectedDirectory.getAbsolutePath());
	}
}
