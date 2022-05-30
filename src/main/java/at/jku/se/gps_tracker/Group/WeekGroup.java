package at.jku.se.gps_tracker.Group;


import at.jku.se.gps_tracker.model.AbstractTrack;
import at.jku.se.gps_tracker.model.Track;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;

public class WeekGroup extends GroupTrack {

    protected int week;


    public WeekGroup(int week, int year)
    {
        super();
        this.week = week;
        super.year = year;
        super.xAxis = "" + week;
        setName();
        super.group = "Week";
    }

    public WeekGroup() {

    }

    public Calendar getFirstDayOfWeek(int week) {

        int year = super.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.setWeekDate(year, week, Calendar.MONDAY);

        return calendar;

    }

    public Calendar getLastDayOfWeek(int week) {

        Calendar calendar = getFirstDayOfWeek(week);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return calendar;

    }

    public void setName() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");

        String weekStr = Integer.toString(week);

        if (week <10)
        {weekStr = String.format("%02d", week);}

        super.setName("W: " + weekStr + " (" + sdf.format(getFirstDayOfWeek(week).getTime()) + " - " + sdf.format(getLastDayOfWeek(week).getTime()) + "."+ getYear() + ")");
    }

    @Override
    public int getWeek() {
        return week;
    }


    @Override
    public String toString() {
        return "WeekGroup{" +
                "week=" + week +
                '}';
    }

    public GroupTrack match(Track element, ObservableList<GroupTrack> list, LocalDate day, int week, int month, int year)
    {
        for (GroupTrack wg: groupList) {
            if (wg.getWeek() == week && wg.getYear() == year) {
              return wg;
            }


    } return null;}

    @Override
    public ObservableList<GroupTrack> group(ObservableList<AbstractTrack> list) {

        super.groupList = FXCollections.observableArrayList();
        ObservableList<Track> tracks = FXCollections.observableArrayList();

        for (AbstractTrack at: list) {
            tracks.add((Track) at);
        }
        for (Track track: tracks) {

            int week = track.getDate().get(WeekFields.of(Locale.GERMANY).weekOfWeekBasedYear());
            int year = track.getDate().getYear();
            if (match(track,groupList, null, week, 0, year) != null)
            {
                match(track,groupList, null, week, 0, year).add(track);
            }
            else {
                groupList.add(new WeekGroup(week, year));
                groupList.get(groupList.size() - 1).add(track);
            }
        }
        Comparator<GroupTrack> comparator = Comparator.comparingInt(GroupTrack::getYear).thenComparing(GroupTrack::getWeek);
        FXCollections.sort(groupList, comparator);
        return groupList;
    }



}
