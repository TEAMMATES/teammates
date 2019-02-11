package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetFeedbackSessionSubmissionResponseStatusAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.StudentFeedbackSessionResponseInfo.StudentsFeedbackSessionResponseStatus;

/**
 * SUT: {@link GetFeedbackSessionSubmissionResponseStatusAction}.
 */
public class GetFeedbackSessionSubmissionResponseStatusActionTest extends
        BaseActionTest<GetFeedbackSessionSubmissionResponseStatusAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_REMIND_SUBMISSION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() {

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyHttpParameterFailure();

        ______TS("Typical case");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName()
        };

        GetFeedbackSessionSubmissionResponseStatusAction pageAction = getAction(submissionParams);
        JsonResult result = getJsonResult(pageAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        StudentsFeedbackSessionResponseStatus output = (StudentsFeedbackSessionResponseStatus) result.getOutput();
        assertEquals(9, output.getStudentsFeedbackSessionResponseStatus().size());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName()
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
