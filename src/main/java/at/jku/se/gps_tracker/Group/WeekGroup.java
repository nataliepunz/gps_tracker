package at.jku.se.gps_tracker.Group;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WeekGroup extends GroupTrack {

    protected int week;


    public WeekGroup(int week, int year)
    {
        super();
        this.week = week;
        super.year = year;
        super.xAxis = week;
        setName();
        super.group = "Week";
    }
    public Calendar getFirstDayOfWeek(int week) {

        int year = super.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.setWeekDate(year, week, Calendar.MONDAY);

        return calendar;

    }

    public Calendar getLastDayOfWeek(int week) {

        Calendar calendar = getFirstDayOfWeek(week);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return calendar;

    }

    public void setName() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");

        String weekStr = Integer.toString(week);

        if (week <10)
        {weekStr = String.format("%02d", week);}

        super.setName("W: " + weekStr + " (" + sdf.format(getFirstDayOfWeek(week).getTime()) + " - " + sdf.format(getLastDayOfWeek(week).getTime()) + "."+ getYear() + ")");
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