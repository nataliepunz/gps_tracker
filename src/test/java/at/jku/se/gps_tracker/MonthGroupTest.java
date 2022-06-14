package at.jku.se.gps_tracker;
import at.jku.se.gps_tracker.Group.GroupTrack;
import at.jku.se.gps_tracker.Group.MonthGroup;
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
 * test Month Group
 * @author Nuray
 *
 */

class MonthGroupTest {

    /**
     * variables that will be used for tests
     * @author Nuray
     */
    MonthGroup mg;
    Track track;
    Track track2;
    ObservableList<AbstractTrack> at = FXCollections.observableArrayList();
    ObservableList<GroupTrack> gt = FXCollections.observableArrayList();
    /**
     * performed before each test
     * @author Nuray
     */
    @BeforeEach
    void setup()  {
        mg = new MonthGroup(3, 2021);
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

        mg.add(track);
        gt.add(mg);
    }

    /**
     * checks if set name produces the expected result
     * @author Nuray
     */
    @Test
    void setNameTest() {
        assertEquals("März 2021", mg.getName());
    }

    /**
     * checks if toString produces the expected result
     * @author Nuray
     */
    @Test
    void  toStringTest() {
        assertEquals("MonthGroup{" +
                "month=" + 3 +
                '}', mg.toString());
    }

    /**
     * checks if getMonth gets the right week
     * @author Nuray
     */
    @Test
    void  getMonth() {
        assertEquals(3, mg.getMonth());
    }

    /**
     * checks if match correctly returns null, when no match is found
     * @author Nuray
     */
    @Test
    void  matchTest() {

        assertNull(mg.match(track2, gt));
    }

    /**
     * checks if group correctly groups groupTrack into MonthGroup list
     * @author Nuray
     */
    @Test
    void  groupTest() {
        MonthGroup newGroup = new MonthGroup(9, 2021);
        gt.add(newGroup);
        assertEquals(gt.toString(), (mg.group(at)).toString());
    }
}
