package teammates.test.cases.webapi;

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
import teammates.ui.webapi.action.FeedbackSessionRemindParticularUsersEmailWorkerAction;

/**
 * SUT: {@link FeedbackSessionRemindParticularUsersEmailWorkerAction}.
 */
public class FeedbackSessionRemindParticularUsersEmailWorkerActionTest
        extends BaseActionTest<FeedbackSessionRemindParticularUsersEmailWorkerAction> {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();

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

        String[] submissionParams = new String[] {
                ParamsNames.SUBMISSION_FEEDBACK, session1.getFeedbackSessionName(),
                ParamsNames.SUBMISSION_COURSE, session1.getCourseId(),
                ParamsNames.SUBMISSION_REMIND_USERLIST, student1.email,
                ParamsNames.SUBMISSION_REMIND_USERLIST, instructor1.email,
                ParamsNames.USER_ID, instructor1.googleId,
                ParamsNames.SUBMISSION_REMIND_USERLIST, "non-existent",
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
                                       session1.getFeedbackSessionName()),
                         paramMap.get(ParamsNames.EMAIL_SUBJECT)[0]);
            String recipient = paramMap.get(ParamsNames.EMAIL_RECEIVER)[0];
            assertTrue(recipient.equals(student1.email) || recipient.equals(instructor1.email));
        }
    }

}
