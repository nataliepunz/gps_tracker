package at.jku.se.gps_tracker.model.Group;

import at.jku.se.gps_tracker.model.Track;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DayGroup extends GroupTrack {

    private LocalDate date;

    private Map<LocalDate, List<Track>> tracksDay;

    public Map<LocalDate, List<Track>> getTracksDay() {
        return tracksDay;
    }

    public void setTracksDay(Map<LocalDate, List<Track>> tracks) {
        this.tracksDay = tracks;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DayGroup{" +
                "date=" + date +
                '}';
    }
}
