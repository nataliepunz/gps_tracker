package at.jku.se.gps_tracker.model;

import java.time.Duration;

public class TrackPoint extends AbstractTrack {
    
    public TrackPoint(String number, double distance, Duration duration, Duration pace, double speed, double elevation) {
		super(number,distance,duration,pace,speed,elevation);
	}
    
	public TrackPoint(String number, double distance, Duration duration, Duration pace, double speed, int averageBPM,
			int maximumBPM, double elevation) {
		super(number,distance,duration,pace,speed,averageBPM,maximumBPM,elevation);
	}
}
