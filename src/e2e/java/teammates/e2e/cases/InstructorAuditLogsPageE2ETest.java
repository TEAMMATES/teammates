package teammates.e2e.cases;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorAuditLogsPage;
import teammates.e2e.pageobjects.StudentFeedbackSubmissionPage;

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
        InstructorAuditLogsPage auditLogsPage = loginToPage(url, InstructorAuditLogsPage.class, instructor.googleId);

        ______TS("verify default datetime");
        String currentLogsFromDate = auditLogsPage.getLogsFromDate();
        String currentLogsToDate = auditLogsPage.getLogsToDate();
        String currentLogsFromTime = auditLogsPage.getLogsFromTime();
        String currentLogsToTime = auditLogsPage.getLogsToTime();

        auditLogsPage.setLogsFromDateTime(Instant.now().minus(1, ChronoUnit.DAYS),
                ZoneId.systemDefault());
        auditLogsPage.setLogsToDateTime(Instant.now(), ZoneId.systemDefault());

        assertEquals(currentLogsFromDate, auditLogsPage.getLogsFromDate());
        assertEquals(currentLogsToDate, auditLogsPage.getLogsToDate());
        assertEquals(currentLogsFromTime, "23:59H");
        assertEquals(currentLogsToTime, "23:59H");

        ______TS("verify empty logs output");
        auditLogsPage.setCourseId(course.getId());
        auditLogsPage.startSearching();

        List<String> sessions = new ArrayList<>(Arrays.asList(feedbackSession.getFeedbackSessionName()));
        for (String sessionName : sessions) {
            assertFalse(auditLogsPage.isLogPresentForSession(sessionName));
        }

        ______TS("verify logs output");
        logout();
        AppUrl studentSubmissionPageUrl = createUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getFeedbackSessionName());
        StudentFeedbackSubmissionPage studentSubmissionPage = loginToPage(studentSubmissionPageUrl,
                StudentFeedbackSubmissionPage.class, student.googleId);
        studentSubmissionPage.populateResponse();
        studentSubmissionPage.submit();

        logout();
        auditLogsPage = loginToPage(url, InstructorAuditLogsPage.class, instructor.googleId);
        auditLogsPage.setCourseId(course.getId());
        auditLogsPage.startSearching();

        assertTrue(auditLogsPage.isLogPresentForSession(feedbackQuestion.getFeedbackSessionName()));
    }
}
