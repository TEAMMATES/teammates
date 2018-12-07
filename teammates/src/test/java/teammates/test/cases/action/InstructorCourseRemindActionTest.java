package teammates.test.cases.action;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.StringHelper;
import teammates.common.util.TaskWrapper;
import teammates.logic.core.StudentsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseRemindAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorCourseRemindAction}.
 */
public class InstructorCourseRemindActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;
        String adminUserId = "admin.user";

        ______TS("Typical case: Send email to remind an instructor to register for the course");
        gaeSimulation.loginAsInstructor(instructorId);
        InstructorAttributes anotherInstructorOfCourse1 = typicalBundle.instructors.get("instructorNotYetJoinCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, anotherInstructorOfCourse1.email
        };

        InstructorCourseRemindAction remindAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(remindAction);

        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, redirectResult.destination);
        assertFalse(redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_REMINDER_SENT_TO + anotherInstructorOfCourse1.email,
                     redirectResult.getStatusMessage());

        String expectedLogSegment = "Registration Key sent to the following users "
                + "in Course <span class=\"bold\">[" + courseId + "]</span>:<br>"
                + anotherInstructorOfCourse1.name + "<span class=\"bold\"> ("
                + anotherInstructorOfCourse1.email + ")" + "</span>.<br>"
                + StringHelper.encrypt(anotherInstructorOfCourse1.key) + "<br>";
        AssertHelper.assertContains(expectedLogSegment, remindAction.getLogMessage());

        verifySpecifiedTasksAdded(remindAction, Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        TaskWrapper taskAdded = remindAction.getTaskQueuer().getTasksAdded().get(0);
        Map<String, String[]> paramMap = taskAdded.getParamMap();
        assertEquals(courseId, paramMap.get(ParamsNames.COURSE_ID)[0]);
        assertEquals(anotherInstructorOfCourse1.email, paramMap.get(ParamsNames.INSTRUCTOR_EMAIL)[0]);

        ______TS("Typical case: Send email to remind a student to register for the course from course details page");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
                Const.ParamsNames.INSTRUCTOR_REMIND_STUDENT_IS_FROM, Const.PageNames.INSTRUCTOR_COURSE_DETAILS_PAGE
        };

        remindAction = getAction(submissionParams);
        redirectResult = getRedirectResult(remindAction);

        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE, redirectResult.destination);
        assertFalse(redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_REMINDER_SENT_TO + student1InCourse1.email,
                     redirectResult.getStatusMessage());

        expectedLogSegment = "Registration Key sent to the following users "
                + "in Course <span class=\"bold\">[" + courseId + "]</span>:<br>"
                + student1InCourse1.name + "<span class=\"bold\"> ("
                + student1InCourse1.email + ")" + "</span>.<br>";
        AssertHelper.assertContains(expectedLogSegment, remindAction.getLogMessage());

        ______TS("Typical case: Send email to remind a student to register for the course from student list page");

        submissionParams[5] = Const.PageNames.INSTRUCTOR_STUDENT_LIST_PAGE;

        remindAction = getAction(submissionParams);
        redirectResult = getRedirectResult(remindAction);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE, redirectResult.destination);
        assertFalse(redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_REMINDER_SENT_TO + student1InCourse1.email,
                redirectResult.getStatusMessage());

        expectedLogSegment = "Registration Key sent to the following users "
                + "in Course <span class=\"bold\">[" + courseId + "]</span>:<br>"
                + student1InCourse1.name + "<span class=\"bold\"> ("
                + student1InCourse1.email + ")" + "</span>.<br>";
        AssertHelper.assertContains(expectedLogSegment, remindAction.getLogMessage());

        verifySpecifiedTasksAdded(remindAction, Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        taskAdded = remindAction.getTaskQueuer().getTasksAdded().get(0);
        paramMap = taskAdded.getParamMap();
        assertEquals(courseId, paramMap.get(ParamsNames.COURSE_ID)[0]);
        assertEquals(student1InCourse1.email, paramMap.get(ParamsNames.STUDENT_EMAIL)[0]);

        ______TS("Masquerade mode: Send emails to all unregistered student to remind registering for the course");
        gaeSimulation.loginAsAdmin(adminUserId);
        StudentAttributes unregisteredStudent1 = StudentAttributes
                .builder(courseId, "Unregistered student 1", "unregistered1@email.com")
                .withSection("Section 1")
                .withTeam("Team Unregistered")
                .withComments("")
                .build();
        StudentAttributes unregisteredStudent2 = StudentAttributes
                .builder(courseId, "Unregistered student 2", "unregistered2@email.com")
                .withSection("Section 1")
                .withTeam("Team Unregistered")
                .withComments("")
                .build();
        StudentsLogic.inst().createStudentCascadeWithoutDocument(unregisteredStudent1);
        StudentsLogic.inst().createStudentCascadeWithoutDocument(unregisteredStudent2);

        /* Reassign the attributes to retrieve their keys */
        unregisteredStudent1 = StudentsLogic.inst().getStudentForEmail(courseId, unregisteredStudent1.email);
        unregisteredStudent2 = StudentsLogic.inst().getStudentForEmail(courseId, unregisteredStudent2.email);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId
        };
        remindAction = getAction(addUserIdToParams(instructorId, submissionParams));
        redirectResult = getRedirectResult(remindAction);
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE, redirectResult.destination);
        assertFalse(redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_REMINDERS_SENT,
                     redirectResult.getStatusMessage());

        // 2 unregistered students, thus 2 emails queued to be sent
        verifySpecifiedTasksAdded(remindAction, Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME, 2);

        List<TaskWrapper> tasksAdded = remindAction.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            paramMap = task.getParamMap();
            assertEquals(courseId, paramMap.get(ParamsNames.COURSE_ID)[0]);
        }

        expectedLogSegment = "Registration Key sent to the following users "
                + "in Course <span class=\"bold\">[" + courseId + "]</span>:<br>"
                + unregisteredStudent1.name + "<span class=\"bold\"> ("
                + unregisteredStudent1.email + ")" + "</span>.<br>"
                + StringHelper.encrypt(unregisteredStudent1.key)
                + "&studentemail=unregistered1%40email.com&courseid=idOfTypicalCourse1<br>"
                + unregisteredStudent2.name + "<span class=\"bold\"> ("
                + unregisteredStudent2.email + ")" + "</span>.<br>"
                + StringHelper.encrypt(unregisteredStudent2.key)
                + "&studentemail=unregistered2%40email.com&courseid=idOfTypicalCourse1<br>";
        AssertHelper.assertContains(expectedLogSegment, remindAction.getLogMessage());

        StudentsLogic.inst().deleteStudentCascadeWithoutDocument(courseId, unregisteredStudent1.email);
        StudentsLogic.inst().deleteStudentCascadeWithoutDocument(courseId, unregisteredStudent2.email);

        ______TS("Typical case: no unregistered students in course");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId
        };
        remindAction = getAction(addUserIdToParams(instructorId, submissionParams));
        redirectResult = getRedirectResult(remindAction);
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE, redirectResult.destination);
        assertFalse(redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_REMINDERS_SENT, redirectResult.getStatusMessage());
        expectedLogSegment = "Registration Key sent to the following users "
                + "in Course <span class=\"bold\">[" + courseId + "]</span>:<br>";
        AssertHelper.assertContains(expectedLogSegment, remindAction.getLogMessage());

        // no unregistered students, thus no emails sent
        verifyNoTasksAdded(remindAction);

        ______TS("Failure case: Invalid email parameter");

        String invalidEmail = "invalidEmail.com";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, invalidEmail
        };

        executeAndAssertEntityNotFoundException(instructorId, submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, invalidEmail
        };

        executeAndAssertEntityNotFoundException(instructorId, submissionParams);

        ______TS("Failure case: Invalid course id parameter");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "invalidCourseId",
                Const.ParamsNames.INSTRUCTOR_EMAIL, anotherInstructorOfCourse1.email
        };

        executeAndAssertEntityNotFoundException(instructorId, submissionParams);
    }

    private void executeAndAssertEntityNotFoundException(String instructorId,
                                                         String[] submissionParams) {
        try {
            InstructorCourseRemindAction remindAction = getAction(addUserIdToParams(instructorId, submissionParams));
            remindAction.executeAndPostProcess();
            signalFailureToDetectException(" - EntityNotFoundException");
        } catch (EntityNotFoundException e) {
            ignoreExpectedException();
        }
    }

    @Override
    protected InstructorCourseRemindAction getAction(String... params) {
        return (InstructorCourseRemindAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").courseId
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
