package teammates.ui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.DeadlineExtensionsData;

/**
 * SUT: {@link GetDeadlineExtensionsAction}.
 */
public class GetDeadlineExtensionsActionTest
        extends BaseActionTest<GetDeadlineExtensionsAction> {

    private static final String COURSE_ID = "course-id";
    private static final String FEEDBACK_SESSION_NAME = "feedback-session-name";

    private FeedbackSession typicalFeedbackSession;
    private Instructor typicalInstructor;
    private Student typicalStudent;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_DEADLINE_EXTENSIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    protected void setUp() {
        Course course = getTypicalCourse();
        course.setId(COURSE_ID);

        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(course);
        typicalFeedbackSession.setName(FEEDBACK_SESSION_NAME);
        typicalFeedbackSession.setDeadlineExtensions(new HashSet<>());

        typicalInstructor = getTypicalInstructor();
        typicalStudent = getTypicalStudent();

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getId())).thenReturn(typicalFeedbackSession);
    }

    @Test
    void testAccessControl_instructorWithModifySessionPrivilege_canAccess() {
        String[] params = getTypicalParams();

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                typicalFeedbackSession.getCourse(),
                Const.InstructorPermissions.CAN_MODIFY_SESSION,
                params);
        verifyInstructorsOfOtherCoursesCannotAccess(params);
    }

    @Test
    void testExecute_noParameters_shouldFail() {
        loginAsInstructor(typicalInstructor.getGoogleId());
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_sessionNotFound_throwsEntityNotFoundException() {
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getId())).thenReturn(null);
        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyEntityNotFoundAcl(getTypicalParams());
    }

    @Test
    void testExecute_withDeadlineExtensions_returnsDeadlineExtensions() throws EntityDoesNotExistException {
        Instant extensionEndTime = Instant.parse("2030-01-01T00:00:00Z");
        DeadlineExtension studentDeadline = new DeadlineExtension(typicalStudent, extensionEndTime);
        DeadlineExtension instructorDeadline = new DeadlineExtension(typicalInstructor, extensionEndTime);

        when(mockLogic.getDeadlineExtensions(typicalFeedbackSession.getId())).thenReturn(
                Set.of(studentDeadline, instructorDeadline));

        GetDeadlineExtensionsAction action = getAction(getTypicalParams());
        JsonResult result = getJsonResult(action);

        DeadlineExtensionsData response =
                (DeadlineExtensionsData) result.getOutput();

        assertEquals(2, response.getUserDeadlines().size());
        assertEquals(extensionEndTime, Instant.ofEpochMilli(response.getUserDeadlines().get(typicalStudent.getId())));
        assertEquals(extensionEndTime, Instant.ofEpochMilli(response.getUserDeadlines().get(typicalInstructor.getId())));
    }

    private String[] getTypicalParams() {
        return new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, typicalFeedbackSession.getId().toString(),
        };
    }
}
