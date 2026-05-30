package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static teammates.ui.request.Intent.INSTRUCTOR_RESULT;
import static teammates.ui.request.Intent.STUDENT_RESULT;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.SessionResultsData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetUserSessionResultsAction}.
 */
public class GetUserSessionResultsActionTest extends BaseActionTest<GetUserSessionResultsAction> {
    private String googleId = "user-googleId";
    private Course course;
    private FeedbackSession session;
    private SessionResultsBundle resultsStub;
    private SessionResultsData expectedResults;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.USER_SESSION_RESULTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        session = getTypicalFeedbackSessionForCourse(course);
        session.setStartTime(Instant.parse("2020-01-01T00:00:00.000Z"));
        session.setEndTime(Instant.parse("2020-10-01T00:00:00.000Z"));
        session.setResultsVisibleFromTime(Instant.MIN);
        List<FeedbackQuestion> questionsStub = new ArrayList<>();
        questionsStub.add(getTypicalFeedbackQuestionForSession(session));
        resultsStub = new SessionResultsBundle(questionsStub,
                new HashSet<>(), new HashSet<>(), new ArrayList<>(),
                new ArrayList<>(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), new HashMap<>(), new CourseRoster(new ArrayList<>(), new ArrayList<>()));
        expectedResults = SessionResultsData.init(resultsStub);
        reset(mockLogic);
    }

    private void prepareMocksBasicParams(Intent intent) {
        loginAsInstructor(googleId);

        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);

        switch (intent) {
        case INSTRUCTOR_RESULT:
            Instructor instructorStub = getTypicalInstructor();
            when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
            when(mockLogic.getSessionResultsForUser(argThat(
                            argument -> Objects.equals(argument.getName(), session.getName())),
                            eq(instructorStub), eq(false)))
                    .thenReturn(resultsStub);
            break;
        case STUDENT_RESULT:
            Student studentStub = getTypicalStudent();
            when(mockLogic.getStudentByGoogleId(session.getCourseId(), googleId)).thenReturn(studentStub);
            when(mockLogic.getSessionResultsForUser(argThat(
                            argument -> Objects.equals(argument.getName(), session.getName())),
                            eq(studentStub), eq(false)))
                    .thenReturn(resultsStub);
            break;
        case FULL_DETAIL, INSTRUCTOR_SUBMISSION, STUDENT_SUBMISSION:
        default:
            break;
        }
    }

    @Test
    void testExecute_instructorResultIntent_success() {
        prepareMocksBasicParams(INSTRUCTOR_RESULT);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, INSTRUCTOR_RESULT.name(),
        };
        GetUserSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();
        assertTrue(isSessionResultsDataEqual(expectedResults, output));
    }

    @Test
    void testExecute_studentResultIntent_success() {
        prepareMocksBasicParams(STUDENT_RESULT);
        logoutUser();
        loginAsStudent(googleId);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, STUDENT_RESULT.name(),
        };
        GetUserSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();
        assertTrue(isSessionResultsDataEqual(expectedResults, output));
    }

    @Test
    void testExecute_fullDetailIntent_throwsInvalidHttpParameterException() {
        prepareMocksBasicParams(Intent.FULL_DETAIL);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.name(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testGetSessionResult_notLoggedInUser_cannotAccess() {
        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);

        verifyWithoutLoginCannotAccess(buildParams(STUDENT_RESULT));
        verifyWithoutLoginCannotAccess(buildParams(INSTRUCTOR_RESULT));
    }

    @Test
    void testGetSessionResult_instructorResultIntentUnpublishedSessionNoPreviewAs_cannotAccess() {
        session.setResultsVisibleFromTime(Instant.MAX);

        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);

        verifyInstructorsCannotAccess(buildParams(INSTRUCTOR_RESULT));
    }

    @Test
    void testGetSessionResult_instructorResultIntentPublishedSessionNoPreviewAs_canAccess() {
        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);

        verifyOnlyInstructorsOfTheSameCourseCanAccess(course, buildParams(INSTRUCTOR_RESULT));
    }

    private String[] buildParams(Intent intent) {
        return new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, intent.name(),
        };
    }

    private boolean isSessionResultsDataEqual(SessionResultsData expected, SessionResultsData actual) {
        List<SessionResultsData.QuestionOutput> expectedQuestions = expected.getQuestions();
        List<SessionResultsData.QuestionOutput> actualQuestions = actual.getQuestions();
        if (expectedQuestions.size() != actualQuestions.size()) {
            return false;
        }
        for (int i = 0; i < expectedQuestions.size(); i++) {
            SessionResultsData.QuestionOutput thisQuestion = expectedQuestions.get(i);
            SessionResultsData.QuestionOutput otherQuestion = actualQuestions.get(i);
            if (!isQuestionOutputEqual(thisQuestion, otherQuestion)) {
                return false;
            }
        }
        return true;
    }

    private boolean isQuestionOutputEqual(SessionResultsData.QuestionOutput expected,
                                          SessionResultsData.QuestionOutput actual) {
        if (!JsonUtils.toJson(expected.getFeedbackQuestion()).equals(JsonUtils.toJson(actual.getFeedbackQuestion()))
                || !expected.getQuestionStatistics().equals(actual.getQuestionStatistics())
                || expected.getHasResponseButNotVisibleForPreview() != actual.getHasResponseButNotVisibleForPreview()
                || expected.getHasCommentNotVisibleForPreview() != actual.getHasCommentNotVisibleForPreview()) {
            return false;
        }
        List<SessionResultsData.ResponseOutput> thisResponses;
        List<SessionResultsData.ResponseOutput> otherResponses;
        thisResponses = expected.getAllResponses();
        otherResponses = actual.getAllResponses();
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

    private boolean isResponseOutputEqual(SessionResultsData.ResponseOutput expected,
                                          SessionResultsData.ResponseOutput actual) {
        return expected.getGiver().equals(actual.getGiver())
                && expected.getGiverTeam().equals(actual.getGiverTeam())
                && expected.getGiverSection().equals(actual.getGiverSection())
                && expected.getRecipient().equals(actual.getRecipient())
                && expected.getRecipientTeam().equals(actual.getRecipientTeam())
                && expected.getRecipientSection().equals(actual.getRecipientSection())
                && expected.getResponseDetails().getJsonString().equals(actual.getResponseDetails().getJsonString());
    }
}
