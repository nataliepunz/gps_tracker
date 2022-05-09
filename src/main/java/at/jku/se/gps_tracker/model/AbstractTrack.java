package at.jku.se.gps_tracker.model;

import javafx.beans.property.*;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public abstract class AbstractTrack {
	
	protected static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	private String name;
	private double distance;
	private Duration duration;
	private int averageBPM;
	private int maximumBPM;
	private double elevation;
	
	protected AbstractTrack() {

	}

	protected AbstractTrack(String name, double distance, Duration duration, double elevation) {
		this.name = name;
		this.distance = distance;
		this.duration = duration;
		this.elevation = elevation;
	}

	protected AbstractTrack(String name, double distance, Duration duration, int averageBPM, int maximumBPM, double elevation) {
		this(name, distance, duration, elevation);
		this.averageBPM = averageBPM;
		this.maximumBPM = maximumBPM;
	}

	public String getName() {
		return name;
	}

	public Double getDistance() {
		return doubleFormatter(distance);
	}

	public Double getDuration() {
		return Double.valueOf(duration.toMinutes());
	}
	
	public Duration getDurationNormal() {
		return duration;
	}

	public Duration getPace() {
		return calculatePace();
	}

	public Double getSpeed() {
		return doubleFormatter(calculateSpeed());
	}

	public int getAverageBPM() {
		return averageBPM;
	}

	public int getHeartbeat() {
		return averageBPM;
	}

	public void setAverageBPM(int averageBPM) {
		this.averageBPM = averageBPM;
	}

	public int getMaximumBPM() {
		return maximumBPM;
	}

	public void setMaximumBPM(int maximumBPM) {
		this.maximumBPM = maximumBPM;
	}

	public Double getElevation() {
		return doubleFormatter(elevation);
	}

	//Quelle: https://stackoverflow.com/a/266846/5750106
	private static String formatDuration(Duration duration) {
		long seconds = duration.getSeconds();
		long absSeconds = Math.abs(seconds);
		String positive = String.format(
				"%d:%02d:%02d",
				absSeconds / 3600,
				(absSeconds % 3600) / 60,
				absSeconds % 60);
		return seconds < 0 ? "-" + positive : positive;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}
	
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
	
	public SimpleStringProperty getDurationProperty() {
		return new SimpleStringProperty(formatDuration(duration));
	}

	public SimpleStringProperty getPaceProperty() {
		return new SimpleStringProperty(formatDuration(calculatePace()));
	}

	public String nameProperty() {
		return name;
	}

	public Double distanceProperty() {
		return doubleFormatter(distance);
	}
	
	public SimpleDoubleProperty getDistanceProperty() {
		return new SimpleDoubleProperty(Math.round(distance));
	}
	public SimpleDoubleProperty getSpeedProperty() {
		return new SimpleDoubleProperty(calculateSpeed());
	}

	//zum Runden auf zwei Dezimalstellen
	private Double doubleFormatter(Double d) {
		return Math.floor(d * 100) / 100;
	}
	
	private Duration calculatePace() {
		if(distance==0 || duration.getSeconds()==0) {
			return Duration.ofSeconds(0);
		} else {
			return Duration.ofSeconds((long) (duration.getSeconds()/distance));
		}
	}
	
	private double calculateSpeed() {
		if(distance==0 || duration.getSeconds()==0) {
			return 0;
		} else {
			return ((distance/duration.getSeconds()) * (60*60)/1000);
		}
	}
}
