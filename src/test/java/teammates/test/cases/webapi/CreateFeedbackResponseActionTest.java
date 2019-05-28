package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.CreateFeedbackResponseAction;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.request.FeedbackResponseCreateRequest;

/**
 * SUT: {@link CreateFeedbackResponseAction}.
 */
public class CreateFeedbackResponseActionTest extends BaseActionTest<CreateFeedbackResponseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // TODO
        Intent studentSubIntent = Intent.STUDENT_SUBMISSION;
        Intent instructorSubIntent = Intent.INSTRUCTOR_SUBMISSION;
        FeedbackQuestionAttributes feedbackQuestion = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        ______TS("not enough attributes");
        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.INTENT,studentSubIntent.toString());
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_QUESTION_ID,feedbackQuestion.getId());
        ______TS("typical case");
        String[] params = {
                Const.ParamsNames.INTENT,studentSubIntent.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID,feedbackQuestion.getId(),
        };

    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        // TODO
    }

    private FeedbackResponseCreateRequest getTypicalResponseCreateRequest(){

    }

}
