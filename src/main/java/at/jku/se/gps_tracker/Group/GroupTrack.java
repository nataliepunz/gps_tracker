package at.jku.se.gps_tracker.Group;

import at.jku.se.gps_tracker.model.AbstractTrack;
import at.jku.se.gps_tracker.model.Track;
import javafx.collections.ObservableList;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class GroupTrack extends AbstractTrack {


    protected int year;
    protected List<AbstractTrack> tracks = new ArrayList<>();
    private int count;
    private String name;
    protected double speed;
    protected Duration pace;

    ObservableList<GroupTrack> groupList;

    protected String xAxis; //dient für die vergleichsfunktionalität
    String group; // dient für die group funktionalität


    public int getWeek()
    {
        return 0;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void add(Track track){
        tracks.add(track);
        count = tracks.size();

        if (tracks.size() <2)
        {
            super.distance += track.getDistance();
            super.averageBPM = track.getAverageBPM();
            super.maximumBPM = track.getMaximumBPM();
            super.duration = track.getDuration();
            super.elevation = track.getElevation();
            this.speed = track.getSpeed();
            this.pace = Duration.ofMinutes(track.getPace().toMinutes());
        }
        else {
            super.distance += track.getDistance();
            super.averageBPM = newAverageBPM(track.getAverageBPM());
            super.maximumBPM = newMaximumBPM(track.getMaximumBPM());
            super.duration = super.getDuration().plus(track.getDuration());
            super.elevation = newElevation(track.getElevation());
            this.speed = newSpeed(track.getSpeed());
            this.pace = Duration.ofMinutes(newPace(track.getPace()));
        }
    }

    public int getYear() {
       return year;
    }


    public void setDistance(Double distance) {
        super.distance += distance;
    }

    public double newSpeed (Double speed) {
        double temp = speed;
        for (AbstractTrack t: tracks) {
            temp += t.getSpeed();
        }
        return temp / tracks.size();
    }

    public double newElevation (double elevation) {
        double temp = elevation;
        for (AbstractTrack t: tracks) {
            temp += t.getElevation();
        }
        return temp / tracks.size();
    }

    public long newPace (Duration pace) {
        Duration temp = pace;
        for (AbstractTrack t: tracks) {
            temp = temp.plus(t.getPace());
        }
        return temp.toMinutes() / tracks.size();
    }

    public int newAverageBPM (int averageBPM) {
        int avg = averageBPM;
        for (AbstractTrack t: tracks) {
            avg += t.getAverageBPM();
        }
        return avg / tracks.size();
    }
    public int newMaximumBPM (int maxBPM) {
        int max = maxBPM;
        for (AbstractTrack t: tracks) {
            if (t.getMaximumBPM() > max )
                max = t.getMaximumBPM();
        }
        return max;
    }

    public String getGroup() {
        return group;
    }

    @Override
    public int getCount(){
        return count;
    }

    public LocalDate getDate() {
        return null;
    }

    public int getMonth() {
        return 0;
    }

    public String getxAxis() {
        return xAxis;
    }

    public ObservableList<GroupTrack> group (ObservableList<AbstractTrack> list)
    {
        return null;
    }

    public GroupTrack match(Track element, ObservableList<GroupTrack> list, LocalDate day, int week, int month, int year){
        return null;
    }

    //gets the last object of a list and adds something into the list of the element
    public void addToLastElementInList (ObservableList<GroupTrack> list, Track track) {
        list.get(list.size() - 1).add(track);
    }
}
