package teammates.test.cases.action;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorFeedbackQuestionCopyAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackQuestionCopyAction}.
 */
public class InstructorFeedbackQuestionCopyActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_COPY;
    }

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        super.prepareTestData();
    }

    @Override
    @Test
    public void testAccessControl() {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
        verifyUnaccessibleWithoutModifyCoursePrivilege(params);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("Not enough parameters");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
        verifyAssumptionFailure();

        // This is commented out as InstructorFeedbackAddAction already assertNotNull using Assumption
        // verifyAssumptionFailure(Const.ParamsNames.COURSE_ID, instructor1ofCourse1.courseId);

        ______TS("Typical case");

        FeedbackSessionAttributes session1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes question1 = FeedbackQuestionsLogic
                                                   .inst()
                                                   .getFeedbackQuestion(session1.getFeedbackSessionName(),
                                                                        session1.getCourseId(), 1);
        FeedbackQuestionAttributes question2 = FeedbackQuestionsLogic
                                                   .inst()
                                                   .getFeedbackQuestion(session1.getFeedbackSessionName(),
                                                                        session1.getCourseId(), 2);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "Second feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME + "-0", question1.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID + "-0", question1.getCourseId(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-0", question1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME + "-1", question2.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID + "-1", question2.getCourseId(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", question2.getId()
        };

        InstructorFeedbackQuestionCopyAction a = getAction(params);
        RedirectResult rr = getRedirectResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "Second+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                rr.getDestinationWithParams());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionCopy|||"
                                    + "instructorFeedbackQuestionCopy|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Created Feedback Question for Feedback Session:"
                                    + "<span class=\"bold\">(Second feedback session)"
                                    + "</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span> "
                                    + "created.<br><span class=\"bold\">"
                                    + "Essay question:</span> What is the best selling point of your product?"
                                    + "Created Feedback Question for "
                                    + "Feedback Session:<span class=\"bold\">(Second feedback session)</span> "
                                    + "for Course <span class=\"bold\">"
                                    + "[idOfTypicalCourse1]</span> created.<br>"
                                    + "<span class=\"bold\">Essay question:</span> Rate 1 other student&#39;s "
                                    + "product|||/page/instructorFeedbackQuestionCopy";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Question text requires sanitization");

        FeedbackSessionAttributes sanitizationSession =
                typicalBundle.feedbackSessions.get("session1InTestingSanitizationCourse");
        question1 = FeedbackQuestionsLogic
                .inst()
                .getFeedbackQuestion(sanitizationSession.getFeedbackSessionName(),
                        sanitizationSession.getCourseId(), 1);

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "Second feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-0", question1.getId()
        };

        a = getAction(params);
        rr = getRedirectResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "Second+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                rr.getDestinationWithParams());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionCopy|||"
                + "instructorFeedbackQuestionCopy|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||"
                + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Created Feedback Question for Feedback Session:"
                + "<span class=\"bold\">(Second feedback session)"
                + "</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span> "
                + "created.<br><span class=\"bold\">"
                + "Essay question:</span> "
                + "Testing quotation marks &#39;&quot; "
                + "Testing unclosed tags &lt;&#x2f;td&gt;&lt;&#x2f;div&gt; "
                + "Testing script injection &lt;script&gt; alert(&#39;hello&#39;); &lt;&#x2f;script&gt;"
                + "|||/page/instructorFeedbackQuestionCopy";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Error: Indicate no questions to be copied");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "Second feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };

        a = getAction(params);
        rr = getRedirectResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "Second+feedback+session",
                        instructor1ofCourse1.googleId,
                        true),
                rr.getDestinationWithParams());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionCopy|||"
                             + "instructorFeedbackQuestionCopy|||true|||"
                             + "Instructor|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "|||/page/instructorFeedbackQuestionCopy";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Masquerade mode");

        FeedbackQuestionAttributes question3 = FeedbackQuestionsLogic
                                                   .inst()
                                                   .getFeedbackQuestion(session1.getFeedbackSessionName(),
                                                                        session1.getCourseId(), 3);
        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "Second feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME + "-0", question3.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID + "-0", question3.getCourseId(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-0", question3.getId(),
        };
        params = addUserIdToParams(instructor1ofCourse1.googleId, params);

        a = getAction(params);
        rr = getRedirectResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "Second+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                rr.getDestinationWithParams());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionCopy|||"
                             + "instructorFeedbackQuestionCopy|||true|||"
                             + "Instructor(M)|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Created Feedback Question for Feedback Session:"
                             + "<span class=\"bold\">(Second feedback session)</span> "
                             + "for Course <span class=\"bold\">[idOfTypicalCourse1]</span> "
                             + "created.<br><span class=\"bold\">"
                             + "Essay question:</span> My comments on the class|||"
                             + "/page/instructorFeedbackQuestionCopy";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedLogMessage, a.getLogMessage(), adminUserId);
    }

    @Override
    protected InstructorFeedbackQuestionCopyAction getAction(String... params) {
        return (InstructorFeedbackQuestionCopyAction) gaeSimulation.getActionObject(getActionUri(), params);
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
}
