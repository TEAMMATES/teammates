package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelperExtension;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.FeedbackSessionUpdateRequest;
import teammates.ui.webapi.UpdateFeedbackSessionAction;

/**
 * SUT: {@link UpdateFeedbackSessionAction}.
 */
public class UpdateFeedbackSessionActionTest extends BaseActionTest<UpdateFeedbackSessionAction> {

    private Course course;
    private Instructor instructor;
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
    void setUp() throws InvalidParametersException, EntityAlreadyExistsException {
        nearestHour = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        endHour = Instant.now().plus(2, java.time.temporal.ChronoUnit.HOURS)
            .truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        responseVisibleHour = Instant.now().plus(3, java.time.temporal.ChronoUnit.HOURS)
        .truncatedTo(java.time.temporal.ChronoUnit.HOURS);

        course = generateCourse1();
        instructor = generateInstructor1InCourse(course);

        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
    }

    @Test
    void testExecute_updateDeadlineExtensionEndTime_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        loginAsInstructor(instructor.getGoogleId());
        FeedbackSession originalFeedbackSession = generateSession1InCourse(course, instructor);

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, originalFeedbackSession.getCourse().getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, originalFeedbackSession.getName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };

        List<DeadlineExtension> originalDeadlines = new ArrayList<>();

        originalDeadlines.add(new DeadlineExtension(instructor, originalFeedbackSession, nearestHour));
        originalFeedbackSession.setDeadlineExtensions(originalDeadlines);

        when(mockLogic.getFeedbackSession(any(), any())).thenReturn(originalFeedbackSession);

        FeedbackSession updatedFeedbackSessionWithLaterEndTime = generateSession1InCourse(course, instructor);
        List<DeadlineExtension> updatedDeadlines = new ArrayList<>();
        updatedDeadlines.add(new DeadlineExtension(instructor,
                updatedFeedbackSessionWithLaterEndTime, endHour));
        updatedFeedbackSessionWithLaterEndTime.setDeadlineExtensions(updatedDeadlines);

        when(mockLogic.updateFeedbackSession(originalFeedbackSession)).thenReturn(updatedFeedbackSessionWithLaterEndTime);

        FeedbackSessionUpdateRequest updateRequest =
                getTypicalFeedbackSessionUpdateRequest(updatedFeedbackSessionWithLaterEndTime);
        UpdateFeedbackSessionAction a = getAction(updateRequest, param);
        getJsonResult(a);

        verify(mockLogic, times(1)).updateDeadlineExtension(any());
        verify(mockLogic).updateDeadlineExtension(argThat((DeadlineExtension de) -> de.getEndTime().equals(endHour)));
    }

    @Test
    void testExecute_createDeadlineExtensionEndTime_success()
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        loginAsInstructor(instructor.getGoogleId());
        FeedbackSession originalFeedbackSession = generateSession1InCourse(course, instructor);

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, originalFeedbackSession.getCourse().getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, originalFeedbackSession.getName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };

        List<DeadlineExtension> originalDeadlines = new ArrayList<>();
        originalFeedbackSession.setDeadlineExtensions(originalDeadlines);

        when(mockLogic.getFeedbackSession(any(), any())).thenReturn(originalFeedbackSession);

        FeedbackSession updatedFeedbackSessionWithLaterEndTime = generateSession1InCourse(course, instructor);
        List<DeadlineExtension> updatedDeadlines = new ArrayList<>();
        updatedDeadlines.add(new DeadlineExtension(instructor,
                updatedFeedbackSessionWithLaterEndTime, nearestHour));
        updatedFeedbackSessionWithLaterEndTime.setDeadlineExtensions(updatedDeadlines);

        when(mockLogic.updateFeedbackSession(originalFeedbackSession)).thenReturn(updatedFeedbackSessionWithLaterEndTime);

        FeedbackSessionUpdateRequest updateRequest =
                getTypicalFeedbackSessionUpdateRequest(updatedFeedbackSessionWithLaterEndTime);
        UpdateFeedbackSessionAction a = getAction(updateRequest, param);
        getJsonResult(a);

        verify(mockLogic, times(1)).createDeadlineExtension(any());
        verify(mockLogic).createDeadlineExtension(argThat((DeadlineExtension de) -> de.getEndTime().equals(nearestHour)));
    }

    private FeedbackSessionUpdateRequest getTypicalFeedbackSessionUpdateRequest(FeedbackSession feedbackSession) {
        FeedbackSessionUpdateRequest updateRequest = new FeedbackSessionUpdateRequest();
        updateRequest.setInstructions("instructions");
        String timeZone = feedbackSession.getCourse().getTimeZone();

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

        Map<String, Long> instructorDeadlines = new HashMap<>();
        Map<String, Long> studentDeadlines = new HashMap<>();

        assert feedbackSession.getDeadlineExtensions() != null;
        for (DeadlineExtension de : feedbackSession.getDeadlineExtensions()) {
            assert de != null;
            if (de.getUser() instanceof Student) {
                studentDeadlines.put(de.getUser().getEmail(), de.getEndTime().toEpochMilli());
            } else if (de.getUser() instanceof Instructor) {
                instructorDeadlines.put(de.getUser().getEmail(), de.getEndTime().toEpochMilli());
            }
        }

        updateRequest.setStudentDeadlines(studentDeadlines);
        updateRequest.setInstructorDeadlines(instructorDeadlines);

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
