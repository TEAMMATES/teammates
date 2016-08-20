package teammates.test.cases.automated;

import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.api.Logic;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

public class CourseJoinRemindEmailTaskQueueTest extends BaseComponentUsingTaskQueueTestCase {
    private static final Logic logic = new Logic();
    
    @SuppressWarnings("serial")
    public static class CourseJoinRemindEmailTaskQueueCallback extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.COURSE_ID));
            assertNotNull(paramMap.get(ParamsNames.COURSE_ID));

            assertTrue(paramMap.containsKey(ParamsNames.STUDENT_EMAIL));
            assertNotNull(paramMap.get(ParamsNames.STUDENT_EMAIL));
            
            CourseJoinRemindEmailTaskQueueCallback.taskCount++;
             
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }
    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.tearDown();
        gaeSimulation.setupWithTaskQueueCallbackClass(CourseJoinRemindEmailTaskQueueCallback.class);
        gaeSimulation.resetDatastore();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }
    
    @Test
    public void testCourseJoinRemindEmail() {
        
        CourseJoinRemindEmailTaskQueueCallback.resetTaskCount();

        ______TS("Send course join remind email");
        
        int counter = 0;

        while (counter != 10) {
            CourseJoinRemindEmailTaskQueueCallback.resetTaskCount();
            logic.sendRegistrationInviteForCourse("idOfUnregisteredCourse");
            if (CourseJoinRemindEmailTaskQueueCallback.verifyTaskCount(2)) {
                break;
            }
            counter++;
        }

        assertEquals(2, CourseJoinRemindEmailTaskQueueCallback.taskCount);
    }
}

