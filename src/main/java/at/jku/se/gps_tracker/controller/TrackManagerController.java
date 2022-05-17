package at.jku.se.gps_tracker.controller;

import at.jku.se.gps_tracker.model.AbstractTrack;
import at.jku.se.gps_tracker.model.DataModel;
import at.jku.se.gps_tracker.model.Track;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class TrackManagerController implements Initializable, ErrorPopUpController {
	//TODO : Optische Korrekturen
	private DataModel model;
	private ObservableList<AbstractTrack> trackList;
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
	
	/** set up the lists and add listeners
	 * 
	 * 
	 */

	private void setUpLists() {
		setTrackList();
		setCategories();
		setUpMenuItems();
	}

	private void setTrackList() {
		trackList = FXCollections.observableArrayList(model.getTrackList());
		model.getTrackList().addListener((ListChangeListener<? super AbstractTrack>) c -> {
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
			setUpMenuItems();
		});
	}
	
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
	 * set up Menu File
	 * 
	 */
	
	@FXML
	private void setDirectory(ActionEvent event) {
		chooseDirectory();
		sideTable.getItems().clear(); //setzt sidetable zurück da sie sonst die letzte instanz anzeigt
	}
	
	@FXML
	private void updateDirectoryFolders(ActionEvent event) {
		model.adjustDirectoryFolders();
	}
	
	@FXML
	private void updateModel(ActionEvent event) {
		model.updateModel();
	}

	@FXML
	private void closeApplication(ActionEvent event) {
		model.closeConnection();
		System.exit(1);
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
			showErrorPopUp("ERROR! Please refer to following information: "+e1.getMessage());
		}
	}
	
	/**
	 * 
	 * Menu Item Track set up
	 */
	
	@FXML
	private Menu mTracks;

	private void setUpMenuItems() {

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

	//je nach Index entsprechend holen! (erster Eintrag ausgewählt --> hier erste (bzw 0 auswählen!)
	private void changeCategory(String category) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException { //index als parameter hinzugefügt - nuray
		model.adjustDirectoryFolder(category);
		model.updateModel();
		changeChart(); //aktualisiert chart
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

		nameCol.setCellValueFactory(cellValue -> new SimpleStringProperty(cellValue.getValue().getName()));

		TableColumn<AbstractTrack, LocalDate> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(
				new PropertyValueFactory<>("date"));

		TableColumn<AbstractTrack, LocalTime> startCol = new TableColumn<>("Start");
		startCol.setCellValueFactory(
				new PropertyValueFactory<>("startTime"));

		TableColumn<AbstractTrack, Number> distanceCol = new TableColumn<>("Distance");
		distanceCol.setCellValueFactory(cellValue -> new SimpleDoubleProperty(cellValue.getValue().getDistance()));

		TableColumn<AbstractTrack, String> durationCol= new TableColumn<>("Duration");
		durationCol.setCellValueFactory(cellValue -> cellValue.getValue().getDurationProperty());

		TableColumn<AbstractTrack, String> paceCol = new TableColumn<>("Pace");
		paceCol.setCellValueFactory(cellValue -> (cellValue.getValue().getPaceProperty()));

		TableColumn<AbstractTrack, Number> speedCol = new TableColumn<>("Speed");
		speedCol.setCellValueFactory(cellValue -> new SimpleDoubleProperty((cellValue.getValue().getSpeed())));

		TableColumn<AbstractTrack, Number> avgBpmCol = new TableColumn<>("Average bpm");
		avgBpmCol.setCellValueFactory(cellValue -> new SimpleIntegerProperty((cellValue.getValue().getAverageBPM())));

		TableColumn<AbstractTrack, Number> maxBpmCol = new TableColumn<>("Max bpm");
		maxBpmCol.setCellValueFactory(cellValue -> new SimpleIntegerProperty((cellValue.getValue().getMaximumBPM())));

		TableColumn<AbstractTrack, Number> elevationCol = new TableColumn<>("Elevation");
		elevationCol.setCellValueFactory(cellValue -> new SimpleDoubleProperty((cellValue.getValue().getElevation())));

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
				showSideTable(sideTable, FXCollections.observableArrayList((model.getTrackPoints(rowData))));
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

		trackList = (ObservableList<AbstractTrack>) tl;


	}

	@FXML
	private void showSideTable(TableView table, List<?> tp ){

		table.getItems().clear();
		table.getColumns().clear();
		//Create columns
		TableColumn<AbstractTrack, String> nameCol = new TableColumn<>("Name");

		nameCol.setCellValueFactory(cellValue -> new SimpleStringProperty(cellValue.getValue().getName()));

		TableColumn<AbstractTrack, Number> distanceCol = new TableColumn<>("Distance");
		distanceCol.setCellValueFactory(cellValue -> new SimpleDoubleProperty(cellValue.getValue().getDistance()));

		TableColumn<AbstractTrack, String> durationCol= new TableColumn<>("Duration");
		durationCol.setCellValueFactory(cellValue -> cellValue.getValue().getDurationProperty());

		TableColumn<AbstractTrack, String> paceCol = new TableColumn<>("Pace");
		paceCol.setCellValueFactory(cellValue -> (cellValue.getValue().getPaceProperty()));

		TableColumn<AbstractTrack, Number> speedCol = new TableColumn<>("Speed");
		speedCol.setCellValueFactory(cellValue -> new SimpleDoubleProperty((cellValue.getValue().getSpeed())));

		TableColumn<AbstractTrack, Number> avgBpmCol = new TableColumn<>("Average bpm");
		avgBpmCol.setCellValueFactory(cellValue -> new SimpleIntegerProperty((cellValue.getValue().getAverageBPM())));

		TableColumn<AbstractTrack, Number> maxBpmCol = new TableColumn<>("Max bpm");
		maxBpmCol.setCellValueFactory(cellValue -> new SimpleIntegerProperty((cellValue.getValue().getMaximumBPM())));

		TableColumn<AbstractTrack, Number> elevationCol = new TableColumn<>("Elevation");
		elevationCol.setCellValueFactory(cellValue -> new SimpleDoubleProperty((cellValue.getValue().getElevation())));

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
		model.updateModel();
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

