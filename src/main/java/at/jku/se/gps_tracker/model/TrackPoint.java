package at.jku.se.gps_tracker.model;

import java.time.Duration;

public class TrackPoint {
	private int number;
	private double distance;
    private Duration duration;
    private Duration pace;
    private double speed;
    private int averageBPM;
    private int maxBPM;
    private double elevation;
     
   
    public TrackPoint(int number, double distance, Duration duration, double pace, double speed, double elevation) {
		this.number=number;
		this.distance=distance;
		this.duration = duration;
		this.pace=Duration.ofSeconds((long)pace);
		this.speed=speed;
		this.elevation=elevation;
	}
    
	public TrackPoint(int number, double distance, Duration duration, double pace, double speed, int averageBPM,
			int maxBPM, double elevation) {
		this(number,distance,duration,pace,speed,elevation);
		this.averageBPM = averageBPM;
		this.maxBPM = maxBPM;
	}
	
	public int getNumber() {
		return number;
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
	public double getElevation() {
		return elevation;
	}
}
