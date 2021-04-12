package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;

/**
 * SUT: {@link DeleteStudentAction}.
 */
public class DeleteStudentActionTest extends BaseActionTest<DeleteStudentAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student2InCourse1 = typicalBundle.students.get("student2InCourse1");

        ______TS("success: delete a student by email");
        loginAsInstructor(instructor1OfCourse1.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
        };

        DeleteStudentAction deleteStudentAction = getAction(submissionParams);
        JsonResult result = getJsonResult(deleteStudentAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        ______TS("success: delete a student by id");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_ID, student2InCourse1.googleId,
        };

        deleteStudentAction = getAction(submissionParams);
        result = getJsonResult(deleteStudentAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        ______TS("failure: course does not exist");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "RANDOM_COURSE",
                Const.ParamsNames.STUDENT_ID, student2InCourse1.googleId,
        };

        deleteStudentAction = getAction(submissionParams);
        result = getJsonResult(deleteStudentAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        ______TS("failure: student does not exist");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_ID, "RANDOM_STUDENT",
        };

        deleteStudentAction = getAction(submissionParams);
        result = getJsonResult(deleteStudentAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        ______TS("failure: incomplete params given");

        verifyHttpParameterFailure();

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        verifyHttpParameterFailure(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, instructor1OfCourse1.email,
        };

        verifyHttpParameterFailure(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.STUDENT_ID, instructor1OfCourse1.courseId,
        };

        verifyHttpParameterFailure(submissionParams);

        ______TS("failure: random email given - fails silently");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, "RANDOM_EMAIL",
        };

        deleteStudentAction = getAction(submissionParams);
        result = getJsonResult(deleteStudentAction);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

    }

    @Override
    @Test
    protected void testAccessControl() throws InvalidParametersException, EntityDoesNotExistException {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student5InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
        };

        verifyAccessibleForAdmin(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT, submissionParams);
    }

}
