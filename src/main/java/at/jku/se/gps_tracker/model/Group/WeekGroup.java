package at.jku.se.gps_tracker.model.Group;

import java.time.Year;

public class WeekGroup extends GroupTrack {

    private Week week;
    private Year year;

    public Week getWeek() {
        return week;
    }

    public Year getYear() {return year; } //notwendig?

    @Override
    public String toString() {
        return "WeekGroup{" +
                "week=" + week +
                '}';
    }
}
