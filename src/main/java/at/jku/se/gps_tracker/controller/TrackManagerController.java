package at.jku.se.gps_tracker.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

import at.jku.se.gps_tracker.model.DataModel;
import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.Visualization;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class TrackManagerController implements Initializable {
	
	private DataModel model;
	private ObservableList<Track> trackList;
	private ObservableList<String> categories;
	
	public TrackManagerController(DataModel model) {
		this.model=model;
	}
		
	private void chooseDirectory() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/fxml/StartView.fxml"));
			Stage popup = new Stage();
			StartViewController c = new StartViewController(model, popup);
			fxmlLoader.setController(c);
			Parent parent = (Parent) fxmlLoader.load();
			Scene popupScene = new Scene(parent);
			popup.setTitle("TrackStar - Choose Directory");
			popup.setAlwaysOnTop(true);
			popup.setScene(popupScene);
			popup.showAndWait();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
	
	@FXML
	private void setDirectory(ActionEvent event) {
		chooseDirectory();
	}
	
	@FXML
	private void updateModel(ActionEvent event) {
		System.out.println("test vor");
		model.updateModel();
		System.out.println("test nach");
	}
	
	@FXML
	private void closeApplication(ActionEvent event) {
		System.exit(1);
	}
	
	//TODO je nach Index entsprechend holen! (erster Eintrag ausgewählt --> hier erste (bzw 0 auswählen!)
	@FXML
	private void changeCategory(ActionEvent event) {
		model.setCurrentDirectoryFolder(0);
	}
	
	private void setUpLists() {
		trackList = FXCollections.observableArrayList(model.getTrackList());
		model.getTrackList().addListener((ListChangeListener<? super Track>) c -> {
	        while (c.next()) {
	            if (c.wasAdded()) {
	            	trackList.addAll(c.getFrom(), c.getAddedSubList());
	            } 
	            if (c.wasRemoved()) {
	            	trackList.removeAll(c.getRemoved());
	            }
	        }
	    });
		
		categories = FXCollections.observableArrayList(model.getDirectoryFolders());
		model.getDirectoryFolders().addListener((ListChangeListener<? super String>) c -> {
	        while (c.next()) {
	            if (c.wasAdded()) {
	            	categories.addAll(c.getFrom(), c.getAddedSubList());
	            } 
	            if (c.wasRemoved()) {
	            	categories.removeAll(c.getRemoved());
	            }
	        }
	        setUpMenuItems();
	    });
	}
	
	@FXML
	private MenuBar menubar;
	
	private void setUpMenuItems() {
		
	}
	
	@FXML
	private TableView mainTable;
	//Implementierungstipps: https://stackoverflow.com/a/45013059

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		chooseDirectory();
		setUpLists();
	}
	
	
	
}
