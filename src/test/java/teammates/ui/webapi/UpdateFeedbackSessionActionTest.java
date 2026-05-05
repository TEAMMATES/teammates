package teammates.ui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelperExtension;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.FeedbackSessionUpdateRequest;

/**
 * SUT: {@link UpdateFeedbackSessionAction}.
 */
public class UpdateFeedbackSessionActionTest extends BaseActionTest<UpdateFeedbackSessionAction> {

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
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        Instant now = Instant.now();
        nearestHour = now.truncatedTo(ChronoUnit.HOURS);
        endHour = now.plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.HOURS);
        responseVisibleHour = now.plus(3, ChronoUnit.HOURS).truncatedTo(ChronoUnit.HOURS);

        course = generateCourse1();
        instructor = generateInstructor1InCourse(course);
        feedbackSession = generateSession1InCourse(course, instructor);

        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getFeedbackSession(feedbackSession.getId())).thenReturn(feedbackSession);
    }

    @Test
    void testExecute_typicalCase_success() throws Exception {
        loginAsInstructor(instructor.getGoogleId());

        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest(feedbackSession);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSession.getId().toString(),
        };

        when(mockLogic.updateFeedbackSession(eq(feedbackSession.getId()), any())).thenReturn(feedbackSession);

        UpdateFeedbackSessionAction a = getAction(updateRequest, params);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        assertEquals(feedbackSession.getId(), response.getFeedbackSessionId());
        verify(mockLogic, times(1)).updateFeedbackSession(eq(feedbackSession.getId()), any());
    }

    @Test
    void testExecute_missingParams_throwsInvalidHttpParameterException() {
        loginAsInstructor(instructor.getGoogleId());

        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest(feedbackSession);

        verifyHttpParameterFailure(updateRequest);
    }

    @Test
    void testExecute_updateThrowsInvalidParametersException_throwsInvalidHttpRequestBodyException() throws Exception {
        loginAsInstructor(instructor.getGoogleId());

        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest(feedbackSession);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSession.getId().toString(),
        };

        when(mockLogic.updateFeedbackSession(eq(feedbackSession.getId()), any()))
                .thenThrow(new InvalidParametersException("invalid parameters"));

        verifyHttpRequestBodyFailure(updateRequest, params);
    }

    @Test
    void testExecute_updateThrowsEntityDoesNotExistException_throwsEntityNotFoundException() throws Exception {
        loginAsInstructor(instructor.getGoogleId());
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest(feedbackSession);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSession.getId().toString(),
        };

        when(mockLogic.updateFeedbackSession(eq(feedbackSession.getId()), any()))
                .thenThrow(new EntityDoesNotExistException("entity does not exist"));

        verifyEntityNotFound(updateRequest, params);
    }

    @Test
    void testAccessControl_nonExistentFeedbackSession_throwsEntityNotFoundException() {
        loginAsInstructor(instructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, "cde128d8-14fe-43a7-b6bd-afa816f5b1f4",
        };

        verifyEntityNotFoundAcl(params);
    }

    @Test
    void testAccessControl_instructorWithoutPrivilege_cannotAccess() {
        InstructorPrivileges observerPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSession.getId().toString(),
        };

        verifyInaccessibleWithoutCorrectSameCoursePrivilege(course, observerPrivileges, params);
    }

    @Test
    void testAccessControl_instructorOfOtherCourse_cannotAccess() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSession.getId().toString(),
        };

        verifyInstructorsOfOtherCoursesCannotAccess(params);
    }

    @Test
    void testAccessControl_nonInstructor_cannotAccess() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSession.getId().toString(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(course, params);
    }

    private FeedbackSessionUpdateRequest getTypicalFeedbackSessionUpdateRequest(FeedbackSession fs) {
        FeedbackSessionUpdateRequest updateRequest = new FeedbackSessionUpdateRequest();
        updateRequest.setInstructions("instructions");
        String timeZone = fs.getCourse().getTimeZone();

        updateRequest.setSubmissionStartTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                2, timeZone).toEpochMilli());
        updateRequest.setSubmissionEndTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, timeZone).toEpochMilli());
        updateRequest.setGracePeriod(5);

        updateRequest.setSessionVisibleSetting(SessionVisibleSetting.CUSTOM);
        updateRequest.setCustomSessionVisibleTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                2, timeZone).toEpochMilli());

        updateRequest.setResponseVisibleSetting(ResponseVisibleSetting.CUSTOM);
        updateRequest.setCustomResponseVisibleTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, timeZone).toEpochMilli());

        updateRequest.setClosingSoonEmailEnabled(false);
        updateRequest.setPublishedEmailEnabled(false);

        return updateRequest;
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

    private FeedbackSession generateSession1InCourse(Course course, Instructor inst) {
        FeedbackSession fs = new FeedbackSession("feedbacksession-1",
                inst.getEmail(), "generic instructions",
                nearestHour, endHour,
                nearestHour, responseVisibleHour,
                Duration.ofHours(10), false, false);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        course.addFeedbackSession(fs);
        return fs;
    }
}
