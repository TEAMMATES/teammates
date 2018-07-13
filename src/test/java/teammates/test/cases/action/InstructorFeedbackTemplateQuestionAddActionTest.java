package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorFeedbackTemplateQuestionAddAction;
import teammates.ui.controller.RedirectResult;

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

        ______TS("Typical case for question 1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_TEMPLATE_NUMBER, "1"
        };


        InstructorFeedbackTemplateQuestionAddAction action = getAction(params);
        RedirectResult result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackTemplateQuestionAdd|||"
                                    + "instructorFeedbackTemplateQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Added Feedback Template Question for Feedback Session:<span class=\"bold\">"
                                    + "(First feedback session)</span> for Course "
                                    + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + " created.<br><span class=\"bold\">Team contribution question:</span> "
                                    + "Your estimate of how much each team member has contributed.|||"
                                    + "/page/instructorFeedbackTemplateQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

    }

    protected String getPageResultDestination(
            String parentUri, String courseId, String fsname, String userId, boolean isError) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.COURSE_ID, courseId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.FEEDBACK_SESSION_NAME, fsname);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.USER_ID, userId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.ERROR, Boolean.toString(isError));
        return pageDestination;
    }

    @Override
    @Test
    public void testAccessControl() {

    }
}
