package teammates.sqlui.webapi;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.request.Intent;
import teammates.ui.webapi.SubmitFeedbackResponsesAction;

/**
 * SUT: {@link SubmitFeedbackResponsesAction}.
 */
public class SubmitFeedbackResponsesActionTest extends BaseActionTest<SubmitFeedbackResponsesAction> {

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
        typicalInstructor = getTypicalInstructor();
        typicalCourse = getTypicalCourse();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
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
    void testAccessControl_noIntent_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString()
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        verifyHttpParameterFailureAcl(params);
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
    void testAccessControl_typicalCase_canAccess() {
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
}
