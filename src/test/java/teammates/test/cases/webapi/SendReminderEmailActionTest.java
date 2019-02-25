package teammates.test.cases.webapi;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.common.util.TaskWrapper;
import teammates.logic.core.StudentsLogic;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.SendReminderEmailAction;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link SendReminderEmailActionTest}.
 */
public class SendReminderEmailActionTest extends BaseActionTest<SendReminderEmailAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_SEND_REMINDER_EMAILS;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        ______TS("Typical case: Send email to remind an instructor to register for the course");

        loginAsInstructor(instructorId);
        InstructorAttributes anotherInstructorOfCourse1 = typicalBundle.instructors.get("instructorNotYetJoinCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, anotherInstructorOfCourse1.email,
        };

        SendReminderEmailAction remindAction = getAction(submissionParams);
        JsonResult r = getJsonResult(remindAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        MessageOutput msg = (MessageOutput) r.getOutput();
        assertEquals("An email has been sent to " + anotherInstructorOfCourse1.email, msg.getMessage());

        verifySpecifiedTasksAdded(remindAction, Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        TaskWrapper taskAdded = remindAction.getTaskQueuer().getTasksAdded().get(0);
        Map<String, String[]> paramMap = taskAdded.getParamMap();
        assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID)[0]);
        assertEquals(anotherInstructorOfCourse1.email, paramMap.get(Const.ParamsNames.INSTRUCTOR_EMAIL)[0]);

        ______TS("Typical case: Send email to remind a student to register for the course from course details page");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
        };

        remindAction = getAction(submissionParams);
        r = getJsonResult(remindAction);

        msg = (MessageOutput) r.getOutput();
        assertEquals("An email has been sent to " + student1InCourse1.email, msg.getMessage());

        ______TS("Typical case: Send email to remind a student to register for the course from student list page");

        remindAction = getAction(submissionParams);
        r = getJsonResult(remindAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("An email has been sent to " + student1InCourse1.email, msg.getMessage());

        verifySpecifiedTasksAdded(remindAction, Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        taskAdded = remindAction.getTaskQueuer().getTasksAdded().get(0);
        paramMap = taskAdded.getParamMap();
        assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID)[0]);
        assertEquals(student1InCourse1.email, paramMap.get(Const.ParamsNames.STUDENT_EMAIL)[0]);

        ______TS("Masquerade mode: Send emails to all unregistered student to remind registering for the course");

        loginAsAdmin();
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
        StudentsLogic.inst().createStudentCascade(unregisteredStudent1);
        StudentsLogic.inst().createStudentCascade(unregisteredStudent2);

        /* Reassign the attributes to retrieve their keys */
        unregisteredStudent1 = StudentsLogic.inst().getStudentForEmail(courseId, unregisteredStudent1.email);
        unregisteredStudent2 = StudentsLogic.inst().getStudentForEmail(courseId, unregisteredStudent2.email);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        remindAction = getAction(addUserIdToParams(instructorId, submissionParams));
        r = getJsonResult(remindAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("Emails have been sent to unregistered students.", msg.getMessage());

        // 2 unregistered students, thus 2 emails queued to be sent
        verifySpecifiedTasksAdded(remindAction, Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME, 2);

        List<TaskWrapper> tasksAdded = remindAction.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            paramMap = task.getParamMap();
            assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID)[0]);
        }

        StudentsLogic.inst().deleteStudentCascade(courseId, unregisteredStudent1.email);
        StudentsLogic.inst().deleteStudentCascade(courseId, unregisteredStudent2.email);

        ______TS("Typical case: no unregistered students in course");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        remindAction = getAction(addUserIdToParams(instructorId, submissionParams));
        r = getJsonResult(remindAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("Emails have been sent to unregistered students.", msg.getMessage());

        // no unregistered students, thus no emails sent
        verifyNoTasksAdded(remindAction);

        ______TS("Failure case: Invalid email parameter");

        String invalidEmail = "invalidEmail.com";
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, invalidEmail,
        };

        try {
            getAction(addUserIdToParams(instructorId, submissionParams));
        } catch (EntityNotFoundException e) {
            assertEquals("Instructor with email " + invalidEmail + " does not exist "
                    + "in course " + courseId + "!", e.getMessage());
        }

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, invalidEmail,
        };

        try {
            getAction(addUserIdToParams(instructorId, submissionParams));
        } catch (EntityNotFoundException e) {
            assertEquals("Student with email " + invalidEmail + " does not exist "
                    + "in course " + courseId + "!", e.getMessage());
        }

        ______TS("Failure case: Invalid course id parameter");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "invalidCourseId",
                Const.ParamsNames.INSTRUCTOR_EMAIL, anotherInstructorOfCourse1.email,
        };

        try {
            getAction(addUserIdToParams(instructorId, submissionParams));
        } catch (EntityNotFoundException e) {
            assertEquals("Course with ID " + courseId + " does not exist!", e.getMessage());
        }
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").courseId,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
