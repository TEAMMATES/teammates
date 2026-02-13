package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.FeedbackSessionCreateRequest;
import teammates.ui.webapi.CreateFeedbackSessionAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateFeedbackSessionAction}.
 */
public class CreateFeedbackSessionActionTest extends BaseActionTest<CreateFeedbackSessionAction> {

    private Course course;
    private Instructor instructor;
    private FeedbackSession feedbackSession;
    private Instant nearestHour;
    private Instant endHour;
    private Instant responseVisibleHour;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() throws InvalidParametersException, EntityAlreadyExistsException {
        nearestHour = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        endHour = Instant.now().plus(2, java.time.temporal.ChronoUnit.HOURS)
            .truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        responseVisibleHour = Instant.now().plus(3, java.time.temporal.ChronoUnit.HOURS)
        .truncatedTo(java.time.temporal.ChronoUnit.HOURS);

        course = generateCourse1();
        instructor = generateInstructor1InCourse(course);
        feedbackSession = generateSession1InCourse(course, instructor);

        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.createFeedbackSession(isA(FeedbackSession.class))).thenReturn(feedbackSession);
    }

    @Test
    protected void testExecute_insufficientParams_failure() {
        loginAsInstructor(instructor.getGoogleId());
        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_createFeedbackSession_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        try (MockedStatic<HibernateUtil> mockedHibernate = Mockito.mockStatic(HibernateUtil.class)) {
            loginAsInstructor(instructor.getGoogleId());
            String[] params = {
                    Const.ParamsNames.COURSE_ID, course.getId(),
            };

            FeedbackSessionCreateRequest createRequest = getTypicalCreateRequest();

            CreateFeedbackSessionAction a = getAction(createRequest, params);
            JsonResult r = getJsonResult(a);

            FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

            FeedbackSession createdFeedbackSession = feedbackSession;

            Mockito.verify(mockLogic, times(1)).createFeedbackSession(isA(FeedbackSession.class));
            mockedHibernate.verify(HibernateUtil::flushSession, times(1));

            assertEquals(createdFeedbackSession.getCourse().getId(), response.getCourseId());
            assertEquals(createdFeedbackSession.getCourse().getTimeZone(), response.getTimeZone());
            assertEquals(createdFeedbackSession.getName(), response.getFeedbackSessionName());

            assertEquals(createdFeedbackSession.getInstructions(), response.getInstructions());

            assertEquals(createdFeedbackSession.getStartTime().toEpochMilli(), response.getSubmissionStartTimestamp());
            assertEquals(createdFeedbackSession.getEndTime().toEpochMilli(), response.getSubmissionEndTimestamp());
            assertEquals(createdFeedbackSession.getGracePeriod().toMinutes(), response.getGracePeriod().longValue());

            assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
            assertEquals(createdFeedbackSession.getSessionVisibleFromTime().toEpochMilli(),
                    response.getCustomSessionVisibleTimestamp().longValue());
            assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
            assertEquals(createdFeedbackSession.getResultsVisibleFromTime().toEpochMilli(),
                    response.getCustomResponseVisibleTimestamp().longValue());

            assertEquals(createdFeedbackSession.isClosingSoonEmailEnabled(), response.getIsClosingSoonEmailEnabled());
            assertEquals(createdFeedbackSession.isPublishedEmailEnabled(), response.getIsPublishedEmailEnabled());

            assertEquals(createdFeedbackSession.getCreatedAt().toEpochMilli(), response.getCreatedAtTimestamp());
            assertNull(createdFeedbackSession.getDeletedAt());

            assertEquals(createdFeedbackSession.getName(), response.getFeedbackSessionName());
            assertEquals(createdFeedbackSession.getInstructions(), response.getInstructions());
            assertEquals(nearestHour.toEpochMilli(), response.getSubmissionStartTimestamp());
            assertEquals(endHour.toEpochMilli(), response.getSubmissionEndTimestamp());
            assertEquals(createdFeedbackSession.getGracePeriod().toMinutes(), response.getGracePeriod().longValue());

            assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
            assertEquals(nearestHour.toEpochMilli(), response.getCustomSessionVisibleTimestamp().longValue());

            assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
            assertEquals(responseVisibleHour.toEpochMilli(), response.getCustomResponseVisibleTimestamp().longValue());

            assertFalse(response.getIsClosingSoonEmailEnabled());
            assertFalse(response.getIsPublishedEmailEnabled());

            assertNotNull(response.getCreatedAtTimestamp());
            assertNull(response.getDeletedAtTimestamp());
        }
    }

    private FeedbackSessionCreateRequest getTypicalCreateRequest() {
        FeedbackSessionCreateRequest createRequest =
                new FeedbackSessionCreateRequest();
        createRequest.setFeedbackSessionName(feedbackSession.getName());
        createRequest.setInstructions(feedbackSession.getInstructions());

        // Preprocess session timings to adhere stricter checks
        createRequest.setSubmissionStartTimestamp(feedbackSession.getStartTime().toEpochMilli());
        createRequest.setSubmissionEndTimestamp(feedbackSession.getEndTime().toEpochMilli());
        createRequest.setGracePeriod(feedbackSession.getGracePeriod().toMinutes());

        createRequest.setSessionVisibleSetting(SessionVisibleSetting.CUSTOM);
        createRequest.setCustomSessionVisibleTimestamp(feedbackSession.getSessionVisibleFromTime().toEpochMilli());

        createRequest.setResponseVisibleSetting(ResponseVisibleSetting.CUSTOM);
        createRequest.setCustomResponseVisibleTimestamp(feedbackSession.getResultsVisibleFromTime().toEpochMilli());

        createRequest.setClosingSoonEmailEnabled(feedbackSession.isClosingSoonEmailEnabled());
        createRequest.setPublishedEmailEnabled(feedbackSession.isPublishedEmailEnabled());

        return createRequest;
    }

    private Course generateCourse1() {
        Course c = new Course("course-1", "Typical Course 1",
                "Africa/Johannesburg", "TEAMMATES Test Institute 0");
        c.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        c.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        return c;
    }

    private Instructor generateInstructor1InCourse(Course courseInstructorIsIn) {
        return new Instructor(courseInstructorIsIn, "instructor-1",
                "instructor-1@tm.tmt", false,
                "", null,
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER));
    }

    private FeedbackSession generateSession1InCourse(Course course, Instructor instructor) {
        FeedbackSession fs = new FeedbackSession("feedbacksession-1", course,
                instructor.getEmail(), "generic instructions",
                nearestHour, endHour,
                nearestHour, responseVisibleHour,
                Duration.ofHours(10), true, false, false);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));

        return fs;
    }
}
