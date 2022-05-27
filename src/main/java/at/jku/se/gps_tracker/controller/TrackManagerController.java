	package at.jku.se.gps_tracker.controller;

		import at.jku.se.gps_tracker.Group.*;
		import at.jku.se.gps_tracker.model.AbstractTrack;
		import at.jku.se.gps_tracker.model.DataModel;

		import at.jku.se.gps_tracker.model.Track;
		import javafx.application.Platform;
		import javafx.beans.property.*;
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
		import javafx.scene.chart.XYChart;
		import javafx.scene.control.*;
		import javafx.scene.control.cell.PropertyValueFactory;
		import javafx.scene.image.Image;
		import javafx.stage.Stage;

		import java.io.IOException;
		import java.lang.reflect.InvocationTargetException;
		import java.lang.reflect.Method;
		import java.net.URL;
		import java.time.LocalDate;
		import java.time.LocalTime;
		import java.time.temporal.WeekFields;
		import java.util.Comparator;
		import java.util.List;
		import java.util.Locale;
		import java.util.ResourceBundle;

public class TrackManagerController implements Initializable, ErrorPopUpController {
	//TODO : Optische Korrekturen
	private DataModel model;
	private ObservableList<AbstractTrack> trackList;
	private ObservableList<AbstractTrack> backUp;
	private ObservableList<String> categories;
	final private ToggleGroup tgMenuTrack;

	final private ObservableList<GroupTrack> weeks = FXCollections.observableArrayList();
	final private ObservableList<MonthGroup> months = FXCollections.observableArrayList();
	final private ObservableList<DayGroup> days = FXCollections.observableArrayList();
	final private ObservableList<YearGroup> years = FXCollections.observableArrayList();


	@FXML
	private ToggleGroup tgGraph;

	@FXML
	private ToggleGroup tgView;

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
				} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
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
		model.updateModel();
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
		Platform.exit();
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
	private TableView mainTable;

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





	//TODO: UserGuide Methode Implementieren
	@FXML
	private void openUserGuide(ActionEvent event){
	}
/* Tabellen */


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
				showSideTable(sideTable, FXCollections.observableArrayList((model.getTrackPoints(rowData))));
			});
			return row ;});

		FilteredList<AbstractTrack> filteredData = new FilteredList<>((ObservableList<AbstractTrack>) tl, b -> true);
		keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(AbstractTrack -> {
			if(newValue.isEmpty() || newValue.isBlank() || newValue == null){
				return true;
			}
			String searchKeyword = newValue.toLowerCase();
			return AbstractTrack.getName().toLowerCase().indexOf(searchKeyword) > -1;
		}));

		SortedList<AbstractTrack> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(table.comparatorProperty());
		table.setItems(sortedData);

		trackList = (ObservableList<AbstractTrack>) tl;
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

	private void showGroupTable(ObservableList<?> tl) {

		mainTable.getColumns().clear();
		trackList.clear(); // weil sortedlist kann nicht gelöscht werden

		//TableView<GroupTrack> table= new TableView<>();

		//Create columns
		TableColumn<AbstractTrack, String> nameCol = new TableColumn<>("Name");

		nameCol.setCellValueFactory(cellValue -> new SimpleStringProperty(cellValue.getValue().getName()));

		TableColumn<GroupTrack, Number> countCol = new TableColumn<>("Count");
		countCol.setCellValueFactory(cellValue -> new SimpleDoubleProperty(cellValue.getValue().getCount()));

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


		mainTable.getColumns().addAll(nameCol, countCol, distanceCol, durationCol, paceCol, speedCol, avgBpmCol, maxBpmCol, elevationCol);
		mainTable.setItems(tl);

		//further adjustments
		mainTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


		if (tl != months) //months cannot be sorted just by string name, added comparator above
		{mainTable.getSortOrder().add(nameCol);
			mainTable.sort();}
		mainTable.refresh();
	}


	@FXML BarChart chart;
	private void createBarChart(String name, String methodName, ObservableList<?> list ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {


		if (methodName.equals("getDuration"))
		{
			methodName = "getDurationMinutes";
		}

		System.out.println(methodName);

		Method method = Track.class.getMethod(methodName);
		chart.setTitle(name);
		chart.getXAxis().setLabel("Track Name");

		switch (methodName) {
			case "getDistance" -> chart.getYAxis().setLabel("Distance");
			case "getElevation" -> chart.getYAxis().setLabel("Elevation");
			case "getDurationMinutes" -> chart.getYAxis().setLabel("Duration");
			case "getHeartbeat" -> chart.getYAxis().setLabel("HeartBeat");
			case "getSpeed" -> chart.getYAxis().setLabel("getSpeed");
			default -> chart.getYAxis().setLabel("");
		}

		chart.getData().clear();
		chart.layout();
		XYChart.Series xy = new XYChart.Series();
		xy.setName(name);
		if (list == trackList) {
			for (AbstractTrack at : trackList) {
				xy.getData().add(new XYChart.Data(at.getName(), method.invoke(at)));
			}
		}

		else if (list == weeks)
		{
			chart.getXAxis().setLabel("Wochen");

			for (GroupTrack gt: weeks)
				xy.getData().add(new XYChart.Data(gt.getName(), method.invoke(gt)));
		}

		else if (list == days)
		{
			chart.getXAxis().setLabel("Tage");
			for (GroupTrack gt: days)

				xy.getData().add(new XYChart.Data(gt.getName(), method.invoke(gt)));
		}

		else if (list == months)
		{
			chart.getXAxis().setLabel("Monate");

			for (GroupTrack gt: months)
				xy.getData().add(new XYChart.Data(gt.getName(), method.invoke(gt)));
		}

		else if (list == years)
		{
			chart.getXAxis().setLabel("Jahre");

			for (GroupTrack gt: years)
				xy.getData().add(new XYChart.Data(gt.getName(), method.invoke(gt)));
		}

		chart.setData(FXCollections.observableArrayList(xy));

	}

	/* aktualiesiert chart nach änderung der kategorie */
	private void changeChart() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		RadioMenuItem rmi = (RadioMenuItem)	tgGraph.getSelectedToggle();
		if(rmi!=null) {
			createBarChart(rmi.getText(), "get"+rmi.getText(), trackList);
		}
	}

