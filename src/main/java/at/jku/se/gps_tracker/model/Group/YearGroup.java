package at.jku.se.gps_tracker.model.Group;

import at.jku.se.gps_tracker.model.Track;

import java.time.Year;
import java.util.List;
import java.util.Map;

public class YearGroup extends GroupTrack {

    private Year year;

    private Map<Year, List<Track>> tracksYear;

    public Map<Year, List<Track>> getTracksYear() {
        return tracksYear;
    }

    public void setTracksYear(Map<Year, List<Track>> tracksYear) {
        this.tracksYear = tracksYear;
    }


    @Override
    public String toString() {
        return "YearGroup{" +
                "year=" + year +
                '}';
    }
}
