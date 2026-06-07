package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.Provider;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Student;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionPublishStatus;
import teammates.ui.output.FeedbackSessionSubmissionStatus;
import teammates.ui.output.FeedbackSessionViewData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetFeedbackSessionAction}.
 */
public class GetFeedbackSessionActionTest extends BaseActionTest<GetFeedbackSessionAction> {

    private Student student1;
    private FeedbackSession feedbackSession1;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        Course course1 = generateCourse1();
        student1 = generateStudent1InCourse(course1);
        feedbackSession1 = generateSession1InCourse(course1);

        when(mockLogic.getFeedbackSession(feedbackSession1.getId())).thenReturn(feedbackSession1);
        when(mockLogic.getStudentByGoogleId(course1.getId(), student1.getAccount().getGoogleId())).thenReturn(student1);
    }

    @Test
    protected void textExecute_studentSubmissionNoExtensionAndBeforeEndTime_statusOpen() {
        loginAsStudent(student1.getAccount().getGoogleId());

        String feedbackSessionId = feedbackSession1.getId().toString();
        String timeZone = feedbackSession1.getCourse().getTimeZone();
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionId,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("get submission by student with no extension; before end time");

        Instant newStartTime = Instant.now();
        Instant newEndTime = newStartTime.plusSeconds(60 * 60);
        Duration newGracePeriod = Duration.ZERO;

        feedbackSession1.setStartTime(newStartTime);
        feedbackSession1.setEndTime(newEndTime);
        feedbackSession1.setGracePeriod(newGracePeriod);

        // mock no deadline extension
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(feedbackSession1.getEndTime());

        GetFeedbackSessionAction a = getAction(params);

        JsonResult r = getJsonResult(a);
        FeedbackSessionViewData response = (FeedbackSessionViewData) r.getOutput();
        FeedbackSessionData responseData = response.getFeedbackSession();

        assertEquals(feedbackSessionId, responseData.getFeedbackSessionId().toString());
        assertEquals(feedbackSession1.getName(), responseData.getFeedbackSessionName());
        assertEquals(timeZone, responseData.getTimeZone());
        assertEquals(feedbackSession1.getInstructions(), responseData.getInstructions());

        assertEquals(feedbackSession1.getStartTime().toEpochMilli(),
                responseData.getSubmissionStartTimestamp());
        assertEquals(newEndTime.toEpochMilli(),
                responseData.getSubmissionEndTimestamp());
        assertEquals(newEndTime.toEpochMilli(),
                responseData.getSubmissionEndWithExtensionTimestamp());
        assertNull(responseData.getGracePeriod());

        assertNull(responseData.getSessionVisibleSetting());
        assertNull(responseData.getSessionVisibleFromTimestamp());
        assertNull(responseData.getCustomSessionVisibleTimestamp());

        assertNull(responseData.getResponseVisibleSetting());
        assertNull(responseData.getResultVisibleFromTimestamp());
        assertNull(responseData.getCustomResponseVisibleTimestamp());

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, responseData.getSubmissionStatus());
        assertEquals(FeedbackSessionPublishStatus.NOT_PUBLISHED, responseData.getPublishStatus());

        assertNull(responseData.getIsClosingSoonEmailEnabled());
        assertNull(responseData.getIsPublishedEmailEnabled());

        assertEquals(0, responseData.getCreatedAtTimestamp());
        assertNull(responseData.getDeletedAtTimestamp());

        logoutUser();
    }

    @Test
    protected void textExecute_studentSubmissionNoExtensionAfterEndTimeWithinGracePeriod_statusGracePeriod() {

        loginAsStudent(student1.getAccount().getGoogleId());

        String feedbackSessionId = feedbackSession1.getId().toString();
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionId,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("get submission by student with no extension; after end time but within grace period");

        Instant newStartTime = Instant.now().plusSeconds(-120);
        Instant newEndTime = newStartTime.plusSeconds(60);
        Duration newGracePeriod = Duration.ofDays(2);

        feedbackSession1.setStartTime(newStartTime);
        feedbackSession1.setEndTime(newEndTime);
        feedbackSession1.setGracePeriod(newGracePeriod);

        // mock no deadline extension
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(feedbackSession1.getEndTime());

        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionViewData response = (FeedbackSessionViewData) r.getOutput();
        FeedbackSessionData responseData = response.getFeedbackSession();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, responseData.getSubmissionStatus());

        assertEquals(newEndTime.toEpochMilli(),
                responseData.getSubmissionEndWithExtensionTimestamp());

        logoutUser();
    }

    @Test
    protected void textExecute_studentSubmissionNoExtensionAfterEndTimeBeyondGracePeriod_statusClosed() {

        loginAsStudent(student1.getAccount().getGoogleId());

        String feedbackSessionId = feedbackSession1.getId().toString();
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionId,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("get submission by student with no extension; after end time and beyond grace period");

        Instant newStartTime = Instant.now().plusSeconds(-60);
        Instant newEndTime = newStartTime.plusSeconds(20);
        Duration newGracePeriod = Duration.ofSeconds(10);

        feedbackSession1.setStartTime(newStartTime);
        feedbackSession1.setEndTime(newEndTime);
        feedbackSession1.setGracePeriod(newGracePeriod);

        // mock no deadline extension
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(feedbackSession1.getEndTime());

        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionViewData response = (FeedbackSessionViewData) r.getOutput();
        FeedbackSessionData responseData = response.getFeedbackSession();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, responseData.getSubmissionStatus());

        assertEquals(newEndTime.toEpochMilli(),
                responseData.getSubmissionEndWithExtensionTimestamp());

        logoutUser();
    }

    @Test
    protected void textExecute_studentSubmissionWithExtensionBeforeEndTime_statusOpen() {

        loginAsStudent(student1.getAccount().getGoogleId());

        String feedbackSessionId = feedbackSession1.getId().toString();
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionId,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("get submission by student with extension; before end time");

        Instant newStartTime = Instant.now();
        Instant newEndTime = newStartTime.plusSeconds(60 * 60);
        Duration newGracePeriod = Duration.ZERO;
        Instant extendedEndTime = newStartTime.plusSeconds(60 * 60 * 21);

        feedbackSession1.setStartTime(newStartTime);
        feedbackSession1.setEndTime(newEndTime);
        feedbackSession1.setGracePeriod(newGracePeriod);

        // mock deadline extension exists for student1
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(extendedEndTime);

        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionViewData response = (FeedbackSessionViewData) r.getOutput();
        FeedbackSessionData responseData = response.getFeedbackSession();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, responseData.getSubmissionStatus());

        assertEquals(extendedEndTime.toEpochMilli(),
                responseData.getSubmissionEndWithExtensionTimestamp());

        logoutUser();
    }

    @Test
    protected void textExecute_studentSubmissionWithExtensionAfterEndTimeBeforeExtendedDeadline_statusOpen() {

        loginAsStudent(student1.getAccount().getGoogleId());

        String feedbackSessionId = feedbackSession1.getId().toString();
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionId,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("get submission by student with extension; after end time but before extended deadline");

        Instant newStartTime = Instant.now().plusSeconds(-60);
        Instant newEndTime = newStartTime.plusSeconds(20);
        Duration newGracePeriod = Duration.ZERO;
        Instant extendedEndTime = Instant.now().plusSeconds(60 * 60);

        feedbackSession1.setStartTime(newStartTime);
        feedbackSession1.setEndTime(newEndTime);
        feedbackSession1.setGracePeriod(newGracePeriod);

        // mock deadline extension exists for student1
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(extendedEndTime);

        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionViewData response = (FeedbackSessionViewData) r.getOutput();
        FeedbackSessionData responseData = response.getFeedbackSession();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, responseData.getSubmissionStatus());

        assertEquals(extendedEndTime.toEpochMilli(),
                responseData.getSubmissionEndWithExtensionTimestamp());

        logoutUser();
    }

    @Test
    protected void textExecute_studentSubmissionWithExtensionAfterExtendedDeadlineWithinGracePeriod_statusGracePeriod() {

        loginAsStudent(student1.getAccount().getGoogleId());

        String feedbackSessionId = feedbackSession1.getId().toString();
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionId,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("get submission by student with extension; after extended deadline but within grace period");

        Instant newStartTime = Instant.now().plusSeconds(-120);
        Instant newEndTime = newStartTime.plusSeconds(20);
        Instant extendedEndTime = newEndTime.plusSeconds(20);
        Duration newGracePeriod = Duration.ofDays(1);

        feedbackSession1.setStartTime(newStartTime);
        feedbackSession1.setEndTime(newEndTime);
        feedbackSession1.setGracePeriod(newGracePeriod);

        // mock deadline extension exists for student1
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(extendedEndTime);

        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionViewData response = (FeedbackSessionViewData) r.getOutput();
        FeedbackSessionData responseData = response.getFeedbackSession();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, responseData.getSubmissionStatus());

        assertEquals(extendedEndTime.toEpochMilli(),
                responseData.getSubmissionEndWithExtensionTimestamp());

        logoutUser();
    }

    @Test
    protected void textExecute_studentSubmissionWithExtensionAfterExtendedDeadlineBeyondGracePeriod_statusClosed() {

        loginAsStudent(student1.getAccount().getGoogleId());

        String feedbackSessionId = feedbackSession1.getId().toString();
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSessionId,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("get submission by student with extension; after extended deadline and beyond grace period");

        Instant newStartTime = Instant.now().plusSeconds(-60 * 60 * 24);
        Instant newEndTime = newStartTime.plusSeconds(10);
        Instant extendedEndTime = newEndTime.plusSeconds(10);
        Duration newGracePeriod = Duration.ofSeconds(10);

        feedbackSession1.setStartTime(newStartTime);
        feedbackSession1.setEndTime(newEndTime);
        feedbackSession1.setGracePeriod(newGracePeriod);

        // mock deadline extension exists for student1
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(extendedEndTime);

        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionViewData response = (FeedbackSessionViewData) r.getOutput();
        FeedbackSessionData responseData = response.getFeedbackSession();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, responseData.getSubmissionStatus());

        assertEquals(extendedEndTime.toEpochMilli(),
                responseData.getSubmissionEndWithExtensionTimestamp());

        logoutUser();
    }

    private Course generateCourse1() {
        Course c = new Course("course-1", "Typical Course 1",
                "Africa/Johannesburg", "TEAMMATES Test Institute 0");
        c.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        c.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        return c;
    }

    private Student generateStudent1InCourse(Course courseStudentIsIn) {
        String email = "student1@gmail.com";
        String name = "student-1";
        String googleId = "student-1";
        String subject = "validStudentSubject";
        String tenantId = "validTenantId";
        Student s = new Student(courseStudentIsIn, name, email, "comment for student-1");
        s.setAccount(new Account(googleId, Provider.TEAMMATES_DEV, subject, tenantId, name, email));
        return s;
    }

    private FeedbackSession generateSession1InCourse(Course course) {
        FeedbackSession fs = new FeedbackSession("feedbacksession-1",
                null, "generic instructions",
                Instant.parse("2012-04-01T22:00:00Z"), Instant.parse("2027-04-30T22:00:00Z"),
                Instant.parse("2012-03-28T22:00:00Z"), Instant.parse("2027-05-01T22:00:00Z"),
                Duration.ofHours(10), true, true);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        course.addFeedbackSession(fs);
        return fs;
    }
}
