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
    protected void testExecute_feedbackQuestionDoesNotExist_failSilently() {
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

}
