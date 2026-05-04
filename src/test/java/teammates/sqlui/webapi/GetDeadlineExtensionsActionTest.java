package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.logic.entity.Course;
import teammates.logic.entity.DeadlineExtension;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Student;
import teammates.ui.output.DeadlineExtensionsData;
import teammates.ui.webapi.GetDeadlineExtensionsAction;
import teammates.ui.webapi.JsonResult;

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
        typicalFeedbackSession.setDeadlineExtensions(new ArrayList<>());

        typicalInstructor = getTypicalInstructor();
        typicalStudent = getTypicalStudent();

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getId())).thenReturn(typicalFeedbackSession);
        when(mockLogic.getStudentsForCourse(COURSE_ID)).thenReturn(new ArrayList<>());
        when(mockLogic.getInstructorsByCourse(COURSE_ID)).thenReturn(new ArrayList<>());
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
    void testExecute_noDeadlineExtensions_returnsEmptyMaps() {
        loginAsInstructor(typicalInstructor.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(COURSE_ID, typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        GetDeadlineExtensionsAction action = getAction(getTypicalParams());
        JsonResult result = getJsonResult(action);

        DeadlineExtensionsData response =
                (DeadlineExtensionsData) result.getOutput();
        assertTrue(response.getStudentDeadlines().isEmpty());
        assertTrue(response.getInstructorDeadlines().isEmpty());
    }

    @Test
    void testExecute_withStudentDeadlineExtension_returnsStudentDeadline() {
        Instant extensionEndTime = Instant.parse("2030-01-01T00:00:00Z");
        DeadlineExtension studentDeadline = new DeadlineExtension(typicalStudent, typicalFeedbackSession, extensionEndTime);
        typicalFeedbackSession.setDeadlineExtensions(List.of(studentDeadline));

        when(mockLogic.getStudentsForCourse(COURSE_ID)).thenReturn(List.of(typicalStudent));
        loginAsInstructor(typicalInstructor.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(COURSE_ID, typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        GetDeadlineExtensionsAction action = getAction(getTypicalParams());
        JsonResult result = getJsonResult(action);

        DeadlineExtensionsData response =
                (DeadlineExtensionsData) result.getOutput();
        assertTrue(response.getInstructorDeadlines().isEmpty());
        assertEquals(1, response.getStudentDeadlines().size());
        assertTrue(response.getStudentDeadlines().containsKey(typicalStudent.getEmail()));
    }

    @Test
    void testExecute_withInstructorDeadlineExtension_returnsInstructorDeadline() {
        Instant extensionEndTime = Instant.parse("2030-01-01T00:00:00Z");
        DeadlineExtension instructorDeadline =
                new DeadlineExtension(typicalInstructor, typicalFeedbackSession, extensionEndTime);
        typicalFeedbackSession.setDeadlineExtensions(List.of(instructorDeadline));

        when(mockLogic.getInstructorsByCourse(COURSE_ID)).thenReturn(List.of(typicalInstructor));
        loginAsInstructor(typicalInstructor.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(COURSE_ID, typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        GetDeadlineExtensionsAction action = getAction(getTypicalParams());
        JsonResult result = getJsonResult(action);

        DeadlineExtensionsData response =
                (DeadlineExtensionsData) result.getOutput();
        assertTrue(response.getStudentDeadlines().isEmpty());
        assertEquals(1, response.getInstructorDeadlines().size());
        assertTrue(response.getInstructorDeadlines().containsKey(typicalInstructor.getEmail()));
    }

    private String[] getTypicalParams() {
        return new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, typicalFeedbackSession.getId().toString(),
        };
    }
}
