package teammates.e2e.cases;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorStudentActivityLogsPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_STUDENT_ACTIVITY_LOGS_PAGE}.
 */
public class InstructorStudentActivityLogsPageE2ETest extends BaseE2ETestCase {
    private InstructorAttributes instructor;
    private CourseAttributes course;
    private FeedbackSessionAttributes feedbackSession;
    private FeedbackQuestionAttributes feedbackQuestion;
    private StudentAttributes student;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorStudentActivityLogsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

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
                ZoneId.systemDefault().getId());
        studentActivityLogsPage.setLogsToDateTime(Instant.now(), ZoneId.systemDefault().getId());

        assertEquals(currentLogsFromDate, studentActivityLogsPage.getLogsFromDate());
        assertEquals(currentLogsToDate, studentActivityLogsPage.getLogsToDate());
        assertEquals(currentLogsFromTime, "23:59H");
        assertEquals(currentLogsToTime, "23:59H");

        ______TS("verify logs output");
        logout();
        AppUrl studentSubmissionPageUrl = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getFeedbackSessionName());
        FeedbackSubmitPage studentSubmissionPage = loginToPage(studentSubmissionPageUrl,
                FeedbackSubmitPage.class, student.getGoogleId());

        StudentAttributes receiver = testData.students.get("benny.tmms@ISActLogs.CS2104");
        FeedbackQuestionAttributes question = testData.feedbackQuestions.get("qn1");
        String questionId = getFeedbackQuestion(question).getId();
        FeedbackTextResponseDetails details = new FeedbackTextResponseDetails("Response");
        FeedbackResponseAttributes response =
                FeedbackResponseAttributes.builder(questionId, student.getEmail(), instructor.getEmail())
                        .withResponseDetails(details)
                        .build();

        studentSubmissionPage.fillTextResponse(1, receiver.getName(), response);
        studentSubmissionPage.clickSubmitQuestionButton(1);

        logout();
        studentActivityLogsPage = loginToPage(url, InstructorStudentActivityLogsPage.class,
                instructor.getGoogleId());
        studentActivityLogsPage.setActivityType("session access and submission");
        studentActivityLogsPage.waitForPageToLoad();
        studentActivityLogsPage.startSearching();

        assertTrue(studentActivityLogsPage.isLogPresentForSession(feedbackQuestion.getFeedbackSessionName()));
    }
}
