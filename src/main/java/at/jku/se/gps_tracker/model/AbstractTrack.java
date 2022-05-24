package at.jku.se.gps_tracker.model;

import javafx.beans.property.*;
import java.time.Duration;

public abstract class AbstractTrack {
		
	private final String name;
	private final double distance;
	private final Duration duration;
	private final int averageBPM;
	private final int maximumBPM;
	private final double elevation;
	
	protected AbstractTrack() {
		this.name = null;
		this.distance = 0;
		this.duration = Duration.ofSeconds(0);
		this.averageBPM = 0;
		this.maximumBPM = 0;
		this.elevation = 0;
	}

	protected AbstractTrack(String name, double distance, Duration duration, int averageBPM, int maximumBPM, double elevation) {
		this.name = name;
		this.distance = distance;
		this.duration = duration;
		this.averageBPM = averageBPM;
		this.maximumBPM = maximumBPM;
		this.elevation = elevation;
	}

	protected AbstractTrack(String name, double distance, Duration duration, double elevation) {
		this(name, distance, duration, 0, 0, elevation);
	}

	public String getName() {
		return name;
	}

	public double getDistance() {
		return distance;
	}
	
	public Duration getDuration() {
		return duration;
	}

	public Duration getPace() {
		return calculatePace();
	}

	public double getSpeed() {
		return calculateSpeed();
	}

	public int getAverageBPM() {
		return averageBPM;
	}

	public int getMaximumBPM() {
		return maximumBPM;
	}

	public double getElevation() {
		return elevation;
	}
		
	public SimpleDoubleProperty getDistanceProperty() {
		return new SimpleDoubleProperty(doubleFormatter(distance));
	}
	
	public SimpleStringProperty getDurationProperty() {
		return new SimpleStringProperty(formatDuration(duration));
	}

	public SimpleStringProperty getPaceProperty() {
		return new SimpleStringProperty(formatDuration(calculatePace()));
	}
	
	public SimpleDoubleProperty getSpeedProperty() {
		return new SimpleDoubleProperty(doubleFormatter(calculateSpeed()));
	}
	
	public SimpleDoubleProperty getElevationProperty() {
		return new SimpleDoubleProperty(doubleFormatter(elevation));
	}
	
	//zum Runden auf zwei Dezimalstellen
	private double doubleFormatter(double number) {
		return (double) Math.round(number * 100) / 100;
	}
	
	private Duration calculatePace() {
		if(distance==0 || duration.getSeconds()==0) {
			return Duration.ofSeconds(0);
		} else {
			double minutes = ((double)duration.getSeconds()/60)/(distance/1000);
			Duration pace = Duration.ofMinutes((long) minutes);
			pace = pace.plusSeconds((long) ((minutes%1)*60));
			return pace;
		}
	}
	
	private double calculateSpeed() {
		if(distance==0 || duration.getSeconds()==0) {
			return 0;
		} else {
			return ((distance/duration.getSeconds()) * 3.6);
		}
	}
	
	//Quelle: https://stackoverflow.com/a/266846/5750106
	private String formatDuration(Duration duration) {
		long seconds = duration.getSeconds();
		long absSeconds = Math.abs(seconds);
		String positive = String.format(
				"%d:%02d:%02d",
				absSeconds / 3600,
				(absSeconds % 3600) / 60,
				absSeconds % 60);
		return seconds < 0 ? "-" + positive : positive;
	}
}
