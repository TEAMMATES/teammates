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
 * SUT: {@link FeedbackSessionResendPublishedEmailWorkerAction}.
 */
public class FeedbackSessionResendPublishedEmailWorkerActionTest
        extends BaseActionTest<FeedbackSessionResendPublishedEmailWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        ______TS("Resend feedback session results published email");

        FeedbackSessionAttributes publishedSession = typicalBundle.feedbackSessions.get("closedSession");
        StudentAttributes student1 = typicalBundle.students.get("student1InCourse1");
        InstructorAttributes instructor1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] usersToRemind = new String[] {
                student1.getEmail(), instructor1.getEmail(), "non-existent",
        };

        FeedbackSessionRemindRequest remindRequest = new FeedbackSessionRemindRequest(publishedSession.getCourseId(),
                publishedSession.getFeedbackSessionName(), instructor1.getGoogleId(), usersToRemind, true);

        FeedbackSessionResendPublishedEmailWorkerAction action = getAction(remindRequest);
        action.execute();

        // send 2 emails + 1 notification as specified in the submission parameters
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 3);

        String courseName = logic.getCourse(publishedSession.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            String expectedSubject = (email.getIsCopy() ? EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX : "")
                    + String.format(EmailType.FEEDBACK_PUBLISHED.getSubject(),
                    courseName, publishedSession.getFeedbackSessionName());
            assertEquals(expectedSubject, email.getSubject());
            String recipient = email.getRecipient();
            assertTrue(recipient.equals(student1.getEmail()) || recipient.equals(instructor1.getEmail()));
        }
    }

}
