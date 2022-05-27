package at.jku.se.gps_tracker.model;

import java.time.Duration;

public class TrackPoint extends AbstractTrack {

	public TrackPoint(String number, double distance, Duration duration, double elevation) {
		super(number,distance,duration,elevation);
	}

	public TrackPoint(String number, double distance, Duration duration, int averageBPM,
					  int maximumBPM, double elevation) {
		super(number,distance,duration,averageBPM,maximumBPM,elevation);
	}
}