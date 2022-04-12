package at.jku.se.gps_tracker.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import at.jku.se.gps_tracker.model.DataModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TrackManagerController implements Initializable {
	
	private DataModel model;
	private Stage primaryStage;
	
	public TrackManagerController(DataModel model) {
		this.model=model;
	}
	
	public TrackManagerController(DataModel model, Stage primaryStage) {
		this(model);
		this.primaryStage=primaryStage;
	}
	
	private void chooseDirectory() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/fxml/StartView.fxml"));
			Stage popup = new Stage();
			StartViewController c = new StartViewController(model, popup, primaryStage);
			fxmlLoader.setController(c);
			Parent parent = (Parent) fxmlLoader.load();
			Scene popupScene = new Scene(parent);
			popup.setTitle("TrackStar - Choose Directory");
			popup.setAlwaysOnTop(true);
			popup.setScene(popupScene);
			popup.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
	
	@FXML
	private void setDirectory(ActionEvent event) {
		chooseDirectory();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		chooseDirectory();
	}
	
	
	
}
