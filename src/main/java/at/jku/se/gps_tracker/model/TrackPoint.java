package at.jku.se.gps_tracker.model;

import java.time.Duration;
/**
 * class to save TrackPoint object
 * extends from AbstratTrack
 * @author Ozan
 */
public class TrackPoint extends AbstractTrack {

	/**
	 * constructor for a TrackPoint without averageBPM and maxmimumBPM, like for GPX-Tracks
	 * @author Ozan
	 * @param number
	 * @param distance
	 * @param duration
	 * @param elevation
	 */
	public TrackPoint(String number, double distance, Duration duration, double elevation) {
		super(number,distance,duration,elevation);
	}

	/**
	 * constructor for a TrackPoint with averageBPM and maxmimumBPM, like for TCX-Tracks
	 * @author Ozan
	 * @param number
	 * @param distance
	 * @param duration
	 * @param averageBPM
	 * @param maximumBPM
	 * @param elevation
	 */
	public TrackPoint(String number, double distance, Duration duration, int averageBPM,
					  int maximumBPM, double elevation) {
		super(number,distance,duration,averageBPM,maximumBPM,elevation);
	}

	public TrackPoint(){

	}

	//for SideTable
	protected Duration pace;
	protected double speed;

	public TrackPoint(String number, double distance, Duration duration, Duration pace, double speed, int averageBPM, int maximumBPM, double elevation) {
		super(number,distance,duration, averageBPM,maximumBPM,elevation);
		//
		this.pace = pace;
		this.speed = speed;
	}
}