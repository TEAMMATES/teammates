package teammates.sqlui.webapi;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteFeedbackQuestionAction;

/**
 * SUT: {@link DeleteFeedbackQuestionAction}.
 */
public class DeleteFeedbackQuestionActionTest extends BaseActionTest<DeleteFeedbackQuestionAction> {

    private final Instructor typicalInstructor = getTypicalInstructor();
    private Course typicalCourse;
    private FeedbackSession typicalFeedbackSession;
    private FeedbackQuestion typicalFeedbackQuestion;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeMethod
    void setUp() {
        reset(mockLogic);
        typicalCourse = typicalInstructor.getCourse();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
        typicalFeedbackQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
    }

    @Test
    void testExecute_feedbackQuestionExists_success() {
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        DeleteFeedbackQuestionAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Feedback question deleted!", actionOutput.getMessage());
    }

    @Test
    void testExecute_feedbackQuestionDoesNotExist_failSilently() {
        UUID nonexistentQuestionId = UUID.fromString("11110000-0000-0000-0000-000000000000");
        when(mockLogic.getFeedbackQuestion(nonexistentQuestionId)).thenReturn(null);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, nonexistentQuestionId.toString(),
        };

        DeleteFeedbackQuestionAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Feedback question deleted!", actionOutput.getMessage());
    }

    @Test
    void testExecute_missingFeedbackQuestionId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, null,
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testSpecificAccessControl_nonExistentFeedbackQuestion_cannotAccess() {
        logoutUser();
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(null);
        String[] submissionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        verifyCannotAccess(submissionParams);
    }

    @Test
    void testAccessControl() throws Exception {
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);

        String[] submissionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        verifyInaccessibleWithoutModifySessionPrivilege(typicalCourse, submissionParams);
        verifyAccessibleWithModifySessionPrivilege(typicalCourse, submissionParams);
    }

}
