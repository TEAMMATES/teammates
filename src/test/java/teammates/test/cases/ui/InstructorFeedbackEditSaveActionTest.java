package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import java.security.InvalidParameterException;

import org.apache.commons.lang3.ArrayUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.ui.controller.InstructorFeedbackEditSaveAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackEditSaveActionTest extends BaseActionTest {

    DataBundle dataBundle;
        
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams =
                createParamsForTypicalFeedbackSession(fs.courseId, fs.feedbackSessionName);
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);        
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
        
        ______TS("success 1: Typical case");
        
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
                        + "&message=The+feedback+session+has+been+updated."
                        + "&error=false",
                rr.getDestinationWithParams());
        
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
        
        ______TS("failure 1: invalid parameters");
        
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
                        + "&message=" 
                        + String.format(
                                FieldValidator.TIME_FRAME_ERROR_MESSAGE, 
                                FieldValidator.START_TIME_FIELD_NAME, 
                                FieldValidator.FEEDBACK_SESSION_NAME, 
                                FieldValidator.SESSION_VISIBLE_TIME_FIELD_NAME).replace(" ", "+")
                        + "&error=true",
                rr.getDestinationWithParams());
        
        ______TS("success 2: Timzone with offset, 'never' show session, 'custom' show results");
        
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
                        + "&message=The+feedback+session+has+been+updated."
                        + "&error=false",
                rr.getDestinationWithParams());
        
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
        
        ______TS("success 3: atopen session visible time, custom results visible time, null timezone, null grace period");
        
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
                        + "&message=The+feedback+session+has+been+updated."
                        + "&error=false",
                rr.getDestinationWithParams());
        
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
        
        ______TS("success 4: Masquerade mode, never release results, invalid timezone and graceperiod");
        
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
                        + "&message=The+feedback+session+has+been+updated."
                        + "&error=false",
                rr.getDestinationWithParams());
        
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
