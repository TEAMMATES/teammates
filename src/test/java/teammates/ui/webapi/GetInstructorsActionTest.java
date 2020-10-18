package teammates.ui.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorPermissionRole;
import teammates.ui.output.InstructorsData;
import teammates.ui.output.JoinState;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetInstructorsAction}.
 */
public class GetInstructorsActionTest extends BaseActionTest<GetInstructorsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTORS;
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

        ______TS("unknown intent");
        assertThrows(InvalidHttpParameterException.class, () -> {
            String[] submissionParams = new String[] {
                    Const.ParamsNames.COURSE_ID, instructor.courseId,
                    Const.ParamsNames.INTENT, "Unknown",
            };

            GetInstructorsAction action = getAction(submissionParams);
            getJsonResult(action);
        });
    }

    @Test
    public void testExecute_withoutIntent_shouldReturnPartialData() {
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, studentAttributes.getCourse(),
                Const.ParamsNames.TEAM_NAME, studentAttributes.getTeam(),
        };
        GetInstructorsAction action = getAction(submissionParams);
        JsonResult jsonResult = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());

        InstructorsData output = (InstructorsData) jsonResult.getOutput();
        List<InstructorData> instructors = output.getInstructors();

        // the #instructors is 5
        assertEquals(5, logic.getInstructorsForCourse(studentAttributes.getCourse()).size());
        // with information hiding, it is 4 instead
        assertEquals(4, instructors.size());
        InstructorData typicalInstructor = instructors.get(0);
        assertEquals("idOfTypicalCourse1", typicalInstructor.getCourseId());
        assertEquals("instructorNotYetJoinedCourse1@email.tmt", typicalInstructor.getEmail());
        assertEquals("Instructor Not Yet Joined Course 1", typicalInstructor.getName());
        assertEquals("Instructor", typicalInstructor.getDisplayedToStudentsAs());
        assertNull(typicalInstructor.getRole());
        assertNull(typicalInstructor.getIsDisplayedToStudents());
        assertNull(typicalInstructor.getGoogleId());
        assertNull(typicalInstructor.getJoinState()); // information is hidden
    }

    @Test
    public void testExecute_withFullDetailIntent_shouldReturnDataWithFullDetail() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        GetInstructorsAction action = getAction(submissionParams);
        JsonResult jsonResult = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, jsonResult.getStatusCode());

        InstructorsData output = (InstructorsData) jsonResult.getOutput();
        List<InstructorData> instructors = output.getInstructors();

        // the #instructors is 5
        assertEquals(5, logic.getInstructorsForCourse(instructor.getCourseId()).size());
        // without information hiding, it is still 5
        assertEquals(5, instructors.size());
        InstructorData typicalInstructor = instructors.get(0);
        assertEquals("idOfHelperOfCourse1", typicalInstructor.getGoogleId());
        assertEquals("idOfTypicalCourse1", typicalInstructor.getCourseId());
        assertEquals("helper@course1.tmt", typicalInstructor.getEmail());
        assertEquals("Helper Course1", typicalInstructor.getName());
        assertEquals("Helper", typicalInstructor.getDisplayedToStudentsAs());
        assertEquals(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM, typicalInstructor.getRole());
        assertFalse(typicalInstructor.getIsDisplayedToStudents());
        assertEquals(JoinState.JOINED, typicalInstructor.getJoinState());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        ______TS("course not exist");
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        assertThrows(EntityNotFoundException.class, () -> {
            String[] submissionParams = new String[] {
                    Const.ParamsNames.COURSE_ID, "randomId",
                    Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
            };
            GetInstructorsAction action = getAction(submissionParams);
            action.checkAccessControl();
        });

        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.googleId);
        assertThrows(EntityNotFoundException.class, () -> {
            String[] submissionParams = new String[] {
                    Const.ParamsNames.COURSE_ID, "randomId",
            };
            GetInstructorsAction action = getAction(submissionParams);
            action.checkAccessControl();
        });

        ______TS("unknown login entity");
        loginAsUnregistered("unregistered");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyCannotAccess(params);

        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };
        verifyCannotAccess(params);

        ______TS("unknown intent");
        loginAsInstructor(instructor.getGoogleId());
        assertThrows(InvalidHttpParameterException.class, () -> {
            String[] submissionParams = new String[] {
                    Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                    Const.ParamsNames.INTENT, "Unknown",
            };

            GetInstructorsAction action = getAction(submissionParams);
            action.checkAccessControl();
        });
    }

    @Test
    public void testAccessControl_withFullDetailIntent_shouldDoAuthenticationOfInstructor() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_withoutIntent_shouldDoAuthenticationOfStudent() {
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.googleId);

        // try to access instructors in his own course
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, studentAttributes.getCourse(),
        };
        verifyCanAccess(submissionParams);

        // try to access instructors in other course
        StudentAttributes otherStudent = typicalBundle.students.get("student1InCourse2");
        assertNotEquals(otherStudent.getCourse(), studentAttributes.getCourse());
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, otherStudent.getCourse(),
        };
        verifyCannotAccess(submissionParams);
    }

}
