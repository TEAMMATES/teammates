package teammates.e2e.cases;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

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
        student = testData.students.get("alice.tmms@IFEdit.CS2104");
        feedbackQuestion = testData.feedbackQuestions.get("qn1");
        feedbackSession = testData.feedbackSessions.get("openSession");
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_AUDIT_LOGS_PAGE).withUserId(instructor.googleId);
        InstructorAuditLogsPage auditLogsPage = loginAdminToPage(url, InstructorAuditLogsPage.class);

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
        for (String log : auditLogsPage.getLogsData()) {
            assertEquals(log, "No activity for this feedback session in selected search period");
        }

        ______TS("verify logs output");
        AppUrl studentSubmissionPageUrl = createUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withCourseId(course.getId())
                .withUserId(student.googleId)
                .withSessionName(feedbackSession.getFeedbackSessionName());
        StudentFeedbackSubmissionPage studentSubmissionPage = loginAdminToPage(studentSubmissionPageUrl,
                StudentFeedbackSubmissionPage.class);
        studentSubmissionPage.populateResponse();
        studentSubmissionPage.submit();

        auditLogsPage = loginAdminToPage(url, InstructorAuditLogsPage.class);
        auditLogsPage.setCourseId(course.getId());
        auditLogsPage.startSearching();
        int emptyLogCounter = 0;
        for (String log : auditLogsPage.getLogsData()) {
            if (log.equals("No activity for this feedback session in selected search period")) {
                emptyLogCounter++;
            }
        }
        // assertNotEquals(auditLogsPage.getLogsData().size(), emptyLogCounter);
    }
}
