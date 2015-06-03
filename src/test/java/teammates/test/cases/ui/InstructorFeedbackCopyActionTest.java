package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackCopyAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackCopyActionTest extends BaseActionTest {
    DataBundle dataBundle;    
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_COPY;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        String[] params = new String[]{
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "Copied Session",
                Const.ParamsNames.COPIED_COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
        verifyUnaccessibleWithoutModifyCoursePrivilege(params);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        //TODO: find a way to test status message from session
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String expectedString = "";
        
        ______TS("Not enough parameters");
        
        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
        verifyAssumptionFailure();
        //TODO make sure IFAA does assertNotNull for required parameters then uncomment
        //verifyAssumptionFailure(Const.ParamsNames.COURSE_ID, instructor1ofCourse1.courseId);
        
        
        ______TS("Typical case");
        
        String[] params = new String[]{
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "Copied Session",
                Const.ParamsNames.COPIED_COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };
        
        InstructorFeedbackCopyAction a = getAction(params);
        RedirectResult rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                         + "?courseid=" + instructor1ofCourse1.courseId
                         + "&fsname=Copied+Session"
                         + "&user=" + instructor1ofCourse1.googleId
                         + "&error=false";
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        expectedString =
                "TEAMMATESLOG|||instructorFeedbackCopy|||instructorFeedbackCopy|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||New Feedback Session "
                + "<span class=\"bold\">(Copied Session)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> created.<br>"
                + "<span class=\"bold\">From:</span> Sun Apr 01 23:59:00 UTC 2012"
                + "<span class=\"bold\"> to</span> Sun Apr 30 23:59:00 UTC 2017<br>"
                + "<span class=\"bold\">Session visible from:</span> Wed Mar 28 23:59:00 UTC 2012<br>"
                + "<span class=\"bold\">Results visible from:</span> Mon May 01 23:59:00 UTC 2017<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: Please please fill in the following questions.>|||/page/instructorFeedbackCopy";
        assertEquals(expectedString, a.getLogMessage());
        
        
        ______TS("Error: Trying to copy with existing feedback session name");
        
        params = new String[]{
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "Second feedback session",
                Const.ParamsNames.COPIED_COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };
        
        a = getAction(params);
        ShowPageResult pageResult = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals("/jsp/instructorFeedbacks.jsp?error=true&user=idOfInstructor1OfCourse1",
                     pageResult.getDestinationWithParams());
        
        expectedString =
                "TEAMMATESLOG|||instructorFeedbackCopy|||instructorFeedbackCopy|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Servlet Action Failure : Trying to create a Feedback Session that exists: Second feedback session/idOfTypicalCourse1|||"
                + "/page/instructorFeedbackCopy";
        assertEquals(expectedString, a.getLogMessage());
        
        
        ______TS("Error: Trying to copy with invalid feedback session name");
        
        params = new String[]{
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "",
                Const.ParamsNames.COPIED_COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };
        
        a = getAction(params);
        pageResult = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals("/jsp/instructorFeedbacks.jsp?error=true&user=idOfInstructor1OfCourse1",
                     pageResult.getDestinationWithParams());
        
        expectedString =
                "TEAMMATESLOG|||instructorFeedbackCopy|||instructorFeedbackCopy|||true|||Instructor|||"
                + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Servlet Action Failure : \"\" is not acceptable to TEAMMATES as feedback session name because it is empty."
                + " The value of feedback session name should be no longer than 38 characters. It should not be empty.|||"
                + "/page/instructorFeedbackCopy";
        assertEquals(expectedString, a.getLogMessage());
        
        
        ______TS("Masquerade mode");
        gaeSimulation.loginAsAdmin("admin.user");

        params = new String[]{
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "Second copied feedback session",
                Const.ParamsNames.COPIED_COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "Second feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };
        params = addUserIdToParams(instructor1ofCourse1.googleId, params);
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                         + "?courseid=" + instructor1ofCourse1.courseId
                         + "&fsname=Second+copied+feedback+session"
                         + "&user=" + instructor1ofCourse1.googleId
                         + "&error=false";
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        expectedString =
                "TEAMMATESLOG|||instructorFeedbackCopy|||instructorFeedbackCopy|||true|||"
                + "Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||New Feedback Session "
                + "<span class=\"bold\">(Second copied feedback session)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> created.<br>"
                + "<span class=\"bold\">From:</span> Sat Jun 01 23:59:00 UTC 2013"
                + "<span class=\"bold\"> to</span> Thu Apr 28 23:59:00 UTC 2016<br>"
                + "<span class=\"bold\">Session visible from:</span> Wed Mar 20 23:59:00 UTC 2013<br>"
                + "<span class=\"bold\">Results visible from:</span> Fri Apr 29 23:59:00 UTC 2016<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: Please please fill in the following questions.>|||/page/instructorFeedbackCopy";
        assertEquals(expectedString, a.getLogMessage());
    }
    
    private InstructorFeedbackCopyAction getAction (String... params) throws Exception {
        return (InstructorFeedbackCopyAction) gaeSimulation.getActionObject(uri, params);
    }
}
