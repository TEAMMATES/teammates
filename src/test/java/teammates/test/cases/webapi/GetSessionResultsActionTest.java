package teammates.test.cases.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetSessionResultsAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.SessionResultsData;
import teammates.ui.webapi.request.Intent;

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
        SessionResultsData expectedResults =
                new SessionResultsData(logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                accessibleFeedbackSession.getFeedbackSessionName(),
                accessibleFeedbackSession.getCourseId(),
                instructorAttributes.getEmail(),
                1,
                Const.FeedbackSessionResults.QUESTION_SORT_TYPE
        ), instructorAttributes);

        assertTrue(isSessionResultsDataEqual(expectedResults, output));

        ______TS("fail: instructor accesses results of non-existent feedback session");

        String nonexistentFeedbackSession = "nonexistentFeedbackSession";
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, nonexistentFeedbackSession,
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };

        a = getAction(submissionParams);
        GetSessionResultsAction finalA = a;

        assertThrows(EntityNotFoundException.class, () -> getJsonResult(finalA));

        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.getGoogleId());

        ______TS("typical: student accesses results of his/her course");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };

        a = getAction(submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (SessionResultsData) r.getOutput();
        expectedResults =
                new SessionResultsData(logic.getFeedbackSessionResultsForStudent(
                        accessibleFeedbackSession.getFeedbackSessionName(),
                        accessibleFeedbackSession.getCourseId(),
                        studentAttributes.getEmail()
                ), studentAttributes);

        assertTrue(isSessionResultsDataEqual(expectedResults, output));

        ______TS("fail: student accesses results of non-existent feedback session");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, nonexistentFeedbackSession,
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };

        a = getAction(submissionParams);
        GetSessionResultsAction finalAction = a;

        assertThrows(EntityNotFoundException.class, () -> getJsonResult(finalAction));

    }

    @Override
    @Test
    protected void testAccessControl() {
        String[] submissionParams;

        ______TS("accessible for admin");
        verifyAccessibleForAdmin();

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
        if (!self.getQuestionId().equals(other.getQuestionId())
                || self.getQuestionNumber() != other.getQuestionNumber()
                || !self.getQuestionDetails().equals(other.getQuestionDetails())
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

}
