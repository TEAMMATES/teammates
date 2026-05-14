package teammates.ui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static teammates.ui.request.Intent.FULL_DETAIL;
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
import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.InstructorPrivileges;
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
 * SUT: {@link GetSessionResultsAction}.
 */
public class GetSessionResultsActionTest extends BaseActionTest<GetSessionResultsAction> {
    private String googleId = "user-googleId";
    private Course course;
    private FeedbackSession session;
    private SessionResultsBundle resultsStub;
    private SessionResultsData expectedResults;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESULT;
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
        Instructor instructorStub = getTypicalInstructor();

        // Common mocked methods
        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);

        // Specific mocked methods
        switch (intent) {
        case FULL_DETAIL:
            when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
            when(mockLogic.getSessionResults(argThat(
                    argument -> Objects.equals(argument.getName(), session.getName())),
                    eq(instructorStub.getEmail()), isNull(), isNull(),
                    eq(FeedbackResultFetchType.BOTH))).thenReturn(resultsStub);
            break;
        case INSTRUCTOR_RESULT:
            when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
            when(mockLogic.getSessionResultsForUser(argThat(
                            argument -> Objects.equals(argument.getName(), session.getName())),
                            eq(instructorStub), isNull(), eq(false)))
                    .thenReturn(resultsStub);
            break;
        case STUDENT_RESULT:
            Student studentStub = getTypicalStudent();
            when(mockLogic.getStudentByGoogleId(session.getCourseId(), googleId)).thenReturn(studentStub);
            when(mockLogic.getSessionResultsForUser(argThat(
                            argument -> Objects.equals(argument.getName(), session.getName())),
                            eq(studentStub), isNull(), eq(false)))
                    .thenReturn(resultsStub);
            break;
        case INSTRUCTOR_SUBMISSION, STUDENT_SUBMISSION:
        default:
            break;
        }

    }

    @Test
    void testExecute_fullDetailIntent_success() {
        prepareMocksBasicParams(FULL_DETAIL);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, FULL_DETAIL.name(),
        };
        GetSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();
        assertTrue(isSessionResultsDataEqual(expectedResults, output));
    }

    @Test
    void testExecute_instructorResultIntent_success() {
        prepareMocksBasicParams(INSTRUCTOR_RESULT);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, INSTRUCTOR_RESULT.name(),
        };
        GetSessionResultsAction action = getAction(params);
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
        GetSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();
        assertTrue(isSessionResultsDataEqual(expectedResults, output));
    }

    @Test
    void testExecute_instructorSubmissionIntent_throwsInvalidHttpParameterException() {
        prepareMocksBasicParams(Intent.INSTRUCTOR_SUBMISSION);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_studentSubmissionIntent_throwsInvalidHttpParameterException() {
        prepareMocksBasicParams(Intent.STUDENT_SUBMISSION);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_invalidParams_throwsInvalidHttpParameterException() {
        loginAsInstructor(googleId);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_withAllParametersAndFullDetailIntent_success() {
        loginAsInstructor(googleId);
        Instructor instructorStub = getTypicalInstructor();
        FeedbackQuestion questionStub = getTypicalFeedbackQuestionForSession(session);

        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);
        when(mockLogic.getInstructorForEmail(course.getId(), instructorStub.getEmail())).thenReturn(instructorStub);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
        when(mockLogic.getSessionResults(argThat(
                        argument -> Objects.equals(argument.getName(), session.getName())),
                eq(instructorStub.getEmail()), eq(questionStub.getId()), eq("sectionName"),
                eq(FeedbackResultFetchType.RECEIVER))).thenReturn(resultsStub);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, FULL_DETAIL.name(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionStub.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "sectionName",
                Const.ParamsNames.FEEDBACK_RESULTS_SECTION_BY_GIVER_RECEIVER, FeedbackResultFetchType.RECEIVER.name(),
                Const.ParamsNames.PREVIEWAS, instructorStub.getEmail(),
        };
        GetSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();
        assertTrue(isSessionResultsDataEqual(expectedResults, output));
    }

    @Test
    void testExecute_withAllParametersAndInstructorResultIntent_success() {
        loginAsInstructor(googleId);
        Instructor instructorStub = getTypicalInstructor();
        FeedbackQuestion questionStub = getTypicalFeedbackQuestionForSession(session);

        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);
        when(mockLogic.getInstructorForEmail(course.getId(), instructorStub.getEmail())).thenReturn(instructorStub);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
        when(mockLogic.getSessionResultsForUser(argThat(
                        argument -> Objects.equals(argument.getName(), session.getName())),
                eq(instructorStub), eq(questionStub.getId()), eq(true))).thenReturn(resultsStub);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, INSTRUCTOR_RESULT.name(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionStub.getId().toString(),
                Const.ParamsNames.PREVIEWAS, instructorStub.getEmail(),
        };
        GetSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();
        assertTrue(isSessionResultsDataEqual(expectedResults, output));
    }

    @Test
    void testExecute_withAllParametersAndStudentResultIntent_success() {
        loginAsStudent(googleId);
        Student studentStub = getTypicalStudent();
        FeedbackQuestion questionStub = getTypicalFeedbackQuestionForSession(session);

        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);
        when(mockLogic.getStudentForEmail(course.getId(), studentStub.getEmail())).thenReturn(studentStub);
        when(mockLogic.getStudentByGoogleId(session.getCourseId(), googleId)).thenReturn(studentStub);
        when(mockLogic.getSessionResultsForUser(argThat(
                        argument -> Objects.equals(argument.getName(), session.getName())),
                eq(studentStub), eq(questionStub.getId()), eq(true))).thenReturn(resultsStub);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, STUDENT_RESULT.name(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionStub.getId().toString(),
                Const.ParamsNames.PREVIEWAS, studentStub.getEmail(),
        };
        GetSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();
        assertTrue(isSessionResultsDataEqual(expectedResults, output));
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

    @Test
    void testGetSessionResult_notLoggedInUser_cannotAccess() {
        when(mockLogic.getFeedbackSession(session.getId()))
                .thenReturn(session);

        verifyWithoutLoginCannotAccess(buildParams(STUDENT_RESULT));
        verifyWithoutLoginCannotAccess(buildParams(INSTRUCTOR_RESULT));
        verifyWithoutLoginCannotAccess(buildParams(FULL_DETAIL));
    }

    @Test
    void testGetSessionResult_instructorResultIntentUnpublishedSessionWithPreviewAs_canAccess() {
        loginAsInstructor(googleId);
        Instructor instructor = getTypicalInstructor();

        session.setResultsVisibleFromTime(Instant.MAX);
        assertFalse(session.isPublished());

        String[] params = buildParamsWithPreview(INSTRUCTOR_RESULT, instructor.getEmail());

        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);

        verifyCanAccess(params);
    }

    @Test
    void testGetSessionResult_instructorResultIntentUnpublishedSessionNoPreviewAs_cannotAccess() {
        session.setResultsVisibleFromTime(Instant.MAX);
        assertFalse(session.isPublished());

        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);

        verifyInstructorsCannotAccess(buildParams(INSTRUCTOR_RESULT));
    }

    @Test
    void testGetSessionResult_instructorResultIntentPublishedSessionNoPreviewAs_canAccess() {
        assertTrue(session.isPublished());

        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);

        verifyOnlyInstructorsOfTheSameCourseCanAccess(course, buildParams(INSTRUCTOR_RESULT));
    }

    @Test
    void testGetSessionResult_instructorResultIntentPublishedSessionWithPreviewAs_canAccess() {
        loginAsInstructor(googleId);
        Instructor instructor = getTypicalInstructor();
        assertTrue(session.isPublished());

        String[] params = buildParamsWithPreview(INSTRUCTOR_RESULT, instructor.getEmail());

        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);

        verifyCanAccess(params);
    }

    @Test
    void testGetSessionResult_fullDetailIntent_canAccess() {
        when(mockLogic.getFeedbackSession(session.getId()))
                .thenReturn(session);

        verifyOnlyInstructorsOfTheSameCourseCanAccess(course, buildParams(FULL_DETAIL));
    }

    @Test
    void testGetSessionResult_studentResultIntentHisPublishedSession_canAccess() {
        session.setSessionVisibleFromTime(Instant.now());
        assertTrue(session.isPublished());

        when(mockLogic.getFeedbackSession(session.getId()))
                .thenReturn(session);

        Student sameCourseStudent = getTypicalStudent();
        sameCourseStudent.setCourse(course);
        when(mockLogic.getStudentByGoogleId(eq(session.getCourseId()), any()))
                .thenReturn(sameCourseStudent);

        verifyStudentsOfTheSameCourseCanAccess(course, buildParams(STUDENT_RESULT));
    }

    @Test
    void testGetSessionResult_studentResultIntentNotHisPublishedSession_cannotAccess() {
        session.setSessionVisibleFromTime(Instant.now());
        assertTrue(session.isPublished());

        when(mockLogic.getFeedbackSession(session.getId()))
                .thenReturn(session);

        verifyStudentsOfOtherCoursesCannotAccess(course, buildParams(STUDENT_RESULT));
    }

    @Test
    void testGetSessionResult_studentResultIntentHisPublishedSessionWithPreviewAs_cannotAccess() {
        Student student = getTypicalStudent();

        when(mockLogic.getFeedbackSession(session.getId()))
                .thenReturn(session);
        when(mockLogic.getStudentForEmail(course.getId(), student.getEmail()))
                .thenReturn(student);

        String[] params = buildParamsWithPreview(STUDENT_RESULT, student.getEmail());
        verifyStudentsCannotAccess(params);
    }

    @Test
    void testGetSessionResult_studentResultIntentUnpublishedSessionNoPreviewAs_cannotAccess() {
        session.setResultsVisibleFromTime(Instant.MAX);
        assertFalse(session.isPublished());

        when(mockLogic.getFeedbackSession(session.getId()))
                .thenReturn(session);

        verifyStudentsCannotAccess(buildParams(STUDENT_RESULT));
    }

    @Test
    void testGetSessionResult_studentResultIntentUnpublishedSessionWithPreviewAs_cannotAccess() {
        Student student = getTypicalStudent();
        session.setResultsVisibleFromTime(Instant.MAX);
        assertFalse(session.isPublished());

        when(mockLogic.getFeedbackSession(session.getId()))
                .thenReturn(session);
        when(mockLogic.getStudentForEmail(course.getId(), student.getEmail()))
                .thenReturn(student);

        String[] params = buildParamsWithPreview(STUDENT_RESULT, student.getEmail());
        verifyStudentsCannotAccess(params);
    }

    @Test
    void testGetSessionResult_invalidIntent_invalidHttpParameterException() {
        loginAsInstructor(googleId);
        Instructor instructor = getTypicalInstructor();

        when(mockLogic.getFeedbackSession(session.getId()))
                .thenReturn(session);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId))
                .thenReturn(instructor);

        verifyHttpParameterFailureAcl(buildParams(Intent.INSTRUCTOR_SUBMISSION));
        verifyHttpParameterFailureAcl(buildParams(Intent.STUDENT_SUBMISSION));
    }

    @Test
    void testGetSessionResult_studentPreviewAsInstructor_cannotAccess() {
        Instructor instructor = getTypicalInstructor();

        when(mockLogic.getFeedbackSession(session.getId()))
                .thenReturn(session);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail()))
                .thenReturn(instructor);

        String[] params = buildParamsWithPreview(INSTRUCTOR_RESULT, instructor.getEmail());
        verifyStudentsCannotAccess(params);
    }

    @Test
    void testGetSessionResult_instructorPreviewAsStudentValidParams_canAccess() {
        loginAsInstructor(googleId);
        Student student = getTypicalStudent();

        when(mockLogic.getFeedbackSession(session.getId()))
                .thenReturn(session);
        when(mockLogic.getStudentForEmail(student.getCourseId(), student.getEmail()))
                .thenReturn(student);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId))
                .thenReturn(getTypicalInstructor());

        String[] params1 = buildParamsWithPreview(STUDENT_RESULT, student.getEmail());
        verifyCanAccess(params1);

        String[] params2 = buildParamsWithPreviewAndModerated(
                STUDENT_RESULT, student.getEmail(), student.getEmail());
        verifyCanAccess(params2);

        session.setSessionVisibleFromTime(Instant.MIN);
        String[] params3 = buildParamsWithModerated(STUDENT_RESULT, student.getEmail());
        verifyCanAccess(params3);
    }

    @Test
    void testGetSessionResult_instructorPreviewAsStudentInvalidParams_cannotAccess() {
        loginAsInstructor(googleId);

        String[] params = buildParams(STUDENT_RESULT);

        when(mockLogic.getStudentByGoogleId(course.getId(), googleId))
                .thenReturn(null);
        when(mockLogic.getFeedbackSession(session.getId()))
                .thenReturn(session);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId))
                .thenReturn(getTypicalInstructor());

        verifyCannotAccess(params);
    }

    @Test
    void testGetSessionResult_instructorWithNoPermissions_cannotAccess() {
        loginAsInstructor(googleId);
        Instructor instructor = getTypicalInstructor();
        instructor.setPrivileges(new InstructorPrivileges());

        String[] params = buildParams(INSTRUCTOR_RESULT);

        when(mockLogic.getFeedbackSession(session.getId()))
                .thenReturn(session);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId))
                .thenReturn(instructor);

        verifyCannotAccess(params);
    }

    private String[] buildParams(Intent intent) {
        return new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, intent.name(),
        };
    }

    private String[] buildParamsWithPreview(Intent intent, String previewEmail) {
        return new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, intent.name(),
                Const.ParamsNames.PREVIEWAS, previewEmail,
        };
    }

    private String[] buildParamsWithPreviewAndModerated(
            Intent intent, String previewEmail, String moderatedPersonEmail) {

        return new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, intent.name(),
                Const.ParamsNames.PREVIEWAS, previewEmail,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedPersonEmail,
        };
    }

    private String[] buildParamsWithModerated(Intent intent, String moderatedPersonEmail) {
        return new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
                Const.ParamsNames.INTENT, intent.name(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedPersonEmail,
        };
    }
}
