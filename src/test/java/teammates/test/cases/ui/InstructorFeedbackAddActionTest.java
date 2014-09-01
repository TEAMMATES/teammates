package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.ui.controller.InstructorFeedbackAddAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackAddActionTest extends BaseActionTest {
    
    private final DataBundle dataBundle = getTypicalDataBundle();    
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor1ofCourse1 =
                dataBundle.instructors.get("instructor1OfCourse1");
        
        ______TS("Not enough parameters");
        
        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
        verifyAssumptionFailure();
        //TODO make sure IFAA does assertNotNull for required parameters then uncomment
        //verifyAssumptionFailure(Const.ParamsNames.COURSE_ID, instructor1ofCourse1.courseId);
        
        ______TS("Typical case");
        
        String[] params =
                createParamsCombinationForFeedbackSession(
                        instructor1ofCourse1.courseId, "ifaat tca fs", 0);
        
        InstructorFeedbackAddAction a = getAction(params);
        RedirectResult rr = (RedirectResult) a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=ifaat+tca+fs"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                rr.getDestinationWithParams());
        
        String expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||" +
                "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||" +
                "instr1@course1.tmt|||New Feedback Session <span class=\"bold\">(ifaat tca fs)</span>" +
                " for Course <span class=\"bold\">[idOfTypicalCourse1]</span> created." +
                "<br><span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012" +
                "<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br>" +
                "<span class=\"bold\">Session visible from:</span> Sun Jan 01 00:00:00 UTC 2012<br>" +
                "<span class=\"bold\">Results visible from:</span> Mon Jun 22 00:00:00 UTC 1970<br>" +
                "<br><span class=\"bold\">Instructions:</span> <Text: instructions>|||/page/instructorFeedbackAdd";
        assertEquals(expectedLogMessage, a.getLogMessage());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ADDED, rr.getStatusMessage());
        
        ______TS("Error: try to add the same session again");
        
        params = 
                createParamsCombinationForFeedbackSession(
                        instructor1ofCourse1.courseId, "ifaat tca fs", 0);
        a = getAction(params);
        ShowPageResult pr = (ShowPageResult) a.executeAndPostProcess();
        assertEquals(
                Const.ViewURIs.INSTRUCTOR_FEEDBACKS+"?error=true&user=idOfInstructor1OfCourse1", 
                pr.getDestinationWithParams());
        assertEquals(true, pr.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EXISTS, pr.getStatusMessage());

        ______TS("Add course with trailing space");
        
        params =
                createParamsCombinationForFeedbackSession(
                        instructor1ofCourse1.courseId, "Course with trailing space ", 1);
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=Course+with+trailing+space"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                rr.getDestinationWithParams());
        
        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||" +
                "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||" +
                "instr1@course1.tmt|||New Feedback Session <span class=\"bold\">(Course with trailing space)</span>" +
                " for Course <span class=\"bold\">[idOfTypicalCourse1]</span> created." +
                "<br><span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012" +
                "<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br>" +
                "<span class=\"bold\">Session visible from:</span> Thu Dec 31 00:00:00 UTC 1970<br>" +
                "<span class=\"bold\">Results visible from:</span> Thu May 08 02:00:00 UTC 2014<br>" +
                "<br><span class=\"bold\">Instructions:</span> <Text: instructions>|||/page/instructorFeedbackAdd";
        assertEquals(expectedLogMessage, a.getLogMessage());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ADDED, rr.getStatusMessage());
        
        ______TS("timezone with minute offset");
        
        params = createParamsCombinationForFeedbackSession(
                        instructor1ofCourse1.courseId, "Course with minute offset timezone", 2);
        params[25] = "5.5";
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=Course+with+minute+offset+timezone"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                rr.getDestinationWithParams());
        
        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||" +
                "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||" +
                "instr1@course1.tmt|||New Feedback Session <span class=\"bold\">(Course with minute offset timezone)</span>" +
                " for Course <span class=\"bold\">[idOfTypicalCourse1]</span> created." +
                "<br><span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012" +
                "<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br>" +
                "<span class=\"bold\">Session visible from:</span> Fri Nov 27 00:00:00 UTC 1970<br>" +
                "<span class=\"bold\">Results visible from:</span> Fri Nov 27 00:00:00 UTC 1970<br>" +
                "<br><span class=\"bold\">Instructions:</span> <Text: &lt;script&lt;script&gt;&gt;test&lt;&#x2f;script&lt;&#x2f;script&gt;&g...>|||/page/instructorFeedbackAdd";
        assertEquals(expectedLogMessage, a.getLogMessage());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ADDED, rr.getStatusMessage());
        
        ______TS("Masquerade mode");
        
        gaeSimulation.loginAsAdmin("admin.user");

        params = createParamsCombinationForFeedbackSession(
                        instructor1ofCourse1.courseId, "masquerade session", 3);
        params = addUserIdToParams(instructor1ofCourse1.googleId, params);
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=masquerade+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                rr.getDestinationWithParams());
        
        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||" +
                "Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||" +
                "instr1@course1.tmt|||New Feedback Session <span class=\"bold\">(masquerade session)</span>" +
                " for Course <span class=\"bold\">[idOfTypicalCourse1]</span> created." +
                "<br><span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012" +
                "<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br>" +
                "<span class=\"bold\">Session visible from:</span> Sun Jan 01 00:00:00 UTC 2012<br>" +
                "<span class=\"bold\">Results visible from:</span> Thu Jan 01 00:00:00 UTC 1970<br>" +
                "<br><span class=\"bold\">Instructions:</span> <Text: >|||/page/instructorFeedbackAdd";
        assertEquals(expectedLogMessage, a.getLogMessage());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ADDED, rr.getStatusMessage());
        
        // remove the sessions that were added
        FeedbackSessionsLogic.inst().deleteFeedbackSessionsForCourse(instructor1ofCourse1.courseId);
    }
    
    private InstructorFeedbackAddAction getAction (String... params) throws Exception {
        return (InstructorFeedbackAddAction) gaeSimulation.getActionObject(uri, params);
    }
}
