package at.jku.se.gps_tracker.model;

import javafx.beans.property.*;
import java.time.Duration;

public abstract class AbstractTrack {
	private String name;
	private double distance;
	private Duration duration;
	private Duration pace;
	private double speed;
	private int averageBPM;
	private int maximumBPM;
	private double elevation;
	
	protected AbstractTrack() {

	}

	protected AbstractTrack(String name, double distance, Duration duration, Duration pace, double speed, double elevation) {
		this.name = name;
		this.distance = distance;
		this.duration = duration;
		this.pace = pace;
		this.speed = speed;
		this.elevation = elevation;
	}

	protected AbstractTrack(String name, double distance, Duration duration, Duration pace, double speed, int averageBPM, int maximumBPM, double elevation) {
		this(name, distance, duration, pace, speed, elevation);
		this.averageBPM = averageBPM;
		this.maximumBPM = maximumBPM;
	}

	public String getName() {
		return name;
	}

	public Double getDistance() {
		return doubleFormatter(distance);
	}

	public Duration getDuration() {
		return duration;
	}

	public Duration getPace() {
		return pace;
	}

	public Double getSpeed() {
		return doubleFormatter(speed);
	}

	public int getAverageBPM() {
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

	public void setPace(Duration pace) {
		this.pace = pace;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}
	
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
	
	public SimpleStringProperty getDurationProperty() {
		return new SimpleStringProperty(formatDuration(duration));
	}

	public SimpleStringProperty getPaceProperty() {
		return new SimpleStringProperty(formatDuration(pace));
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
		return new SimpleDoubleProperty(speed);
	}

	//zum Runden auf zwei Dezimalstellen
	private Double doubleFormatter(Double d) {
		return Math.floor(d * 100) / 100;
	}
}
