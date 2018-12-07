package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorEditInstructorFeedbackPageAction;
import teammates.ui.controller.ShowPageResult;

/**
 * SUT: {@link InstructorEditInstructorFeedbackPageAction}.
 */
public class InstructorEditInstructorFeedbackPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_EDIT_INSTRUCTOR_FEEDBACK_PAGE;
    }

    @Override
    protected void prepareTestData() {
        super.prepareTestData();
        dataBundle = loadDataBundle("/InstructorEditInstructorFeedbackPageTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = dataBundle.instructors.get("IEIFPTCourseinstr");
        InstructorAttributes moderatedInstructor = dataBundle.instructors.get("IEIFPTCoursehelper1");
        InstructorEditInstructorFeedbackPageAction editInstructorFpAction;
        ShowPageResult showPageResult;

        String courseId = moderatedInstructor.courseId;
        String feedbackSessionName = "";
        String moderatedInstructorEmail = "IEIFPTCoursehelper1@gmail.tmt";
        String[] submissionParams;

        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("typical success case");
        feedbackSessionName = "First feedback session";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedInstructorEmail
        };

        editInstructorFpAction = getAction(submissionParams);
        showPageResult = getShowPageResult(editInstructorFpAction);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT,
                        false,
                        instructor.googleId),
                showPageResult.getDestinationWithParams());
        assertEquals("", showPageResult.getStatusMessage());
        AssertHelper.assertLogMessageEquals(
                "TEAMMATESLOG|||instructorEditInstructorFeedbackPage|||instructorEditInstructorFeedbackPage"
                    + "|||true|||Instructor|||IEIFPTCourseinstr|||IEIFPTCourseinstr|||IEIFPTCourseintr@course1.tmt|||"
                    + "Moderating feedback session for instructor (" + moderatedInstructor.email + ")<br>"
                    + "Session Name: First feedback session<br>Course ID: IEIFPTCourse|||"
                    + "/page/instructorEditInstructorFeedbackPage",
                editInstructorFpAction.getLogMessage());

        ______TS("success: another feedback");
        feedbackSessionName = "Another feedback session";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedInstructorEmail
        };

        editInstructorFpAction = getAction(submissionParams);
        showPageResult = getShowPageResult(editInstructorFpAction);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT,
                        false,
                        instructor.googleId),
                showPageResult.getDestinationWithParams());
        assertEquals("", showPageResult.getStatusMessage());
        String logMessage = "TEAMMATESLOG|||instructorEditInstructorFeedbackPage|||"
                            + "instructorEditInstructorFeedbackPage|||true|||Instructor|||IEIFPTCourseinstr|||"
                            + "IEIFPTCourseinstr|||IEIFPTCourseintr@course1.tmt|||"
                            + "Moderating feedback session for instructor (" + moderatedInstructor.email + ")<br>"
                            + "Session Name: Another feedback session<br>Course ID: IEIFPTCourse|||"
                            + "/page/instructorEditInstructorFeedbackPage";
        AssertHelper.assertLogMessageEquals(logMessage, editInstructorFpAction.getLogMessage());

        ______TS("failure: accessing non-existent moderatedinstructor email");
        gaeSimulation.loginAsInstructor(instructor.googleId);
        moderatedInstructorEmail = "non-exIstentEmail@gsail.tmt";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedInstructorEmail
        };

        try {
            editInstructorFpAction = getAction(submissionParams);
            editInstructorFpAction.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (EntityNotFoundException enfe) {
            assertEquals("Instructor Email " + moderatedInstructorEmail
                         + " does not exist in " + courseId + ".", enfe.getMessage());
        }
    }

    @Override
    protected InstructorEditInstructorFeedbackPageAction getAction(String... params) {
        return (InstructorEditInstructorFeedbackPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes moderatedInstructor = typicalBundle.instructors.get("helperOfCourse1");
        String courseId = moderatedInstructor.courseId;
        String feedbackSessionName = "First feedback session";
        String moderatedInstructorEmail = "helper@course1.tmt";

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedInstructorEmail
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
    }
}
