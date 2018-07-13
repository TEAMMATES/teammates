package teammates.test.cases.action;

import org.testng.annotations.Test;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackTemplateQuestionAddAction;

/**
 * SUT: {@link teammates.ui.controller.InstructorFeedbackTemplateQuestionAddAction}.
 */
public class InstructorFeedbackTemplateQuestionAddActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_TEMPLATE_QUESTION_ADD;
    }

    @Override
    protected InstructorFeedbackTemplateQuestionAddAction getAction(String[] params) {
        return (InstructorFeedbackTemplateQuestionAddAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {

    }

    @Override
    @Test
    public void testAccessControl() {

    }
}
