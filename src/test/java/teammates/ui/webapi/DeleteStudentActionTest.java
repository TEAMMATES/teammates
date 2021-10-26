package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
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
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        DeleteStudentAction deleteStudentAction = getAction(submissionParams);
        getJsonResult(deleteStudentAction);

        ______TS("success: delete a student by id");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_ID, student2InCourse1.getGoogleId(),
        };

        deleteStudentAction = getAction(submissionParams);
        getJsonResult(deleteStudentAction);

        ______TS("silent failure: course does not exist");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "RANDOM_COURSE",
                Const.ParamsNames.STUDENT_ID, student2InCourse1.getGoogleId(),
        };

        deleteStudentAction = getAction(submissionParams);
        getJsonResult(deleteStudentAction);

        ______TS("silent failure: student does not exist");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_ID, "RANDOM_STUDENT",
        };

        deleteStudentAction = getAction(submissionParams);
        getJsonResult(deleteStudentAction);

        ______TS("failure: incomplete params given");

        verifyHttpParameterFailure();

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        verifyHttpParameterFailure(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, instructor1OfCourse1.getEmail(),
        };

        verifyHttpParameterFailure(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.STUDENT_ID, instructor1OfCourse1.getCourseId(),
        };

        verifyHttpParameterFailure(submissionParams);

        ______TS("silent failure: random email given");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, "RANDOM_EMAIL",
        };

        deleteStudentAction = getAction(submissionParams);
        getJsonResult(deleteStudentAction);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student5InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyAccessibleForAdmin(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT, submissionParams);
    }

}
