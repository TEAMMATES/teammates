package teammates.test.cases.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionAttributes;
import teammates.test.cases.BaseTestCase;

public class SessionAttributesTest extends BaseTestCase {
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testSort() {
        ArrayList<SessionAttributes> testList = new ArrayList<SessionAttributes>();
        ArrayList<SessionAttributes> expected = new ArrayList<SessionAttributes>();
        
        Calendar cal = Calendar.getInstance();
        
        cal.set(2014, 0, 1);
        Date time1 = cal.getTime();
        
        cal.set(2014, 1, 1);
        Date time2 = cal.getTime();
        
        cal.set(2014, 2, 1);
        Date time3 = cal.getTime();
        
        SessionAttributes s1, s2, s3, s4, s5;
        s1 = new miniFeedback(time1, time2, "Session 1");
        s2 = new miniEval(time2, time3, "Session 2");
        s3 = new miniFeedback(time1, time2, "Session 3");
        s4 = new miniEval(time1, time3, "Session 4");
        s5 = new miniFeedback(time2, time3, "Session 5");
        
        testList.add(s1); testList.add(s2); testList.add(s3);
        testList.add(s4); testList.add(s5);
        
        expected.add(s1); expected.add(s3); expected.add(s4);
        expected.add(s2); expected.add(s5);
        
        Collections.sort(testList, SessionAttributes.ASCENDING_ORDER);
        for(int i = 0; i < testList.size(); i++){
            AssertJUnit.assertEquals(expected.get(i), testList.get(i));
        }
        
        testList.clear();
        testList.add(s1); testList.add(s2); testList.add(s3);
        testList.add(s4); testList.add(s5);
        
        expected.clear();
        expected.add(s2); expected.add(s5); expected.add(s4);
        expected.add(s1); expected.add(s3);
        
        Collections.sort(testList, SessionAttributes.DESCENDING_ORDER);
        for(int i = 0; i < testList.size(); i++){
            AssertJUnit.assertEquals(expected.get(i), testList.get(i));
        }
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }
    
    private class miniEval implements SessionAttributes{
        public Date startTime, endTime;
        public String name;
        
        public miniEval(Date startTime, Date endTime, String name){
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
    
    private class miniFeedback implements SessionAttributes{
        
        public Date startTime, endTime;
        public String name;
        
        public miniFeedback(Date startTime, Date endTime, String name){
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