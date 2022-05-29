package at.jku.se.gps_tracker.controller;

import at.jku.se.gps_tracker.model.AbstractTrack;
import at.jku.se.gps_tracker.model.DataModel;
import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FilenameUtils;
/**
 * main controller for handling user input and visualizing information
 * @author Ozan, Nuray, Natalie
 *
 */
public class TrackManagerController implements Initializable, ErrorPopUpController {
	//TODO : Optische Korrekturen
	private DataModel model;
	private ObservableList<Track> trackList;
	//private ObservableList<GroupTracks> groupedTracks; die Implementierung der GroupedTracks erfolgt über eine eigene Liste. die trackList bleibt im Hintergrund so wie sie ist.
	private ObservableList<String> categories;
	final private ToggleGroup tgMenuTrack;

	@FXML
	private ToggleGroup tgGraph;
	
	@FXML
	private MenuBar menubar;

	public TrackManagerController(DataModel model) {
		this.model=model;
		tgMenuTrack = new ToggleGroup();
	}
	
	/**
	 * executed at start of the application
	 * sets the necessary lists up by loading them in from the datamodel and adding a listener to keep them in sync throughout the runtime
	 * @author Ozan 
	 */
	private void setUpLists() {
		setTrackList();
		setCategories();
		setUpTrackMenuItems();
	}
	
	/**
	 * sets the tracklist up based on read tracks
	 * @author Ozan
	 */
	private void setTrackList() {
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
	}
	
