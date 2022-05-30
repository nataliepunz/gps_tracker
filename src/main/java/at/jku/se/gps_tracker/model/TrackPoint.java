package at.jku.se.gps_tracker.model;

import java.time.Duration;

public class TrackPoint extends AbstractTrack {

	//for SideTable
	protected Duration pace;
	protected double speed;

	public TrackPoint(String number, double distance, Duration duration, double elevation) {
		super(number,distance,duration,elevation);
	}

	public TrackPoint(){

	}

	public TrackPoint(String number, double distance, Duration duration, int averageBPM,
					  int maximumBPM, double elevation) {
		super(number,distance,duration,averageBPM,maximumBPM,elevation);
	}

	public TrackPoint(String number, double distance, Duration duration, Duration pace, double speed, int averageBPM, int maximumBPM, double elevation) {
		super(number,distance,duration, averageBPM,maximumBPM,elevation);
		//
		this.pace = pace;
		this.speed = speed;
	}
}