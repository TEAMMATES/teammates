package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.ui.output.StudentProfileData;

/**
 * SUT: {@link GetStudentProfileAction}.
 */
public class GetStudentProfileActionTest extends BaseActionTest<GetStudentProfileAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        // See test cases below.
    }

    @Test
    public void testExecute_withExistingProfileAndNoParameter_shouldReturnOwnProfile() {
        AccountAttributes student1InCourse1 = typicalBundle.accounts.get("student1InCourse1");
        StudentProfileAttributes expectedProfile = typicalBundle.profiles.get("student1InCourse1");
        String expectedName = student1InCourse1.name;
        loginAsStudent(student1InCourse1.googleId);
        testGetCorrectProfile(expectedProfile, expectedName);
    }

    @Test
    public void testExecute_withMissingCourseId_shouldReturnOwnProfile() {
        AccountAttributes student1InCourse1 = typicalBundle.accounts.get("student1InCourse1");
        StudentProfileAttributes expectedProfile = typicalBundle.profiles.get("student1InCourse1");
        String expectedName = student1InCourse1.name;
        loginAsStudent(student1InCourse1.googleId);
        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };
        testGetCorrectProfile(expectedProfile, expectedName, submissionParams);
    }

    @Test
    public void testExecute_withMissingStudentEmail_shouldReturnOwnProfile() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentProfileAttributes expectedProfile = typicalBundle.profiles.get("student1InCourse1");
        String expectedName = typicalBundle.accounts.get("student1InCourse1").name;
        loginAsStudent(student1InCourse1.googleId);
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };
        testGetCorrectProfile(expectedProfile, expectedName, submissionParams);
    }

    @Test
    public void testExecute_withStudentEmailAndCourseId_shouldReturnProfileByStudentEmail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        StudentProfileAttributes expectedProfile = typicalBundle.profiles.get("student1InCourse1");
        expectedProfile.email = null;
        expectedProfile.shortName = null;
        String expectedName = typicalBundle.accounts.get("student1InCourse1").name;
        loginAsStudent(student2InCourse1.googleId);
        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };
        testGetCorrectProfile(expectedProfile, expectedName, submissionParams);
    }

    @Test
    public void testExecute_withProfileNotYetCreated_shouldReturnEmptyProfile() {
        AccountAttributes student2InCourse1 = typicalBundle.accounts.get("student2InCourse1");
        String expectedName = student2InCourse1.name;
        StudentProfileAttributes expectedProfile = StudentProfileAttributes.builder(student2InCourse1.googleId).build();
        loginAsStudent(student2InCourse1.googleId);
        testGetCorrectProfile(expectedProfile, expectedName);
    }

    @Test
    public void testExecute_getProfileOfUnregisteredStudent_shouldReturnEmptyProfile() throws Exception {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        // Prepare an unregistered teammate
        StudentAttributes unregisteredStudentInCourse1 =
                StudentAttributes.builder(student1InCourse1.getCourse(), "student1InUnregisteredCourse@gmail.tmt")
                .withGoogleId("")
                .withName("unregistered student in course 1")
                .withComment("")
                .withSectionName(student1InCourse1.getSection())
                .withTeamName(student1InCourse1.getTeam())
                .build();
        logic.createStudent(unregisteredStudentInCourse1);
        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, unregisteredStudentInCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, unregisteredStudentInCourse1.getCourse(),
        };
        loginAsStudent(student1InCourse1.getGoogleId());
        StudentProfileAttributes expectedProfile = StudentProfileAttributes.builder("").build();

        GetStudentProfileAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        StudentProfileData actualProfile = (StudentProfileData) result.getOutput();
        assertEquals("unregistered student in course 1", actualProfile.getName());
        assertNull(actualProfile.getEmail());
        assertNull(expectedProfile.shortName, actualProfile.getShortName());
        assertEquals(expectedProfile.institute, actualProfile.getInstitute());
        assertEquals(expectedProfile.moreInfo, actualProfile.getMoreInfo());
        assertEquals(expectedProfile.nationality, actualProfile.getNationality());
        assertEquals(expectedProfile.gender, actualProfile.getGender());
    }

    private void testGetCorrectProfile(StudentProfileAttributes expectedProfile,
                                       String expectedName, String... submissionParams) {
        GetStudentProfileAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        StudentProfileData actualProfile = (StudentProfileData) result.getOutput();
        assertEquals(expectedName, actualProfile.getName());
        assertEquals(expectedProfile.email, actualProfile.getEmail());
        assertEquals(expectedProfile.shortName, actualProfile.getShortName());
        assertEquals(expectedProfile.institute, actualProfile.getInstitute());
        assertEquals(expectedProfile.moreInfo, actualProfile.getMoreInfo());
        assertEquals(expectedProfile.nationality, actualProfile.getNationality());
        assertEquals(expectedProfile.gender, actualProfile.getGender());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        // See test cases below.
    }

    @Test
    public void testAccessControl_withoutCorrectAuthInfo_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
    }

    @Test
    public void testAccessControl_studentNotExistInCourse_shouldFail() {
        StudentAttributes student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        CourseAttributes typicalCourse1 = typicalBundle.courses.get("typicalCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse2.getEmail(),
        };
        verifyInaccessibleForInstructors(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
    }

    @Test
    public void testAccessControl_studentAccessHisOwnProfile_shouldPass() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);
        verifyCanAccess();
    }

    @Test
    public void testAccessControl_studentAccessHisTeammateProfile_shouldPass() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes teammate = typicalBundle.students.get("student2InCourse1");
        loginAsStudent(student1InCourse1.googleId);
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, teammate.getEmail(),
        };
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentAccessHisClassmateButNotTeammateProfile_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes classmate = typicalBundle.students.get("student5InCourse1");
        loginAsStudent(student1InCourse1.googleId);
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, classmate.getEmail(),
        };
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentAccessStudentInOtherCourse_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes studentNotInCourse1 = typicalBundle.students.get("student1InCourse2");
        loginAsStudent(student1InCourse1.googleId);
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, studentNotInCourse1.getEmail(),
        };
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_instructorAccessProfileWithMissingStudentEmail_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };
        verifyInaccessibleForInstructors(submissionParams);
    }

    @Test
    public void testAccessControl_instructorAccessProfileWithMissingCourseId_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };
        verifyInaccessibleForInstructors(submissionParams);
    }

    @Test
    public void testAccessControl_instructorAccessProfileFromHisCourse_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };
        loginAsInstructor(instructor1OfCourse1.googleId);
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_instructorWithoutViewStudentInSectionPrivilege_shouldFail() throws Exception {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        instructor1OfCourse1.privileges
                .updatePrivilege(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        InstructorAttributes.UpdateOptionsWithEmail updateOptions =
                InstructorAttributes
                        .updateOptionsWithEmailBuilder(instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail())
                        .withPrivileges(instructor1OfCourse1.privileges)
                        .build();
        logic.updateInstructor(updateOptions);

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_instructorAccessOtherCourse_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
    }

    @Test
    public void testAccessControl_withMasqueradeMode_shouldPass() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsAdmin();
        verifyCanMasquerade(student1InCourse1.googleId);
    }
}
