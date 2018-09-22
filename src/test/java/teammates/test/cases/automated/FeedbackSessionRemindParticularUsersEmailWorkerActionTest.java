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
import teammates.ui.automated.FeedbackSessionRemindParticularUsersEmailWorkerAction;

/**
 * SUT: {@link FeedbackSessionRemindParticularUsersEmailWorkerAction}.
 */
public class FeedbackSessionRemindParticularUsersEmailWorkerActionTest extends BaseAutomatedActionTest {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_WORKER_URL;
    }

    @Test
    public void allTests() {

        ______TS("Send feedback session reminder email");

        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student1 = dataBundle.students.get("student1InCourse1");
        InstructorAttributes instructor1 = dataBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                ParamsNames.SUBMISSION_FEEDBACK, session1.getFeedbackSessionName(),
                ParamsNames.SUBMISSION_COURSE, session1.getCourseId(),
                ParamsNames.SUBMISSION_REMIND_USERLIST, student1.email,
                ParamsNames.SUBMISSION_REMIND_USERLIST, instructor1.email,
                ParamsNames.USER_ID, instructor1.googleId,
                ParamsNames.SUBMISSION_REMIND_USERLIST, "non-existent"
        };

        FeedbackSessionRemindParticularUsersEmailWorkerAction action = getAction(submissionParams);
        action.execute();

        // send 3 emails as specified in the submission parameters
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 3);

        String courseName = coursesLogic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = action.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            Map<String, String[]> paramMap = task.getParamMap();
            assertEquals(String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(), courseName,
                                       session1.getSessionName()),
                         paramMap.get(ParamsNames.EMAIL_SUBJECT)[0]);
            String recipient = paramMap.get(ParamsNames.EMAIL_RECEIVER)[0];
            assertTrue(recipient.equals(student1.email) || recipient.equals(instructor1.email));
        }
    }

    @Override
    protected FeedbackSessionRemindParticularUsersEmailWorkerAction getAction(String... params) {
        return (FeedbackSessionRemindParticularUsersEmailWorkerAction)
                gaeSimulation.getAutomatedActionObject(getActionUri(), params);
    }

}
