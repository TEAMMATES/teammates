package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link FeedbackSessionRemindEmailWorkerAction}.
 */
public class FeedbackSessionRemindEmailWorkerActionTest
        extends BaseActionTest<FeedbackSessionRemindEmailWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_REMIND_EMAIL_WORKER_URL;
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
    public void testExecute() throws Exception {

        ______TS("Send feedback session reminder email");

        FeedbackSessionAttributes session1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                ParamsNames.FEEDBACK_SESSION_NAME, session1.getFeedbackSessionName(),
                ParamsNames.COURSE_ID, session1.getCourseId(),
                ParamsNames.INSTRUCTOR_ID, instructor1.getGoogleId(),
        };

        FeedbackSessionRemindEmailWorkerAction action = getAction(submissionParams);
        action.execute();

        // 1 student and 4 instructors sent reminder, 1 instructor notified
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 6);

        Set<String> giverSet =
                logic.getGiverSetThatAnswerFeedbackSession(session1.getCourseId(), session1.getFeedbackSessionName());

        List<String> studentRecipientList = new ArrayList<>();
        for (StudentAttributes student : logic.getStudentsForCourse(session1.getCourseId())) {
            if (!giverSet.contains(student.getEmail())) {
                studentRecipientList.add(student.getEmail());
            }
        }

        List<String> instructorRecipientList = new ArrayList<>();
        List<String> instructorNotifiedList = new ArrayList<>();
        for (InstructorAttributes instructor : logic.getInstructorsForCourse(session1.getCourseId())) {
            if (!giverSet.contains(instructor.getEmail())) {
                instructorRecipientList.add(instructor.email);
            }
        }
        instructorNotifiedList.add(logic.getInstructorForGoogleId(session1.getCourseId(),
                instructor1.getGoogleId()).email);

        String courseName = logic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = action.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            assertEquals(String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(), courseName,
                                       session1.getFeedbackSessionName()),
                         email.getSubject());

            String header = "The email below has been sent to students of course: [" + session1.getCourseId() + "]";
            String content = email.getContent();
            String recipient = email.getRecipient();

            if (content.contains(header)) { // notification to only requesting instructors
                assertTrue(instructorNotifiedList.contains(recipient));
                instructorNotifiedList.remove(recipient);
                continue;
            }
            if (studentRecipientList.contains(recipient)) {
                studentRecipientList.remove(recipient);
                continue;
            }
            if (instructorRecipientList.contains(recipient)) {
                instructorRecipientList.remove(recipient);
                continue;
            }
            fail("Email recipient " + recipient + " is not in the list!");
        }

        // Ensure that every email recipient is accounted for
        assertTrue(String.valueOf(studentRecipientList.size()), studentRecipientList.isEmpty());
        assertTrue(String.valueOf(instructorRecipientList.size()), instructorRecipientList.isEmpty());
        assertTrue(instructorNotifiedList.isEmpty());

    }

}
