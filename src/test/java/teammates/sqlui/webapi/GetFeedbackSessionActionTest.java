package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionPublishStatus;
import teammates.ui.output.FeedbackSessionSubmissionStatus;
import teammates.ui.request.Intent;
import teammates.ui.webapi.GetFeedbackSessionAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetFeedbackSessionAction}.
 */
public class GetFeedbackSessionActionTest extends BaseActionTest<GetFeedbackSessionAction> {

    private Student student1;
    private FeedbackSession feedbackSession1;
    private String courseId;
    private String feedbackSessionName;
    private String timeZone;
    private String[] params;

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

        when(mockLogic.getFeedbackSession(feedbackSession1.getName(), course1.getId())).thenReturn(feedbackSession1);
        when(mockLogic.getStudentByGoogleId(course1.getId(), student1.getAccount().getGoogleId())).thenReturn(student1);

        loginAsStudent(student1.getAccount().getGoogleId());

        courseId = feedbackSession1.getCourse().getId();
        feedbackSessionName = feedbackSession1.getName();
        timeZone = feedbackSession1.getCourse().getTimeZone();
        params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
    }

    @AfterMethod
    void tearDown() {
         logoutUser();
    }

    @Test
    protected void textExecute_studentSubmissionNoExtensionAndBeforeEndTime_statusOpen() {

        ______TS("get submission by student with no extension; before end time");

        Instant newStartTime = Instant.now();
        Instant newEndTime = newStartTime.plusSeconds(60 * 60);
        Duration newGracePeriod = Duration.ZERO;

        configureFeedbackSession(newStartTime, newEndTime, newGracePeriod);

        // mock no deadline extension
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(feedbackSession1.getEndTime());

        GetFeedbackSessionAction a = getAction(params);

        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(courseId, response.getCourseId());
        assertEquals(feedbackSessionName, response.getFeedbackSessionName());
        assertEquals(timeZone, response.getTimeZone());
        assertEquals(feedbackSession1.getInstructions(), response.getInstructions());

        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(feedbackSession1.getStartTime(),
                        timeZone, true).toEpochMilli(),
                response.getSubmissionStartTimestamp());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(newEndTime, timeZone, true)
                        .toEpochMilli(),
                response.getSubmissionEndTimestamp());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(newEndTime, timeZone, true)
                        .toEpochMilli(),
                response.getSubmissionEndWithExtensionTimestamp());
        assertNull(response.getGracePeriod());

        assertNull(response.getSessionVisibleSetting());
        assertNull(response.getSessionVisibleFromTimestamp());
        assertNull(response.getCustomSessionVisibleTimestamp());

        assertNull(response.getResponseVisibleSetting());
        assertNull(response.getResultVisibleFromTimestamp());
        assertNull(response.getCustomResponseVisibleTimestamp());

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());
        assertEquals(FeedbackSessionPublishStatus.NOT_PUBLISHED, response.getPublishStatus());

        assertNull(response.getIsClosingSoonEmailEnabled());
        assertNull(response.getIsPublishedEmailEnabled());

        assertEquals(0, response.getCreatedAtTimestamp());
        assertNull(response.getDeletedAtTimestamp());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());


    }

    @Test
    protected void textExecute_studentSubmissionNoExtensionAfterEndTimeWithinGracePeriod_statusGracePeriod() {

        ______TS("get submission by student with no extension; after end time but within grace period");

        Instant newStartTime = Instant.now().plusSeconds(-120);
        Instant newEndTime = newStartTime.plusSeconds(60);
        Duration newGracePeriod = Duration.ofDays(2);


        configureFeedbackSession(newStartTime, newEndTime, newGracePeriod);


        // mock no deadline extension
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(feedbackSession1.getEndTime());

        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(newEndTime, timeZone, true)
                        .toEpochMilli(),
                response.getSubmissionEndWithExtensionTimestamp());


    }

    @Test
    protected void textExecute_studentSubmissionNoExtensionAfterEndTimeBeyondGracePeriod_statusClosed() {

        ______TS("get submission by student with no extension; after end time and beyond grace period");

        Instant newStartTime = Instant.now().plusSeconds(-60);
        Instant newEndTime = newStartTime.plusSeconds(20);
        Duration newGracePeriod = Duration.ofSeconds(10);

        configureFeedbackSession(newStartTime, newEndTime, newGracePeriod);

        // mock no deadline extension
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(feedbackSession1.getEndTime());

        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(newEndTime, timeZone, true)
                        .toEpochMilli(),
                response.getSubmissionEndWithExtensionTimestamp());


    }

    @Test
    protected void textExecute_studentSubmissionWithExtensionBeforeEndTime_statusOpen() {

        ______TS("get submission by student with extension; before end time");

        Instant newStartTime = Instant.now();
        Instant newEndTime = newStartTime.plusSeconds(60 * 60);
        Duration newGracePeriod = Duration.ZERO;
        Instant extendedEndTime = newStartTime.plusSeconds(60 * 60 * 21);

        configureFeedbackSession(newStartTime, newEndTime,
                newGracePeriod, new DeadlineExtension(student1, feedbackSession1, extendedEndTime));

        // mock deadline extension exists for student1
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(extendedEndTime);

        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().containsKey(student1.getEmail()));
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getInstructorDeadlines().isEmpty());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(extendedEndTime, timeZone, true)
                        .toEpochMilli(),
                response.getSubmissionEndWithExtensionTimestamp());


    }

    @Test
    protected void textExecute_studentSubmissionWithExtensionAfterEndTimeBeforeExtendedDeadline_statusOpen() {

        ______TS("get submission by student with extension; after end time but before extended deadline");

        Instant newStartTime = Instant.now().plusSeconds(-60);
        Instant newEndTime = newStartTime.plusSeconds(20);
        Duration newGracePeriod = Duration.ZERO;
        Instant extendedEndTime = Instant.now().plusSeconds(60 * 60);

        configureFeedbackSession(newStartTime, newEndTime,
                newGracePeriod, new DeadlineExtension(student1, feedbackSession1, extendedEndTime));

        // mock deadline extension exists for student1
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(extendedEndTime);

        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.OPEN, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().containsKey(student1.getEmail()));
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getInstructorDeadlines().isEmpty());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(extendedEndTime, timeZone, true)
                        .toEpochMilli(),
                response.getSubmissionEndWithExtensionTimestamp());


    }

    @Test
    protected void textExecute_studentSubmissionWithExtensionAfterExtendedDeadlineWithinGracePeriod_statusGracePeriod() {

        ______TS("get submission by student with extension; after extended deadline but within grace period");

        Instant newStartTime = Instant.now().plusSeconds(-120);
        Instant newEndTime = newStartTime.plusSeconds(20);
        Instant extendedEndTime = newEndTime.plusSeconds(20);
        Duration newGracePeriod = Duration.ofDays(1);

        configureFeedbackSession(newStartTime, newEndTime,
                newGracePeriod, new DeadlineExtension(student1, feedbackSession1, extendedEndTime));

        // mock deadline extension exists for student1
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(extendedEndTime);

        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().containsKey(student1.getEmail()));
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getInstructorDeadlines().isEmpty());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(extendedEndTime, timeZone, true)
                        .toEpochMilli(),
                response.getSubmissionEndWithExtensionTimestamp());


    }

    @Test
    protected void textExecute_studentSubmissionWithExtensionAfterExtendedDeadlineBeyondGracePeriod_statusClosed() {

        ______TS("get submission by student with extension; after extended deadline and beyond grace period");

        Instant newStartTime = Instant.now().plusSeconds(-60 * 60 * 24);
        Instant newEndTime = newStartTime.plusSeconds(10);
        Instant extendedEndTime = newEndTime.plusSeconds(10);
        Duration newGracePeriod = Duration.ofSeconds(10);

        configureFeedbackSession(newStartTime, newEndTime,
                newGracePeriod, new DeadlineExtension(student1, feedbackSession1, extendedEndTime));

        // mock deadline extension exists for student1
        when(mockLogic.getDeadlineForUser(feedbackSession1, student1)).thenReturn(extendedEndTime);

        GetFeedbackSessionAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, response.getSubmissionStatus());

        assertTrue(response.getStudentDeadlines().containsKey(student1.getEmail()));
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getInstructorDeadlines().isEmpty());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(extendedEndTime, timeZone, true)
                        .toEpochMilli(),
                response.getSubmissionEndWithExtensionTimestamp());


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
        Student s = new Student(courseStudentIsIn, name, email, "comment for student-1");
        s.setAccount(new Account(googleId, name, email));
        return s;
    }

    private FeedbackSession generateSession1InCourse(Course course) {
        FeedbackSession fs = new FeedbackSession("feedbacksession-1", course,
                "instructor1@gmail.com", "generic instructions",
                Instant.parse("2012-04-01T22:00:00Z"), Instant.parse("2027-04-30T22:00:00Z"),
                Instant.parse("2012-03-28T22:00:00Z"), Instant.parse("2027-05-01T22:00:00Z"),
                Duration.ofHours(10), true, true, true);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));

        return fs;
    }

    private void configureFeedbackSession(Instant start, Instant end,
                                          Duration gracePeriod, DeadlineExtension... extensions) {
        feedbackSession1.setStartTime(start);
        feedbackSession1.setEndTime(end);
        feedbackSession1.setGracePeriod(gracePeriod);

        for(DeadlineExtension d : extensions) {
            feedbackSession1.getDeadlineExtensions().add(d);
        }
    }
}
