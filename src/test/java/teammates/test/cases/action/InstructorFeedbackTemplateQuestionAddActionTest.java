package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorFeedbackTemplateQuestionAddAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackTemplateQuestionAddAction}.
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
    public void testExecuteAndPostProcess() {
        // split out into smaller tests
    }

    @Test
    public void testAddTypicalTemplateQuestion() {

        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
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

        FeedbackQuestionAttributes fqa = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.getFeedbackSessionName(),
                fs.getCourseId(), 6);
        assertNotNull(fqa);
    }

    @Test
    public void testIncompleteParameters() {

        ______TS("Missing course, session name and question number");

        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
        verifyAssumptionFailure();

        ______TS("Missing question number");

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        InstructorFeedbackTemplateQuestionAddAction action = getAction(params);
        RedirectResult result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        true),
                result.getDestinationWithParams());

        assertEquals("No template questions are indicated to be added", result.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackTemplateQuestionAdd|||"
                + "instructorFeedbackTemplateQuestionAdd|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||"
                + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "|||/page/instructorFeedbackTemplateQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }

    @Test
    public void testInvalidQuestionNumber() {

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("Input for template question is more than the range");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_TEMPLATE_NUMBER, "6"
        };
        verifyAssumptionFailure(params);

        ______TS("Input for template question is lesser than the range");
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_TEMPLATE_NUMBER, "0");
        verifyAssumptionFailure(params);

        ______TS("Input for template question is a word");
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_TEMPLATE_NUMBER, "ABC");
        try {
            InstructorFeedbackTemplateQuestionAddAction action = getAction(params);
            action.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (NumberFormatException e) {
            ignoreExpectedException();
        }
    }

    @Test
    public void testMasqueradeMode() {

        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_TEMPLATE_NUMBER, "2"
        };
        params = addUserIdToParams(instructor1ofCourse1.googleId, params);

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

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackTemplateQuestionAdd|||"
                                    + "instructorFeedbackTemplateQuestionAdd|||true|||"
                                    + "Instructor(M)|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Added Feedback Template Question for Feedback Session:<span class=\"bold\">"
                                    + "(First feedback session)</span> for Course "
                                    + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + " created.<br><span class=\"bold\">Essay question:</span> "
                                    + "Comments about your contribution (shown to other teammates)|||"
                                    + "/page/instructorFeedbackTemplateQuestionAdd";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedLogMessage, action.getLogMessage(), adminUserId);

        FeedbackQuestionAttributes fqa = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.getFeedbackSessionName(),
                fs.getCourseId(), 6);
        assertNotNull(fqa);
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
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.FEEDBACK_QUESTION_TEMPLATE_NUMBER, "1"
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
        verifyUnaccessibleWithoutModifyCoursePrivilege(params);
    }
}
