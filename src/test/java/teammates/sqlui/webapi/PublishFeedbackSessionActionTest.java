package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionPublishStatus;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.PublishFeedbackSessionAction;

/**
 * SUT: {@link PublishFeedbackSessionAction}.
 */
public class PublishFeedbackSessionActionTest extends BaseActionTest<PublishFeedbackSessionAction> {

    private Course course1;
    private FeedbackSession feedbackSession1, feedbackSession2;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_PUBLISH;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() throws InvalidParametersException, EntityDoesNotExistException {
        course1 = generateCourse1();
        feedbackSession1 = generateSession1InCourse(course1);
        feedbackSession2 = generateSession2InCourse(course1);
        Instructor instructor1 = generateInstructor1InCourse(course1);

        when(mockLogic.getFeedbackSession(feedbackSession1.getName(), course1.getId())).thenReturn(feedbackSession1);
        when(mockLogic.publishFeedbackSession(
                feedbackSession1.getName(), course1.getId())).thenReturn(feedbackSession2);
        when(mockLogic.getInstructorByGoogleId(
                course1.getId(), instructor1.getAccount().getGoogleId())).thenReturn(instructor1);

        loginAsInstructor(instructor1.getAccount().getGoogleId());
    }

    @Test
    protected void testExecute() {
        ______TS("Typical case");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSession1.getName(),
        };

        PublishFeedbackSessionAction publishFeedbackSessionAction = getAction(params);

        JsonResult result = getJsonResult(publishFeedbackSessionAction);
        FeedbackSessionData feedbackSessionData = (FeedbackSessionData) result.getOutput();

        when(mockLogic.getFeedbackSession(feedbackSession1.getName(), course1.getId())).thenReturn(feedbackSession2);

        assertEquals(feedbackSessionData.getFeedbackSessionName(), feedbackSession1.getName());
        assertEquals(FeedbackSessionPublishStatus.PUBLISHED, feedbackSessionData.getPublishStatus());
        assertTrue(mockLogic.getFeedbackSession(feedbackSession1.getName(), course1.getId()).isPublished());

        ______TS("Typical case: Session is already published");
        // Attempt to publish the same session again.

        result = getJsonResult(getAction(params));
        feedbackSessionData = (FeedbackSessionData) result.getOutput();

        assertEquals(feedbackSessionData.getFeedbackSessionName(), feedbackSession1.getName());
        assertEquals(FeedbackSessionPublishStatus.PUBLISHED, feedbackSessionData.getPublishStatus());
        assertTrue(mockLogic.getFeedbackSession(feedbackSession1.getName(), course1.getId()).isPublished());
    }

    @Test
    public void testExecute_invalidRequests_shouldFail() {
        ______TS("non existent session name");

        String randomSessionName = "randomName";

        assertNotNull(mockLogic.getFeedbackSession(feedbackSession1.getName(), course1.getId()));

        String[] params = {
                Const.ParamsNames.COURSE_ID, course1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, randomSessionName,
        };

        assertNull(mockLogic.getFeedbackSession(randomSessionName, course1.getId()));

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Feedback session not found", enfe.getMessage());

        ______TS("non existent course id");

        String randomCourseId = "randomCourseId";

        params = new String[] {
                Const.ParamsNames.COURSE_ID, randomCourseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSession1.getName(),
        };
        assertNull(mockLogic.getFeedbackSession(feedbackSession1.getName(), randomCourseId));

        enfe = verifyEntityNotFound(params);
        assertEquals("Feedback session not found", enfe.getMessage());
    }

    private Course generateCourse1() {
        Course c = new Course("course-1", "Typical Course 1",
                "Africa/Johannesburg", "TEAMMATES Test Institute 0");
        c.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        c.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        return c;
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

    private FeedbackSession generateSession2InCourse(Course course) {
        FeedbackSession fs = new FeedbackSession("feedbacksession-1", course,
                "instructor1@gmail.com", "generic instructions",
                Instant.parse("2012-04-01T22:00:00Z"), Instant.parse("2020-04-30T22:00:00Z"),
                Instant.parse("2012-03-28T22:00:00Z"), Instant.parse("2020-05-01T22:00:00Z"),
                Duration.ofHours(10), true, true, true);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));

        return fs;
    }

    private Instructor generateInstructor1InCourse(Course course) {
        InstructorPrivileges instructorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        Instructor instructor = new Instructor(course, "instructor-name", "valid-instructor@email.tmt",
                true, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges);

        instructor.setAccount(new Account("valid-instructor", "instructor-name", "valid-instructor@email.tmt"));

        return instructor;
    }

}
