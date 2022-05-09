package at.jku.se.gps_tracker.model.Group;

import at.jku.se.gps_tracker.model.Track;

import java.time.Duration;
import java.time.Year;
import java.util.List;

public abstract class GroupTrack {

    private String name;
    private double distance;
    private Duration duration;
    private Duration pace;
    private double speed;
    private int averageBPM;
    private int maxBPM;
    private double elevation;

    private List<Track> tracks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getPace() {
        return pace;
    }

    public void setPace(Duration pace) {
        this.pace = pace;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getAverageBPM() {
        return averageBPM;
    }

    public void setAverageBPM(int averageBPM) {
        this.averageBPM = averageBPM;
    }

    public int getMaxBPM() {
        return maxBPM;
    }

    public void setMaxBPM(int maxBPM) {
        this.maxBPM = maxBPM;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

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

    public Boolean contains(Track track){
        return tracks.contains(track);
    }

    public void remove(Track track){
        tracks.remove(track);
    }

    public void add(Track track){
        tracks.add(track);
    }

    public Year getYear() {
        //TODO: methode implementieren
        return null;
    }
}
