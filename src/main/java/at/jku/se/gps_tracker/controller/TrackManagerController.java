package at.jku.se.gps_tracker.controller;

import at.jku.se.gps_tracker.model.AbstractTrack;
import at.jku.se.gps_tracker.model.DataModel;
import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class TrackManagerController implements Initializable, ErrorPopUpController {
	//TODO : Optische Korrekturen
	private DataModel model;
	private ObservableList<AbstractTrack> trackList;
	private ObservableList<TrackPoint> trackPoints;
	private ObservableList<String> categories;

	@FXML
	private TableView mainTable;

	@FXML
	private TableView sideTable;

	public TrackManagerController(DataModel model) {
		this.model=model;
	}

	private void setUpLists() {
		setTrackList();
		setCategories();
	}

	private void showSideTable(ObservableList<TrackPoint> table, TableView tv ) throws IOException {
		showTrackTable(tv, table);
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
			try {
				setUpMenuItems();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@FXML
	private void setDirectory(ActionEvent event) throws IOException {
		chooseDirectory();
	}

	@FXML
	private void updateModel(ActionEvent event) {
		model.updateModel();
	}

	@FXML
	private void closeApplication(ActionEvent event) {
		System.exit(1);
	}

	//je nach Index entsprechend holen! (erster Eintrag ausgewählt --> hier erste (bzw 0 auswählen!)
	@FXML
	private void changeCategory(ActionEvent event, int index) throws IOException { //index als parameter hinzugefügt - nuray

		model.setCurrentDirectoryFolder(index);
		setUpLists();
		showTrackTable(mainTable, trackList);
	}


	@FXML
	private MenuBar menubar;

	@FXML
	private Menu mTracks;




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

	/* Action Handler für die Graph MenuItems */

	@FXML
	private void visualizeDistance(ActionEvent event) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

		RadioMenuItem rmi = (RadioMenuItem) event.getSource();
		String font = rmi.getText();
		createBarChart(font, "getDistance");
	}

	@FXML
	private void visualizeHeartbeat(ActionEvent event) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

		RadioMenuItem rmi = (RadioMenuItem) event.getSource();
		String font = rmi.getText();
		createBarChart(font, "getAverageBPM");
	}

	@FXML
	private void visualizeDuration(ActionEvent event) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

		RadioMenuItem rmi = (RadioMenuItem) event.getSource();
		String font = rmi.getText();
		createBarChart(font, "getDurationMinutes");


	}

	@FXML
	private void visualizeElevation(ActionEvent event) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

		RadioMenuItem rmi = (RadioMenuItem) event.getSource();
		String font = rmi.getText();
		createBarChart(font, "getElevation");
	}

	@FXML
	private void visualizeSpeed(ActionEvent event) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		RadioMenuItem rmi = (RadioMenuItem) event.getSource();
		String font = rmi.getText();
		createBarChart(font, "getSpeed");

	}
	//TODO: UserGuide Methode Implementieren
	@FXML
	private void openUserGuide(ActionEvent event){
	}

	private void setUpMenuItems() throws IOException {

		//nuray
		//die inhalte der liste categories unter tracks anzeigen
		mTracks.getItems().clear();
		List<RadioMenuItem> temp = new ArrayList<>();
		ToggleGroup tg = new ToggleGroup();

		for (String cat: categories) {
			temp.add(new RadioMenuItem(cat));
		}

		for (RadioMenuItem ri: temp)
		{
			mTracks.getItems().add(ri);
			ri.setToggleGroup(tg);
			ri.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {
						changeCategory(event, temp.indexOf(ri));
					} catch (IOException e) {
						e.printStackTrace();
					}

				}});}}


	//Implementierungstipps: https://stackoverflow.com/a/45013059
	private void showTrackTable(TableView table, List<?> trackList) throws IOException {
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

		table.setRowFactory( tv -> {
			TableRow<Track> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				Track rowData = row.getItem();
				trackPoints = FXCollections.observableArrayList((rowData.getTrackPoints()));
				try {
					showSideTable(trackPoints, sideTable);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return row ;});

		//Adding data to the table

		table.getColumns().addAll(nameCol, dateCol,  startCol, distanceCol, durationCol, paceCol, speedCol, avgBpmCol, maxBpmCol, elevationCol);
		table.setItems((ObservableList<AbstractTrack>) trackList);

		//further adjustments
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		table.refresh();

	}

	//TODO Methode für SideTable


	@FXML BarChart chart;
	private void createBarChart(String name, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {


		AbstractTrack abstr = new Track();
		Method method = abstr.getClass().getMethod(methodName);

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();

		chart.setTitle(name);
		xAxis.setLabel("Track Name");
		yAxis.setLabel("Value");

		chart.getData().clear();
		chart.layout();


		XYChart.Series xy = new XYChart.Series();
		xy.setName("Category");
		for (AbstractTrack at: trackList)
		{
			xy.getData().add(new XYChart.Data(at.getName(), method.invoke(at)));
		}
		chart.getData().addAll(xy);

	}
	private void chooseDirectory() throws IOException {

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
		setUpLists();
		setUpMenuItems(); // damit die Menu Items nach ChangeCategory aktualisieren

	}

	@FXML ToggleGroup tgGraph;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			chooseDirectory();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			showTrackTable(mainTable, trackList);
		} catch (IOException e) {
			e.printStackTrace();
		}}}

//the simplest way to print current value (text of the selected radio button)


//register on change eve




