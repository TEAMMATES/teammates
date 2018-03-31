package teammates.test.cases.datatransfer;

import java.time.Instant;
import java.util.ArrayList;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.SessionAttributes;
import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link SessionAttributes}.
 */
public class SessionAttributesTest extends BaseTestCase {

    @Test
    public void testSort() {
        ArrayList<SessionAttributes> testList = new ArrayList<>();
        ArrayList<SessionAttributes> expected = new ArrayList<>();

        Instant time1 = TimeHelper.parseInstant("2014-01-01 12:00 AM +0000");
        Instant time2 = TimeHelper.parseInstant("2014-02-01 12:00 AM +0000");
        Instant time3 = TimeHelper.parseInstant("2014-03-01 12:00 AM +0000");

        SessionAttributes s1 = new MiniFeedback(time1, time2, "Session 1");
        SessionAttributes s2 = new MiniEval(time2, time3, "Session 2");
        SessionAttributes s3 = new MiniFeedback(time1, time2, "Session 3");
        SessionAttributes s4 = new MiniEval(time1, time3, "Session 4");
        SessionAttributes s5 = new MiniFeedback(time2, time3, "Session 5");

        testList.add(s1);
        testList.add(s2);
        testList.add(s3);
        testList.add(s4);
        testList.add(s5);

        expected.add(s1);
        expected.add(s3);
        expected.add(s4);
        expected.add(s2);
        expected.add(s5);

        testList.sort(SessionAttributes.ASCENDING_ORDER);
        for (int i = 0; i < testList.size(); i++) {
            assertEquals(expected.get(i), testList.get(i));
        }

        testList.clear();
        testList.add(s1);
        testList.add(s2);
        testList.add(s3);
        testList.add(s4);
        testList.add(s5);

        expected.clear();
        expected.add(s2);
        expected.add(s5);
        expected.add(s4);
        expected.add(s1);
        expected.add(s3);

        testList.sort(SessionAttributes.DESCENDING_ORDER);
        for (int i = 0; i < testList.size(); i++) {
            assertEquals(expected.get(i), testList.get(i));
        }
    }

    private static class MiniEval implements SessionAttributes {
        public Instant startTime;
        public Instant endTime;
        public String name;

        MiniEval(Instant startTime, Instant endTime, String name) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.name = name;
        }

        @Override
        public Instant getSessionStartTime() {
            return this.startTime;
        }

        @Override
        public Instant getSessionEndTime() {
            return this.endTime;
        }

        @Override
        public String getSessionName() {
            return this.name;
        }

    }

    private static class MiniFeedback implements SessionAttributes {

        public Instant startTime;
        public Instant endTime;
        public String name;

        MiniFeedback(Instant startTime, Instant endTime, String name) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.name = name;
        }

        @Override
        public Instant getSessionStartTime() {
            return this.startTime;
        }

        @Override
        public Instant getSessionEndTime() {
            return this.endTime;
        }

        @Override
        public String getSessionName() {
            return this.name;
        }
    }
}
