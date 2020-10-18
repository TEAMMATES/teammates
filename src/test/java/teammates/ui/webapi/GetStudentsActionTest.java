package teammates.ui.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.JoinState;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;

/**
 * SUT: {@link GetStudentsAction}.
 */
public class GetStudentsActionTest extends BaseActionTest<GetStudentsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        ______TS("Invalid parameters");
        // no parameters
        verifyHttpParameterFailure();
    }

    @Test
    public void testExecute_withOnlyCourseId_shouldReturnAllStudentsOfTheCourse() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
        };
        GetStudentsAction action = getAction(submissionParams);
        JsonResult jsonResult = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());

        StudentsData output = (StudentsData) jsonResult.getOutput();
        List<StudentData> students = output.getStudents();

        assertEquals(5, students.size());
        StudentData typicalStudent = students.get(0);
        assertNull(typicalStudent.getGoogleId());
        assertNull(typicalStudent.getKey());
        assertEquals("idOfTypicalCourse1", typicalStudent.getCourseId());
        assertEquals("student1InCourse1@gmail.tmt", typicalStudent.getEmail());
        assertEquals("student1 In Course1</td></div>'\"", typicalStudent.getName());
        assertEquals("Course1</td></div>'\"", typicalStudent.getLastName());
        assertEquals(JoinState.JOINED, typicalStudent.getJoinState());
        assertEquals("comment for student1InCourse1</td></div>'\"", typicalStudent.getComments());
        assertEquals("Team 1.1</td></div>'\"", typicalStudent.getTeamName());
        assertEquals("Section 1", typicalStudent.getSectionName());
    }

    @Test
    public void testExecute_withCourseIdAndTeamName_shouldReturnAllStudentsOfTheTeam() {
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, studentAttributes.getCourse(),
                Const.ParamsNames.TEAM_NAME, studentAttributes.getTeam(),
        };
        GetStudentsAction action = getAction(submissionParams);
        JsonResult jsonResult = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());

        StudentsData output = (StudentsData) jsonResult.getOutput();
        List<StudentData> students = output.getStudents();

        assertEquals(4, students.size());
        StudentData typicalStudent = students.get(0);
        assertNull(typicalStudent.getGoogleId());
        assertNull(typicalStudent.getKey());
        assertEquals("idOfTypicalCourse1", typicalStudent.getCourseId());
        assertEquals("student1InCourse1@gmail.tmt", typicalStudent.getEmail());
        assertEquals("student1 In Course1</td></div>'\"", typicalStudent.getName());
        assertEquals("Course1</td></div>'\"", typicalStudent.getLastName());
        assertNull(typicalStudent.getJoinState()); // information is hidden
        assertNull(typicalStudent.getComments()); // information is hidden
        assertEquals("Team 1.1</td></div>'\"", typicalStudent.getTeamName());
        assertEquals("Section 1", typicalStudent.getSectionName());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        ______TS("unknown courseId for (instructor access)");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "randomId",
        };
        verifyCannotAccess(submissionParams);

        ______TS("unknown courseId and/or teamName (student access)");
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(studentAttributes.googleId);
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "randomId",
        };
        verifyCannotAccess(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, studentAttributes.getCourse(),
                Const.ParamsNames.TEAM_NAME, "randomTeamName",
        };
        verifyCannotAccess(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "randomId",
                Const.ParamsNames.TEAM_NAME, "randomTeamName",
        };
        verifyCannotAccess(submissionParams);

        ______TS("unknown login entity");
        loginAsUnregistered("unregistered");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };
        verifyCannotAccess(params);

        params = new String[] {
                Const.ParamsNames.COURSE_ID, studentAttributes.getCourse(),
                Const.ParamsNames.TEAM_NAME, studentAttributes.getTeam(),
        };
        verifyCannotAccess(params);

    }

    @Test
    public void testAccessControl_withOnlyCourseId_shouldDoAuthenticationOfInstructor() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_withCourseIdAndTeamName_shouldDoAuthenticationOfStudent() {
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.googleId);

        ______TS("Acccess students' own team should pass");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, studentAttributes.getCourse(),
                Const.ParamsNames.TEAM_NAME, studentAttributes.getTeam(),
        };
        verifyCanAccess(submissionParams);

        ______TS("Acccess other team should fail");
        StudentAttributes otherStudent = typicalBundle.students.get("student5InCourse1");
        assertEquals(otherStudent.getCourse(), studentAttributes.getCourse());
        assertNotEquals(otherStudent.getTeam(), studentAttributes.getTeam());
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, studentAttributes.getCourse(),
                Const.ParamsNames.TEAM_NAME, otherStudent.getTeam(),
        };
        verifyCannotAccess(submissionParams);
    }
}
