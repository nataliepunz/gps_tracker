package at.jku.se.gps_tracker.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import at.jku.se.gps_tracker.data.DataBaseOperations;

public class Track extends AbstractTrack {

	private LocalDate date;
	private LocalTime startTime;
	private List<TrackPoint> trackPoints;
	private String parentDirectory;
	private DataBaseOperations conn;

	public Track(String parentDirectory, String name, LocalDate date, LocalTime startTime, double distance, Duration duration, Duration pace, double speed, double elevation, List<TrackPoint> trackPoints) {
		super(name,distance,duration,pace,speed,elevation);
		this.date = date;
		this.startTime = startTime;
		this.trackPoints = trackPoints;
		this.parentDirectory=parentDirectory;
	}

	public Track(DataBaseOperations conn, String parentDirectory, String name, LocalDate date, LocalTime startTime, double distance, Duration duration, Duration pace, double speed, int averageBPM, int maximumBPM, double elevation) {
		super(name,distance,duration,pace,speed,averageBPM,maximumBPM,elevation);
		this.conn=conn;
		this.date = date;
		this.startTime = startTime;
		this.trackPoints = null;
		this.parentDirectory=parentDirectory;
	}
	
	public Track(String parentDirectory, String name, LocalDate date, LocalTime startTime, double distance, Duration duration, Duration pace, double speed, int averageBPM, int maximumBPM, double elevation, List<TrackPoint> trackPoints) {
		super(name,distance,duration,pace,speed,averageBPM,maximumBPM,elevation);
		this.date = date;
		this.startTime = startTime;
		this.trackPoints = trackPoints;
		this.parentDirectory=parentDirectory;
	}

	public Track() {

	}

	public LocalDate getDate() {
		return date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public List<TrackPoint> getTrackPoints() {
		if(trackPoints==null) {
			return conn.getTrackPoints(this);
		} else {
			return trackPoints;
		}
	}
		

	@Override
	public String toString() {
		return this.getName().toString();
	}

	public String getParentDirectory() {
		return this.parentDirectory;
	}

}