	/**
	 * sets the categories based on the directory folders inside the choosen folder
	 * also calls the setUpMenuItems method to re-setUp the RadioMenuItems for the filter choices after changes in the directory
	 * @author Ozan
	 * 
	 */
	private void setCategories() {
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
			setUpTrackMenuItems();
		});
	}
	
	/**
	 * adds a listener to the toggelgroup to get the choosen filter option and calls changeCategory to set the new filter category
	 * -- the exception handling is done because of nuray non-excpetion handling in her method
	 * @author Ozan
	 */
	private void setUpMenuTrack() {
		tgMenuTrack.selectedToggleProperty().addListener(listener -> {
			if (tgMenuTrack.getSelectedToggle() != null) {
		        RadioMenuItem selectedItem = (RadioMenuItem) tgMenuTrack.getSelectedToggle();
				try {
					changeCategory(selectedItem.getText());
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * set up the Menu File in the application 
	 * @author Ozan
	 */
	
	/**
	 * calls chooseDirectory to set the new directory in the datamodel
	 * then calls the method syncTracks to read any new tracks in or delete none exisiting tracks
	 * -- sideTable method call by nuray
	 * @author Ozan
	 * @param event JAVA-FX necessity
	 */
	@FXML
	private void setDirectory(ActionEvent event) {
		chooseDirectory();
		syncTracks();
		sideTable.getItems().clear(); //setzt sidetable zurück da sie sonst die letzte instanz anzeigt
	}
	
	/**
	 * calls the setDirectoryFolders method in the datamodel to update the subfolders
	 * calls changeModel in datamodel to read in the tracks for the new directory folder 
	 * @author Ozan
	 * @param event JAVA-FX necessity
	 */
	@FXML
	private void updateDirectoryFolders(ActionEvent event) {
		model.setDirectoryFolders();
		model.changeModel();
	}
	
	/**
	 * synchronises the current folder and the database entries
	 * @author Ozan
	 * @param event JAVA-FX necessity
	 */
	@FXML
	private void updateModel(ActionEvent event) {
		syncTracks();
	}

	/**
	 * closes the connection to the database and closes the application
	 * @author Ozan
	 * @param event JAVA-FX necessity
	 */
	@FXML
	private void closeApplication(ActionEvent event) {
		model.closeConnection();
		Platform.exit();
	}
	
	/**
	 * synchronises the current folder and the database entries
	 * either by calling every subfolder if all tracks are reqeuested or only the selected subfolder
	 * @author Ozan
	 */
	private void syncTracks() {
		if(DataModel.ALL_TRACK_KEYWORD.equals(model.getDirectoryFolder())) {
			for(String folder : model.getDirectoryFolders()) {
				syncTracks(folder);
			}
		} else {
			syncTracks(model.getDirectoryFolder());
		}		
	}
	
	/**
	 * implements the syncTracks method
	 * reads the difference between folder - database (to add) or database - folder (to delete)
	 * calls the add or remove method in datamodel accordingly
	 * @author Ozan
	 * @param directoryFolder
	 */
	private void syncTracks(String directoryFolder) {
		List<String> toAddTracks;
		List<String> toDeleteTracks;
		try {
			toAddTracks = model.getDifferenceDriveAndDB(true, directoryFolder);
			toDeleteTracks = model.getDifferenceDriveAndDB(false, directoryFolder);
		} catch (FileNotFoundException e1) {
			showErrorPopUpNoWait("ERROR! NOT POSSIBLE TO ACCESS DIRECTORY FOLDER "+directoryFolder+". REMEMBER TO UPDATE AFTER EVERY FOLDER STRUCTURE CHANGE!");
				if(!DataModel.ALL_TRACK_KEYWORD.equals(model.getDirectoryFolder())){
					model.setDirectoryFolders();
					model.changeModel();
					model.updateTrackListFromDB();
					syncTracks();
			}
			return;
		} catch (NullPointerException e2) {
			showErrorPopUpNoWait("ERROR NOT VALID SETUP!");
			return;
		}
		
		for(String s : toAddTracks) {
			try {
				model.addTrack(s);
			} catch (FileNotFoundException e) {
				showErrorPopUpNoWait("ERROR! FILE "+ FilenameUtils.getName(s) +" COULD NOT BE FOUND!");
			} catch (XMLStreamException e) {
				showErrorPopUpNoWait("ERROR! FILE "+ FilenameUtils.getName(s) +" COULD NOT BE PARSED!");
			}
		}
		
		for(String s : toDeleteTracks) {
			model.removeTrack(s, directoryFolder);
		}
	}
	
	/**
	 * loads in a new View/Window for choosing the desired directory
	 * @author Ozan
	 */
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
			popup.getIcons().add(new Image(getClass().getResourceAsStream("/icon/folder.jpg")));
			popup.setAlwaysOnTop(true);
			popup.setScene(popupScene);
			popup.showAndWait();
		} catch (IOException e1) {
			showErrorPopUp("ERROR! Please refer to following information: "+e1.getMessage());
		}
	}
	
	/**
	 * 
	 * Menu Item Track set up
	 */
		
	@FXML
	private Menu mTracks;
	
	/**
	 * sets the track menu items up and sets the first one (if it exists) as the default one
	 * also includes the keyword for requesting all tracks
	 * @author Ozan
	 */
	private void setUpTrackMenuItems() {

		mTracks.getItems().clear();
		tgMenuTrack.selectToggle(null);
		RadioMenuItem help;
		RadioMenuItem first=null;

		for (String cat: categories) {
			help = new RadioMenuItem(cat);
			if(first==null) first=help;
			help.setToggleGroup(tgMenuTrack);
			mTracks.getItems().add(help);
		}
		
		help = new RadioMenuItem(DataModel.ALL_TRACK_KEYWORD);
		help.setToggleGroup(tgMenuTrack);
		mTracks.getItems().add(help);
		
		if(first!=null) {
			tgMenuTrack.selectToggle(first);
		}
	}

	/**
	 * 
	 * changes the active subdirectory and updates the model accordingly
	 * only if a active connection with the database exists
	 * @param subfolder the desired
	 * @author Ozan (except changeChart and their exceptions -> Nuray)
	 * @throws InvocationTargetException  due to changeChart method
	 * @throws NoSuchMethodException due to changeChart method
	 * @throws IllegalAccessException due to changeChart method
	 */
	private void changeCategory(String subfolder) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException { //index als parameter hinzugefügt - nuray
		if(model.checkConnection()) {
			model.setDirectoryFolder(subfolder);
			model.updateTrackListFromDB();
			syncTracks();
			changeChart();
		}
	}


	@FXML
	private TableView<AbstractTrack> mainTable;

	@FXML
	private TableView<AbstractTrack> sideTable;

	@FXML
	private TextField keywordTextField;
	

	/* Action Handler für die Segment MenuItems
	 * TODO Methoden sinnvoll implementieren */
	@FXML
	private void segment1m(ActionEvent event){

	}

	@FXML
	private void segment10m(ActionEvent event){

	}

	@FXML
	private void segment100m(ActionEvent event){

	}

	@FXML
	private void segment400m(ActionEvent event){

	}

	@FXML
	private void segment500m(ActionEvent event){

	}
	@FXML
	private void segment1000m(ActionEvent event){

	}

	@FXML
	private void segment5000m(ActionEvent event){

	}

	@FXML
	private void segment10000m(ActionEvent event){

	}
	@FXML
	private void segmentQuarterMarathon(ActionEvent event){

	}

	@FXML
	private void segmentHalfMarathon(ActionEvent event){

	}

	@FXML
	private void segmentTrackPoints(ActionEvent event){

	}

	/* Action Handler für die View MenuItems
	 * TODO Methoden sinnvoll implementieren */
	@FXML
	private void viewDay(ActionEvent event){

	}

	@FXML
	private void viewMonth(ActionEvent event){

	}

	@FXML
	private void viewWeek(ActionEvent event){

	}

	//TODO: UserGuide Methode Implementieren
	@FXML
	private void openUserGuide(ActionEvent event){
	}

	//Implementierungstipps: https://stackoverflow.com/a/45013059
	private void showTrackTable(TableView table, List<?> tl) {
		//clear table
		table.getItems().clear();
		table.getColumns().clear();

		//Create columns
		TableColumn<AbstractTrack, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<AbstractTrack, LocalDate> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

		TableColumn<AbstractTrack, LocalTime> startCol = new TableColumn<>("Start");
		startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));

		TableColumn<AbstractTrack, Number> distanceCol = new TableColumn<>("Distance");
		distanceCol.setCellValueFactory(cellValue -> cellValue.getValue().getDistanceProperty());

		TableColumn<AbstractTrack, String> durationCol= new TableColumn<>("Duration");
		durationCol.setCellValueFactory(cellValue -> cellValue.getValue().getDurationProperty());

		TableColumn<AbstractTrack, String> paceCol = new TableColumn<>("Pace");
		paceCol.setCellValueFactory(cellValue -> cellValue.getValue().getPaceProperty());

		TableColumn<AbstractTrack, Number> speedCol = new TableColumn<>("Speed");
		speedCol.setCellValueFactory(cellValue -> cellValue.getValue().getSpeedProperty());

		TableColumn<AbstractTrack, Number> avgBpmCol = new TableColumn<>("Average bpm");
		avgBpmCol.setCellValueFactory(new PropertyValueFactory<>("averageBPM"));

		TableColumn<AbstractTrack, Number> maxBpmCol = new TableColumn<>("Max bpm");
		maxBpmCol.setCellValueFactory(new PropertyValueFactory<>("maximumBPM"));

		TableColumn<AbstractTrack, Number> elevationCol = new TableColumn<>("Elevation");
		elevationCol.setCellValueFactory(cellValue -> cellValue.getValue().getElevationProperty());

			table.getColumns().addAll(nameCol, dateCol, startCol, distanceCol, durationCol, paceCol, speedCol, avgBpmCol, maxBpmCol, elevationCol);
			table.setItems((ObservableList<AbstractTrack>) tl);

		//further adjustments
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		table.refresh();

		mainTable = table;

		// add event for rows
		table.setRowFactory( tv -> {
			TableRow<Track> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				Track rowData = row.getItem();
				showSideTable(sideTable, FXCollections.observableArrayList((getTrackPointsOnClick(rowData))));
			});
			return row ;});

		FilteredList<AbstractTrack> filteredData = new FilteredList<>((ObservableList<AbstractTrack>) tl, b -> true);
		keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(AbstractTrack -> {
				if(newValue.isEmpty() || newValue.isBlank() || newValue == null){
					return true;
				}
				String searchKeyword = newValue.toLowerCase();
				if(AbstractTrack.getName().toLowerCase().indexOf(searchKeyword) > -1) {
					return true;
				} else
					return false;
			});
		});

		SortedList<AbstractTrack> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(table.comparatorProperty());
		table.setItems(sortedData);

		trackList = (ObservableList<Track>) tl;
	}
	
	/**
	 * returns the trackPoints of the given track
	 * @author Ozan
	 * @param track the track from which trackPoints should be retrieved
	 * @return TrackPoints of the given track as List
	 */
	private List<TrackPoint> getTrackPointsOnClick(Track track) {
		try {
			return model.getTrackPoints(track);
		} catch (FileNotFoundException e) {
			syncTracks();
			showErrorPopUpNoWait("TRACK NOT FOUND ANYMORE! REMEMBER TO UPDATE TRACKS AFTER EVERY CHANGE!");
		} catch (XMLStreamException e) {
			syncTracks();
			showErrorPopUpNoWait("TRACK NOT CONFIRMING TO SPECIFICATION!");
		}
		return new ArrayList<>();
	}

	private void showSideTable(TableView table, List<?> tp ){

		table.getItems().clear();
		table.getColumns().clear();
		//Create columns
		
		
		TableColumn<AbstractTrack, String> nameCol = new TableColumn<>("Nr");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<AbstractTrack, Number> distanceCol = new TableColumn<>("Distance");
		distanceCol.setCellValueFactory(cellValue -> cellValue.getValue().getDistanceProperty());

		TableColumn<AbstractTrack, String> durationCol= new TableColumn<>("Duration");
		durationCol.setCellValueFactory(cellValue -> cellValue.getValue().getDurationProperty());

		TableColumn<AbstractTrack, String> paceCol = new TableColumn<>("Pace");
		paceCol.setCellValueFactory(cellValue -> cellValue.getValue().getPaceProperty());

		TableColumn<AbstractTrack, Number> speedCol = new TableColumn<>("Speed");
		speedCol.setCellValueFactory(cellValue -> cellValue.getValue().getSpeedProperty());

		TableColumn<AbstractTrack, Number> avgBpmCol = new TableColumn<>("Average bpm");
		avgBpmCol.setCellValueFactory(new PropertyValueFactory<>("averageBPM"));

		TableColumn<AbstractTrack, Number> maxBpmCol = new TableColumn<>("Max bpm");
		maxBpmCol.setCellValueFactory(new PropertyValueFactory<>("maximumBPM"));

		TableColumn<AbstractTrack, Number> elevationCol = new TableColumn<>("Elevation");
		elevationCol.setCellValueFactory(cellValue -> cellValue.getValue().getElevationProperty());

		table.getColumns().addAll(nameCol, distanceCol, durationCol, paceCol, speedCol, avgBpmCol, maxBpmCol, elevationCol);
		table.setItems((ObservableList<AbstractTrack>) tp);

		//further adjustments
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		table.refresh();

		sideTable = table;
	}
	/* Action Handler für die Graph MenuItems */


	@FXML BarChart chart;
	private void createBarChart(String name, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

		Method method = new Track().getClass().getMethod(methodName);

		chart.setTitle(name);
		chart.getXAxis().setLabel("Track Name");
		chart.getYAxis().setLabel("Value");

		chart.getData().clear();
		chart.layout();
		XYChart.Series xy = new XYChart.Series();
		xy.setName(name);
		for (AbstractTrack at: trackList)
		{
			xy.getData().add(new XYChart.Data(at.getName(), method.invoke(at)));
		}
		chart.setData(FXCollections.observableArrayList(xy));

	}

	/* aktualiesiert chart nach änderung der kategorie */
	private void changeChart() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		RadioMenuItem rmi = (RadioMenuItem)	tgGraph.getSelectedToggle();
		if(rmi!=null) {
			createBarChart(rmi.getText(), "get"+rmi.getText());
		}
	}

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setUpLists();
		chooseDirectory();
		setUpMenuTrack();
		syncTracks();
		showTrackTable(mainTable, trackList);

		initializeHandlers();
	}

	public void initializeHandlers()
	{
		/* vergewissert, dass events beim ersten klick nicht ignoriert werden.
		deshalb werden handlers so angelegt, statt in fxml zu definieren
		 */

		tgGraph.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
		public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
		if (tgGraph.getSelectedToggle() != null) {
		RadioMenuItem selectedItem = (RadioMenuItem) tgGraph.getSelectedToggle();
		String method = "get" +selectedItem.getText();
		try {
		createBarChart(selectedItem.getText(), method);
		} catch (NoSuchMethodException e) {
		e.printStackTrace();
		} catch (InvocationTargetException e) {
		e.printStackTrace();
		} catch (IllegalAccessException e) {
		e.printStackTrace();
		}}}});



	}

}

