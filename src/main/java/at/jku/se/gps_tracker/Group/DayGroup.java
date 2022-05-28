package at.jku.se.gps_tracker.Group;
import java.time.LocalDate;


public class DayGroup extends GroupTrack {

    private LocalDate date;


    public DayGroup(LocalDate date) {
        super();
        this.date = date;
        setName();
        super.group = "Day";
    }

    @Override
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
