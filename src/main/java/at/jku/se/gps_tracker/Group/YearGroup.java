package at.jku.se.gps_tracker.Group;


public class YearGroup extends GroupTrack {

    public YearGroup(int year)
    {
        super();
        super.year = year;
        setName();
        super.group = "Year";
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
