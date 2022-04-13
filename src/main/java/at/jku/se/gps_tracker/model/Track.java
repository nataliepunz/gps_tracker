package at.jku.se.gps_tracker.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Track implements Visualization {
	
	//private String activity;
	private String name;
	private LocalDate date;
	private LocalTime startTime;
	private double distance;
    private Duration duration;
    private Duration pace;
    private double speed;
    private int averageBPM;
    private int maxBPM;
    private double elevation;
    private List<TrackPoint> trackPoints;
    
    public Track(/*String activity, */String name, LocalDate date, LocalTime startTime, double distance, Duration duration, double pace, double speed, double elevation, List<TrackPoint> trackPoints) {
		//this.activity = activity;
		this.name = name;
		this.date = date;
		this.startTime = startTime;
		this.distance = distance;
		this.duration = duration;
		this.elevation = elevation;
		this.trackPoints = trackPoints;
		this.speed = speed;
		this.pace = Duration.ofSeconds((long) pace);
	}
    
    public Track(/*String activity, */String name, LocalDate date, LocalTime startTime, double distance, Duration duration, double pace, double speed, int averageBPM, int maxBPM, double elevation, List<TrackPoint> trackPoints) {
		this(/* activity, */name, date, startTime, distance, duration, pace, speed, elevation, trackPoints);
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
	public Duration getPace() {
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
		return (int) elevation;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

    }

