package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetSessionResultsAction;
import teammates.ui.webapi.request.Intent;

/**
 * Access Control Test: {@link GetSessionResultsAction}.
 */
public class GetSessionResultsActionTest extends BaseActionTest<GetSessionResultsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESULT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void testExecute() throws Exception {
        //TODO: Add execution test
    }

    @Override
    protected void testAccessControl() throws Exception {
        //See test cases below.
    }

    @Test
    public void testAccessControl_withoutCorrectAuthInfoAccessStudentResult_shouldFail() {
        CourseAttributes typicalCourse1 = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("publishedSession");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
    }

    @Test
    public void testAccessControl_studentAccessOwnCourseSessionResult_shouldPass() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        CourseAttributes typicalCourse1 = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("publishedSession");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };
        loginAsStudent(student1InCourse1.googleId);
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentAccessUnpublishedSessionStudentResult_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        CourseAttributes typicalCourse1 = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session2InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };
        loginAsStudent(student1InCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_accessStudentSessionResultWithMasqueradeMode_shouldPass() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        CourseAttributes typicalCourse1 = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("publishedSession");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };
        loginAsAdmin();
        verifyCanMasquerade(student1InCourse1.googleId, submissionParams);
    }

    @Test
    public void testAccessControl_studentAccessOtherCourseSessionResult_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        CourseAttributes typicalCourse1 = typicalBundle.courses.get("typicalCourse1");
        CourseAttributes typicalCourse2 = typicalBundle.courses.get("typicalCourse2");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session1InCourse2");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse2.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };

        loginAsStudent(student1InCourse1.googleId);
        verifyCannotAccess(submissionParams);

        // Malicious api call using course Id of the student to bypass the check
        submissionParams[1] = typicalCourse1.getId();
        verifyEntityNotFound(submissionParams);
    }

    @Test
    public void testAccessControl_instructorAccessHisCourseInstructorResult_shouldPass() {
        CourseAttributes typicalCourse1 = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
