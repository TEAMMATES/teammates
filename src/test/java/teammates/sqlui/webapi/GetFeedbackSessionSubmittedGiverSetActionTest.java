package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Sets;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionSubmittedGiverSet;
import teammates.ui.webapi.GetFeedbackSessionSubmittedGiverSetAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetFeedbackSessionSubmittedGiverSetAction}.
 */
public class GetFeedbackSessionSubmittedGiverSetActionTest extends
        BaseActionTest<GetFeedbackSessionSubmittedGiverSetAction> {

    private Instructor instructor;
    private Course course;
    private FeedbackSession feedbackSession;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_SUBMITTED_GIVER_SET;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        course = generateCourse1();
        instructor = generateInstructor1InCourse(course);
        feedbackSession = generateSession1InCourse(course, instructor);
    }

    @Test
    protected void testExecute_notEnoughParams_verifyHttpParamFailure() {
        loginAsInstructor(instructor.getGoogleId());
        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_getGiverSet_success() {
        Set<String> givers = new HashSet<>();
        givers.addAll(Arrays.asList(new String[] {
                "student1InCourse1@gmail.tmt", "student2InCourse1@gmail.tmt",
                "student5InCourse1@gmail.tmt", "student3InCourse1@gmail.tmt", "instructor1@course1.tmt", }));
        when(mockLogic.getGiverSetThatAnsweredFeedbackSession(feedbackSession.getName(), course.getId()))
                .thenReturn(givers);

        loginAsInstructor(instructor.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSession.getName(), };

        GetFeedbackSessionSubmittedGiverSetAction pageAction = getAction(submissionParams);
        JsonResult result = getJsonResult(pageAction);

        FeedbackSessionSubmittedGiverSet output = (FeedbackSessionSubmittedGiverSet) result.getOutput();
        assertEquals(Sets.newHashSet("student1InCourse1@gmail.tmt", "student2InCourse1@gmail.tmt",
                "student5InCourse1@gmail.tmt", "student3InCourse1@gmail.tmt", "instructor1@course1.tmt"),
                output.getGiverIdentifiers());
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
        Instant nearestHour = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        Instant endHour = Instant.now().plus(2, java.time.temporal.ChronoUnit.HOURS)
                .truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        Instant responseVisibleHour = Instant.now().plus(3, java.time.temporal.ChronoUnit.HOURS)
                .truncatedTo(java.time.temporal.ChronoUnit.HOURS);

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
