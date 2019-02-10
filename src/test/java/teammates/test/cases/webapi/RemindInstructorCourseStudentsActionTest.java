package teammates.test.cases.webapi;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.TaskWrapper;
import teammates.logic.core.StudentsLogic;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.RemindInstructorCourseStudentsAction;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link RemindInstructorCourseStudentsAction}.
 */
public class RemindInstructorCourseStudentsActionTest extends BaseActionTest<RemindInstructorCourseStudentsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_COURSE_DETAILS_REMIND;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    public void testExecute() throws Exception {

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

        RemindInstructorCourseStudentsAction remindAction = getAction(submissionParams);
        JsonResult result = getJsonResult(remindAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        MessageOutput output = (MessageOutput) result.getOutput();
        assertEquals(Const.StatusMessages.COURSE_REMINDER_SENT_TO + anotherInstructorOfCourse1.email,
                output.getMessage());

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
        result = getJsonResult(remindAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        output = (MessageOutput) result.getOutput();
        assertEquals(Const.StatusMessages.COURSE_REMINDER_SENT_TO + student1InCourse1.email,
                output.getMessage());

        ______TS("Typical case: Send email to remind a student to register for the course from student list page");

        remindAction = getAction(submissionParams);
        result = getJsonResult(remindAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        output = (MessageOutput) result.getOutput();
        assertEquals(Const.StatusMessages.COURSE_REMINDER_SENT_TO + student1InCourse1.email,
                output.getMessage());

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
        result = getJsonResult(remindAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        output = (MessageOutput) result.getOutput();
        assertEquals(Const.StatusMessages.COURSE_REMINDERS_SENT, output.getMessage());

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
        result = getJsonResult(remindAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        output = (MessageOutput) result.getOutput();
        assertEquals(Const.StatusMessages.COURSE_REMINDERS_SENT, output.getMessage());

        // no unregistered students, thus no emails sent
        verifyNoTasksAdded(remindAction);
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