/*

--
Gruppierungsmethoden
* */

	private void groupWeek() {

		ObservableList<Track> tracks = FXCollections.observableArrayList();

		for (AbstractTrack at : backUp) {
			tracks.add((Track) at);
		}
		for (Track track: tracks)
		{
			int week = track.getDate().get(WeekFields.of(Locale.GERMANY).weekOfWeekBasedYear());
			int year = track.getDate().getYear();
			boolean added = false;
			for (GroupTrack wg: weeks)
			{
				if (wg.getWeek() == week && wg.getYear() == year)
				{
					wg.add(track);
					added = true;
					break;
				}
			}

			if (!added){
				weeks.add(new WeekGroup(week, year));
				weeks.get(weeks.size()-1).add(track);}
		}
	}

	private void groupMonth() {

		ObservableList<Track> tracks = FXCollections.observableArrayList();

		for (AbstractTrack at : backUp) {
			tracks.add((Track) at);
		}

		for (Track track: tracks)
		{
			int month = track.getDate().getMonthValue();
			int year = track.getDate().getYear();
			boolean added = false;
			for (MonthGroup mg: months)
			{
				if (mg.getMonth() == month && mg.getYear() == year)
				{
					mg.add(track);
					added = true;
					break;
				}
			}

			if (!added){
				months.add(new MonthGroup(month, year));
				months.get(months.size()-1).add(track);}
		}

		Comparator<MonthGroup> comparator = Comparator.comparingInt(MonthGroup::getMonth);
		FXCollections.sort(months, comparator);
	}




	private void groupDay() {

		ObservableList<Track> tracks = FXCollections.observableArrayList();

		for (AbstractTrack at : backUp) {
			tracks.add((Track) at);
		}
		for (Track track: tracks)
		{

			LocalDate day = track.getDate();
			boolean added = false;
			for (DayGroup dg: days)
			{
				if (dg.getDate() == day )
				{
					dg.add(track);
					added = true;
					break;
				}
			}

			if (!added){
				days.add(new DayGroup(day));
				days.get(days.size()-1).add(track);}
		}
	}

	private void groupYear() {

		ObservableList<Track> tracks = FXCollections.observableArrayList();

		for (AbstractTrack at : backUp) {
			tracks.add((Track) at);
		}

		for (Track track: tracks)
		{

			int year = track.getDate().getYear();

			boolean added = false;
			for (YearGroup dg: years)
			{
				if (dg.getYear() == year )
				{
					dg.add(track);
					added = true;
					break;
				}
			}

			if (!added){
				years.add(new YearGroup(year));
				years.get(years.size()-1).add(track);}
		}
	}


	private void groupAll() {

		groupWeek();
		groupMonth();
		groupDay();
		groupYear();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setUpLists();
		chooseDirectory();
		setUpMenuTrack();
		model.updateModel();
		showTrackTable(mainTable, trackList);
		backUp = trackList;
		groupAll();
		initializeHandlers();

	}

	public void initializeHandlers()
	{
		/* vergewissert, dass events beim ersten klick nicht ignoriert werden.
		deshalb werden handlers so angelegt, statt in fxml zu definieren
		 */

		tgGraph.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
			if (tgGraph.getSelectedToggle() != null) {
				RadioMenuItem selectedItem = (RadioMenuItem) tgGraph.getSelectedToggle();
				RadioMenuItem rmi = (RadioMenuItem) tgView.getSelectedToggle();

				String method = "get" + selectedItem.getText();

				if (rmi!=null)
				{
					if (rmi.getText().equals("Week")) {
						try {
							createBarChart(selectedItem.getText(), method, weeks);
						} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}

					else if (rmi.getText().equals("Month")) {
						try {
							createBarChart(selectedItem.getText(), method, months);
						} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}

					else if (rmi.getText().equals("Day")) {
						try {
							createBarChart(selectedItem.getText(), method, days);
						} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}

					else if (rmi.getText().equals("Year")) {
						try {
							createBarChart(selectedItem.getText(), method, years);
						} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
				else {
					try {
						createBarChart(selectedItem.getText(), method, trackList);
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}}
			}
		});


		tgView.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
			if (tgView.getSelectedToggle() != null) {
				RadioMenuItem selectedItem = (RadioMenuItem) tgView.getSelectedToggle();
				RadioMenuItem rmi = (RadioMenuItem) tgGraph.getSelectedToggle();
				String method = null;
				if (rmi!= null)
				{ method = "get" + rmi.getText();}

				if (selectedItem.getText().equals("Week"))
				{
					showGroupTable(weeks);

					if (rmi!= null){
						try {
							createBarChart(selectedItem.getText(), method, weeks);
						} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}

				else if (selectedItem.getText().equals("Day"))
				{
					showGroupTable(days);

					if (rmi!= null){
						try {
							createBarChart(selectedItem.getText(), method, days);
						} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}

				else if (selectedItem.getText().equals("Month"))
				{
					showGroupTable(months);

					if (rmi!= null){
						try {
							createBarChart(selectedItem.getText(), method, months);
						} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}


				else if (selectedItem.getText().equals("Year"))
				{
					showGroupTable(years);

					if (rmi!= null){
						try {
							createBarChart(selectedItem.getText(), method, years);
						} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}});



	}

}

