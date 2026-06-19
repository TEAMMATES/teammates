package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetInstructorsAction}.
 */
public class GetInstructorsActionIT extends BaseActionIT<GetInstructorsAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTORS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor);

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
            assertNull(instructorData.getAccountId());
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

    @Test(groups = GroupNames.INTEGRATION)
    public void courseNotFound_loggedInAsInstructor_fullDetailIntent() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "does-not-exist-id",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyEntityNotFoundAcl(params);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void courseNotFound_loggedInAsStudent_intentUndefined() {
        Student student = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "does-not-exist-id",
        };

        verifyEntityNotFoundAcl(params);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void unknownUser_fullDetailIntent_cannotAccess() {
        loginAsUnregistered("unregistered");

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyCannotAccess(params);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void unknownUser_intentUndefined_cannotAccess() {
        loginAsUnregistered("unregistered");

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        verifyCannotAccess(params);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void instructor_invalidIntent_shouldFailParameterCheck() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, "Unknown",
        };

        verifyHttpParameterFailureAcl(params);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void instructor_fullDetailIntent_canAccessOwnCourse() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(instructor.getCourse(), params);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void student_intentUndefined_canAccessOwnCourse() {
        Student student = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student);

        String[] params = {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
        };

        verifyCanAccess(params);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void student_intentUndefined_cannotAccessOtherCourse() {
        Student student = typicalBundle.students.get("student1InCourse1");
        Student otherStudent = typicalBundle.students.get("student1InCourse2");

        assertNotEquals(otherStudent.getCourse(), student.getCourse());

        loginAsStudent(student);

        String[] params = {
                Const.ParamsNames.COURSE_ID, otherStudent.getCourseId(),
        };

        verifyCannotAccess(params);
    }

}
