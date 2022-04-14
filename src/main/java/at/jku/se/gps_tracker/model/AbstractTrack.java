package at.jku.se.gps_tracker.model;

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
	
	protected AbstractTrack(String name, double distance, Duration duration, Duration pace, double speed, double elevation) {
		this.name=name;
		this.distance=distance;
		this.duration = duration;
		this.pace=pace;
		this.speed=speed;
		this.elevation=elevation;
	}
	
	protected AbstractTrack(String name, double distance, Duration duration, Duration pace, double speed, int averageBPM, int maximumBPM, double elevation) {
		this(name,distance,duration,pace,speed,elevation);
		this.averageBPM=averageBPM;
		this.maximumBPM=maximumBPM;
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
		return pace;
	}
	public double getSpeed() {
		return speed;
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
	
}
