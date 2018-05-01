package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessageColor;
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
    public void testExecuteAndPostProcess() throws TeammatesException {
        InstructorAttributes instructor1ofCourse1 =
                typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
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
                + "<span class=\"bold\">From:</span> 2012-01-31T22:00:00Z"
                + "<span class=\"bold\"> to</span> 2014-12-31T22:00:00Z<br>"
                + "<span class=\"bold\">Session visible from:</span> 2011-12-31T22:00:00Z<br>"
                + "<span class=\"bold\">Results visible from:</span> 1970-06-22T00:00:00Z<br>"
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

        ______TS("Error: Invalid parameters (invalid session name > 38 characters, invalid publish date)");

        String longFsName = StringHelperExtension.generateStringOfLength(39);
        params = createParamsCombinationForFeedbackSession(
                         instructor1ofCourse1.courseId, longFsName, 1);
        params[21] = "invalid publish date";
        a = getAction(params);
        pr = getShowPageResult(a);
        expectedString = getPageResultDestination(
                Const.ViewURIs.INSTRUCTOR_FEEDBACK_SESSIONS, true, "idOfInstructor1OfCourse1");
        assertEquals(expectedString, pr.getDestinationWithParams());
        assertTrue(pr.isError);

        expectedString = teammatesLog + "Servlet Action Failure : " + "\"" + longFsName + "\" "
                + "is not acceptable to TEAMMATES as a/an feedback session name because it is too long. "
                + "The value of a/an feedback session name should be no longer than 38 characters. "
                + "It should not be empty.<br>"
                + "The provided time for the responses to become visible is not acceptable to TEAMMATES as "
                + "it cannot be empty."
                + "|||/page/instructorFeedbackAdd";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        ______TS("Error: Overlap publish time with invalid parameters (invalid session name, > 38 characters)");

        longFsName = StringHelperExtension.generateStringOfLength(39);
        params = createParamsCombinationForFeedbackSession(
                instructor1ofCourse1.courseId, longFsName, 1);
        backDoorLogic.updateCourse(course.getId(), course.getName(), "Asia/Jerusalem");
        // After Sun, 25 Oct 2015, 01:59:59 AM: clocks fell back to Sun, 25 Oct 2015, 01:00:00 AM
        params[21] = "Sun, 25 Oct, 2015";
        params[23] = "1";
        a = getAction(params);
        pr = getShowPageResult(a);
        expectedString = getPageResultDestination(
                Const.ViewURIs.INSTRUCTOR_FEEDBACK_SESSIONS, true, "idOfInstructor1OfCourse1");
        assertEquals(expectedString, pr.getDestinationWithParams());
        assertTrue(pr.isError);

        String overlapWarning = String.format(Const.StatusMessages.AMBIGUOUS_LOCAL_DATE_TIME_OVERLAP,
                "time when the results will be visible",
                "Sun, 25 Oct 2015, 01:00 AM",
                "Sun, 25 Oct 2015, 01:00 AM IDT (UTC+0300)",
                "Sun, 25 Oct 2015, 01:00 AM IST (UTC+0200)",
                "Sun, 25 Oct 2015, 01:00 AM IDT (UTC+0300)");

        String lengthError = "\"" + longFsName + "\" "
                + "is not acceptable to TEAMMATES as a/an feedback session name because it is too long. "
                + "The value of a/an feedback session name should be no longer than 38 characters. "
                + "It should not be empty.";

        verifyStatusMessage(pr.getStatusToUser().get(0), overlapWarning, StatusMessageColor.WARNING);
        verifyStatusMessage(pr.getStatusToUser().get(1), lengthError, StatusMessageColor.DANGER);

        expectedString = teammatesLog + "Servlet Action Failure : " + lengthError + "|||/page/instructorFeedbackAdd";
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
                + "<span class=\"bold\">From:</span> 2012-01-31T22:00:00Z"
                + "<span class=\"bold\"> to</span> 2014-12-31T22:00:00Z<br>"
                + "<span class=\"bold\">Session visible from:</span> 1970-12-31T00:00:00Z<br>"
                + "<span class=\"bold\">Results visible from:</span> 2014-05-07T23:00:00Z<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: instructions>|||/page/instructorFeedbackAdd";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ADDED, rr.getStatusMessage());

        ______TS("DST time zone, gap end time");

        params = createParamsForTypicalFeedbackSession(
                         instructor1ofCourse1.courseId, "Course with DST time zone");
        backDoorLogic.updateCourse(course.getId(), course.getName(), "Asia/Jerusalem");
        // After Fri, 28 Mar 2014, 01:59:59 AM: clocks sprang forward to Fri, 28 Mar 2014, 03:00:00 AM
        params[9] = "Fri, 28 Mar, 2014";
        params[11] = "2";

        a = getAction(params);
        rr = getRedirectResult(a);

        expectedString = getPageResultDestination(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                instructor1ofCourse1.courseId,
                "Course+with+DST+time+zone",
                instructor1ofCourse1.googleId,
                false);
        assertEquals(expectedString, rr.getDestinationWithParams());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||New Feedback Session "
                + "<span class=\"bold\">(Course with DST time zone)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> created.<br>"
                + "<span class=\"bold\">From:</span> 2012-01-31T22:00:00Z"
                + "<span class=\"bold\"> to</span> 2014-03-28T00:00:00Z<br>"
                + "<span class=\"bold\">Session visible from:</span> 2011-12-31T22:00:00Z<br>"
                + "<span class=\"bold\">Results visible from:</span> 1970-06-22T00:00:00Z<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: instructions>|||"
                + "/page/instructorFeedbackAdd";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());
        expectedString = String.format(Const.StatusMessages.AMBIGUOUS_LOCAL_DATE_TIME_GAP,
                "end time", "Fri, 28 Mar 2014, 02:00 AM", "Fri, 28 Mar 2014, 03:00 AM IDT (UTC+0300)") + "<br>"
                + Const.StatusMessages.FEEDBACK_SESSION_ADDED;
        assertEquals(expectedString, rr.getStatusMessage());

        ______TS("Masquerade mode");

        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        params = createParamsCombinationForFeedbackSession(
                         instructor1ofCourse1.courseId, "masquerade session", 2);
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
                + "<span class=\"bold\">From:</span> 2012-01-31T22:00:00Z"
                + "<span class=\"bold\"> to</span> 2014-12-31T22:00:00Z<br>"
                + "<span class=\"bold\">Session visible from:</span> 2011-12-31T22:00:00Z<br>"
                + "<span class=\"bold\">Results visible from:</span> 1970-01-01T00:00:00Z<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: >|||/page/instructorFeedbackAdd";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedString, a.getLogMessage(), adminUserId);
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ADDED, rr.getStatusMessage());

        ______TS("Unsuccessful case: test null course ID parameter");
        params = new String[] {};

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
                typicalBundle.instructors.get("instructor1OfCourse1");

        String[] params =
                createParamsForTypicalFeedbackSession(
                        instructor1ofCourse1.courseId, "ifaat tca fs");

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
        verifyUnaccessibleWithoutModifyCoursePrivilege(params);

        // delete the sessions
        FeedbackSessionsLogic.inst().deleteFeedbackSessionCascade("ifaat tca fs", instructor1ofCourse1.courseId);
    }
}
