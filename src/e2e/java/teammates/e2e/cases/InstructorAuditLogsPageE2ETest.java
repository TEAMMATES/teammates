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
import teammates.e2e.pageobjects.InstructorAuditLogsPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_AUDIT_LOGS_PAGE}.
 */
public class InstructorAuditLogsPageE2ETest extends BaseE2ETestCase {
    private InstructorAttributes instructor;
    private CourseAttributes course;
    private FeedbackSessionAttributes feedbackSession;
    private FeedbackQuestionAttributes feedbackQuestion;
    private StudentAttributes student;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorAuditLogsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        student = testData.students.get("alice.tmms@IAuditLogs.CS2104");
        feedbackQuestion = testData.feedbackQuestions.get("qn1");
        feedbackSession = testData.feedbackSessions.get("openSession");
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_AUDIT_LOGS_PAGE);
        InstructorAuditLogsPage auditLogsPage = loginToPage(url, InstructorAuditLogsPage.class, instructor.getGoogleId());

        ______TS("verify default datetime");
        String currentLogsFromDate = auditLogsPage.getLogsFromDate();
        String currentLogsToDate = auditLogsPage.getLogsToDate();
        String currentLogsFromTime = auditLogsPage.getLogsFromTime();
        String currentLogsToTime = auditLogsPage.getLogsToTime();

        auditLogsPage.setLogsFromDateTime(Instant.now().minus(1, ChronoUnit.DAYS),
                ZoneId.systemDefault().getId());
        auditLogsPage.setLogsToDateTime(Instant.now(), ZoneId.systemDefault().getId());

        assertEquals(currentLogsFromDate, auditLogsPage.getLogsFromDate());
        assertEquals(currentLogsToDate, auditLogsPage.getLogsToDate());
        assertEquals(currentLogsFromTime, "23:59H");
        assertEquals(currentLogsToTime, "23:59H");

        ______TS("verify logs output");
        logout();
        AppUrl studentSubmissionPageUrl = createUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getFeedbackSessionName());
        FeedbackSubmitPage studentSubmissionPage = loginToPage(studentSubmissionPageUrl,
                FeedbackSubmitPage.class, student.getGoogleId());

        StudentAttributes receiver = testData.students.get("benny.tmms@IAuditLogs.CS2104");
        FeedbackQuestionAttributes question = testData.feedbackQuestions.get("qn1");
        String questionId = getFeedbackQuestion(question).getId();
        FeedbackTextResponseDetails details = new FeedbackTextResponseDetails("Response");
        FeedbackResponseAttributes response =
                FeedbackResponseAttributes.builder(questionId, student.getEmail(), instructor.getEmail())
                        .withResponseDetails(details)
                        .build();

        studentSubmissionPage.submitTextResponse(1, receiver.getName(), response);

        logout();
        auditLogsPage = loginToPage(url, InstructorAuditLogsPage.class, instructor.getGoogleId());
        auditLogsPage.setCourseId(course.getId());
        auditLogsPage.startSearching();

        assertTrue(auditLogsPage.isLogPresentForSession(feedbackQuestion.getFeedbackSessionName()));
    }
}
