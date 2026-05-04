package teammates.ui.webapi;

import static org.mockito.Mockito.when;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.logic.entity.Course;
import teammates.ui.output.CourseSectionNamesData;

/**
 * SUT: {@link GetCourseSectionNamesAction}.
 */
public class GetCourseSectionNamesActionTest extends BaseActionTest<GetCourseSectionNamesAction> {

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

        when(mockLogic.getSectionNamesForCourse(courseId)).thenThrow(new EntityDoesNotExistException(""));

        String[] params = {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_courseExists_success() throws EntityDoesNotExistException {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        List<String> sectionNames = List.of("section-name-1", "section-name-2");

        when(mockLogic.getSectionNamesForCourse(course.getId())).thenReturn(sectionNames);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        GetCourseSectionNamesAction action = getAction(params);
        CourseSectionNamesData actionOutput = (CourseSectionNamesData) getJsonResult(action).getOutput();

        assertEquals(JsonUtils.toJson(new CourseSectionNamesData(sectionNames)), JsonUtils.toJson(actionOutput));
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
