package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CourseData;
import teammates.ui.output.CoursesData;
import teammates.ui.webapi.Action;
import teammates.ui.webapi.GetCoursesAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetCoursesAction}.
 */
public class GetCoursesActionTest extends BaseActionTest<GetCoursesAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    void testExecute_withInstructorAndActiveCourses_success() {
        Instructor instructor = getTypicalInstructor();
        Course course = getTypicalCourse();
        List<Course> courseList = new ArrayList<>();
        courseList.add(course);
        List<Instructor> instructorList = new ArrayList<>();
        instructorList.add(instructor);

        loginAsInstructor(instructor.getGoogleId());

        List<teammates.common.datatransfer.attributes.InstructorAttributes> instructorAttributesList = new ArrayList<>();
        List<CourseAttributes> courseAttributesList = new ArrayList<>();

        List<CourseData> courseDataList = new ArrayList<>();
        courseDataList.add(new CourseData(course));
        //when(mockLogic.getCoursesForInstructors(instructor.getId(), true)).thenReturn(getTypicalCourses());
        when(mockLogic.getInstructorsForGoogleId(instructor.getGoogleId())).thenReturn(instructorList);
        when(mockLogic.getCoursesForInstructors(any())).thenReturn(courseList);


        Logic dataStoreLogic = mock(Logic.class);
        when(dataStoreLogic.getInstructorsForGoogleId(instructor.getGoogleId(),true)).thenReturn(instructorAttributesList);
        when(dataStoreLogic.getCoursesForInstructor(any())).thenReturn(courseAttributesList);
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE,
        };
        GetCoursesAction a = getAction(params);
        JsonResult result = a.execute();
        CoursesData x = (CoursesData) result.getOutput();
        assertEquals(courseDataList.size(), x.getCourses().size());


    }
}
