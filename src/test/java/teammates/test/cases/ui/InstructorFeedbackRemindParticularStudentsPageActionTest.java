package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackRemindParticularStudentsPageAction;
import teammates.ui.controller.InstructorFeedbackRemindParticularStudentsPageData;

public class InstructorFeedbackRemindParticularStudentsPageActionTest extends
        BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("session1InCourse1");
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        ______TS("Not enough parameters");
        verifyAssumptionFailure();
        
        ______TS("Typical case");
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.feedbackSessionName
        };
        
        InstructorFeedbackRemindParticularStudentsPageAction a = getAction(submissionParams);
        AjaxResult r = getAjaxResult(a);
        
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorFeedbackRemindParticularStudentsPageData pageData = 
                (InstructorFeedbackRemindParticularStudentsPageData) r.data;
        assertEquals(5, pageData.responseStatus.noResponse.size());
        assertFalse(pageData.responseStatus.noResponse.contains("student1InCourse1@gmail.tmt"));
        assertFalse(pageData.responseStatus.noResponse.contains("student2InCourse1@gmail.tmt"));
        assertFalse(pageData.responseStatus.noResponse.contains("student3InCourse1@gmail.tmt"));
        assertTrue(pageData.responseStatus.noResponse.contains("student4InCourse1@gmail.tmt"));
        assertTrue(pageData.responseStatus.noResponse.contains("student5InCourse1@gmail.tmt"));
        assertFalse(pageData.responseStatus.noResponse.contains("instructor1@course1.tmt"));
        assertTrue(pageData.responseStatus.noResponse.contains("instructor2@course1.tmt"));
        assertTrue(pageData.responseStatus.noResponse.contains("instructor3@course1.tmt"));
        assertTrue(pageData.responseStatus.noResponse.contains("helper@course1.tmt"));
    }
    
    private InstructorFeedbackRemindParticularStudentsPageAction getAction(String... params)
            throws Exception {

        return (InstructorFeedbackRemindParticularStudentsPageAction) (gaeSimulation
                .getActionObject(uri, params));

    }
}
