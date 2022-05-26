package at.jku.se.gps_tracker.model.Group;

import at.jku.se.gps_tracker.model.Track;

import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MonthGroup extends GroupTrack {

    private int month;

    public MonthGroup(int month, int year)
    {
        super();
        this.month = month;
        super.year = year;
        setName();
    }

    public void setMonth(int month) {
        this.month = month;
    }

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
