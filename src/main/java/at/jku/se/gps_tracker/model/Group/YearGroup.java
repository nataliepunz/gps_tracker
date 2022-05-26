package at.jku.se.gps_tracker.model.Group;

import at.jku.se.gps_tracker.model.Track;


public class YearGroup extends GroupTrack {

    public YearGroup(int year)
    {
        super();
        super.year = year;
        setName();
    }

    public void setName() {
        super.setName("Jahr:" + year);
    }
    @Override
    public String toString() {
        return "YearGroup{" +
                "year=" + year +
                '}';
    }


}
