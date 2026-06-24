package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.EnrollStudentsData;
import teammates.ui.request.StudentEnrollRequest;
import teammates.ui.request.StudentsEnrollRequest;

/**
 * Tests for {@link EnrollStudentsAction}.
 */
public class EnrollStudentsActionTest extends BaseActionTest<EnrollStudentsAction, EnrollStudentsData> {

    @Test(groups = GroupNames.ACTION)
    public void enrollStudentsAction_newStudents_enrollsSuccessfully() {
        var course = given.course("course");
        var instructorAccount = given.account("instructor-account");
        given.instructor("instructor", i -> i.course(course.alias())
                .account(instructorAccount.alias()).coOwner());
        persistGivenData(given);

        StudentsEnrollRequest requestBody = new StudentsEnrollRequest(List.of(
                new StudentEnrollRequest("Alice", "alice@test.com", "Team 1", "Section 1", ""),
                new StudentEnrollRequest("Bob", "bob@test.com", "Team 1", "Section 1", "")));

        EnrollStudentsData result = execute(new RequestContext()
                .withAccountAuth(instructorAccount.id())
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withRequest(requestBody));

        assertEquals(2, result.getStudentsData().getStudents().size());
        assertEquals(0, result.getUnsuccessfulEnrolls().size());
    }

    @Test(groups = GroupNames.ACTION)
    public void enrollStudentsAction_existingStudent_updatesStudentDetails() {
        var course = given.course("course");
        given.student("student", s -> s.course(course.alias())
                .name("Old Name").email("student@test.com"));
        var instructorAccount = given.account("instructor-account");
        given.instructor("instructor", i -> i.course(course.alias())
                .account(instructorAccount.alias()).coOwner());
        persistGivenData(given);

        StudentsEnrollRequest requestBody = new StudentsEnrollRequest(List.of(
                new StudentEnrollRequest("New Name", "student@test.com", "Team 1", "Section 1", "updated")));

        EnrollStudentsData result = execute(new RequestContext()
                .withAccountAuth(instructorAccount.id())
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withRequest(requestBody));

        assertEquals(1, result.getStudentsData().getStudents().size());
        assertEquals(0, result.getUnsuccessfulEnrolls().size());
        assertEquals("New Name", result.getStudentsData().getStudents().get(0).getName());
    }

    @Test(groups = GroupNames.ACTION)
    public void enrollStudentsAction_sectionExceedsLimit_throwsInvalidOperationException() {
        var course = given.course("course");
        var instructorAccount = given.account("instructor-account");
        given.instructor("instructor", i -> i.course(course.alias())
                .account(instructorAccount.alias()).coOwner());
        persistGivenData(given);

        List<StudentEnrollRequest> enrollRequests = new ArrayList<>();
        for (int i = 0; i <= Const.SECTION_SIZE_LIMIT; i++) {
            enrollRequests.add(
                    new StudentEnrollRequest("Student " + i, "s" + i + "@test.com", "Team 1", "Section 1", ""));
        }
        StudentsEnrollRequest requestBody = new StudentsEnrollRequest(enrollRequests);

        assertActionThrows(InvalidOperationException.class, new RequestContext()
                .withAccountAuth(instructorAccount.id())
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withRequest(requestBody));
    }

    @Test(groups = GroupNames.ACTION)
    public void enrollStudentsAction_instructorWithoutModifyStudentPrivilege_throwsUnauthorizedAccessException() {
        var course = given.course("course");
        var instructorAccount = given.account("instructor-account");
        given.instructor("instructor", i -> i.course(course.alias())
                .account(instructorAccount.alias()).noPrivileges());
        persistGivenData(given);

        StudentsEnrollRequest requestBody = new StudentsEnrollRequest(List.of(
                new StudentEnrollRequest("Alice", "alice@test.com", "Team 1", "Section 1", "")));

        assertActionThrows(UnauthorizedAccessException.class, new RequestContext()
                .withAccountAuth(instructorAccount.id())
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withRequest(requestBody));
    }
}
