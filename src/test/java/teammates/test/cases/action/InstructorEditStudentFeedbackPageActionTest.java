package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorEditStudentFeedbackPageAction;
import teammates.ui.controller.ShowPageResult;

/**
 * SUT: {@link InstructorEditStudentFeedbackPageAction}.
 */
public class InstructorEditStudentFeedbackPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE;
    }

    @Override
    protected void prepareTestData() {
        super.prepareTestData();
        dataBundle = loadDataBundle("/InstructorEditStudentFeedbackPageTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = dataBundle.instructors.get("IESFPTCourseinstr");
        InstructorAttributes instructorHelper = dataBundle.instructors.get("IESFPTCoursehelper1");
        String idOfInstructor = instructor.googleId;
        String idOfInstructorHelper = instructorHelper.googleId;
        StudentAttributes student = dataBundle.students.get("student1InCourse1");

        gaeSimulation.loginAsInstructor(idOfInstructor);

        ______TS("typical success case");

        String feedbackSessionName = "First feedback session";
        String courseId = student.course;
        String moderatedStudentEmail = student.email;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        InstructorEditStudentFeedbackPageAction editPageAction = getAction(submissionParams);
        ShowPageResult showPageResult = getShowPageResult(editPageAction);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT, false, idOfInstructor),
                showPageResult.getDestinationWithParams());
        assertEquals("", showPageResult.getStatusMessage());

        AssertHelper.assertLogMessageEquals(
                "TEAMMATESLOG|||instructorEditStudentFeedbackPage|||instructorEditStudentFeedbackPage|||true|||"
                        + "Instructor|||IESFPTCourseinstr|||IESFPTCourseinstr|||IESFPTCourseintr@course1.tmt|||"
                        + "Moderating feedback session for student (" + student.email + ")<br>"
                        + "Session Name: First feedback session<br>Course ID: IESFPTCourse|||"
                        + "/page/instructorEditStudentFeedbackPage",
                editPageAction.getLogMessage());

        ______TS("success: another feedback");

        feedbackSessionName = "Another feedback session";
        courseId = student.course;
        moderatedStudentEmail = student.email;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        editPageAction = getAction(submissionParams);
        showPageResult = getShowPageResult(editPageAction);

        ______TS("success case: closed session");

        feedbackSessionName = "Closed feedback session";
        courseId = student.course;
        moderatedStudentEmail = student.email;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        editPageAction = getAction(submissionParams);
        showPageResult = getShowPageResult(editPageAction);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT,
                        false,
                        idOfInstructor),
                showPageResult.getDestinationWithParams());
        assertEquals("", showPageResult.getStatusMessage());

        gaeSimulation.loginAsInstructor(idOfInstructor);
        ______TS("success case: moderate team");

        feedbackSessionName = "Closed feedback session";
        courseId = student.course;
        String moderatedStudentTeam = student.team;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentTeam
        };

        editPageAction = getAction(submissionParams);
        showPageResult = getShowPageResult(editPageAction);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT,
                        false,
                        idOfInstructor),
                showPageResult.getDestinationWithParams());
        assertEquals("", showPageResult.getStatusMessage());

        AssertHelper.assertLogMessageEquals(
                "TEAMMATESLOG|||instructorEditStudentFeedbackPage|||instructorEditStudentFeedbackPage|||true|||"
                        + "Instructor|||IESFPTCourseinstr|||IESFPTCourseinstr|||IESFPTCourseintr@course1.tmt|||"
                        + "Moderating feedback session for student (" + student.email + ")<br>"
                        + "Session Name: Closed feedback session<br>Course ID: IESFPTCourse|||"
                        + "/page/instructorEditStudentFeedbackPage",
                editPageAction.getLogMessage());

        gaeSimulation.loginAsInstructor(idOfInstructorHelper);

        ______TS("failure: does not have privilege");

        feedbackSessionName = "First feedback session";
        courseId = "IESFPTCourse";
        moderatedStudentEmail = student.email;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        try {
            editPageAction = getAction(submissionParams);
            showPageResult = getShowPageResult(editPageAction);
        } catch (UnauthorizedAccessException e) {
            assertEquals(
                    "Feedback session [First feedback session] is not accessible to instructor ["
                            + instructorHelper.email + "] for privilege ["
                            + Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                            + "] on section [Section 1]",
                    e.getMessage());
        }

        gaeSimulation.loginAsInstructor(idOfInstructor);

        ______TS("failure: non-existent moderatedstudent email");

        moderatedStudentEmail = "non-exIstentEmail@gsail.tmt";

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        try {
            editPageAction = getAction(submissionParams);
            showPageResult = getShowPageResult(editPageAction);
            signalFailureToDetectException();
        } catch (EntityNotFoundException enfe) {
            assertEquals("An entity with the identifier "
                            + moderatedStudentEmail + " does not exist in " + courseId
                            + ".",
                         enfe.getMessage());

        }
    }

    @Override
    protected InstructorEditStudentFeedbackPageAction getAction(String... params) {
        return (InstructorEditStudentFeedbackPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {

        StudentAttributes student = typicalBundle.students.get("student1InCourse1");

        String feedbackSessionName = "First feedback session";
        String courseId = student.course;
        String moderatedStudentEmail = student.email;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifySessionCommentInSectionsPrivilege(submissionParams);
    }
}
