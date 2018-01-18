package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.SessionAttributes;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link SessionAttributes}.
 */
public class SessionAttributesTest extends BaseTestCase {

    @Test
    public void testSort() {
        ArrayList<SessionAttributes> testList = new ArrayList<>();
        ArrayList<SessionAttributes> expected = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        cal.set(2014, 0, 1);
        Date time1 = cal.getTime();

        cal.set(2014, 1, 1);
        Date time2 = cal.getTime();

        cal.set(2014, 2, 1);
        Date time3 = cal.getTime();

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
        public Date startTime;
        public Date endTime;
        public String name;

        MiniEval(Date startTime, Date endTime, String name) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.name = name;
        }

        @Override
        public Date getSessionStartTime() {
            return this.startTime;
        }

        @Override
        public Date getSessionEndTime() {
            return this.endTime;
        }

        @Override
        public String getSessionName() {
            return this.name;
        }

    }

    private static class MiniFeedback implements SessionAttributes {

        public Date startTime;
        public Date endTime;
        public String name;

        MiniFeedback(Date startTime, Date endTime, String name) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.name = name;
        }

        @Override
        public Date getSessionStartTime() {
            return this.startTime;
        }

        @Override
        public Date getSessionEndTime() {
            return this.endTime;
        }

        @Override
        public String getSessionName() {
            return this.name;
        }
    }
}
