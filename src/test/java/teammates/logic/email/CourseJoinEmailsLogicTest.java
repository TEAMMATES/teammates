package teammates.logic.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const.TaskQueue;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.logic.api.MockTaskQueuer;
import teammates.logic.email.model.CourseEmailContext;
import teammates.logic.email.model.EmailContact;
import teammates.logic.email.model.UserCourseRegisteredEmailContext;
import teammates.test.BaseTestCase;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link CourseJoinEmailsLogic}.
 */
public class CourseJoinEmailsLogicTest extends BaseTestCase {

    private MockTaskQueuer taskQueuer;
    private CourseJoinEmailsLogic courseJoinEmailsLogic;

    @BeforeMethod
    public void setUpMethod() {
        taskQueuer = new MockTaskQueuer();
        courseJoinEmailsLogic = new CourseJoinEmailsLogic(EmailQueueService.withTaskQueuer(taskQueuer));
    }

    @Test
    public void enqueueUserCourseRegisteredEmail_validContext_enqueuesPriorityEmail() {
        CourseEmailContext courseContext = new CourseEmailContext(
                "CS101",
                "Software Engineering",
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));
        UserCourseRegisteredEmailContext userContext = new UserCourseRegisteredEmailContext(
                "student@teammates.tmt",
                "Student Name",
                false,
                "https://example.com/student/home");

        courseJoinEmailsLogic.enqueueUserCourseRegisteredEmail(courseContext, userContext);

        assertEquals(1, taskQueuer.getTasksAdded().size());
        TaskWrapper task = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, task.getQueueName());

        SendEmailRequest request = (SendEmailRequest) task.getRequestBody();
        EmailWrapper email = request.getEmail();
        assertEquals("student@teammates.tmt", email.getRecipient());
        assertEquals(EmailType.USER_COURSE_REGISTER, email.getType());
        assertEquals("TEAMMATES: Registered for Course [Software Engineering][Course ID: CS101]", email.getSubject());
        assertTrue(email.getContent().contains("Thank you for registering as a student"));
    }
}
