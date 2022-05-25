package at.jku.se.gps_tracker.model.Group;

import at.jku.se.gps_tracker.model.Track;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DayGroup extends GroupTrack {

    private LocalDate date;

    public DayGroup(LocalDate date) {
        super();
        this.date = date;
        setName();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setName(){
        super.setName(date.toString());
    }

    @Override
    public String toString() {
        return "DayGroup{" +
                "date=" + date +
                '}';
    }
}
