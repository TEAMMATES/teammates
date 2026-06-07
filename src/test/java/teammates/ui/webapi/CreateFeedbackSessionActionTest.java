package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionViewData;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.FeedbackSessionCreateRequest;

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

            FeedbackSessionViewData response = (FeedbackSessionViewData) r.getOutput();
            FeedbackSessionData responseData = response.getFeedbackSession();

            FeedbackSession createdFeedbackSession = feedbackSession;

            Mockito.verify(mockLogic, times(1)).createFeedbackSession(isA(FeedbackSession.class));
            mockedHibernate.verify(HibernateUtil::flushSession, times(1));

            assertEquals(createdFeedbackSession.getCourseId(), responseData.getCourseId());
            assertEquals(createdFeedbackSession.getCourse().getTimeZone(), responseData.getTimeZone());
            assertEquals(createdFeedbackSession.getName(), responseData.getFeedbackSessionName());

            assertEquals(createdFeedbackSession.getInstructions(), responseData.getInstructions());

            assertEquals(createdFeedbackSession.getStartTime().toEpochMilli(), responseData.getSubmissionStartTimestamp());
            assertEquals(createdFeedbackSession.getEndTime().toEpochMilli(), responseData.getSubmissionEndTimestamp());
            assertEquals(createdFeedbackSession.getGracePeriod().toMinutes(), responseData.getGracePeriod().longValue());

            assertEquals(SessionVisibleSetting.CUSTOM, responseData.getSessionVisibleSetting());
            assertEquals(createdFeedbackSession.getSessionVisibleFromTime().toEpochMilli(),
                    responseData.getCustomSessionVisibleTimestamp().longValue());
            assertEquals(ResponseVisibleSetting.CUSTOM, responseData.getResponseVisibleSetting());
            assertEquals(createdFeedbackSession.getResultsVisibleFromTime().toEpochMilli(),
                    responseData.getCustomResponseVisibleTimestamp().longValue());

            assertEquals(createdFeedbackSession.isClosingSoonEmailEnabled(), responseData.getIsClosingSoonEmailEnabled());
            assertEquals(createdFeedbackSession.isPublishedEmailEnabled(), responseData.getIsPublishedEmailEnabled());

            assertEquals(createdFeedbackSession.getCreatedAt().toEpochMilli(), responseData.getCreatedAtTimestamp());
            assertNull(createdFeedbackSession.getDeletedAt());

            assertEquals(createdFeedbackSession.getName(), responseData.getFeedbackSessionName());
            assertEquals(createdFeedbackSession.getInstructions(), responseData.getInstructions());
            assertEquals(nearestHour.toEpochMilli(), responseData.getSubmissionStartTimestamp());
            assertEquals(endHour.toEpochMilli(), responseData.getSubmissionEndTimestamp());
            assertEquals(createdFeedbackSession.getGracePeriod().toMinutes(), responseData.getGracePeriod().longValue());

            assertEquals(SessionVisibleSetting.CUSTOM, responseData.getSessionVisibleSetting());
            assertEquals(nearestHour.toEpochMilli(), responseData.getCustomSessionVisibleTimestamp().longValue());

            assertEquals(ResponseVisibleSetting.CUSTOM, responseData.getResponseVisibleSetting());
            assertEquals(responseVisibleHour.toEpochMilli(), responseData.getCustomResponseVisibleTimestamp().longValue());

            assertFalse(responseData.getIsClosingSoonEmailEnabled());
            assertFalse(responseData.getIsPublishedEmailEnabled());

            assertNotNull(responseData.getCreatedAtTimestamp());
            assertNull(responseData.getDeletedAtTimestamp());
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
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.MANAGER));
    }

    private FeedbackSession generateSession1InCourse(Course course, Instructor instructor) {
        FeedbackSession fs = new FeedbackSession("feedbacksession-1",
                instructor, "generic instructions",
                nearestHour, endHour,
                nearestHour, responseVisibleHour,
                Duration.ofHours(10), false, false);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        course.addFeedbackSession(fs);
        return fs;
    }
}
