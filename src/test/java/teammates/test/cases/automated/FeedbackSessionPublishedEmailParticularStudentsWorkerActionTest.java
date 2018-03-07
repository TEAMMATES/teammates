package teammates.test.cases.automated;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailType;
import teammates.common.util.TaskWrapper;
import teammates.logic.core.CoursesLogic;
import teammates.ui.automated.FeedbackSessionPublishedEmailParticularStudentsWorkerAction;

/**
 * SUT: {@link FeedbackSessionRemindParticularUsersEmailWorkerAction}.
 */
public class FeedbackSessionPublishedEmailParticularStudentsWorkerActionTest extends BaseAutomatedActionTest {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_PARTICULAR_USERS_EMAIL_WORKER_URL;
    }

    @Test
    public void allTests() {

        ______TS("Resend feedback session results published email");

        FeedbackSessionAttributes publishedSession = dataBundle.feedbackSessions.get("publishedSession1InCourse1");
        StudentAttributes student1 = dataBundle.students.get("student1InCourse1");
        InstructorAttributes instructor1 = dataBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                ParamsNames.SUBMISSION_FEEDBACK, publishedSession.getFeedbackSessionName(),
                ParamsNames.SUBMISSION_COURSE, publishedSession.getCourseId(),
                ParamsNames.SUBMISSION_RESEND_PUBLISHED_LINKS_USERLIST, student1.email,
                ParamsNames.SUBMISSION_RESEND_PUBLISHED_LINKS_USERLIST, instructor1.email,
                ParamsNames.SUBMISSION_RESEND_PUBLISHED_LINKS_USERLIST, "non-existent"
        };

        FeedbackSessionPublishedEmailParticularStudentsWorkerAction action = getAction(submissionParams);
        action.execute();

        // send 2 emails as specified in the submission parameters
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 2);

        String courseName = coursesLogic.getCourse(publishedSession.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = action.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            Map<String, String[]> paramMap = task.getParamMap();
            assertEquals(String.format(EmailType.FEEDBACK_PUBLISHED.getSubject(), courseName,
                                       publishedSession.getSessionName()),
                         paramMap.get(ParamsNames.EMAIL_SUBJECT)[0]);
            String recipient = paramMap.get(ParamsNames.EMAIL_RECEIVER)[0];
            assertTrue(recipient.equals(student1.email) || recipient.equals(instructor1.email));
        }
    }

    @Override
    protected FeedbackSessionPublishedEmailParticularStudentsWorkerAction getAction(String... params) {
        return (FeedbackSessionPublishedEmailParticularStudentsWorkerAction)
                gaeSimulation.getAutomatedActionObject(getActionUri(), params);
    }

}
