package at.jku.se.gps_tracker.model;

import java.time.Duration;

public class TrackPoint {
	private int number;
	private double distance;
    private Duration duration;
    private double pace;
    private double speed;
    private int averageBPM;
    private int maxBPM;
    private double elevation;
    
    public TrackPoint(int number, double distance, double elevation) {
    	this.number=number;
    	this.distance=distance;
    	this.elevation=elevation;
    }
    
    public TrackPoint(int number, double distance, Duration duration, double elevation) {
		this(number, distance,elevation);
		this.duration = duration;
		if(distance==0 || duration.getSeconds()==0) {
			this.speed = 0;
			this.pace = 0;
		} else {
			this.speed = (double) distance/duration.getSeconds();
			this.pace = (double) duration.getSeconds()/distance;
		}
	}

	public TrackPoint(int number, double distance, Duration duration, int averageBPM,
			int maxBPM, double elevation) {
		this(number,distance,duration,elevation);
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
	public double getElevation() {
		return elevation;
	}
}
