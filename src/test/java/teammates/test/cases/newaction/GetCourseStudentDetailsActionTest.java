package teammates.test.cases.newaction;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.ui.newcontroller.GetCourseStudentDetailsAction;
import teammates.ui.newcontroller.GetCourseStudentDetailsAction.StudentInfo;
import teammates.ui.newcontroller.JsonResult;

/**
 * SUT: {@link GetCourseStudentDetailsAction}.
 */
public class GetCourseStudentDetailsActionTest extends BaseActionTest<GetCourseStudentDetailsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_STUDENT_DETAILS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() {

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentProfileAttributes student1InCourse1Profile = typicalBundle.profiles.get("student1InCourse1");
        StudentAttributes student2InCourse1 = typicalBundle.students.get("student2InCourse1");

        String instructorId = instructor1OfCourse1.googleId;
        loginAsInstructor(instructorId);

        ______TS("Invalid parameters");

        //no parameters
        verifyHttpParameterFailure();

        //null student email
        String[] invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
        };
        verifyHttpParameterFailure(invalidParams);

        //null course id
        invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
        };
        verifyHttpParameterFailure(invalidParams);

        ______TS("Typical case, view student details, with profile");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
        };

        GetCourseStudentDetailsAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        StudentInfo output = (StudentInfo) r.getOutput();
        student1InCourse1.googleId = null;
        student1InCourse1.key = null;
        assertEquals(student1InCourse1.toString(), output.getStudent().toString());
        student1InCourse1Profile.googleId = null;
        student1InCourse1Profile.modifiedDate = null;
        assertEquals(student1InCourse1Profile.toString(), output.getStudentProfile().toString());

        ______TS("Typical case, view student details, without profile");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student2InCourse1.email
        };

        a = getAction(submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (StudentInfo) r.getOutput();
        student2InCourse1.googleId = null;
        student2InCourse1.key = null;
        assertEquals(student2InCourse1.toString(), output.getStudent().toString());
        assertNull(output.getStudentProfile());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyInaccessibleWithoutViewStudentInSectionsPrivilege(submissionParams);
    }
}
