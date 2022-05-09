package at.jku.se.gps_tracker.model;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class Track extends AbstractTrack {

	private LocalDate date;
	private LocalTime startTime;
	private List<TrackPoint> trackPoints;
	private String parentDirectory;
	
	private Track(TrackBuilder trackBuilder) {
		super(trackBuilder.name,trackBuilder.distance,trackBuilder.duration,trackBuilder.averageBPM,trackBuilder.maximumBPM,trackBuilder.elevation);
		this.date=trackBuilder.trackDate;
		this.startTime = trackBuilder.trackTime;
		this.parentDirectory=trackBuilder.parentDirectory;
		this.trackPoints = trackBuilder.trackPoints;
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
		return this.trackPoints;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public String getParentDirectory() {
		return this.parentDirectory;
	}

	public void setTrackPoints(List<TrackPoint> trackpoints) {
		this.trackPoints=trackpoints;
	}
	
	public static class TrackBuilder{
		private String name;
		private String parentDirectory;
		private LocalDate trackDate;
		private LocalTime trackTime;
		private double distance;
		private Duration duration;
		private int averageBPM;
		private int maximumBPM;
		private double elevation;
		private List<TrackPoint> trackPoints;
		
		public TrackBuilder (String parentDirectory, String name, Instant trackTimeDate) {
			this.name=name;
			this.parentDirectory=parentDirectory;
			this.trackDate=LocalDate.ofInstant(trackTimeDate, ZoneId.systemDefault());
			this.trackTime=LocalTime.ofInstant(trackTimeDate, ZoneId.systemDefault());
		}
		
		public TrackBuilder (String parentDirectory, String name, LocalDate trackDate, LocalTime trackTime) {
			this.name=name;
			this.parentDirectory=parentDirectory;
			this.trackDate=trackDate;
			this.trackTime=trackTime;
		}
		
		public TrackBuilder distance(double distance) {
			this.distance=distance;
			return this;
		}
		
		public TrackBuilder duration(Duration duration) {
			this.duration=duration;
			return this;
		}
		
		public TrackBuilder averageBPM(int averageBPM) {
			this.averageBPM=averageBPM;
			return this;
		}
		
		public TrackBuilder maximumBPM(int maximumBPM) {
			this.maximumBPM=maximumBPM;
			return this;
		}
		
		public TrackBuilder elevation(double elevation) {
			this.elevation=elevation;
			return this;
		}
		
		public TrackBuilder trackPoints(List<TrackPoint> trackPoints) {
			this.trackPoints=trackPoints;
			return this;
		}
		
		public Track build() {
			return new Track(this);
		}
		
	}
	
}

