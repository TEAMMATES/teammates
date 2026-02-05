package teammates.e2e.cases.sql;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPageSql;
import teammates.e2e.pageobjects.InstructorStudentActivityLogsPage;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_STUDENT_ACTIVITY_LOGS_PAGE}.
 */
public class InstructorStudentActivityLogsPageE2ETest extends BaseE2ETestCase {
    private Instructor instructor;
    private Course course;
    private FeedbackSession feedbackSession;
    private FeedbackQuestion feedbackQuestion;
    private Student student;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/InstructorStudentActivityLogsPageE2ETestSql.json"));

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        student = testData.students.get("alice.tmms@ISActLogs.CS2104");
        feedbackQuestion = testData.feedbackQuestions.get("qn1");
        feedbackSession = testData.feedbackSessions.get("openSession");
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_ACTIVITY_LOGS_PAGE)
                .withCourseId("tm.e2e.ISActLogs.CS2104");
        InstructorStudentActivityLogsPage studentActivityLogsPage =
                loginToPage(url, InstructorStudentActivityLogsPage.class, instructor.getGoogleId());

        ______TS("verify default datetime");
        String currentLogsFromDate = studentActivityLogsPage.getLogsFromDate();
        String currentLogsToDate = studentActivityLogsPage.getLogsToDate();
        String currentLogsFromTime = studentActivityLogsPage.getLogsFromTime();
        String currentLogsToTime = studentActivityLogsPage.getLogsToTime();

        studentActivityLogsPage.setLogsFromDateTime(
                Instant.now().minus(1, ChronoUnit.DAYS),
                ZoneId.of(course.getTimeZone()).getId());
        studentActivityLogsPage.setLogsToDateTime(Instant.now(), ZoneId.of(course.getTimeZone()).getId());

        assertEquals(currentLogsFromDate, studentActivityLogsPage.getLogsFromDate());
        assertEquals(currentLogsToDate, studentActivityLogsPage.getLogsToDate());
        assertEquals(currentLogsFromTime, "23:59H");
        assertEquals(currentLogsToTime, "23:59H");

        ______TS("verify logs output");
        logout();
        AppUrl studentSubmissionPageUrl = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getName());
        FeedbackSubmitPageSql studentSubmissionPage = loginToPage(studentSubmissionPageUrl,
                FeedbackSubmitPageSql.class, student.getGoogleId());

        Student receiver = testData.students.get("benny.tmms@ISActLogs.CS2104");

        FeedbackTextResponseDetails details = new FeedbackTextResponseDetails("Response");
        FeedbackResponse response = FeedbackResponse.makeResponse(
                feedbackQuestion, student.getEmail(), student.getSection(),
                receiver.getEmail(), receiver.getSection(), details);

        studentSubmissionPage.fillTextResponse(1, receiver.getName(), response);
        studentSubmissionPage.clickSubmitQuestionButton(1);

        logout();
        studentActivityLogsPage = loginToPage(url, InstructorStudentActivityLogsPage.class, instructor.getGoogleId());

        studentActivityLogsPage.setActivityType("session access and submission");
        studentActivityLogsPage.setSessionDropdown(feedbackSession.getName());

        studentActivityLogsPage.waitForPageToLoad();
        studentActivityLogsPage.startSearching();
        studentActivityLogsPage.waitForLogsToLoad();

        assertTrue(studentActivityLogsPage.isLogPresentForSession(feedbackSession.getName()));
    }
}
