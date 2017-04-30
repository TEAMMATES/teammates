package teammates.test.cases.automated;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailType;
import teammates.common.util.TaskWrapper;
import teammates.logic.core.CoursesLogic;
import teammates.ui.automated.FeedbackSessionUnpublishedEmailWorkerAction;

/**
 * SUT: {@link FeedbackSessionUnpublishedEmailWorkerAction}.
 */
public class FeedbackSessionUnpublishedEmailWorkerActionTest extends BaseAutomatedActionTest {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_UNPUBLISHED_EMAIL_WORKER_URL;
    }

    @Test
    public void allTests() {
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                ParamsNames.EMAIL_COURSE, session1.getCourseId(),
                ParamsNames.EMAIL_FEEDBACK, session1.getFeedbackSessionName()
        };

        FeedbackSessionUnpublishedEmailWorkerAction action = getAction(submissionParams);
        action.execute();

        // 5 students and 5 instructors in course1
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 10);

        String courseName = coursesLogic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = action.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            Map<String, String[]> paramMap = task.getParamMap();
            assertEquals(String.format(EmailType.FEEDBACK_UNPUBLISHED.getSubject(), courseName,
                                       session1.getSessionName()),
                         paramMap.get(ParamsNames.EMAIL_SUBJECT)[0]);
        }
    }

    @Override
    protected FeedbackSessionUnpublishedEmailWorkerAction getAction(String... params) {
        return (FeedbackSessionUnpublishedEmailWorkerAction)
                gaeSimulation.getAutomatedActionObject(getActionUri(), params);
    }

}
