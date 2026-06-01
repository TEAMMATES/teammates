package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.Course;
import teammates.storage.entity.Section;
import teammates.ui.output.CourseSectionsData;

/**
 * SUT: {@link GetCourseSectionsAction}.
 */
public class GetCourseSectionsActionTest extends BaseActionTest<GetCourseSectionsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_SECTIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    void testExecute_courseDoesNotExist_throwsEntityDoesNotExistException() throws EntityDoesNotExistException {
        String courseId = "invalid-course-id";

        when(mockLogic.getSectionsForCourse(courseId)).thenThrow(new EntityDoesNotExistException(""));

        String[] params = {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_courseExists_success() throws EntityDoesNotExistException {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        Set<Section> sections = Set.of(new Section("section-name-1"), new Section("section-name-2"));

        when(mockLogic.getSectionsForCourse(course.getId())).thenReturn(sections);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        GetCourseSectionsAction action = getAction(params);
        CourseSectionsData actionOutput = (CourseSectionsData) getJsonResult(action).getOutput();

        assertEquals(JsonUtils.toJson(new CourseSectionsData(sections)), JsonUtils.toJson(actionOutput));
    }

    @Test
    void testAccessControl() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(course, params);
    }
}
