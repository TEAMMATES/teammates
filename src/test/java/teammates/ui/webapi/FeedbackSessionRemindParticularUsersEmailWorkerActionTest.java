package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.ui.request.FeedbackSessionRemindRequest;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link FeedbackSessionRemindParticularUsersEmailWorkerAction}.
 */
public class FeedbackSessionRemindParticularUsersEmailWorkerActionTest
        extends BaseActionTest<FeedbackSessionRemindParticularUsersEmailWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }

    @Override
    @Test
    public void testExecute() {

        ______TS("Send feedback session reminder email");

        FeedbackSessionAttributes session1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student1 = typicalBundle.students.get("student1InCourse1");
        InstructorAttributes instructor1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] usersToRemind = new String[] {
                student1.email, instructor1.email, "non-existent",
        };

        FeedbackSessionRemindRequest remindRequest = new FeedbackSessionRemindRequest(session1.getCourseId(),
                session1.getFeedbackSessionName(), instructor1.googleId, usersToRemind);

        FeedbackSessionRemindParticularUsersEmailWorkerAction action = getAction(remindRequest);
        action.execute();

        // send 3 emails as specified in the submission parameters
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 3);

        String courseName = logic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = action.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            assertEquals(String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(), courseName,
                                       session1.getFeedbackSessionName()),
                         email.getSubject());
            String recipient = email.getRecipient();
            assertTrue(recipient.equals(student1.email) || recipient.equals(instructor1.email));
        }
    }

}
