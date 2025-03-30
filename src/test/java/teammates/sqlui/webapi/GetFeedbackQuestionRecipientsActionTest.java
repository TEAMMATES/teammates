package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackQuestionRecipientsData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.GetFeedbackQuestionRecipientsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetFeedbackQuestionRecipientsAction}.
 */
public class GetFeedbackQuestionRecipientsActionTest extends BaseActionTest<GetFeedbackQuestionRecipientsAction> {

    private FeedbackSession typicalFeedbackSession;
    private FeedbackQuestion typicalFeedbackQuestion;
    private Student typicalStudent;
    private Instructor typicalInstructor;
    private Map<String, FeedbackQuestionRecipient> typicalRecipients;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTION_RECIPIENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        typicalFeedbackQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
        typicalFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);
        typicalFeedbackSession.setSessionVisibleFromTime(Instant.now());

        typicalStudent = getTypicalStudent();
        typicalStudent.setAccount(getTypicalAccount());
        typicalInstructor = getTypicalInstructor();
        typicalInstructor.setAccount(getTypicalAccount());

        typicalRecipients = new HashMap<>();
        typicalRecipients.put("recipient1", new FeedbackQuestionRecipient("Recipient 1", "recipient1@teammates.tmt"));
        typicalRecipients.put("recipient2", new FeedbackQuestionRecipient("Recipient 2", "recipient2@teammates.tmt"));

        reset(mockLogic);
    }

    @Test
    void testExecute_studentSubmission_typicalSuccessCase() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId()))
                .thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getStudentByGoogleId(typicalStudent.getCourseId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);
        when(mockLogic.getRecipientsOfQuestion(
                argThat(arg -> arg.getId().equals(typicalFeedbackQuestion.getId())),
                argThat(arg -> arg == null),
                argThat(arg -> arg.getGoogleId().equals(typicalStudent.getGoogleId()))))
                .thenReturn(typicalRecipients);

        loginAsStudent(typicalStudent.getGoogleId());

        GetFeedbackQuestionRecipientsAction action = getAction(params);
        JsonResult result = action.execute();
        FeedbackQuestionRecipientsData data = (FeedbackQuestionRecipientsData) result.getOutput();

        assertEquals(typicalRecipients.get("recipient1").getName(), data.getRecipients().get(0).getName());
        assertEquals(typicalRecipients.get("recipient2").getName(), data.getRecipients().get(1).getName());
    }

    @Test
    void testExecute_instructorSubmission_typicalSuccessCase() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId()))
                .thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getInstructorByGoogleId(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getRecipientsOfQuestion(
                argThat(arg -> arg.getId().equals(typicalFeedbackQuestion.getId())),
                argThat(arg -> arg.getGoogleId().equals(typicalInstructor.getGoogleId())),
                argThat(arg -> arg == null)))
                .thenReturn(typicalRecipients);

        loginAsInstructor(typicalInstructor.getGoogleId());

        GetFeedbackQuestionRecipientsAction action = getAction(params);
        JsonResult result = action.execute();
        FeedbackQuestionRecipientsData data = (FeedbackQuestionRecipientsData) result.getOutput();

        assertEquals(typicalRecipients.size(), data.getRecipients().size());
        assertEquals(typicalRecipients.get("recipient1").getName(), data.getRecipients().get(0).getName());
        assertEquals(typicalRecipients.get("recipient2").getName(), data.getRecipients().get(1).getName());
    }

    @Test
    void testExecute_invalidIntent_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, "INSTRUCTOR_RESULT",
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId()))
                .thenReturn(typicalFeedbackQuestion);

        loginAsStudent(typicalStudent.getGoogleId());

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingParams_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId()))
                .thenReturn(typicalFeedbackQuestion);

        loginAsStudent(typicalStudent.getGoogleId());

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_invalidQuestionId_throwsEntityNotFoundException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, UUID.randomUUID().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackQuestion(any(UUID.class))).thenReturn(null);

        loginAsStudent(typicalStudent.getGoogleId());

        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_differentRecipientTypes_shouldReturnRecipientsCorrectly() {
        // Test SELF recipient type
        FeedbackQuestion selfQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
        selfQuestion.setRecipientType(FeedbackParticipantType.SELF);
        String[] selfParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, selfQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        Map<String, FeedbackQuestionRecipient> selfRecipients = new HashMap<>();
        selfRecipients.put("self", new FeedbackQuestionRecipient(typicalStudent.getName(), typicalStudent.getEmail()));

        when(mockLogic.getFeedbackQuestion(selfQuestion.getId())).thenReturn(selfQuestion);
        when(mockLogic.getStudentByGoogleId(typicalStudent.getCourseId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);
        when(mockLogic.getRecipientsOfQuestion(any(), any(), any())).thenReturn(selfRecipients);

        loginAsStudent(typicalStudent.getGoogleId());

        GetFeedbackQuestionRecipientsAction action = getAction(selfParams);
        JsonResult result = action.execute();
        FeedbackQuestionRecipientsData data = (FeedbackQuestionRecipientsData) result.getOutput();

        assertEquals(1, data.getRecipients().size());
        assertEquals(typicalStudent.getEmail(), data.getRecipients().get(0).getIdentifier());

        // Test TEAMS recipient type
        FeedbackQuestion teamQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
        teamQuestion.setRecipientType(FeedbackParticipantType.TEAMS);
        String[] teamParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, teamQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        Map<String, FeedbackQuestionRecipient> teamRecipients = new HashMap<>();
        teamRecipients.put("team1", new FeedbackQuestionRecipient("Team 1", "team1"));

        when(mockLogic.getFeedbackQuestion(teamQuestion.getId())).thenReturn(teamQuestion);
        when(mockLogic.getRecipientsOfQuestion(any(), any(), any())).thenReturn(teamRecipients);

        action = getAction(teamParams);
        result = action.execute();
        data = (FeedbackQuestionRecipientsData) result.getOutput();

        assertEquals(1, data.getRecipients().size());
        assertEquals("team1", data.getRecipients().get(0).getIdentifier());
    }

    @Test
    void testAccessControl_withoutLogin_cannotAccess() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId()))
                .thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getFeedbackSession(typicalFeedbackQuestion.getFeedbackSession().getName(),
                typicalFeedbackQuestion.getCourseId()))
                .thenReturn(typicalFeedbackSession);

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_studentAccess_validationOfScenarios() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId()))
                .thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getFeedbackSession(typicalFeedbackQuestion.getFeedbackSession().getName(),
                typicalFeedbackQuestion.getCourseId()))
                .thenReturn(typicalFeedbackSession);

        ______TS("Student accessing own feedback - can access");
        loginAsStudent(typicalStudent.getGoogleId());
        when(mockLogic.getStudentByGoogleId(typicalStudent.getCourseId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);
        verifyCanAccess(params);

        ______TS("Student attempting to preview as instructor - cannot access");
        String[] previewParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, typicalInstructor.getEmail(),
        };
        verifyCannotAccess(previewParams);
    }

    @Test
    void testAccessControl_instructorAccess_validationOfScenarios() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId()))
                .thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getFeedbackSession(typicalFeedbackQuestion.getFeedbackSession().getName(),
                typicalFeedbackQuestion.getCourseId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(typicalInstructor.getCourseId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        ______TS("Instructor accessing own feedback - can access");
        typicalFeedbackQuestion.setGiverType(FeedbackParticipantType.INSTRUCTORS);
        loginAsInstructor(typicalInstructor.getGoogleId());
        verifyCanAccess(params);

        ______TS("Instructor preview as student - can access");
        typicalFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);
        String[] previewParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, typicalStudent.getEmail(),
        };
        when(mockLogic.getStudentForEmail(typicalStudent.getCourseId(), typicalStudent.getEmail()))
                .thenReturn(typicalStudent);
        verifyCanAccess(previewParams);

        ______TS("Instructor moderating as student - can access");
        List<FeedbackParticipantType> currentShowResponsesTo = new ArrayList<>(typicalFeedbackQuestion.getShowResponsesTo());
        List<FeedbackParticipantType> currenShowGiverNameTo = new ArrayList<>(typicalFeedbackQuestion.getShowGiverNameTo());
        List<FeedbackParticipantType> currentShowRecipientNameTo =
                        new ArrayList<>(typicalFeedbackQuestion.getShowRecipientNameTo());

        currentShowResponsesTo.add(FeedbackParticipantType.INSTRUCTORS);
        currenShowGiverNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        currentShowRecipientNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        typicalFeedbackQuestion.setShowResponsesTo(currentShowResponsesTo);
        typicalFeedbackQuestion.setShowGiverNameTo(currenShowGiverNameTo);
        typicalFeedbackQuestion.setShowRecipientNameTo(currentShowRecipientNameTo);

        String[] moderatedParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, typicalStudent.getEmail(),
        };
        verifyCanAccess(moderatedParams);
    }
}
