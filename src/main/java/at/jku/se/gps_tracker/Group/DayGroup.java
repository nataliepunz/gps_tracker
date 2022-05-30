package at.jku.se.gps_tracker.Group;
import at.jku.se.gps_tracker.model.AbstractTrack;
import at.jku.se.gps_tracker.model.Track;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;


public class DayGroup extends GroupTrack {

    private LocalDate date;


    public DayGroup(LocalDate date) {
        super();
        this.date = date;
        setName();
        super.group = "Day";
        super.xAxis = String.format("%02d",date.getMonthValue()) + "- " + String.format("%02d", date.getDayOfMonth()) ;
        super.year = date.getYear();
    }

    public DayGroup() {

    }

    @Override
    public LocalDate getDate() {
        return date;
    }


    public void setName(){
        super.setName(date.toString());
    }

    @Override
    public String toString() {
        return "DayGroup{" +
                "date=" + date +
                '}';
    }

    public GroupTrack match(Track element, ObservableList<GroupTrack> list, LocalDate day, int month, int year )
    {

        for (GroupTrack dg: groupList) {
            if (dg.getDate() == day) {
                return dg;

        }} return null;}

    @Override
    public ObservableList<GroupTrack> group(ObservableList<AbstractTrack> list) {

        super.groupList = FXCollections.observableArrayList();
        ObservableList<Track> tracks = FXCollections.observableArrayList();

        for (AbstractTrack at: list) {
            tracks.add((Track) at);
        }
        for (Track track: tracks) {
            LocalDate day = track.getDate();
            if (match(track, groupList, day, 0, 0, 0 ) != null)
                match(track, groupList, day, 0, 0, 0).add(track);
            else
                groupList.add(new DayGroup(day));
                addToLastElementInList(groupList, track);
        }
        return groupList;
    }
}
