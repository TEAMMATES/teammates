package teammates.sqlui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteFeedbackQuestionAction;

import java.util.UUID;

/**
 * SUT: {@link DeleteFeedbackQuestionAction}.
 */
public class DeleteFeedbackQuestionActionTest extends BaseActionTest<DeleteFeedbackQuestionAction> {

    // private final SqlDataBundle typicalBundle = getTypicalSqlDataBundle();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    protected void testExecute_feedbackQuestionExists_success() {
        Instructor typicalInstructor = getTypicalInstructor();
        Course typicalCourse = typicalInstructor.getCourse();
        FeedbackSession typicalFeedbackSessionForCourse = getTypicalFeedbackSessionForCourse(typicalCourse);
        FeedbackQuestion typicalQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSessionForCourse);

        loginAsInstructor(typicalInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getId().toString(),
        };

        DeleteFeedbackQuestionAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Feedback question deleted!", actionOutput.getMessage());
    }

    @Test
    protected void testExecute_feedbackQuestionDoesNotExist_failSilently() {
        UUID nonexistentQuestionId = UUID.fromString("11110000-0000-0000-0000-000000000000");
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, nonexistentQuestionId.toString(),
        };

        DeleteFeedbackQuestionAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Feedback question deleted!", actionOutput.getMessage());
    }

    @Test
    protected void testExecute_invalidFeedbackQuestionId_failSilently() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "invalid-feedbackquestion-id",
        };

        DeleteFeedbackQuestionAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Feedback question deleted!", actionOutput.getMessage());
    }

    @Test
    protected void textExecute_missingFeedbackQuestionId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, null,
        };

        verifyHttpParameterFailure(params);
    }

}
