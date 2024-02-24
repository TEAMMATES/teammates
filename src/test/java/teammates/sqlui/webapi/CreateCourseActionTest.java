package teammates.sqlui.webapi;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CourseData;
import teammates.ui.request.CourseCreateRequest;
import teammates.ui.webapi.CreateCourseAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateCourseAction}.
 */
public class CreateCourseActionTest extends BaseActionTest<CreateCourseAction> {

    String googleId = "user-googleId";
    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test (enabled = false)
    void testExecute_courseDoesNotExist_success() throws InvalidParametersException, EntityAlreadyExistsException {
        loginAsInstructor(googleId);
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        Instructor instructor = new Instructor(course, "name", "instructoremail@tm.tmt", false, "", null, null);

        Course expectedCourse = new Course(course.getId(), course.getName(), course.getTimeZone(), course.getInstitute());
        expectedCourse.setCreatedAt(Instant.parse("2022-01-01T00:00:00Z"));

        when(mockLogic.createCourse(course)).thenReturn(expectedCourse);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);
        mockHibernateUtil.when(HibernateUtil::flushSession).thenAnswer(Answers.RETURNS_DEFAULTS);

        CourseCreateRequest request = new CourseCreateRequest();
        request.setCourseName(course.getName());
        request.setTimeZone(course.getTimeZone());
        request.setCourseId(course.getId());

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, course.getInstitute(),
        };

        CreateCourseAction action = getAction(request, params);
        JsonResult result = getJsonResult(action);
        CourseData actionOutput = (CourseData) result.getOutput();
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        assertEquals(course.getId(), actionOutput.getCourseId());
        assertEquals(course.getName(), actionOutput.getCourseName());
        assertEquals(course.getTimeZone(), actionOutput.getTimeZone());

        assertNull(course.getCreatedAt());
        assertNotNull(actionOutput.getCreationTimestamp());
    }

    @Test (enabled = false)
    void testExecute_courseAlreadyExists_throwsInvalidOperationException()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = new Course("existing-course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        when(mockLogic.createCourse(course)).thenThrow(new EntityAlreadyExistsException(""));

        CourseCreateRequest request = new CourseCreateRequest();
        request.setCourseName(course.getName());
        request.setTimeZone(course.getTimeZone());
        request.setCourseId(course.getId());

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, course.getInstitute(),
        };

        verifyInvalidOperation(request, params);
    }

    @Test (enabled = false)
    void testExecute_invalidCourseName_throwsInvalidHttpRequestBodyException()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = new Course("invalid-course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        when(mockLogic.createCourse(course)).thenThrow(new InvalidParametersException(""));

        CourseCreateRequest request = new CourseCreateRequest();
        request.setCourseName(course.getName());
        request.setTimeZone(course.getTimeZone());
        request.setCourseId(course.getId());

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, course.getInstitute(),
        };

        verifyHttpRequestBodyFailure(request, params);
    }

    @Test (enabled = false)
    void testSpecificAccessControl_asInstructorAndCanCreateCourse_canAccess() {
        String institute = "institute";
        loginAsInstructor(googleId);
        when(mockLogic.canInstructorCreateCourse(googleId, institute)).thenReturn(true);

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute,
        };

        verifyCanAccess(params);
    }

    @Test (enabled = false)
    void testSpecificAccessControl_asInstructorAndCannotCreateCourse_cannotAccess() {
        String institute = "institute";
        loginAsInstructor(googleId);
        when(mockLogic.canInstructorCreateCourse(googleId, institute)).thenReturn(false);

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute,
        };

        verifyCannotAccess(params);
    }

    @Test (enabled = false)
    void testSpecificAccessControl_notInstructor_cannotAccess() {
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, "institute",
        };
        loginAsStudent(googleId);
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }
}
