package at.jku.se.gps_tracker.model.Group;

import java.time.Year;

public class WeekGroup extends GroupTrack {

    private int week;


    public WeekGroup(int week)
    {
        super();
        this.week = week;
        setName();
    }


    public void setName() {
        super.setName("W:" + week);
    }

    @Override
    public int getWeek() {
        return week;
    }


    @Override
    public String toString() {
        return "WeekGroup{" +
                "week=" + week +
                '}';
    }

}
