package at.jku.se.gps_tracker.model.Group;

import at.jku.se.gps_tracker.model.AbstractTrack;
import at.jku.se.gps_tracker.model.Track;

import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public abstract class GroupTrack extends AbstractTrack {


    protected static int year;
    private List<Track> tracks = new ArrayList<>();
    private int count = tracks.size();
    private String name;

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public Boolean compatible(Track track){

        //TODO: methode implementieren
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
            super.setAverageBPM((track.getAverageBPM()));
            super.setMaximumBPM(track.getMaximumBPM());
            super.setDuration(track.getDurationNormal());
            super.setElevation(track.getElevation());
        }
        else {
            super.distance += track.getDistance();
            super.setAverageBPM(newAverageBPM(track.getAverageBPM()));
            super.setMaximumBPM(newMaximumBPM(track.getMaximumBPM()));
            super.setDuration(super.getDurationNormal().plus(track.getDurationNormal()));
            super.setElevation(newElevation(track.getElevation()));
        }
    }

    public int getYear() {
       return year;
    }
    @Override
    public void setDistance(Double distance) {
        super.distance += distance;
    }

    public Duration newDuration (Duration duration) {

            Duration dur =  super.getDurationNormal();
            dur.plus(duration);

        return dur;
    }

    public double newElevation (double elevation) {
        double temp = elevation;
        for (Track t: tracks) {
            temp += t.getElevation();
        }
        return temp / tracks.size();
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
