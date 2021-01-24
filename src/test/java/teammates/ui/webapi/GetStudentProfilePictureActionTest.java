package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.test.FileHelper;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link GetStudentProfilePictureAction}.
 */
public class GetStudentProfilePictureActionTest extends BaseActionTest<GetStudentProfilePictureAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE_PICTURE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        String student1PicPath = "src/test/resources/images/profile_pic.png";
        byte[] student1PicBytes = FileHelper.readFileAsBytes(student1PicPath);

        ______TS("Success case: student gets his own image");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);

        writeFileToStorage(student1InCourse1.googleId, student1PicPath);

        GetStudentProfilePictureAction action = getAction();
        ImageResult imageResult = getImageResult(action);

        assertEquals(HttpStatus.SC_OK, imageResult.getStatusCode());
        assertArrayEquals(student1PicBytes, imageResult.getBytes());

        ______TS("Success case: student passes in incomplete params but still gets his own image");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };

        action = getAction(submissionParams);
        imageResult = getImageResult(action);

        assertEquals(HttpStatus.SC_OK, imageResult.getStatusCode());

        submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        action = getAction(submissionParams);
        imageResult = getImageResult(action);

        assertEquals(HttpStatus.SC_OK, imageResult.getStatusCode());
        assertArrayEquals(student1PicBytes, imageResult.getBytes());

        ______TS("Success case: student gets his teammate's image");
        StudentAttributes student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        gaeSimulation.logoutUser();
        loginAsStudent(student2InCourse1.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        action = getAction(submissionParams);
        imageResult = getImageResult(action);

        assertEquals(HttpStatus.SC_OK, imageResult.getStatusCode());
        assertArrayEquals(student1PicBytes, imageResult.getBytes());

        ______TS("Success case: instructor with privilege views image of his student");
        gaeSimulation.logoutUser();
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        action = getAction(submissionParams);
        imageResult = getImageResult(action);

        assertEquals(HttpStatus.SC_OK, imageResult.getStatusCode());
        assertArrayEquals(student1PicBytes, imageResult.getBytes());

        ______TS("Failure case: requesting image of an unregistered student");

        StudentAttributes unregStudent = typicalBundle.students.get("student1InUnregisteredCourse");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, unregStudent.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, unregStudent.getEmail(),
        };

        action = getAction(submissionParams);
        imageResult = getImageResult(action);

        assertEquals(HttpStatus.SC_NO_CONTENT, imageResult.getStatusCode());
        assertEquals(0, imageResult.getBytes().length);

        ______TS("Success case: requested student has no profile picture");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student2InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student2InCourse1.getEmail(),
        };

        action = getAction(submissionParams);
        imageResult = getImageResult(action);

        assertEquals(HttpStatus.SC_NO_CONTENT, imageResult.getStatusCode());
        assertEquals(0, imageResult.getBytes().length);

        ______TS("Failure case: requesting image of a non-existing student");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student2InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, "non-existent@student.com",
        };

        action = getAction(submissionParams);
        JsonResult jsonResult = getJsonResult(action);
        MessageOutput message = (MessageOutput) jsonResult.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, jsonResult.getStatusCode());
        assertEquals("No student found", message.getMessage());

        deleteFile(student1InCourse1.googleId);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        StudentAttributes student1InCourse3 = typicalBundle.students.get("student1InCourse3");
        StudentAttributes student5InCourse1 = typicalBundle.students.get("student5InCourse1");

        ______TS("Failure case: student can only view his own team in the course");

        //student from another team
        gaeSimulation.logoutUser();
        loginAsStudent(student5InCourse1.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyCannotAccess(submissionParams);

        //student from another course
        gaeSimulation.logoutUser();
        loginAsStudent(student1InCourse3.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyCannotAccess(submissionParams);

        ______TS("Success case: student can only view his own team in the course");

        gaeSimulation.logoutUser();
        loginAsStudent(student2InCourse1.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyCanAccess(submissionParams);

        ______TS("Success case: student can view his own photo but instructor or admin cannot");

        gaeSimulation.logoutUser();
        loginAsStudent(student1InCourse1.googleId);

        verifyCanAccess();
        verifyInaccessibleForInstructors();
        verifyInaccessibleForAdmin();

        ______TS("Success/Failure case: only instructors with privilege can view photo");

        gaeSimulation.logoutUser();

        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");
        loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);

        grantInstructorWithSectionPrivilege(helperOfCourse1,
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS,
                new String[] {"Section 1"});
        verifyCanAccess(submissionParams);

        ______TS("Failure case: error in params (passing in non-existent email/id)");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "RANDOM_COURSE",
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForInstructors(submissionParams);
        verifyInaccessibleForAdmin(submissionParams);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getId(),
                Const.ParamsNames.STUDENT_EMAIL, "RANDOM_EMAIL",
        };

        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForInstructors(submissionParams);
        verifyInaccessibleForAdmin(submissionParams);
    }
}
