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
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testAccessControl() {
        String[] params = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
        verifyUnaccessibleWithoutModifyCoursePrivilege(params);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        ______TS("Not enough parameters");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
        verifyAssumptionFailure();

        // This is commented out as InstructorFeedbackAddAction already assertNotNull using Assumption
        // verifyAssumptionFailure(Const.ParamsNames.COURSE_ID, instructor1ofCourse1.courseId);

        ______TS("Typical case");

        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes question1 = FeedbackQuestionsLogic
                                                   .inst()
                                                   .getFeedbackQuestion(session1.getFeedbackSessionName(),
                                                                        session1.getCourseId(), 1);
        FeedbackQuestionAttributes question2 = FeedbackQuestionsLogic
                                                   .inst()
                                                   .getFeedbackQuestion(session1.getFeedbackSessionName(),
                                                                        session1.getCourseId(), 2);

        String[] params = new String[]{
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

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=" + instructor1ofCourse1.courseId
                     + "&fsname=Second+feedback+session" + "&user=" + instructor1ofCourse1.googleId + "&error=false",
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
                                    + "<span class=\"bold\">Essay question:</span> Rate 1 other student's "
                                    + "product|||/page/instructorFeedbackQuestionCopy";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Error: Indicate no questions to be copied");

        params = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "Second feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };

        a = getAction(params);
        rr = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=" + instructor1ofCourse1.courseId
                     + "&fsname=Second+feedback+session" + "&user=" + instructor1ofCourse1.googleId + "&error=true",
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
        gaeSimulation.loginAsAdmin("admin.user");

        params = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "Second feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME + "-0", question3.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID + "-0", question3.getCourseId(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-0", question3.getId(),
        };
        params = addUserIdToParams(instructor1ofCourse1.googleId, params);

        a = getAction(params);
        rr = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE + "?courseid=" + instructor1ofCourse1.courseId
                     + "&fsname=Second+feedback+session" + "&user=" + instructor1ofCourse1.googleId + "&error=false",
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
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
    }

    @Override
    protected InstructorFeedbackQuestionCopyAction getAction(String... params) {
        return (InstructorFeedbackQuestionCopyAction) gaeSimulation.getActionObject(getActionUri(), params);
    }
}
