package teammates.ui.webapi;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.TaskWrapper;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link SendJoinReminderEmailActionTest}.
 */
public class SendJoinReminderEmailActionTest extends BaseActionTest<SendJoinReminderEmailAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.JOIN_REMIND;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();
        String courseId = instructor1OfCourse1.getCourseId();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail());

        ______TS("Typical case: Send email to remind an instructor to register for the course");

        loginAsInstructor(instructorId);
        InstructorAttributes anotherInstructorOfCourse1 = typicalBundle.instructors.get("instructorNotYetJoinCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, anotherInstructorOfCourse1.getEmail(),
        };

        SendJoinReminderEmailAction sendJoinReminderEmailAction = getAction(submissionParams);
        JsonResult result = getJsonResult(sendJoinReminderEmailAction);

        MessageOutput msg = (MessageOutput) result.getOutput();
        assertEquals("An email has been sent to " + anotherInstructorOfCourse1.getEmail(), msg.getMessage());

        verifySpecifiedTasksAdded(Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        TaskWrapper taskAdded = mockTaskQueuer.getTasksAdded().get(0);
        Map<String, String> paramMap = taskAdded.getParamMap();
        assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID));
        assertEquals(anotherInstructorOfCourse1.getEmail(), paramMap.get(Const.ParamsNames.INSTRUCTOR_EMAIL));

        ______TS("Typical case: Send email to remind a student to register for the course");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        sendJoinReminderEmailAction = getAction(submissionParams);
        result = getJsonResult(sendJoinReminderEmailAction);

        msg = (MessageOutput) result.getOutput();
        assertEquals("An email has been sent to " + student1InCourse1.getEmail(), msg.getMessage());

        verifySpecifiedTasksAdded(Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        taskAdded = mockTaskQueuer.getTasksAdded().get(0);
        paramMap = taskAdded.getParamMap();
        assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID));
        assertEquals(student1InCourse1.getEmail(), paramMap.get(Const.ParamsNames.STUDENT_EMAIL));

        ______TS("Masquerade mode: Send emails to all unregistered student to remind registering for the course");

        loginAsAdmin();
        StudentAttributes unregisteredStudent1 = StudentAttributes
                .builder(courseId, "unregistered1@email.com")
                .withName("Unregistered student 1")
                .withSectionName("Section 1")
                .withTeamName("Team Unregistered")
                .withComment("")
                .build();
        StudentAttributes unregisteredStudent2 = StudentAttributes
                .builder(courseId, "unregistered2@email.com")
                .withName("Unregistered student 2")
                .withSectionName("Section 1")
                .withTeamName("Team Unregistered")
                .withComment("")
                .build();
        logic.createStudent(unregisteredStudent1);
        logic.createStudent(unregisteredStudent2);

        // Reassign the attributes to retrieve their keys
        unregisteredStudent1 = logic.getStudentForEmail(courseId, unregisteredStudent1.getEmail());
        unregisteredStudent2 = logic.getStudentForEmail(courseId, unregisteredStudent2.getEmail());

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        sendJoinReminderEmailAction = getAction(addUserIdToParams(instructorId, submissionParams));
        result = getJsonResult(sendJoinReminderEmailAction);

        msg = (MessageOutput) result.getOutput();
        assertEquals("Emails have been sent to unregistered students.", msg.getMessage());

        // 2 unregistered students, thus 2 emails queued to be sent
        verifySpecifiedTasksAdded(Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME, 2);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            paramMap = task.getParamMap();
            assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID));
        }

        logic.deleteStudentCascade(courseId, unregisteredStudent1.getEmail());
        logic.deleteStudentCascade(courseId, unregisteredStudent2.getEmail());

        ______TS("Typical case: no unregistered students in course");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        sendJoinReminderEmailAction = getAction(addUserIdToParams(instructorId, submissionParams));
        result = getJsonResult(sendJoinReminderEmailAction);

        msg = (MessageOutput) result.getOutput();
        assertEquals("Emails have been sent to unregistered students.", msg.getMessage());

        // no unregistered students, thus no emails sent
        verifyNoTasksAdded();

        ______TS("Failure case: Invalid email parameter");

        String invalidEmail = "invalidEmail.com";
        String[] invalidInstructorEmailSubmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, invalidEmail,
        };

        EntityNotFoundException entityNotFoundException = verifyEntityNotFound(
                addUserIdToParams(instructorId, invalidInstructorEmailSubmissionParams));
        assertEquals("Instructor with email " + invalidEmail + " does not exist "
                + "in course " + courseId + "!", entityNotFoundException.getMessage());

        String[] invalidStudentEmailSubmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENT_EMAIL, invalidEmail,
        };

        entityNotFoundException = verifyEntityNotFound(invalidStudentEmailSubmissionParams);
        assertEquals("Student with email " + invalidEmail + " does not exist "
                + "in course " + courseId + "!", entityNotFoundException.getMessage());

        ______TS("Failure case: Invalid course id parameter");

        String[] invalidCourseIdSubmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "invalidCourseId",
                Const.ParamsNames.INSTRUCTOR_EMAIL, anotherInstructorOfCourse1.getEmail(),
        };

        entityNotFoundException = verifyEntityNotFound(invalidCourseIdSubmissionParams);
        assertEquals("Course with ID invalidCourseId does not exist!", entityNotFoundException.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").getCourseId(),
        };

        ______TS("Sending registration emails to all students");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT, submissionParams);

        ______TS("Sending registration emails to student");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, typicalBundle.students.get("student1InCourse1").getEmail(),
        };
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT, submissionParams);

        ______TS("Sending registration emails to instructor");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, typicalBundle.instructors.get("instructor1OfCourse1").getEmail(),
        };
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, submissionParams);
    }

}
