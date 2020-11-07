package teammates.ui.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.UserRole;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.ui.output.SessionResultsData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetSessionResultsAction}.
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
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructorAttributes = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructorAttributes.getGoogleId());

        ______TS("typical: instructor accesses results of his/her course");

        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };

        GetSessionResultsAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        SessionResultsData output = (SessionResultsData) r.getOutput();

        SessionResultsData expectedResults = SessionResultsData.initForInstructor(
                logic.getSessionResultsForUser(accessibleFeedbackSession.getFeedbackSessionName(),
                        accessibleFeedbackSession.getCourseId(),
                        instructorAttributes.getEmail(),
                        UserRole.INSTRUCTOR, null, null));

        assertTrue(isSessionResultsDataEqual(expectedResults, output));

        ______TS("typical: student accesses results of his/her course");

        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };

        a = getAction(submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (SessionResultsData) r.getOutput();
        expectedResults = SessionResultsData.initForStudent(
                logic.getSessionResultsForUser(accessibleFeedbackSession.getFeedbackSessionName(),
                        accessibleFeedbackSession.getCourseId(),
                        studentAttributes.getEmail(),
                        UserRole.STUDENT, null, null),
                studentAttributes);

        assertTrue(isSessionResultsDataEqual(expectedResults, output));

    }

    @Override
    @Test
    protected void testAccessControl() {
        String[] submissionParams;

        ______TS("accessible for authenticated instructor");
        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);

        ______TS("inaccessible for authenticated student when unpublished");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };
        String[] finalParams = submissionParams;
        assertThrows(UnauthorizedAccessException.class, () -> verifyAccessibleForStudentsOfTheSameCourse(finalParams));

        ______TS("accessible for authenticated student when published");
        FeedbackSessionAttributes publishedFeedbackSession = typicalBundle.feedbackSessions.get("closedSession");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, publishedFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };
        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);

        ______TS("invalid intent");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(submissionParams);
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(submissionParams);
    }

    private boolean isSessionResultsDataEqual(SessionResultsData self, SessionResultsData other) {
        List<SessionResultsData.QuestionOutput> thisQuestions = self.getQuestions();
        List<SessionResultsData.QuestionOutput> otherQuestions = other.getQuestions();
        if (thisQuestions.size() != otherQuestions.size()) {
            return false;
        }
        for (int i = 0; i < thisQuestions.size(); i++) {
            SessionResultsData.QuestionOutput thisQuestion = thisQuestions.get(i);
            SessionResultsData.QuestionOutput otherQuestion = otherQuestions.get(i);
            if (!isQuestionOutputEqual(thisQuestion, otherQuestion)) {
                return false;
            }
        }
        return true;
    }

    private boolean isQuestionOutputEqual(SessionResultsData.QuestionOutput self,
                                          SessionResultsData.QuestionOutput other) {
        if (!JsonUtils.toJson(self.getFeedbackQuestion()).equals(JsonUtils.toJson(self.getFeedbackQuestion()))
                || !self.getQuestionStatistics().equals(other.getQuestionStatistics())) {
            return false;
        }
        List<SessionResultsData.ResponseOutput> thisResponses;
        List<SessionResultsData.ResponseOutput> otherResponses;
        thisResponses = self.getAllResponses();
        otherResponses = other.getAllResponses();
        if (thisResponses.size() != otherResponses.size()) {
            return false;
        }
        for (int j = 0; j < thisResponses.size(); j++) {
            if (!isResponseOutputEqual(thisResponses.get(j), otherResponses.get(j))) {
                return false;
            }
        }
        return true;
    }

    private boolean isResponseOutputEqual(SessionResultsData.ResponseOutput self,
                                          SessionResultsData.ResponseOutput other) {
        return self.getGiver().equals(other.getGiver())
                && self.getGiverTeam().equals(other.getGiverTeam())
                && self.getGiverSection().equals(other.getGiverSection())
                && self.getRecipient().equals(other.getRecipient())
                && self.getRecipientTeam().equals(other.getRecipientTeam())
                && self.getRecipientSection().equals(other.getRecipientSection())
                && self.getResponseDetails().getJsonString().equals(other.getResponseDetails().getJsonString());
    }

    @Test
    public void testAccessControl_withRegistrationKey_shouldPass() {
        CourseAttributes typicalCourse1 = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student1 = typicalBundle.students.get("student1InCourse1");
        student1 = logic.getStudentForEmail(student1.course, student1.email);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.REGKEY, StringHelper.encrypt(student1.key),
        };

        publishSession(feedbackSessionAttributes.getFeedbackSessionName(), typicalCourse1.getId());
        verifyAccessibleForUnregisteredUsers(submissionParams);
    }

    @Test
    public void testAccessControl_withoutCorrectAuthInfoAccessStudentResult_shouldFail() {
        CourseAttributes typicalCourse1 = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };

        publishSession(feedbackSessionAttributes.getFeedbackSessionName(), typicalCourse1.getId());
        verifyInaccessibleForUnregisteredUsers(submissionParams);
    }

    @Test
    public void testAccessControl_studentAccessOwnCourseSessionResult_shouldPass() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        CourseAttributes typicalCourse1 = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };

        publishSession(feedbackSessionAttributes.getFeedbackSessionName(), typicalCourse1.getId());
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
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };

        publishSession(feedbackSessionAttributes.getFeedbackSessionName(), typicalCourse1.getId());
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

    private void publishSession(String feedbackSessionName, String courseId) {
        try {
            logic.publishFeedbackSession(feedbackSessionName, courseId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
