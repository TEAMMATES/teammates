package teammates.it.ui.webapi;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;
import teammates.ui.webapi.GetStudentsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetStudentsAction}.
 */
public class GetStudentsActionIT extends BaseActionIT<GetStudentsAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.ResourceURIs.STUDENTS;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        Student student = typicalBundle.students.get("student1InCourse1");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor.getGoogleId());

        ______TS("Typical Success Case with only course id, logged in as instructor");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        GetStudentsAction getStudentsAction = getAction(params);
        JsonResult jsonResult = getJsonResult(getStudentsAction);
        StudentsData response = (StudentsData) jsonResult.getOutput();
        List<StudentData> students = response.getStudents();

        assertEquals(5, students.size());

        StudentData firstStudentInStudents = students.get(0);

        assertNull(firstStudentInStudents.getGoogleId());
        assertNull(firstStudentInStudents.getKey());
        assertEquals(student.getName(), firstStudentInStudents.getName());
        assertEquals(student.getCourseId(), firstStudentInStudents.getCourseId());

        logoutUser();
        loginAsStudent(student.getGoogleId());

        ______TS("Typical Success Case with course id and team name, logged in as student");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.TEAM_NAME, student.getTeamName(),
        };

        getStudentsAction = getAction(params);
        jsonResult = getJsonResult(getStudentsAction);
        response = (StudentsData) jsonResult.getOutput();
        students = response.getStudents();

        Student expectedOtherTeamMember = typicalBundle.students.get("student2InCourse1");

        assertEquals(4, students.size());

        StudentData actualOtherTeamMember = students.get(1);

        assertNull(actualOtherTeamMember.getGoogleId());
        assertNull(actualOtherTeamMember.getKey());
        assertEquals(expectedOtherTeamMember.getName(), actualOtherTeamMember.getName());
        assertEquals(expectedOtherTeamMember.getCourseId(), actualOtherTeamMember.getCourseId());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        Student student = typicalBundle.students.get("student1InCourse1");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("Only instructors with correct privilege can access");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        loginAsInstructor(instructor.getGoogleId());

        verifyCanAccess(params);

        ______TS("Student to view team members");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.TEAM_NAME, student.getTeamName(),
        };

        loginAsStudent(student.getGoogleId());

        verifyCanAccess(params);

        ______TS("Unknown login entity");
        loginAsUnregistered("does-not-exist-id");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyCannotAccess(params);

        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.TEAM_NAME, student.getTeamName(),
        };

        verifyCannotAccess(params);
    }

}
