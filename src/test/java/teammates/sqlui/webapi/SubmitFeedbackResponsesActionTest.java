package teammates.sqlui.webapi;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.common.util.TimeHelperExtension;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.request.Intent;
import teammates.ui.webapi.SubmitFeedbackResponsesAction;

/**
 * SUT: {@link SubmitFeedbackResponsesAction}.
 */
public class SubmitFeedbackResponsesActionTest extends BaseActionTest<SubmitFeedbackResponsesAction> {

    private Student typicalStudent;
    private Instructor typicalInstructor;
    private Course typicalCourse;
    private FeedbackSession typicalFeedbackSession;
    private FeedbackQuestion typicalFeedbackQuestion;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        typicalStudent = getTypicalStudent();
        typicalInstructor = getTypicalInstructor();
        typicalCourse = getTypicalCourse();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
        typicalFeedbackSession.setStartTime(Instant.now());
        typicalFeedbackQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
        typicalFeedbackQuestion.setShowGiverNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        typicalFeedbackQuestion.setShowRecipientNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        typicalFeedbackQuestion.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));
    }

    @AfterMethod
    void tearDown() {
        reset(mockLogic);
    }

    @Test
    void testAccessControl_missingParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailureAcl();
    }

    @Test
    void testAccessControl_noFeedbackQuestionId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString()
        };

        verifyHttpParameterFailureAcl(params);
    }

    @Test
    void testAccessControl_feedbackQuestionDoesNotExist_throwsEntityNotFoundException() {
        UUID id = UUID.fromString("11110000-0000-0000-0000-000000000000");
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, id.toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString()
        };

        when(mockLogic.getFeedbackQuestion(id)).thenReturn(null);

        verifyEntityNotFoundAcl(params);
    }

    @Test
    void testAccessControl_moderatedQuestionCannotBeSeenByInstructor_throwsUnauthorizedAccessException() {
        typicalFeedbackQuestion.setShowGiverNameTo(new ArrayList<>());
        typicalFeedbackQuestion.setShowRecipientNameTo(new ArrayList<>());
        typicalFeedbackQuestion.setShowResponsesTo(new ArrayList<>());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, typicalInstructor.getEmail()
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_previewRequest_throwsUnauthorizedAccessException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, typicalInstructor.getEmail()
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_noIntent_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString()
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        verifyHttpParameterFailureAcl(params);
    }

    @Test
    void testAccessControl_instructorResultIntent_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString()
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        verifyHttpParameterFailureAcl(params);
    }

    @Test
    void testAccessControl_studentResultIntent_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString()
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        verifyHttpParameterFailureAcl(params);
    }

    @Test
    void testAccessControl_fullDetailIntent_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString()
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        verifyHttpParameterFailureAcl(params);
    }

    @Test
    void testAccessControl_moderationRequestForStudentSubmission_canAccess() {
        typicalFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, typicalStudent.getEmail()
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getStudentForEmail(typicalCourse.getId(), typicalStudent.getEmail()))
                .thenReturn(typicalStudent);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCanAccess(params);
    }

    // More tests here

    @Test
    void testAccessControl_moderationRequestForInstructorSubmission_canAccess() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, typicalInstructor.getEmail()
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getInstructorForEmail(typicalCourse.getId(), typicalInstructor.getEmail()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_submissionIsNotOpen_throwsUnauthorizedAccessException() {
        typicalFeedbackSession.setStartTime(TimeHelperExtension.getInstantDaysOffsetFromNow(1));
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getDeadlineForUser(typicalFeedbackSession, typicalInstructor))
                .thenReturn(typicalFeedbackSession.getEndTime());

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_submissionBeforeEndTimeBeforeDeadline_canAccess() {

    }
}
