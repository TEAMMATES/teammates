package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link BinFeedbackSessionAction}.
 */
public class BinFeedbackSessionActionTest extends BaseActionTest<BinFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.BIN_SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, fs.getCourseId());
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName());

        ______TS("typical success case");

        assertNotNull(logic.getFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId()));

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };
        BinFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        assertEquals(r.getStatusCode(), HttpStatus.SC_OK);

        assertNull(logic.getFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId()));
        assertNotNull(logic.getFeedbackSessionFromRecycleBin(fs.getFeedbackSessionName(), fs.getCourseId()));
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("non-existent feedback session");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "randomName",
        };

        loginAsInstructor(instructor1OfCourse1.googleId);
        verifyEntityNotFound(submissionParams);

        ______TS("other verifications");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        verifyInaccessibleWithoutModifySessionPrivilege(submissionParams);

        ______TS("only instructors of the same course with correct privilege can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }
}
