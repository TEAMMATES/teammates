package teammates.test.cases.action;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorFeedbackCopyAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackCopyAction}.
 */
public class InstructorFeedbackCopyActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_COPY;
    }

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        super.prepareTestData();
    }

    @Override
    @Test
    public void testAccessControl() {

        String[] params = new String[] {
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "Copied Session",
                Const.ParamsNames.COPIED_COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
        verifyUnaccessibleWithoutModifyCoursePrivilege(params);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        //TODO: find a way to test status message from session
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String expectedString = "";
        String teammatesLogMessage =
                "TEAMMATESLOG|||instructorFeedbackCopy|||instructorFeedbackCopy|||true|||Instructor|||"
                + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||";

        ______TS("Not enough parameters");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
        verifyAssumptionFailure();
        //TODO make sure IFAA does assertNotNull for required parameters then uncomment
        //verifyAssumptionFailure(Const.ParamsNames.COURSE_ID, instructor1ofCourse1.courseId);

        ______TS("Typical case");

        String feedbackSessionName = "First feedback session";
        String courseId = "idOfTypicalCourse1";
        String[] params = new String[] {
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "Copied Session",
                Const.ParamsNames.COPIED_COURSE_ID, "idOfTypicalCourse2",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.COURSE_ID, courseId
        };

        String oldTimeZone = typicalBundle.courses.get("typicalCourse1").getTimeZone().getId();
        String newTimeZone = typicalBundle.courses.get("typicalCourse2").getTimeZone().getId();
        assertNotEquals(oldTimeZone, newTimeZone);

        String sessionTimeZone =
                FeedbackSessionsLogic.inst().getFeedbackSession(feedbackSessionName, courseId).getTimeZone().getId();
        assertEquals(sessionTimeZone, oldTimeZone);

        InstructorFeedbackCopyAction a = getAction(params);
        RedirectResult rr = getRedirectResult(a);

        expectedString = getPageResultDestination(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                "idOfTypicalCourse2",
                "Copied+Session",
                instructor1ofCourse1.googleId,
                false);
        assertEquals(expectedString, rr.getDestinationWithParams());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackCopy|||instructorFeedbackCopy|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||New Feedback Session "
                + "<span class=\"bold\">(Copied Session)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse2]</span> created.<br>"
                + "<span class=\"bold\">From:</span> 2012-04-01T21:59:00Z"
                + "<span class=\"bold\"> to</span> 2027-04-30T21:59:00Z<br>"
                + "<span class=\"bold\">Session visible from:</span> 2012-03-28T21:59:00Z<br>"
                + "<span class=\"bold\">Results visible from:</span> 2027-05-01T21:59:00Z<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: Please please fill in the following questions.>|||/page/instructorFeedbackCopy";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        sessionTimeZone = FeedbackSessionsLogic.inst().getFeedbackSession("Copied Session",
                "idOfTypicalCourse2").getTimeZone().getId();
        assertEquals(sessionTimeZone, newTimeZone);

        ______TS("Error: Trying to copy with existing feedback session name");

        params = new String[] {
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "Second feedback session",
                Const.ParamsNames.COPIED_COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.COURSE_ID, courseId
        };

        a = getAction(params);
        RedirectResult pageResult = getRedirectResult(a);

        assertEquals(Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE)
                           .withParam(Const.ParamsNames.ERROR, Boolean.TRUE.toString())
                           .withParam(Const.ParamsNames.USER_ID, instructor1ofCourse1.googleId)
                           .toString(),
                     pageResult.getDestinationWithParams());
        assertTrue(pageResult.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EXISTS, pageResult.getStatusMessage());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackCopy|||instructorFeedbackCopy|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Servlet Action Failure : Trying to create a Feedback Session that exists: "
                + "Second feedback session/idOfTypicalCourse1|||"
                + "/page/instructorFeedbackCopy";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        ______TS("Error: Trying to copy with invalid feedback session name");

        params = new String[] {
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "",
                Const.ParamsNames.COPIED_COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.COURSE_ID, courseId
        };

        a = getAction(params);
        pageResult = getRedirectResult(a);

        assertEquals(Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE)
                           .withParam(Const.ParamsNames.ERROR, Boolean.TRUE.toString())
                           .withParam(Const.ParamsNames.USER_ID, instructor1ofCourse1.googleId)
                           .toString(),
                     pageResult.getDestinationWithParams());
        assertTrue(pageResult.isError);
        assertEquals(getPopulatedEmptyStringErrorMessage(
                         FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                         FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME,
                         FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH),
                     pageResult.getStatusMessage());

        expectedString =
                teammatesLogMessage + "Servlet Action Failure : "
                + "The field 'feedback session name' is empty. "
                + "The value of a/an feedback session name should be no longer than 38 characters. "
                + "It should not be empty.|||/page/instructorFeedbackCopy";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        ______TS("Masquerade mode");

        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        params = new String[] {
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "Second copied feedback session",
                Const.ParamsNames.COPIED_COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "Second feedback session",
                Const.ParamsNames.COURSE_ID, courseId
        };
        params = addUserIdToParams(instructor1ofCourse1.googleId, params);

        a = getAction(params);
        rr = getRedirectResult(a);

        expectedString = getPageResultDestination(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                instructor1ofCourse1.courseId,
                "Second+copied+feedback+session",
                instructor1ofCourse1.googleId,
                false);
        assertEquals(expectedString, rr.getDestinationWithParams());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackCopy|||instructorFeedbackCopy|||true|||"
                + "Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||New Feedback Session "
                + "<span class=\"bold\">(Second copied feedback session)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> created.<br>"
                + "<span class=\"bold\">From:</span> 2013-06-01T21:59:00Z"
                + "<span class=\"bold\"> to</span> 2026-04-28T21:59:00Z<br>"
                + "<span class=\"bold\">Session visible from:</span> 2013-03-20T21:59:00Z<br>"
                + "<span class=\"bold\">Results visible from:</span> 2026-04-29T21:59:00Z<br><br>"
                + "<span class=\"bold\">Instructions:</span> "
                + "<Text: Please please fill in the following questions.>|||/page/instructorFeedbackCopy";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedString, a.getLogMessage(), adminUserId);
    }

    @Override
    protected InstructorFeedbackCopyAction getAction(String... params) {
        return (InstructorFeedbackCopyAction) gaeSimulation.getActionObject(getActionUri(), params);
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
}
