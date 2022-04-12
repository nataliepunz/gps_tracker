package at.jku.se.gps_tracker.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;

public class Track implements Visualization {
	
	//private String activity;
	private String name;
	private LocalDate date;
	private LocalTime startTime;
	private double distance;
    private Duration duration;
    private double pace;
    private double speed;
    private int averageBPM;
    private int maxBPM;
    private int elevation;
    private List<TrackPoint> trackPoints;
    
    public Track(/*String activity, */String name, LocalDate date, LocalTime startTime, double distance, Duration duration, int elevation, List<TrackPoint> trackPoints) {
		//this.activity = activity;
		this.name = name;
		this.date = date;
		this.startTime = startTime;
		this.distance = distance;
		this.duration = duration;
		this.elevation = elevation;
		this.trackPoints = trackPoints;
	}
    
    public Track(/*String activity, */String name, LocalDate date, LocalTime startTime, double distance, Duration duration, int averageBPM, int maxBPM, int elevation, List<TrackPoint> trackPoints) {
		this(/* activity, */name, date, startTime, distance, duration, elevation, trackPoints);
		this.averageBPM = averageBPM;
		this.maxBPM = maxBPM;
	}
    /*
	public String getActivity() {
		return activity;
	}
	*/
	public String getName() {
		return name;
	}

	public LocalDate getDate() {
		return date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public double getDistance() {
		return distance;
	}
	public Duration getDuration() {
		return duration;
	}
	public double getPace() {
		return pace;
	}
	public double getSpeed() {
		return speed;
	}
	public int getAverageBPM() {
		return averageBPM;
	}
	public int getMaxBPM() {
		return maxBPM;
	}
	public int getElevation() {
		return elevation;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

    }

