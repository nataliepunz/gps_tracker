package at.jku.se.gps_tracker.Group;

import at.jku.se.gps_tracker.model.AbstractTrack;
import at.jku.se.gps_tracker.model.Track;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.Locale;

public class MonthGroup extends GroupTrack {

    protected int month;

    public MonthGroup(int month, int year)
    {
        super();
        this.month = month;
        super.xAxis = ""+ month;
        super.year = year;
        setName();
        super.group = "Month";
    }

    public MonthGroup() {

    }

    @Override
    public int getMonth() {
        return month;
    }


    public void setName(){
        String monthName =  Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE ,Locale.GERMANY);
        super.setName(monthName + " " + super.getYear());
    }

    @Override
    public String toString() {
        return "MonthGroup{" +
                "month=" + month +
                '}';
    }

    public GroupTrack match(Track element, ObservableList<GroupTrack> list )
    {
        int month = element.getDate().getMonthValue();
        int year = element.getDate().getYear();
        for (GroupTrack mg: list) {
            if (mg.getMonth() == month && mg.getYear() == year) {
               return mg;
            }

        } return null;}

    @Override
    public ObservableList<GroupTrack> group(ObservableList<AbstractTrack> list) {


        super.groupList = FXCollections.observableArrayList();
        ObservableList<Track> tracks = FXCollections.observableArrayList();

        for (AbstractTrack at : list) {
            tracks.add((Track) at);

        }

        for (Track track : tracks) {

            int month = track.getDate().getMonthValue();
            int year = track.getDate().getYear();

            if (match(track, groupList) != null) {
                match(track, groupList).add(track);
            } else {
                groupList.add(new MonthGroup(month, year));
                addToLastElementInList(groupList, track);
            }
        }
        Comparator<GroupTrack> comparator = Comparator.comparingInt(GroupTrack::getYear).thenComparing(GroupTrack::getMonth);
        FXCollections.sort(groupList, comparator);
        return groupList;

    }}
