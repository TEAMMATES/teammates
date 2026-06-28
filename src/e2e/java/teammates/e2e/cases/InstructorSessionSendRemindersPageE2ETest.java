package teammates.e2e.cases;

import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorSessionSendRemindersPage;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_SEND_REMINDERS_PAGE}.
 */
public class InstructorSessionSendRemindersPageE2ETest extends BaseE2ETestCase {
    private static final String SUCCESS_MESSAGE = "Reminder e-mails have been sent out to those students"
            + " and instructors. Please allow up to 1 hour for all the notification emails to be sent out.";

    private Instructor instructor;
    private Course course;
    private FeedbackSession feedbackSession;
    private Set<String> selectedStudentEmails;
    private Set<String> submittedStudentEmails;
    private Set<String> submittedInstructorEmails;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadDataBundle("/InstructorSessionSendRemindersPageE2ETest.json"));

        instructor = testData.instructors.get("ISesSr.instructor1");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("session");
        selectedStudentEmails = Set.of(
                testData.students.get("ISesSr.alice").getEmail(),
                testData.students.get("ISesSr.charlie").getEmail());
        submittedStudentEmails = Set.of(
                testData.students.get("ISesSr.benny").getEmail(),
                testData.students.get("ISesSr.danny").getEmail());
        submittedInstructorEmails = Set.of(testData.instructors.get("ISesSr.instructor1").getEmail());
    }

    @Test
    @Override
    public void testAll() {
        InstructorSessionSendRemindersPage sendRemindersPage = loginToInstructorSessionSendRemindersPage();

        ______TS("verify data loaded correctly");
        sendRemindersPage.waitForPageToLoad(true);
        sendRemindersPage.verifySessionDetails(course, feedbackSession);
        sendRemindersPage.verifyStudentListDetails(testData.students.values(), submittedStudentEmails);
        sendRemindersPage.verifyInstructorListDetails(testData.instructors.values(), submittedInstructorEmails);

        ______TS("send reminders to a fixed set of students");
        sendRemindersPage.selectStudentsByEmail(selectedStudentEmails.toArray(String[]::new));
        sendRemindersPage.verifySelectedStudents(selectedStudentEmails);
        sendRemindersPage.submitReminders();
        sendRemindersPage.verifyStatusMessage(SUCCESS_MESSAGE);
    }

    private InstructorSessionSendRemindersPage loginToInstructorSessionSendRemindersPage() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_SEND_REMINDERS_PAGE)
                .withFeedbackSessionId(feedbackSession.getId());

        return loginToPage(url, InstructorSessionSendRemindersPage.class, instructor.getEmail());
    }
}
