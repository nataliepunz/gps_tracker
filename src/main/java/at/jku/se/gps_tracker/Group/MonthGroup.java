package at.jku.se.gps_tracker.Group;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class MonthGroup extends GroupTrack {

    protected int month;

    public MonthGroup(int month, int year)
    {
        super();
        this.month = month;
        super.xAxis = month;
        super.year = year;
        setName();
        super.group = "Month";
    }

    @Override
    public int getMonth() {
        return month;
    }


    public void setName(){
        String monthName =  Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE ,Locale.GERMANY);
        super.setName(monthName + " " + super.getYear());
    }

    @Override
    public String toString() {
        return "MonthGroup{" +
                "month=" + month +
                '}';
    }


}
