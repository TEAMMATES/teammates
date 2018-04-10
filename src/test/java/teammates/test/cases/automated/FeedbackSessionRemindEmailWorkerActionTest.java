package teammates.test.cases.automated;

import java.util.ArrayList;
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
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.ui.automated.FeedbackSessionRemindEmailWorkerAction;

/**
 * SUT: {@link FeedbackSessionRemindEmailWorkerAction}.
 */
public class FeedbackSessionRemindEmailWorkerActionTest extends BaseAutomatedActionTest {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_REMIND_EMAIL_WORKER_URL;
    }

    @Test
    public void allTests() throws Exception {

        ______TS("Send feedback session reminder email");

        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor1 = dataBundle.instructors.get("instructor1OfCourse1");

        // re-read from Datastore to update the respondents list
        session1 = fsLogic.getFeedbackSession(session1.getFeedbackSessionName(), session1.getCourseId());

        String[] submissionParams = new String[] {
                ParamsNames.SUBMISSION_FEEDBACK, session1.getFeedbackSessionName(),
                ParamsNames.SUBMISSION_COURSE, session1.getCourseId(),
                ParamsNames.USER_ID, instructor1.getGoogleId()
        };

        FeedbackSessionRemindEmailWorkerAction action = getAction(submissionParams);
        action.execute();

        // 2 students and 4 instructors sent reminder, 1 instructor notified
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 7);

        List<String> studentRecipientList = new ArrayList<>();
        for (StudentAttributes student : studentsLogic.getStudentsForCourse(session1.getCourseId())) {
            if (!fsLogic.isFeedbackSessionCompletedByStudent(session1, student.email)) {
                studentRecipientList.add(student.email);
            }
        }

        List<String> instructorRecipientList = new ArrayList<>();
        List<String> instructorNotifiedList = new ArrayList<>();
        for (InstructorAttributes instructor : instructorsLogic.getInstructorsForCourse(session1.getCourseId())) {
            if (!fsLogic.isFeedbackSessionCompletedByInstructor(session1, instructor.email)) {
                instructorRecipientList.add(instructor.email);
            }
        }
        instructorNotifiedList.add(instructorsLogic.getInstructorForGoogleId(session1.getCourseId(),
                instructor1.getGoogleId()).email);

        String courseName = coursesLogic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = action.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            Map<String, String[]> paramMap = task.getParamMap();
            assertEquals(String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(), courseName,
                                       session1.getSessionName()),
                         paramMap.get(ParamsNames.EMAIL_SUBJECT)[0]);

            String header = "The email below has been sent to students of course: [" + session1.getCourseId() + "]";
            String content = paramMap.get(ParamsNames.EMAIL_CONTENT)[0];
            String recipient = paramMap.get(ParamsNames.EMAIL_RECEIVER)[0];

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

    @Override
    protected FeedbackSessionRemindEmailWorkerAction getAction(String... params) {
        return (FeedbackSessionRemindEmailWorkerAction) gaeSimulation.getAutomatedActionObject(getActionUri(), params);
    }

}
