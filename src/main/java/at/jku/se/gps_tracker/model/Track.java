package at.jku.se.gps_tracker.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Track extends AbstractTrack {
	
	private LocalDate date;
	private LocalTime startTime;
    private List<TrackPoint> trackPoints;
    
    public Track(String name, LocalDate date, LocalTime startTime, double distance, Duration duration, Duration pace, double speed, double elevation, List<TrackPoint> trackPoints) {
		super(name,distance,duration,pace,speed,elevation);
    	this.date = date;
		this.startTime = startTime;
		this.trackPoints = trackPoints;
	}
    
    public Track(String name, LocalDate date, LocalTime startTime, double distance, Duration duration, Duration pace, double speed, int averageBPM, int maximumBPM, double elevation, List<TrackPoint> trackPoints) {
		super(name,distance,duration,pace,speed,averageBPM,maximumBPM,elevation);
		this.date = date;
		this.startTime = startTime;
		this.trackPoints = trackPoints;
	}
    	
	public LocalDate getDate() {
		return date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public List<TrackPoint> getTrackPoints() {
		return trackPoints;
	}

	@Override
	public String toString() {
		return this.getName();
	}

    }

