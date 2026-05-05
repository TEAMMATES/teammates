package teammates.ui.webapi;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.request.FeedbackSessionRespondentRemindRequest;

/**
 * SUT: {@link RemindFeedbackSessionSubmissionAction}.
 */
public class RemindFeedbackSessionSubmissionActionTest
        extends BaseActionTest<RemindFeedbackSessionSubmissionAction> {

    private Course course;
    private Instructor instructor;
    private Student student;
    private Instant nearestHour;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_REMIND_SUBMISSION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        Mockito.reset(mockLogic, mockEmailGenerator);

        nearestHour = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.HOURS);

        course = generateCourse1();
        instructor = generateInstructor1InCourse(course);
        student = generateStudent1InCourse(course);

        loginAsInstructor(instructor.getGoogleId());

        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
    }

    @Test
    protected void testExecute_feedbackSessionNotPublished_warningMessage() {
        FeedbackSession closedFeedbackSession = generateClosedSessionInCourse(course, instructor);

        when(mockLogic.getFeedbackSession(closedFeedbackSession.getId()))
                .thenReturn(closedFeedbackSession);

        String[] paramsFeedbackSessionNotOpen = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, closedFeedbackSession.getId().toString(),
        };

        UUID[] usersToRemind = {instructor.getId(), student.getId()};
        FeedbackSessionRespondentRemindRequest remindRequest = new FeedbackSessionRespondentRemindRequest();
        remindRequest.setUsersToRemind(usersToRemind);

        InvalidOperationException ioe = verifyInvalidOperation(remindRequest, paramsFeedbackSessionNotOpen);
        assertEquals("Reminder email could not be sent out "
                + "as the feedback session is not open for submissions.", ioe.getMessage());

        verifyNoTasksAdded();
    }

    @Test
    protected void testExecute_openedFeedbackSession_success() {
        FeedbackSession openedFeedbackSession = generateOpenedSessionInCourse(course, instructor);
        EmailWrapper mockEmail = mock(EmailWrapper.class);

        when(mockLogic.getFeedbackSession(openedFeedbackSession.getId()))
                .thenReturn(openedFeedbackSession);
        when(mockLogic.getUser(student.getId())).thenReturn(student);
        when(mockLogic.getUser(instructor.getId())).thenReturn(instructor);
        when(mockEmailGenerator.generateFeedbackSessionReminderEmails(
                isA(FeedbackSession.class), anyList(), anyList(), isNull()))
                .thenReturn(List.of(mockEmail));

        UUID[] usersToRemind = {instructor.getId(), student.getId()};
        FeedbackSessionRespondentRemindRequest remindRequest = new FeedbackSessionRespondentRemindRequest();
        remindRequest.setUsersToRemind(usersToRemind);

        String[] paramsTypical = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, openedFeedbackSession.getId().toString(),
        };

        RemindFeedbackSessionSubmissionAction validAction = getAction(remindRequest, paramsTypical);
        getJsonResult(validAction);

        verifySpecifiedTasksAdded(Const.TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, 1);
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

    private Student generateStudent1InCourse(Course courseStudentIsIn) {
        String email = "student1@gmail.com";
        String name = "student-1";
        String googleId = "student-1";
        Student s = new Student(courseStudentIsIn, name, email, "comment for student-1");
        s.setAccount(new Account(googleId, name, email));
        return s;
    }

    private FeedbackSession generateOpenedSessionInCourse(Course course, Instructor instructor) {
        Instant beforeNow = nearestHour.minus(3, java.time.temporal.ChronoUnit.HOURS);
        Instant afterNow = nearestHour.plus(3, java.time.temporal.ChronoUnit.HOURS);
        FeedbackSession fs = new FeedbackSession("published-feedback-session",
                instructor.getEmail(), "generic instructions",
                beforeNow, afterNow,
                beforeNow, afterNow,
                Duration.ofHours(0), false, false);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        course.addFeedbackSession(fs);
        return fs;
    }

    private FeedbackSession generateClosedSessionInCourse(Course course, Instructor instructor) {
        Instant beforeNow = nearestHour.minus(3, java.time.temporal.ChronoUnit.HOURS);
        FeedbackSession fs = new FeedbackSession("unpublished-feedback-session",
                instructor.getEmail(), "generic instructions",
                beforeNow,
                beforeNow,
                beforeNow, beforeNow,
                Duration.ofHours(0), false, false);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        course.addFeedbackSession(fs);
        return fs;
    }
}
