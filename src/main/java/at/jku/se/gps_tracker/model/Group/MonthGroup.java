package at.jku.se.gps_tracker.model.Group;

import at.jku.se.gps_tracker.model.Track;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Map;

public class MonthGroup extends GroupTrack {

    private Month month;
    private Year year;

    private Map<Month, List<Track>> tracksMonth;

    public Map<Month, List<Track>> getTracksMonth() {
        return tracksMonth;
    }

    public void setTracksMonth(Map<Month, List<Track>> tracksMonth) {
        this.tracksMonth = tracksMonth;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public Month getMonth() {
        return month;
    }

    public Year getYear() {return year; } //notwendig?

    @Override
    public String toString() {
        return "MonthGroup{" +
                "month=" + month +
                '}';
    }
}
