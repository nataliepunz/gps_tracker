package at.jku.se.gps_tracker.model;

import java.time.Duration;

public interface Visualization {
	String getName();
	double getDistance();
	Duration getDuration();
	Duration getPace();
	double getSpeed();
	int getAverageBPM();
	int getMaxBPM();
	int getElevation();
}
