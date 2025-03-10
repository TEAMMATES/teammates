package teammates.sqlui.webapi;

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

import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.SqlCourseRoster;
import teammates.common.datatransfer.SqlSessionResultsBundle;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.SessionResultsData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.GetSessionResultsAction;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetSessionResultsAction}.
 */
public class GetSessionResultsActionTest extends BaseActionTest<GetSessionResultsAction> {
    private String googleId = "user-googleId";
    private Course course;
    private FeedbackSession session;
    private SqlSessionResultsBundle resultsStub;
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
        resultsStub = new SqlSessionResultsBundle(questionsStub,
                new HashSet<>(), new HashSet<>(), new ArrayList<>(),
                new ArrayList<>(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), new HashMap<>(), new SqlCourseRoster(new ArrayList<>(), new ArrayList<>()));
        expectedResults = SessionResultsData.initForInstructor(resultsStub);
        reset(mockLogic);
    }

    private void prepareMocksBasicParams(Intent intent) {
        loginAsInstructor(googleId);
        Instructor instructorStub = getTypicalInstructor();

        // Common mocked methods
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);

        // Specific mocked methods
        switch (intent) {
        case FULL_DETAIL:
            when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
            when(mockLogic.getSessionResultsForCourse(argThat(
                    argument -> Objects.equals(argument.getName(), session.getName())),
                    eq(course.getId()), eq(instructorStub.getEmail()), isNull(), isNull(),
                    eq(FeedbackResultFetchType.BOTH))).thenReturn(resultsStub);
            break;
        case INSTRUCTOR_RESULT:
            when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
            when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
            when(mockLogic.getSessionResultsForUser(argThat(
                            argument -> Objects.equals(argument.getName(), session.getName())),
                    eq(course.getId()), eq(instructorStub.getEmail()),
                    eq(true), isNull(), eq(false))).thenReturn(resultsStub);
            break;
        case STUDENT_RESULT:
            Student studentStub = getTypicalStudent();
            when(mockLogic.getStudentByGoogleId(session.getCourseId(), googleId)).thenReturn(studentStub);
            when(mockLogic.getSessionResultsForUser(argThat(
                            argument -> Objects.equals(argument.getName(), session.getName())),
                    eq(course.getId()), eq(studentStub.getEmail()),
                    eq(false), isNull(), eq(false))).thenReturn(resultsStub);
            break;
        case INSTRUCTOR_SUBMISSION:
        case STUDENT_SUBMISSION:
        default:
            break;
        }

    }

    @Test
    void testExecute_fullDetailIntent_success() {
        prepareMocksBasicParams(FULL_DETAIL);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, FULL_DETAIL.name(),
        };
        GetSessionResultsAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) actionOutput.getOutput();
        assertTrue(isSessionResultsDataEqual(expectedResults, output));

        logoutUser();
        loginAsStudent(googleId);
        JsonResult actionOutput1 = getJsonResult(action);
        SessionResultsData output1 = (SessionResultsData) actionOutput1.getOutput();
        assertTrue(isSessionResultsDataEqual(expectedResults, output1));
    }

    @Test
    void testExecute_instructorResultIntent_success() {
        prepareMocksBasicParams(INSTRUCTOR_RESULT);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
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
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
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
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_studentSubmissionIntent_throwsInvalidHttpParameterException() {
        prepareMocksBasicParams(Intent.STUDENT_SUBMISSION);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_invalidParams_throwsInvalidHttpParameterException() {
        loginAsInstructor(googleId);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_withAllParametersAndFullDetailIntent_success() {
        loginAsInstructor(googleId);
        Instructor instructorStub = getTypicalInstructor();
        FeedbackQuestion questionStub = getTypicalFeedbackQuestionForSession(session);
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorForEmail(course.getId(), instructorStub.getEmail())).thenReturn(instructorStub);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
        when(mockLogic.getSessionResultsForCourse(argThat(
                        argument -> Objects.equals(argument.getName(), session.getName())),
                eq(course.getId()), eq(instructorStub.getEmail()), eq(questionStub.getId()), eq("sectionName"),
                eq(FeedbackResultFetchType.RECEIVER))).thenReturn(resultsStub);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
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
    void testCheckSpecificAccessControl_notLoggedInUser_cannotAccess() {
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        String[] params1 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, STUDENT_RESULT.name(),
        };
        verifyCannotAccess(params1);

        String [] params2 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, INSTRUCTOR_RESULT.name(),
        };
        verifyCannotAccess(params2);

    }

    @Test
    void testCheckSpecificAccessControl_instructorResultIntentUnpublishedSessionWithPreviewAs_canAccess() {
        loginAsInstructor(googleId);
        Instructor instructor = getTypicalInstructor();
        session.setResultsVisibleFromTime(Instant.MAX); // unpublished session
        assertFalse(session.isPublished());
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, INSTRUCTOR_RESULT.name(),
                Const.ParamsNames.PREVIEWAS, instructor.getEmail(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);
        verifyCanAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_instructorResultIntentUnpublishedSessionNoPreviewAs_cannotAccess() {
        loginAsInstructor(googleId);
        Instructor instructor = getTypicalInstructor();
        session.setResultsVisibleFromTime(Instant.MAX); // unpublished session
        assertFalse(session.isPublished());
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, INSTRUCTOR_RESULT.name(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);
        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_instructorResultIntentPublishedSessionNoPreviewAs_canAccess() {
        loginAsInstructor(googleId);
        Instructor instructor = getTypicalInstructor();
        assertTrue(session.isPublished());
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, INSTRUCTOR_RESULT.name(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);
        verifyCanAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_instructorResultIntentPublishedSessionWithPreviewAs_canAccess() {
        loginAsInstructor(googleId);
        Instructor instructor = getTypicalInstructor();
        assertTrue(session.isPublished());
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, INSTRUCTOR_RESULT.name(),
                Const.ParamsNames.PREVIEWAS, instructor.getEmail(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);
        verifyCanAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_fullDetailIntent_canAccess() {
        loginAsInstructor(googleId);
        Instructor instructor = getTypicalInstructor();
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, FULL_DETAIL.name(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructor);
        when(mockLogic.getInstructorForEmail(session.getCourseId(), instructor.getEmail())).thenReturn(instructor);
        verifyCanAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_studentResultIntentHisPublishedSession_canAccess() {
        loginAsStudent(googleId);
        session.setSessionVisibleFromTime(Instant.now());
        assertTrue(session.isPublished());
        Student student = getTypicalStudent();
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, STUDENT_RESULT.name(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getStudentByGoogleId(session.getCourseId(), googleId)).thenReturn(student);
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_studentResultIntentNotHisPublishedSession_cannotAccess() {
        loginAsStudent(googleId);
        session.setSessionVisibleFromTime(Instant.now());
        assertTrue(session.isPublished());
        Student student = getTypicalStudent();
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, "another-course-id",
                Const.ParamsNames.INTENT, STUDENT_RESULT.name(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), "another-course-id")).thenReturn(session);
        when(mockLogic.getStudentByGoogleId("another-course-id", student.getEmail())).thenReturn(null);
        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_studentResultIntentHisPublishedSessionWithPreviewAs_canAccess() {
        loginAsStudent(googleId);
        session.setSessionVisibleFromTime(Instant.now());
        assertTrue(session.isPublished());
        Student student = getTypicalStudent();
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, STUDENT_RESULT.name(),
                Const.ParamsNames.PREVIEWAS, student.getEmail(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getStudentForEmail(session.getCourseId(), student.getEmail())).thenReturn(student);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(getTypicalInstructor());
        verifyCanAccess(params);

    }

    @Test
    void testCheckSpecificAccessControl_studentResultIntentUnpublishedSessionNoPreviewAs_cannotAccess() {
        loginAsStudent(googleId);
        session.setResultsVisibleFromTime(Instant.MAX);
        assertFalse(session.isPublished());
        Student student = getTypicalStudent();
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, STUDENT_RESULT.name(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getStudentForEmail(session.getCourseId(), student.getEmail())).thenReturn(student);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(getTypicalInstructor());
        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_studentResultIntentUnpublishedSessionWithPreviewAs_canAccess() {
        loginAsStudent(googleId);
        Student student = getTypicalStudent();
        session.setResultsVisibleFromTime(Instant.MAX);
        assertFalse(session.isPublished());
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, STUDENT_RESULT.name(),
                Const.ParamsNames.PREVIEWAS, student.getEmail(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getStudentForEmail(session.getCourseId(), student.getEmail())).thenReturn(student);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(getTypicalInstructor());
        verifyCanAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_invalidIntent_invalidHttpParameterException() {
        loginAsInstructor(googleId);
        Instructor instructor = getTypicalInstructor();
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructor);
        GetSessionResultsAction a = getAction(params);
        assertThrows(InvalidHttpParameterException.class, a::checkAccessControl);

        String [] params2 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        GetSessionResultsAction b = getAction(params2);
        assertThrows(InvalidHttpParameterException.class, b::checkAccessControl);
    }

    @Test
    void testSpecificAccessControl_studentPreviewAsInstructor_canAccess() {
        loginAsStudent(googleId);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, INSTRUCTOR_RESULT.name(),
                Const.ParamsNames.PREVIEWAS, getTypicalInstructor().getEmail(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorForEmail(session.getCourseId(), getTypicalInstructor().getEmail()))
                .thenReturn(getTypicalInstructor());
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(getTypicalInstructor());
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorPreviewAsStudent_canAccess() {
        loginAsInstructor(googleId);
        Student student = getTypicalStudent();
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, STUDENT_RESULT.name(),
                Const.ParamsNames.PREVIEWAS, student.getEmail(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getStudentForEmail(student.getCourseId(), student.getEmail())).thenReturn(student);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(getTypicalInstructor());
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithNoPermissions_cannotAccess() {
        loginAsInstructor(googleId);
        Instructor instructor = getTypicalInstructor();
        instructor.setPrivileges(new InstructorPrivileges());
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.INTENT, INSTRUCTOR_RESULT.name(),
        };
        when(mockLogic.getFeedbackSession(session.getName(), session.getCourseId())).thenReturn(session);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);
        verifyCannotAccess(params);
    }
}
