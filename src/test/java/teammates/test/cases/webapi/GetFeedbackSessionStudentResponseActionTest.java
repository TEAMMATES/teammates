package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetFeedbackSessionStudentResponseAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackSessionStudentsResponseData;

/**
 * SUT: {@link GetFeedbackSessionStudentResponseAction}.
 */
public class GetFeedbackSessionStudentResponseActionTest extends
        BaseActionTest<GetFeedbackSessionStudentResponseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_STUDENTS_RESPONSE;
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
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName(),
        };

        GetFeedbackSessionStudentResponseAction pageAction = getAction(submissionParams);
        JsonResult result = getJsonResult(pageAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        FeedbackSessionStudentsResponseData output = (FeedbackSessionStudentsResponseData) result.getOutput();
        assertEquals(9, output.getStudentsResponse().size());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
