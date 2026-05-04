package teammates.it.ui.webapi;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Student;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.GetInstructorsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetInstructorsAction}.
 */
public class GetInstructorsActionIT extends BaseActionIT<GetInstructorsAction> {
    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
    }

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
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor.getGoogleId());

        ______TS("Typical Success Case with FULL_DETAIL");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        GetInstructorsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);

        InstructorsData output = (InstructorsData) jsonResult.getOutput();
        List<InstructorData> instructors = output.getInstructors();

        assertEquals(3, instructors.size());

        ______TS("Typical Success Case with no intent");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, null,
        };

        action = getAction(params);
        jsonResult = getJsonResult(action);

        output = (InstructorsData) jsonResult.getOutput();
        instructors = output.getInstructors();

        assertEquals(3, instructors.size());

        for (InstructorData instructorData : instructors) {
            assertNull(instructorData.getGoogleId());
            assertNull(instructorData.getJoinState());
            assertNull(instructorData.getIsDisplayedToStudents());
            assertNull(instructorData.getRole());
        }

        ______TS("Unknown intent");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, "Unknown",
        };

        verifyHttpParameterFailure(params);
    }

    @Override
    protected void testAccessControl() throws Exception {
        // Tested separately
    }

    @Test
    public void courseNotFound_loggedInAsInstructor_fullDetailIntent() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, "does-not-exist-id",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyEntityNotFoundAcl(params);
    }

    @Test
    public void courseNotFound_loggedInAsStudent_intentUndefined() {
        Student student = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, "does-not-exist-id",
        };

        verifyEntityNotFoundAcl(params);
    }

    @Test
    public void unknownUser_fullDetailIntent_cannotAccess() {
        loginAsUnregistered("unregistered");

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    public void unknownUser_intentUndefined_cannotAccess() {
        loginAsUnregistered("unregistered");

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        verifyCannotAccess(params);
    }

    @Test
    public void instructor_invalidIntent_shouldFailParameterCheck() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, "Unknown",
        };

        verifyHttpParameterFailureAcl(params);
    }

    @Test
    public void instructor_fullDetailIntent_canAccessOwnCourse() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(instructor.getCourse(), params);
    }

    @Test
    public void student_intentUndefined_canAccessOwnCourse() {
        Student student = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
        };

        verifyCanAccess(params);
    }

    @Test
    public void student_intentUndefined_cannotAccessOtherCourse() {
        Student student = typicalBundle.students.get("student1InCourse1");
        Student otherStudent = typicalBundle.students.get("student1InCourse2");

        assertNotEquals(otherStudent.getCourse(), student.getCourse());

        loginAsStudent(student.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, otherStudent.getCourseId(),
        };

        verifyCannotAccess(params);
    }

}
