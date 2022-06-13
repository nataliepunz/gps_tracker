package at.jku.se.gps_tracker;
import at.jku.se.gps_tracker.Group.GroupTrack;
import at.jku.se.gps_tracker.Group.WeekGroup;
import at.jku.se.gps_tracker.model.AbstractTrack;
import at.jku.se.gps_tracker.model.Track;
import at.jku.se.gps_tracker.model.TrackPoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test Group Track
 * @author Nuray
 *
 */

class WeekGroupTest {


    WeekGroup wg;
    Track track;
    Track track2;
    ObservableList<AbstractTrack> at = FXCollections.observableArrayList();
    ObservableList<GroupTrack> gt = FXCollections.observableArrayList();

    @BeforeEach
    void setup()  {
        wg = new WeekGroup(10, 2021);
        track = new Track.TrackBuilder("testParentDirectory", "testFileName", "testName", LocalDate.of(2021,3,9), LocalTime.now().truncatedTo(ChronoUnit.SECONDS))
                .distance(12345)
                .duration(Duration.ofSeconds(100))
                .averageBPM(100)
                .maximumBPM(600)
                .elevation(123)
                .trackPoints(Arrays.asList(new TrackPoint("1", 0, null, 0)))
                .build();

         track2 = new Track.TrackBuilder("testParentDirectory", "testFileName", "testName", LocalDate.of(2021,9,3), LocalTime.now().truncatedTo(ChronoUnit.SECONDS))
                .distance(12345)
                .duration(Duration.ofSeconds(100))
                .averageBPM(100)
                .maximumBPM(600)
                .elevation(123)
                .trackPoints(Arrays.asList(new TrackPoint("1", 0, null, 0)))
                .build();

            at.add(track);
            at.add(track2);

              wg.add(track);
              gt.add(wg);


    }

    @Test
    void getFirstDayOfWeekTest() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.MARCH, 8);
        assertEquals(calendar.getTime(), wg.getFirstDayOfWeek(10).getTime());
    }

    @Test
    void getLastDayOfWeekTest() {

        Calendar calendar = wg.getFirstDayOfWeek(10);
        calendar.set(2021, Calendar.MARCH, 14);
        assertEquals(calendar.getTime(), wg.getLastDayOfWeek(10).getTime());
    }

    @Test
    void setNameTest() {
        assertEquals("W: 10 (08.03 - 14.03.2021)", wg.getName());
    }

    @Test
    void  toStringTest() {
        assertEquals("WeekGroup{" +
            "week=" + 10 +
                    '}', wg.toString());
    }

    @Test
    void  getWeekTest() {
        assertEquals(10, wg.getWeek());

}

    @Test
    void  matchTest() {

        assertNull(wg.match(track2, gt, track2.getDate(), 35,0, 2021));

    }

    @Test
    void  groupTest() {
        WeekGroup newGroup = new WeekGroup(35, 2021);
        gt.add(newGroup);
        assertEquals(gt.toString(), (wg.group(at)).toString());

    }





}
