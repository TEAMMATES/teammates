package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.apache.commons.lang3.ArrayUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackEditSaveAction;
import teammates.ui.controller.RedirectResult;

public class InstructorFeedbackEditSaveActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
        
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor1ofCourse1 =
                dataBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = 
                dataBundle.feedbackSessions.get("session1InCourse1");
        
        ______TS("Not enough parameters");
        
        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
        verifyAssumptionFailure();
        //TODO make sure IFESA does assertNotNull for required parameters then uncomment
        //verifyAssumptionFailure(Const.ParamsNames.COURSE_ID, instructor1ofCourse1.courseId,
        //                        Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName);
        
        ______TS("success: Typical case");
        
        String[] params =
                createParamsForTypicalFeedbackSession(
                        instructor1ofCourse1.courseId, session.feedbackSessionName);
        
        InstructorFeedbackEditSaveAction a = getAction(params);
        RedirectResult rr = (RedirectResult) a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                rr.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EDITED, rr.getStatusMessage());
        
        String expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackEditSave|||instructorFeedbackEditSave|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.com|||Updated Feedback Session <span class=\"bold\">"
                + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]"
                + "</span> created.<br><span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012"
                + "<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br><span class=\"bold\">"
                + "Session visible from:</span> Sun Jan 01 00:00:00 UTC 2012<br><span class=\"bold\">"
                + "Results visible from:</span> Mon Jun 22 00:00:00 UTC 1970<br><br><span class=\"bold\">"
                + "Instructions:</span> <Text: instructions>|||/page/instructorFeedbackEditSave";
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("failure: invalid parameters");
        
        params[15] = "01/03/2012";
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=true",
                rr.getDestinationWithParams());
        
        assertEquals("The start time for this feedback session cannot be"
                     + " earlier than the time when the session will be visible.", rr.getStatusMessage());
        
        ______TS("success: Timzone with offset, 'never' show session, 'custom' show results");
        
        params = createParamsForTypicalFeedbackSession(
                        instructor1ofCourse1.courseId, session.feedbackSessionName);
        params[25] = "5.75";
        params[13] = Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_NEVER;
        params[19] = Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER;
        
        //remove instructions, grace period, start time to test null conditions
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                rr.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EDITED, rr.getStatusMessage());
        
        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackEditSave|||instructorFeedbackEditSave|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.com|||Updated Feedback Session <span class=\"bold\">"
                + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]"
                + "</span> created.<br><span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012"
                + "<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br><span class=\"bold\">"
                + "Session visible from:</span> Fri Nov 27 00:00:00 UTC 1970<br><span class=\"bold\">"
                + "Results visible from:</span> Fri Nov 27 00:00:00 UTC 1970<br><br><span class=\"bold\">"
                + "Instructions:</span> <Text: instructions>|||/page/instructorFeedbackEditSave";
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("success: atopen session visible time, custom results visible time, null timezone, null grace period");
        
        params = createParamsCombinationForFeedbackSession(
                instructor1ofCourse1.courseId, session.feedbackSessionName, 1);
        
        //remove grace period (first) and then time zone
        params = ArrayUtils.remove(params, 26);
        params = ArrayUtils.remove(params, 26);
        params = ArrayUtils.remove(params, 24);
        params = ArrayUtils.remove(params, 24);
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                rr.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EDITED, rr.getStatusMessage());
        
        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackEditSave|||instructorFeedbackEditSave|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.com|||Updated Feedback Session <span class=\"bold\">"
                + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]"
                + "</span> created.<br><span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012"
                + "<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br><span class=\"bold\">"
                + "Session visible from:</span> Thu Dec 31 00:00:00 UTC 1970<br><span class=\"bold\">"
                + "Results visible from:</span> Thu May 08 02:00:00 UTC 2014<br><br><span class=\"bold\">"
                + "Instructions:</span> <Text: instructions>|||/page/instructorFeedbackEditSave";
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("success: Masquerade mode, never release results, invalid timezone and graceperiod");
        
        gaeSimulation.loginAsAdmin("admin.user");

        params = createParamsForTypicalFeedbackSession(
                instructor1ofCourse1.courseId, session.feedbackSessionName);
        params[19] = Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_NEVER;
        params[25] = " ";
        params[27] = "12dsf";
        
        params = addUserIdToParams(instructor1ofCourse1.googleId, params);
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                rr.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EDITED, rr.getStatusMessage());
        
        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackEditSave|||instructorFeedbackEditSave|||true|||"
                + "Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.com|||Updated Feedback Session <span class=\"bold\">"
                + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]"
                + "</span> created.<br><span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012"
                + "<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br><span class=\"bold\">"
                + "Session visible from:</span> Sun Jan 01 00:00:00 UTC 2012<br><span class=\"bold\">"
                + "Results visible from:</span> Fri Nov 27 00:00:00 UTC 1970<br><br><span class=\"bold\">"
                + "Instructions:</span> <Text: instructions>|||/page/instructorFeedbackEditSave";
        assertEquals(expectedLogMessage, a.getLogMessage());
    }
    
    private InstructorFeedbackEditSaveAction getAction (String... params) throws Exception {
        return (InstructorFeedbackEditSaveAction) gaeSimulation.getActionObject(uri, params);
    }
}
