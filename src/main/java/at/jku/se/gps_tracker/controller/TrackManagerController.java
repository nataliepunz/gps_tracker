package at.jku.se.gps_tracker.controller;

import at.jku.se.gps_tracker.Group. * ;
import at.jku.se.gps_tracker.model.AbstractTrack;
import at.jku.se.gps_tracker.model.DataModel;

import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;
import javafx.application.Platform;
import javafx.beans.property. * ;
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
import javafx.scene.control. * ;
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
import java.time.temporal.WeekFields;
import java.util. * ;

import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.FilenameUtils;

public class TrackManagerController implements Initializable,
        ErrorPopUpController {
    //TODO : Optische Korrekturen
    private DataModel model;
    private ObservableList < AbstractTrack > trackList;
    private ObservableList < AbstractTrack > backUp;
    private ObservableList < String > categories;
    final private ToggleGroup tgMenuTrack;

    String year1;
    String year2;

    private ObservableList < GroupTrack > group = FXCollections.observableArrayList();

    @FXML
    private ToggleGroup tgGraph;

    @FXML
    private ToggleGroup tgView;

    @FXML
    private MenuBar menubar;

    @FXML
    private Menu mYears;

    public TrackManagerController(DataModel model) {
        this.model = model;
        tgMenuTrack = new ToggleGroup();
    }

    /**
     * set up the lists and add listeners
     */
    
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
        model.getTrackList().addListener((ListChangeListener < ?super Track > ) c ->{
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
	  * also calls the setUpTrackMenuItems method to re-setUp the RadioMenuItems for the filter choices after changes in the directory
	  * @author Ozan
	  * 
	  */
    private void setCategories() {
        categories = FXCollections.observableArrayList(model.getDirectoryFolders());
        model.getDirectoryFolders().addListener((ListChangeListener < ?super String > ) c ->{
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
            } catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
		});
    }

    /**
     * set up Menu File
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

    /* erstellt dynamisch abhängig von der trackliste die items für jahresvergleich */
    @SuppressWarnings("null")
    private void setUpYearsItems() {

        RadioMenuItem selected = (RadioMenuItem) tgMenuTrack.getSelectedToggle(); //letzte selektion wird gespeichert, damit man sie wiederherstellt
        setTrackListAll(); //alle tracks werden geladen
        groupYear(); //sie werden gruppiert, da man so die jahre schnelle erhält
        CheckMenuItem temp;
        CheckMenuItem first;
        List < Integer > items = new ArrayList < >();

        //fügt die Jahre in ein Item hinzu und erstellt die MenuItems
        for (GroupTrack at: group) {
            temp = null;

            if (!items.contains(at.getYear())) {
                items.add(at.getYear());
                temp = new CheckMenuItem(String.valueOf(at.getYear()));

            }
            if (temp != null) mYears.getItems().add(temp);
        }

        //Event handler, wurde hier gemacht da die variablen (laut fehlermeldung) fix sein müssen und bei der vorherigen iteration waren sie es nicht
        for (MenuItem cmi: mYears.getItems()) {
            if (cmi != sep && cmi != cmiYearly) { //es sollen nur actionhandler für dynamische items initialisiert werden
                CheckMenuItem ci = (CheckMenuItem) cmi;
                ci.setOnAction(e ->{
                if (ci.isSelected()) {
                    if (year1 != null && year2 != null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("WARNING");
                        alert.setHeaderText("You can only compare 2 ");
                        alert.setContentText("The first one you selected got deselected");

                        //deselektieren eines items
                        for (MenuItem mi: mYears.getItems()) {
                            CheckMenuItem findSelected = (CheckMenuItem) mi;
                            if (findSelected.isSelected()) {
                                findSelected.setSelected(false);
                                year1 = ci.getText(); //setzen des neuen items
                                break;}

                            }}
                     else {
                        if (year1 == null) // setzt die jahr1 für vergleich
                        {
                            year1 = ci.getText();
                        }

                        else //noinspection ConstantConditions
                            if (year2 == null) // setzt die jahr1 für vergleich
                        {
                            year2= ci.getText();
                        }
                    }
                }
                else { //bei deselektion
                    if (year1!= null)
                    year1 = null;
                    else year2 = null;}

                });
            }
        }

        selected.setSelected(true); //ursprüngliche selektikon
    }

    @FXML
    private CheckMenuItem cmiYearly;@FXML
    private SeparatorMenuItem sep;

    //je nach Index entsprechend holen! (erster Eintrag ausgewählt --> hier erste (bzw 0 auswählen!)
    private void changeCategory(String category) throws
            InvocationTargetException,
            NoSuchMethodException,
            IllegalAccessException { //index als parameter hinzugefügt - nuray
            model.adjustDirectoryFolder(category);
            model.updateModel();
            changeChart(); //aktualisiert charts
    }

    @FXML
    private TableView < AbstractTrack > mainTable;

    @FXML
    private TableView < AbstractTrack > sideTable;

    @FXML
    private TextField keywordTextField;

    /* Action Handler für die Segment MenuItems
     * TODO Methoden sinnvoll implementieren */
    @FXML
    private void segment1m(ActionEvent event) {

    }

    //spult alle Tracks in die Liste, ist notwendig für Vergleichsfunktionalität
    private void setTrackListAll() {
        RadioMenuItem allTracks;
        for (Toggle mi: tgMenuTrack.getToggles()) {
            allTracks = (RadioMenuItem) mi;
            if (Objects.equals(allTracks.getText(), DataModel.ALL_TRACK_KEYWORD)) {

                allTracks.setSelected(true);
            }
            break;
        }
    }

    //Event Handler für Years -> Yearly Comparison
    @FXML
    private void eventYearly(ActionEvent event) {
        if (cmiYearly.isSelected()) {
            RadioMenuItem graph = (RadioMenuItem) tgGraph.getSelectedToggle();
            RadioMenuItem view = (RadioMenuItem) tgView.getSelectedToggle();

            if (year1 == null || year2 == null) { //falls jahre noch nicht ausgewählt wurden
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("One or two selectedy years missing");
                alert.setContentText("You have to select 2 years");
                alert.showAndWait();
            } else if (graph == null || view == null) { //falls graph und view items nicht ausgewählt wurden
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No view or graph item selected");
                alert.setContentText("Please select both!");
                alert.showAndWait();

            } else {
                String method;
                method = "get" + graph.getText();
                setTrackListAll(); //um die Jahre zu vergleichen, muss die trackList alle Tracks zeigen
                //gruppiert die liste nach letzter selektion bei view
                if (view.getText().equals("Day")) {
                    groupDay();
                } else if (view.getText().equals("Week")) {
                    groupWeek();
                } else if (view.getText().equals("Month")) {
                    groupMonth();
                } else if (view.getText().equals("Year")) {
                    groupYear();
                }

                try {
                    createBarChart(method, group, Integer.parseInt(year1), Integer.parseInt(year2));
                } catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void segment10m(ActionEvent event) {

    }

    @FXML
    private void segment100m(ActionEvent event) {

    }

    @FXML
    private void segment400m(ActionEvent event) {

    }

    @FXML
    private void segment500m(ActionEvent event) {

    }

    @FXML
    private void segment1000m(ActionEvent event) {

    }

    @FXML
    private void segment5000m(ActionEvent event) {

    }

    @FXML
    private void segment10000m(ActionEvent event) {

    }

    @FXML
    private void segmentQuarterMarathon(ActionEvent event) {

    }

    @FXML
    private void segmentHalfMarathon(ActionEvent event) {

    }

    @FXML
    private void segmentTrackPoints(ActionEvent event) {

    }

    //TODO: UserGuide Methode Implementieren
    @FXML
    private void openUserGuide(ActionEvent event) {}

    /* Tabellen */

    @SuppressWarnings("unchecked") //Grund: https://stackoverflow.com/questions/4257883/warning-for-generic-varargs
    private void showTrackTable(TableView < AbstractTrack > table, ObservableList < AbstractTrack > tl) {
        //clear table
        table.getItems().clear();
        table.getColumns().clear();

        //Create columns
        TableColumn < AbstractTrack,
                String > nameCol = new TableColumn < >("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory < >("name"));

        TableColumn < AbstractTrack,
                LocalDate > dateCol = new TableColumn < >("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory < >("date"));

        TableColumn < AbstractTrack,
                LocalTime > startCol = new TableColumn < >("Start");
        startCol.setCellValueFactory(new PropertyValueFactory < >("startTime"));

        TableColumn < AbstractTrack,
                Number > distanceCol = new TableColumn < >("Distance");
        distanceCol.setCellValueFactory(cellValue ->cellValue.getValue().getDistanceProperty());

        TableColumn < AbstractTrack,
                String > durationCol = new TableColumn < >("Duration");
        durationCol.setCellValueFactory(cellValue ->cellValue.getValue().getDurationProperty());

        TableColumn < AbstractTrack,
                String > paceCol = new TableColumn < >("Pace");
        paceCol.setCellValueFactory(cellValue ->cellValue.getValue().getPaceProperty());

        TableColumn < AbstractTrack,
                Number > speedCol = new TableColumn < >("Speed");
        speedCol.setCellValueFactory(cellValue ->cellValue.getValue().getSpeedProperty());

        TableColumn < AbstractTrack,
                Number > avgBpmCol = new TableColumn < >("Average bpm");
        avgBpmCol.setCellValueFactory(new PropertyValueFactory < >("averageBPM"));

        TableColumn < AbstractTrack,
                Number > maxBpmCol = new TableColumn < >("Max bpm");
        maxBpmCol.setCellValueFactory(new PropertyValueFactory < >("maximumBPM"));

        TableColumn < AbstractTrack,
                Number > elevationCol = new TableColumn < >("Elevation");
        elevationCol.setCellValueFactory(cellValue ->cellValue.getValue().getElevationProperty());

        table.getColumns().addAll(nameCol, dateCol, startCol, distanceCol, durationCol, paceCol, speedCol, avgBpmCol, maxBpmCol, elevationCol);
        table.setItems(tl);

        /* further adjustments */
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.refresh();

        mainTable = table;

        // add event for rows
        table.setRowFactory(tv ->{
                TableRow < AbstractTrack > row = new TableRow < >();
        row.setOnMouseClicked(event ->{
                AbstractTrack rowData = row.getItem();
        showSideTable(sideTable, FXCollections.observableArrayList((model.getTrackPoints((Track) rowData))));
		                	});
             return row;
		    });

        FilteredList < AbstractTrack > filteredData = new FilteredList < >(tl, b ->true);
        keywordTextField.textProperty().addListener((observable, oldValue, newValue) ->filteredData.setPredicate(AbstractTrack ->{
        if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
            return true;
        }
        String searchKeyword = newValue.toLowerCase();
        return AbstractTrack.getName().toLowerCase().contains(searchKeyword);
	    	    }));

        SortedList < AbstractTrack > sortedData = new SortedList < >(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        trackList = tl;
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
          
    @SuppressWarnings("unchecked") //Grund: https://stackoverflow.com/questions/4257883/warning-for-generic-varargs
    private void showSideTable(TableView < AbstractTrack > table, ObservableList < AbstractTrack > tp) {

        table.getItems().clear();
        table.getColumns().clear();
        //Create columns
        TableColumn < AbstractTrack,
                String > nameCol = new TableColumn < >("Nr");
        nameCol.setCellValueFactory(new PropertyValueFactory < >("name"));

        TableColumn < AbstractTrack,
                Number > distanceCol = new TableColumn < >("Distance");
        distanceCol.setCellValueFactory(cellValue ->cellValue.getValue().getDistanceProperty());

        TableColumn < AbstractTrack,
                String > durationCol = new TableColumn < >("Duration");
        durationCol.setCellValueFactory(cellValue ->cellValue.getValue().getDurationProperty());

        TableColumn < AbstractTrack,
                String > paceCol = new TableColumn < >("Pace");
        paceCol.setCellValueFactory(cellValue ->cellValue.getValue().getPaceProperty());

        TableColumn < AbstractTrack,
                Number > speedCol = new TableColumn < >("Speed");
        speedCol.setCellValueFactory(cellValue ->cellValue.getValue().getSpeedProperty());

        TableColumn < AbstractTrack,
                Number > avgBpmCol = new TableColumn < >("Average bpm");
        avgBpmCol.setCellValueFactory(new PropertyValueFactory < >("averageBPM"));

        TableColumn < AbstractTrack,
                Number > maxBpmCol = new TableColumn < >("Max bpm");
        maxBpmCol.setCellValueFactory(new PropertyValueFactory < >("maximumBPM"));

        TableColumn < AbstractTrack,
                Number > elevationCol = new TableColumn < >("Elevation");
        elevationCol.setCellValueFactory(cellValue ->cellValue.getValue().getElevationProperty());

        table.getColumns().addAll(nameCol, distanceCol, durationCol, paceCol, speedCol, avgBpmCol, maxBpmCol, elevationCol);
        table.setItems(tp);

        //further adjustments
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.refresh();

        sideTable = table;
    }

    @SuppressWarnings("unchecked")
    private void showGroupTable(ObservableList < AbstractTrack > tl) {

        mainTable.getColumns().clear();

        //Create columns
        TableColumn < AbstractTrack,
                String > nameCol = new TableColumn < >("Name");

        nameCol.setCellValueFactory(cellValue ->new SimpleStringProperty(cellValue.getValue().getName()));

        TableColumn < AbstractTrack,
                Number > countCol = new TableColumn < >("Count");
        countCol.setCellValueFactory(cellValue ->new SimpleDoubleProperty(cellValue.getValue().getCount()));

        TableColumn < AbstractTrack,
                Number > distanceCol = new TableColumn < >("Distance");
        distanceCol.setCellValueFactory(cellValue ->new SimpleDoubleProperty(cellValue.getValue().getDistance()));

        TableColumn < AbstractTrack,
                String > durationCol = new TableColumn < >("Duration");
        durationCol.setCellValueFactory(cellValue ->cellValue.getValue().getDurationProperty());

        TableColumn < AbstractTrack,
                String > paceCol = new TableColumn < >("Pace");
        paceCol.setCellValueFactory(cellValue ->(cellValue.getValue().getPaceProperty()));

        TableColumn < AbstractTrack,
                Number > speedCol = new TableColumn < >("Speed");
        speedCol.setCellValueFactory(cellValue ->new SimpleDoubleProperty((cellValue.getValue().getSpeed())));

        TableColumn < AbstractTrack,
                Number > avgBpmCol = new TableColumn < >("Average bpm");
        avgBpmCol.setCellValueFactory(cellValue ->new SimpleIntegerProperty((cellValue.getValue().getAverageBPM())));

        TableColumn < AbstractTrack,
                Number > maxBpmCol = new TableColumn < >("Max bpm");
        maxBpmCol.setCellValueFactory(cellValue ->new SimpleIntegerProperty((cellValue.getValue().getMaximumBPM())));

        TableColumn < AbstractTrack,
                Number > elevationCol = new TableColumn < >("Elevation");
        elevationCol.setCellValueFactory(cellValue ->new SimpleDoubleProperty((cellValue.getValue().getElevation())));

        mainTable.getColumns().addAll(nameCol, countCol, distanceCol, durationCol, paceCol, speedCol, avgBpmCol, maxBpmCol, elevationCol);
        mainTable.setItems(tl);

        //further adjustments
        mainTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        ObservableList < GroupTrack > temp = FXCollections.observableArrayList();
        temp.add((GroupTrack) tl.get(0));

        if (temp.get(0).getGroup().equals("Month")) //months cannot be sorted just by string name, added comparator above
        {
            mainTable.getSortOrder().add(nameCol);
            mainTable.sort();
        }
        mainTable.refresh();
    }

    @FXML
    BarChart < String,
            Object > chart;

    @SuppressWarnings("unchecked") //
    private void createBarChart(String name, String methodName, ObservableList < AbstractTrack > list) throws NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {

        if (methodName.equals("getDuration")) {
            methodName = "getDurationMinutes";
        }
        if (methodName.equals("getHeartbeat")) {
            methodName = "getAverageBPM";
        }

        Method method = Track.class.getMethod(methodName);
        chart.setTitle(name);
        chart.getXAxis().setLabel("Track Name");

        switch (methodName) {
            case "getDistance" ->chart.getYAxis().setLabel("Distance");
            case "getElevation" ->chart.getYAxis().setLabel("Elevation");
            case "getDurationMinutes" ->chart.getYAxis().setLabel("Duration");
            case "getAverageBPM" ->chart.getYAxis().setLabel("HeartBeat");
            case "getSpeed" ->chart.getYAxis().setLabel("Speed");
            default ->chart.getYAxis().setLabel("");
        }

        chart.getData().clear();
        chart.layout();
        XYChart.Series < String,
                Object > xy = new XYChart.Series < >();
        xy.setName(name);
        if (list == trackList) {
            for (AbstractTrack at: trackList) {
                xy.getData().add(new XYChart.Data < >(at.getName(), method.invoke(at)));
            }
        }

        if (list == turnIntoAbstractTrack((group))) {

            switch (group.get(0).getName()) {
                case "Woche" ->chart.getXAxis().setLabel("Wochen");
                case "Monat" ->chart.getXAxis().setLabel("Monate");
                case "Tag" ->chart.getXAxis().setLabel("Tage");
                case "Jahr" ->chart.getXAxis().setLabel("Jahre");
                default ->chart.getYAxis().setLabel("");
            }
        }

        for (GroupTrack gt: group)
            xy.getData().add(new XYChart.Data < >(gt.getName(), method.invoke(gt)));
        chart.setData(FXCollections.observableArrayList(xy));

    }

    /* aktualisiert chart nach änderung der kategorie */
    private void changeChart() throws InvocationTargetException,
            NoSuchMethodException,
            IllegalAccessException {
        RadioMenuItem rmi = (RadioMenuItem) tgGraph.getSelectedToggle();
        if (rmi != null) {
            createBarChart(rmi.getText(), "get" + rmi.getText(), trackList);
        }
    }

	/*

		--
		Gruppierungsmethoden
		* */

    private void groupWeek() {

        group = FXCollections.observableArrayList();
        ObservableList < Track > tracks = FXCollections.observableArrayList();

        for (AbstractTrack at: backUp) {
            tracks.add((Track) at);
        }
        for (Track track: tracks) {
            int week = track.getDate().get(WeekFields.of(Locale.GERMANY).weekOfWeekBasedYear());
            int year = track.getDate().getYear();
            boolean added = false;
            for (GroupTrack wg: group) {
                if (wg.getWeek() == week && wg.getYear() == year) {
                    wg.add(track);
                    added = true;
                    break;
                }
            }
            if (!added) {
                group.add(new WeekGroup(week, year));
                group.get(group.size() - 1).add(track);
            }
        }
    }

    private void groupMonth() {

        group = FXCollections.observableArrayList();
        ObservableList < Track > tracks = FXCollections.observableArrayList();

        for (AbstractTrack at: backUp) {
            tracks.add((Track) at);

        }

        for (Track track: tracks) {

            int month = track.getDate().getMonthValue();
            int year = track.getDate().getYear();
            boolean added = false;
            for (GroupTrack mg: group) {
                if (mg.getMonth() == month && mg.getYear() == year) {
                    mg.add(track);
                    added = true;
                    break;
                }
            }

            if (!added) {
                group.add(new MonthGroup(month, year));
                group.get(group.size() - 1).add(track);
            }
        }

        Comparator < GroupTrack > comparator = Comparator.comparingInt(GroupTrack::getMonth);
        FXCollections.sort(group, comparator);
    }

    private void groupDay() {

        group = FXCollections.observableArrayList();
        ObservableList < Track > tracks = FXCollections.observableArrayList();

        for (AbstractTrack at: backUp) {
            tracks.add((Track) at);
        }
        for (Track track: tracks) {

            LocalDate day = track.getDate();
            boolean added = false;
            for (GroupTrack dg: group) {
                if (dg.getDate() == day) {
                    dg.add(track);
                    added = true;
                    break;
                }
            }

            if (!added) {
                group.add(new DayGroup(day));
                group.get(group.size() - 1).add(track);
            }
        }
    }

    private void groupYear() {

        group = FXCollections.observableArrayList();
        ObservableList < Track > tracks = FXCollections.observableArrayList();

        for (AbstractTrack at: backUp) {
            tracks.add((Track) at);
        }

        for (Track track: tracks) {

            int year = track.getDate().getYear();

            boolean added = false;
            for (GroupTrack dg: group) {
                if (dg.getYear() == year) {
                    dg.add(track);
                    added = true;
                    break;
                }
            }

            if (!added) {
                group.add(new YearGroup(year));
                group.get(group.size() - 1).add(track);
            }
        }
    }

    //für die fälle wo man grouptrack benötigt
    private ObservableList < AbstractTrack > turnIntoAbstractTrack(ObservableList < GroupTrack > list) {

        ObservableList < AbstractTrack > result = FXCollections.observableArrayList();
        result.addAll(list);

        return result;

    }

    //View Funktionalitäten
    //Create BarChart mit View Funktionalität wurde hier ausgelagert, um endlose if bedingungen bei chreatebarChart zu vermeiden
    @SuppressWarnings("unchecked")
    private void createBarChart(String methodName, ObservableList < GroupTrack > list, int year1, int year2) throws NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {

        if (methodName.equals("getDuration")) {
            methodName = "getDurationMinutes";
        }
        if (methodName.equals("getHeartbeat")) {
            methodName = "getAverageBPM";
        }

        Method method = Track.class.getMethod(methodName);
        chart.setTitle("Comparison");
        chart.getXAxis().setLabel("Years");

        if (methodName.equals("getDistance")) {
            chart.getYAxis().setLabel("Distance");
        } else if (methodName.equals("getElevation")) {
            chart.getYAxis().setLabel("Elevation");
        } else if (methodName.equals("getDurationMinutes")) {
            chart.getYAxis().setLabel("Duration");
        } else if (methodName.equals("getAverageBPM")) {
            chart.getYAxis().setLabel("HeartBeat");
        } else if (methodName.equals("getSpeed")) {
            chart.getYAxis().setLabel("Speed");
        }

        chart.getData().clear();
        chart.layout();
        XYChart.Series < String,
                Object > series1 = new XYChart.Series < >();
        XYChart.Series < String,
                Object > series2 = new XYChart.Series < >();

        //Legenden für die Reihen
        series1.setName("" + year1);
        series2.setName("" + year2);

        //Benennung der X Achse
        if (Objects.equals(group.get(0).getGroup(), "Week")) {
            chart.getXAxis().setLabel("Wochen");
        } else if (Objects.equals(group.get(0).getGroup(), "Month")) {
            chart.getXAxis().setLabel("Monate");
        } else if (Objects.equals(group.get(0).getGroup(), "Day")) {
            chart.getXAxis().setLabel("Tage");
        } else if (Objects.equals(group.get(0).getGroup(), "Year")) {
            chart.getXAxis().setLabel("Jahre");
        }

        //Hinzufügen von Datenreihen
        for (GroupTrack gt: list)

            if (gt.getYear() == year1) { //getxAxis dient dazu, dass alle einheitlich heißen ansonsten wäre das nicht der Fall, bsp. August 2020 und August 2021
                series1.getData().add(new XYChart.Data < >(String.valueOf(gt.getxAxis()), method.invoke(gt)));
            } else if (gt.getYear() == year2) {
                series2.getData().add(new XYChart.Data < >(String.valueOf(gt.getxAxis()), method.invoke(gt)));
            }
        chart.getData().addAll(series1, series2);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        setUpLists();
		    chooseDirectory();
		    setUpMenuTrack();
		    syncTracks();
        showTrackTable(mainTable, trackList);
        backUp = trackList;
        setUpYearsItems();
        initializeHandlers();

    }

    public void initializeHandlers() {
		/* vergewissert, dass events beim ersten klick nicht ignoriert werden.
			deshalb werden handlers so angelegt, statt in fxml zu definieren
			 */

        tgGraph.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) ->{
        if (tgGraph.getSelectedToggle() != null) {
            RadioMenuItem selectedItem = (RadioMenuItem) tgGraph.getSelectedToggle();
            RadioMenuItem rmi = (RadioMenuItem) tgView.getSelectedToggle();

            String method = "get" + selectedItem.getText();

            if (rmi != null) {
                if (cmiYearly.isSelected()) { //falls ausgewählt, während yearly comparison noch immer selektiert ist
                    if (year1 != null && year2 != null) {
                        try {
                            createBarChart(method, group, Integer.parseInt(year1), Integer.parseInt(year2));
                        } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    try {
                        createBarChart(selectedItem.getText(), method, turnIntoAbstractTrack(group));
                    } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                try {
                    createBarChart(selectedItem.getText(), method, trackList);
                } catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }

            }

        }
		});

        tgView.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) ->{
        if (tgView.getSelectedToggle() != null) {
            RadioMenuItem selectedItem = (RadioMenuItem) tgView.getSelectedToggle();
            RadioMenuItem rmi = (RadioMenuItem) tgGraph.getSelectedToggle();
            String method;

            group.clear();
            if (year1 != null && year2 != null) {
                setTrackListAll();
            }
            //Gruppiert Elemente je nachdem, was ausgewählt wurde
            if (selectedItem.getText().equals("Day")) {
                groupDay();
            } else if (Objects.equals(selectedItem.getText(), "Week")) {
                groupWeek();
            } else if (Objects.equals(selectedItem.getText(), "Month")) {
                groupMonth();
            } else if (Objects.equals(selectedItem.getText(), "Year")) {
                groupYear();
            }

            showGroupTable(turnIntoAbstractTrack(group));

            //wenn auch ein graph item ausgewählt...
            if (rmi != null) {
                method = "get" + rmi.getText();
                if (cmiYearly.isSelected() && year1 != null & year2 != null) { //falls auch view funktionalität ausgewählt
                    try {
                        createBarChart(method, group, Integer.parseInt(year1), Integer.parseInt(year2));
                    } catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {

                    try {
                        createBarChart(selectedItem.getText(), method, turnIntoAbstractTrack(group));
                    } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
		});

    }

}