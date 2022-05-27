package at.jku.se.gps_tracker.model.Group;

import at.jku.se.gps_tracker.model.AbstractTrack;
import at.jku.se.gps_tracker.model.Track;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public abstract class GroupTrack extends AbstractTrack {


    protected static int year;
    private List<Track> tracks = new ArrayList<>();
    private int count;
    private String name;

    private double speed;
    private Duration pace;


    public List<Track> getTracks() {
        return tracks;
    }

    public Boolean compatible(Track track){
        return true;
    }

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

    public Boolean contains(Track track){
        return tracks.contains(track);
    }

    public void remove(Track track){
        tracks.remove(track);
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
            speed = track.getSpeed();
            pace = track.getPace();
        }
        else {
            super.distance += track.getDistance();
            super.averageBPM = newAverageBPM(track.getAverageBPM());
            super.maximumBPM = newMaximumBPM(track.getMaximumBPM());
            super.duration = super.getDuration().plus(track.getDuration());
            super.elevation = newElevation(track.getElevation());
            speed = newSpeed(track.getSpeed());
            pace = Duration.ofMinutes(newPace(track.getPace()));
        }
    }


    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    public void setPace(Duration pace)
    {
        this.pace = pace;
    }
    public int getYear() {
       return year;
    }


    public void setDistance(Double distance) {
        super.distance += distance;
    }

    public double newSpeed (Double speed) {
        double temp = speed;
        for (Track t: tracks) {
            temp += t.getSpeed();
        }
        return temp / tracks.size();
    }

    public double newElevation (double elevation) {
        double temp = elevation;
        for (Track t: tracks) {
            temp += t.getElevation();
        }
        return temp / tracks.size();
    }

    public long newPace (Duration pace) {
        Duration temp = pace;
        for (Track t: tracks) {
            temp.plus(t.getPace());
        }
        return temp.toMinutes() / tracks.size();
    }

    public int newAverageBPM (int averageBPM) {
        int avg = averageBPM;
        for (Track t: tracks) {
            avg += t.getAverageBPM();
        }
        return avg / tracks.size();
    }
    public int newMaximumBPM (int maxBPM) {
        int max = 0;
        for (Track t: tracks) {
            if (t.getMaximumBPM() > max )
                max = t.getMaximumBPM();
        }
        return max;
    }

    public int getCount(){
        return count;
    }

}
