package at.jku.se.gps_tracker.Group;


import at.jku.se.gps_tracker.model.AbstractTrack;
import at.jku.se.gps_tracker.model.Track;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class YearGroup extends GroupTrack {

    public YearGroup(int year)
    {
        super();
        super.year = year;
        setName();
        super.group = "Year";
        super.xAxis = ""+year; // name for x axis
    }

    public YearGroup() {}

    public void setName() {
        super.setName("Jahr:" + year);
    }
    @Override
    public String toString() {
        return "YearGroup{" +
                "year=" + year +
                '}';
    }
    @Override
    public GroupTrack match (Track element, ObservableList<GroupTrack> list, LocalDate day, int week,  int month, int year)
    {
        for (GroupTrack dg: list) {
            if (dg.getYear() == year) {

                return dg;

    }}return null; }

    @Override
    public ObservableList<GroupTrack> group(ObservableList<AbstractTrack> list) {

        super.groupList = FXCollections.observableArrayList();
        ObservableList<Track> tracks = FXCollections.observableArrayList();

        for (AbstractTrack at: list) {
            tracks.add((Track) at);

        }
        for (Track track: tracks) {

            int year = track.getDate().getYear();
            if (match(track, groupList, null, 0, 0, year) != null )
                match(track, groupList, null, 0,0, year).add(track);
            else
                groupList.add(new YearGroup(year));
                addToLastElementInList(groupList, track);
            }
        return groupList;
    }



}
