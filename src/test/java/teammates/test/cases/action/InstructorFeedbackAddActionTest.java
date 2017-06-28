package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.StringHelperExtension;
import teammates.ui.controller.InstructorFeedbackAddAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

/**
 * SUT: {@link InstructorFeedbackAddAction}.
 */
public class InstructorFeedbackAddActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1ofCourse1 =
                dataBundle.instructors.get("instructor1OfCourse1");
        String expectedString = "";
        String teammatesLog = "TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||";

        ______TS("Not enough parameters");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
        verifyAssumptionFailure();
        //TODO make sure IFAA does assertNotNull for required parameters then uncomment
        //verifyAssumptionFailure(Const.ParamsNames.COURSE_ID, instructor1ofCourse1.courseId);

        ______TS("Typical case");

        String[] params = createParamsCombinationForFeedbackSession(
                                  instructor1ofCourse1.courseId, "ifaat tca fs", 0);

        InstructorFeedbackAddAction a = getAction(params);
        RedirectResult rr = getRedirectResult(a);

        expectedString = getPageResultDestination(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                instructor1ofCourse1.courseId,
                "ifaat+tca+fs",
                instructor1ofCourse1.googleId,
                false);
        assertEquals(expectedString, rr.getDestinationWithParams());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||New Feedback Session "
                + "<span class=\"bold\">(ifaat tca fs)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> created.<br>"
                + "<span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012"
                + "<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br>"
                + "<span class=\"bold\">Session visible from:</span> Sun Jan 01 00:00:00 UTC 2012<br>"
                + "<span class=\"bold\">Results visible from:</span> Mon Jun 22 00:00:00 UTC 1970<br>"
                + "<br><span class=\"bold\">Instructions:</span> "
                + "<Text: instructions>|||/page/instructorFeedbackAdd";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ADDED, rr.getStatusMessage());

        ______TS("Error: try to add the same session again");

        params = createParamsCombinationForFeedbackSession(
                         instructor1ofCourse1.courseId, "ifaat tca fs", 0);
        a = getAction(params);
        ShowPageResult pr = getShowPageResult(a);
        expectedString = getPageResultDestination(
                Const.ViewURIs.INSTRUCTOR_FEEDBACK_SESSIONS, true, "idOfInstructor1OfCourse1");
        assertEquals(expectedString, pr.getDestinationWithParams());
        assertTrue(pr.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EXISTS, pr.getStatusMessage());

        ______TS("Error: Invalid parameter (invalid sesssion name, > 38 characters)");

        String longFsName = StringHelperExtension.generateStringOfLength(39);
        params = createParamsCombinationForFeedbackSession(
                         instructor1ofCourse1.courseId, longFsName, 0);
        a = getAction(params);
        pr = getShowPageResult(a);
        expectedString = getPageResultDestination(
                Const.ViewURIs.INSTRUCTOR_FEEDBACK_SESSIONS, true, "idOfInstructor1OfCourse1");
        assertEquals(expectedString, pr.getDestinationWithParams());
        assertTrue(pr.isError);

        expectedString =
                teammatesLog + "Servlet Action Failure : " + "\"" + longFsName + "\" "
                + "is not acceptable to TEAMMATES as a/an feedback session name because it is too long. "
                + "The value of a/an feedback session name should be no longer than 38 characters. "
                + "It should not be empty.|||/page/instructorFeedbackAdd";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        ______TS("Add course with extra space (in middle and trailing)");

        params = createParamsCombinationForFeedbackSession(
                         instructor1ofCourse1.courseId, "Course with extra  space ", 1);

        a = getAction(params);
        rr = getRedirectResult(a);

        expectedString = getPageResultDestination(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                instructor1ofCourse1.courseId,
                "Course+with+extra+space",
                instructor1ofCourse1.googleId,
                false);
        assertEquals(expectedString, rr.getDestinationWithParams());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||New Feedback Session "
                + "<span class=\"bold\">(Course with extra space)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> created.<br>"
                + "<span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012"
                + "<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br>"
                + "<span class=\"bold\">Session visible from:</span> Thu Dec 31 00:00:00 UTC 1970<br>"
                + "<span class=\"bold\">Results visible from:</span> Thu May 08 02:00:00 UTC 2014<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: instructions>|||/page/instructorFeedbackAdd";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ADDED, rr.getStatusMessage());

        ______TS("timezone with minute offset");

        params = createParamsCombinationForFeedbackSession(
                         instructor1ofCourse1.courseId, "Course with minute offset timezone", 2);
        params[25] = "5.5";

        a = getAction(params);
        rr = getRedirectResult(a);

        expectedString = getPageResultDestination(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                instructor1ofCourse1.courseId,
                "Course+with+minute+offset+timezone",
                instructor1ofCourse1.googleId,
                false);
        assertEquals(expectedString, rr.getDestinationWithParams());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||New Feedback Session "
                + "<span class=\"bold\">(Course with minute offset timezone)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> created.<br>"
                + "<span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012"
                + "<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br>"
                + "<span class=\"bold\">Session visible from:</span> Fri Nov 27 00:00:00 UTC 1970<br>"
                + "<span class=\"bold\">Results visible from:</span> Fri Nov 27 00:00:00 UTC 1970<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: instructions>|||"
                + "/page/instructorFeedbackAdd";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ADDED, rr.getStatusMessage());

        ______TS("Masquerade mode");

        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        params = createParamsCombinationForFeedbackSession(
                         instructor1ofCourse1.courseId, "masquerade session", 3);
        params = addUserIdToParams(instructor1ofCourse1.googleId, params);

        a = getAction(params);
        rr = getRedirectResult(a);

        expectedString = getPageResultDestination(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                instructor1ofCourse1.courseId,
                "masquerade+session",
                instructor1ofCourse1.googleId,
                false);
        assertEquals(expectedString, rr.getDestinationWithParams());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||"
                + "Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||New Feedback Session "
                + "<span class=\"bold\">(masquerade session)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> created.<br>"
                + "<span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012"
                + "<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br>"
                + "<span class=\"bold\">Session visible from:</span> Sun Jan 01 00:00:00 UTC 2012<br>"
                + "<span class=\"bold\">Results visible from:</span> Thu Jan 01 00:00:00 UTC 1970<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: >|||/page/instructorFeedbackAdd";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedString, a.getLogMessage(), adminUserId);
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ADDED, rr.getStatusMessage());

        ______TS("Unsuccessful case: test null course ID parameter");
        params = new String[]{};

        try {
            a = getAction(params);
            getRedirectResult(a);
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER, Const.ParamsNames.COURSE_ID),
                         e.getMessage());
        }
        // remove the sessions that were added
        FeedbackSessionsLogic.inst().deleteFeedbackSessionsForCourseCascade(instructor1ofCourse1.courseId);
    }

    @Override
    protected InstructorFeedbackAddAction getAction(String... params) {
        return (InstructorFeedbackAddAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    protected String getPageResultDestination(
            String parentUri, String courseId, String fsname, String userId, boolean isError) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.COURSE_ID, courseId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.FEEDBACK_SESSION_NAME, fsname);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.USER_ID, userId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.ERROR, Boolean.toString(isError));
        return pageDestination;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1ofCourse1 =
                dataBundle.instructors.get("instructor1OfCourse1");

        String[] params =
                createParamsForTypicalFeedbackSession(
                        instructor1ofCourse1.courseId, "ifaat tca fs");

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
        verifyUnaccessibleWithoutModifyCoursePrivilege(params);

        // delete the sessions
        FeedbackSessionsLogic.inst().deleteFeedbackSessionCascade("ifaat tca fs", instructor1ofCourse1.courseId);
    }
}
