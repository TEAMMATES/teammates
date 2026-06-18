package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.test.GroupNames;
import teammates.ui.output.CoursesData;

/**
 * Tests for {@link GetStudentCoursesAction}.
 */
public class GetStudentCoursesActionTest extends BaseActionTest<GetStudentCoursesAction, CoursesData> {

    @Test(groups = GroupNames.ACTION)
    public void getStudentCoursesAction_withEnrolledCourse_returnsCourse() {
        var account = given.account("account");
        var course = given.course("course");
        given.student("student", s -> s.course(course.alias()).account(account.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id());

        CoursesData result = execute(request);

        assertEquals(1, result.getCourses().size());
        assertEquals(course.id(), result.getCourses().get(0).getCourse().getCourseId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getStudentCoursesAction_withNoEnrolledCourses_returnsEmptyList() {
        var account = given.account("account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id());

        CoursesData result = execute(request);

        assertEquals(0, result.getCourses().size());
    }
}
