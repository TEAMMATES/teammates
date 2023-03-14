package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CourseSectionNamesData;
import teammates.ui.webapi.GetCourseSectionNamesAction;

/**
 * SUT: {@link GetCourseSectionNamesAction}.
 */
public class GetCourseSectionNamesActionTest extends BaseActionTest<GetCourseSectionNamesAction> {

    String googleId = "user-googleId";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_SECTIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    void textExecute_courseDoesNotExist_throwsEntityDoesNotExistException() throws EntityDoesNotExistException {
        String courseId = "invalid-course-id";

        when(mockLogic.getSectionNamesForCourse(courseId)).thenThrow(new EntityDoesNotExistException(""));

        String[] params = {
            Const.ParamsNames.COURSE_ID, courseId
        };

        verifyEntityNotFound(params);
    }

    @Test
    void textExecute_courseExists_success() throws EntityDoesNotExistException {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        List<String> sectionNames = List.of("section-name-1", "section-name-2");

        when(mockLogic.getSectionNamesForCourse(course.getId())).thenReturn(sectionNames);

        String[] params = {
            Const.ParamsNames.COURSE_ID, course.getId()
        };

        GetCourseSectionNamesAction action = getAction(params);
        CourseSectionNamesData actionOutput = (CourseSectionNamesData) getJsonResult(action).getOutput();

        assertEquals(JsonUtils.toJson(new CourseSectionNamesData(sectionNames)), JsonUtils.toJson(actionOutput));
    }

    @Test
    void testSpecificAccessControl_instructor_canAccess() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        Instructor instructorOfCourse = new Instructor(course, "name", "instructoremail@tm.tmt", false, "", null, new InstructorPrivileges());

        loginAsInstructor(googleId);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructorOfCourse);

        String[] params = {
            Const.ParamsNames.COURSE_ID, course.getId()
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorOfAnotherCourse_cannotAccess() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        Course anotherCourse = new Course("another-course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        Instructor instructorOfAnotherCourse = new Instructor(anotherCourse, "name", "instructoremail@tm.tmt", false, "", null, new InstructorPrivileges());

        loginAsInstructor(googleId);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructorOfAnotherCourse);

        String[] params = {
            Const.ParamsNames.COURSE_ID, course.getId()
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_invalidInstructor_cannotAccess() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        loginAsInstructor(googleId);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(null);

        String[] params = {
            Const.ParamsNames.COURSE_ID, course.getId()
        };

        verifyCannotAccess(params);
    }
}
