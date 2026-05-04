package teammates.ui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
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
import teammates.logic.entity.Course;
import teammates.logic.entity.DeadlineExtension;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.ui.request.DeadlineExtensionsUpdateRequest;

/**
 * SUT: {@link UpdateDeadlineExtensionsAction}.
 */
public class UpdateDeadlineExtensionsActionTest
        extends BaseActionTest<UpdateDeadlineExtensionsAction> {

    private Course course;
    private Instructor instructor;
    private Instant nearestHour;
    private Instant endHour;
    private Instant responseVisibleHour;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_DEADLINE_EXTENSIONS;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        nearestHour = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        endHour = Instant.now().plus(2, java.time.temporal.ChronoUnit.HOURS)
                .truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        responseVisibleHour = Instant.now().plus(3, java.time.temporal.ChronoUnit.HOURS)
                .truncatedTo(java.time.temporal.ChronoUnit.HOURS);

        course = generateCourse1();
        instructor = generateInstructor1InCourse(course);

        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentsForCourse(course.getId())).thenReturn(new ArrayList<>());
        when(mockLogic.getInstructorsByCourse(course.getId())).thenReturn(new ArrayList<>(List.of(instructor)));

        when(mockLogic.verifyInstructorsExistInCourse(anyString(), anyList())).thenReturn(true);
        when(mockLogic.verifyStudentsExistInCourse(anyString(), anyList())).thenReturn(true);
    }

    @Test
    void testExecute_updateDeadlineExtensionEndTime_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        loginAsInstructor(instructor.getGoogleId());
        FeedbackSession originalFeedbackSession = generateSession1InCourse(course, instructor);

        String[] param = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, originalFeedbackSession.getId().toString(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };

        List<DeadlineExtension> originalDeadlines = new ArrayList<>();
        originalDeadlines.add(new DeadlineExtension(instructor, originalFeedbackSession, nearestHour));
        originalFeedbackSession.setDeadlineExtensions(originalDeadlines);

        when(mockLogic.getFeedbackSession(originalFeedbackSession.getId())).thenReturn(originalFeedbackSession);

        DeadlineExtensionsUpdateRequest updateRequest =
                buildUpdateRequest(instructor.getEmail(), endHour);
        UpdateDeadlineExtensionsAction a = getAction(updateRequest, param);
        getJsonResult(a);

        verify(mockLogic, times(1)).updateDeadlineExtension(any());
        verify(mockLogic).updateDeadlineExtension(argThat((DeadlineExtension de) -> de.getEndTime().equals(endHour)));
    }

    @Test
    void testExecute_createDeadlineExtensionEndTime_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        loginAsInstructor(instructor.getGoogleId());
        FeedbackSession originalFeedbackSession = generateSession1InCourse(course, instructor);

        String[] param = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, originalFeedbackSession.getId().toString(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };

        originalFeedbackSession.setDeadlineExtensions(new ArrayList<>());

        when(mockLogic.getFeedbackSession(originalFeedbackSession.getId())).thenReturn(originalFeedbackSession);
        when(mockLogic.getInstructorForEmail(originalFeedbackSession.getCourseId(), instructor.getEmail()))
                .thenReturn(instructor);

        DeadlineExtensionsUpdateRequest updateRequest =
                buildUpdateRequest(instructor.getEmail(), nearestHour);
        UpdateDeadlineExtensionsAction a = getAction(updateRequest, param);
        getJsonResult(a);

        verify(mockLogic, times(1)).createDeadlineExtension(any());
        verify(mockLogic).createDeadlineExtension(argThat((DeadlineExtension de) -> de.getEndTime().equals(nearestHour)));
    }

    /**
     * Builds a {@link DeadlineExtensionsUpdateRequest} with the given deadline values.
     * Pass null for an email to omit that user from the request.
     */
    private DeadlineExtensionsUpdateRequest buildUpdateRequest(
            String instructorEmail, Instant instructorDeadlineInstant) {
        DeadlineExtensionsUpdateRequest request =
                new DeadlineExtensionsUpdateRequest();

        Map<String, Long> instructorDeadlines = new HashMap<>();
        if (instructorEmail != null && instructorDeadlineInstant != null) {
            instructorDeadlines.put(instructorEmail, instructorDeadlineInstant.toEpochMilli());
        }
        request.setInstructorDeadlines(instructorDeadlines);

        Map<String, Long> studentDeadlines = new HashMap<>();
        request.setStudentDeadlines(studentDeadlines);

        return request;
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
                Duration.ofHours(10), false, false);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        return fs;
    }
}
