package teammates.test.cases.action;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentCourseDetailsPageAction;
import teammates.ui.controller.StudentProfileEditSaveAction;
import teammates.ui.pagedata.StudentCourseDetailsPageData;

/**
 * SUT: {@link StudentCourseDetailsPageAction}.
 */
public class StudentCourseDetailsPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        String idOfCourseOfStudent = student1InCourse1.course;
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, idOfCourseOfStudent
        };

        ______TS("Invalid parameters");
        // parameters missing.
        verifyAssumptionFailure(new String[] {});

        ______TS("Typical case, student in the same course");
        StudentCourseDetailsPageAction pageAction = getAction(submissionParams);
        ShowPageResult pageResult = getShowPageResult(pageAction);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.STUDENT_COURSE_DETAILS, false, "student1InCourse1"),
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        StudentCourseDetailsPageData pageData = (StudentCourseDetailsPageData) pageResult.data;

        assertEquals(student1InCourse1.course, pageData.getStudentCourseDetailsPanel().getCourseId());
        assertEquals(student1InCourse1.googleId, pageData.account.googleId);
        assertEquals(student1InCourse1.getIdentificationString(), pageData.student.getIdentificationString());
        assertEquals(student1InCourse1.team, pageData.getStudentCourseDetailsPanel().getStudentTeam());

        List<StudentAttributes> expectedStudentsList = StudentsLogic.inst().getStudentsForTeam(
                                                                    student1InCourse1.team, student1InCourse1.course);

        List<StudentAttributes> actualStudentsList = pageData.getStudentCourseDetailsPanel().getTeammates();

        AssertHelper.assertSameContentIgnoreOrder(expectedStudentsList, actualStudentsList);

        // assertEquals(StudentsLogic.inst().getStudentsForTeam(student1InCourse1.team, student1InCourse1), pageData.);
        // above comparison method failed, so use the one below

        List<InstructorAttributes> expectedInstructorsList = InstructorsLogic.inst()
                                                                .getInstructorsForCourse(student1InCourse1.course);
        List<InstructorAttributes> actualInstructorsList = pageData.getStudentCourseDetailsPanel().getInstructors();

        AssertHelper.assertSameContentIgnoreOrder(expectedInstructorsList, actualInstructorsList);

        String expectedLogMessage = "TEAMMATESLOG|||studentCourseDetailsPage|||studentCourseDetailsPage|||true|||"
                                    + "Student|||Student 1 in course 1|||student1InCourse1|||"
                                    + "student1InCourse1@gmail.tmt|||studentCourseDetails Page Load<br>"
                                    + "Viewing team details for <span class=\"bold\">[idOfTypicalCourse1] "
                                    + "Typical Course 1 with 2 Evals</span>|||/page/studentCourseDetailsPage";

        AssertHelper.assertLogMessageEquals(expectedLogMessage, pageAction.getLogMessage());

        ______TS("Typical case, the student is not in the course");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse2"
        };

        StudentCourseDetailsPageAction redirectAction = getAction(submissionParams);
        RedirectResult redirectResult = this.getRedirectResult(redirectAction);

        assertEquals(
                getPageResultDestination(Const.ActionURIs.STUDENT_HOME_PAGE, true, "student1InCourse1"),
                redirectResult.getDestinationWithParams());

        assertTrue(redirectResult.isError);
        assertEquals("You are not registered in the course idOfTypicalCourse2", redirectResult.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||studentCourseDetailsPage|||studentCourseDetailsPage|||true|||"
                             + "Student|||Student 1 in course 1|||student1InCourse1|||"
                             + "student1InCourse1@gmail.tmt|||studentCourseDetails Page Load<br>"
                             + "Viewing team details for <span class=\"bold\">[idOfTypicalCourse1] "
                             + "Typical Course 1 with 2 Evals</span>|||/page/studentCourseDetailsPage";

        AssertHelper.assertLogMessageEquals(expectedLogMessage, pageAction.getLogMessage());

        ______TS("Typical case, student contains data requiring sanitization");
        StudentAttributes studentTestingSanitization = typicalBundle.students.get("student1InTestingSanitizationCourse");
        gaeSimulation.loginAsStudent(studentTestingSanitization.googleId);
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, studentTestingSanitization.course
        };

        pageAction = getAction(submissionParams);
        pageResult = getShowPageResult(pageAction);

        assertEquals(Const.ViewURIs.STUDENT_COURSE_DETAILS
                        + "?error=false&user=" + studentTestingSanitization.googleId,
                     pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||studentCourseDetailsPage|||studentCourseDetailsPage|||true|||"
                + "Student|||" + SanitizationHelper.sanitizeForHtml("Stud1<script> alert('hi!'); </script>")
                + "|||student1InTestingSanitizationCourse|||"
                + "normal@sanitization.tmt|||studentCourseDetails Page Load<br>"
                + "Viewing team details for <span class=\"bold\">[idOfTestingSanitizationCourse] "
                + SanitizationHelper.sanitizeForHtml("Testing<script> alert('hi!'); </script>")
                + "</span>|||/page/studentCourseDetailsPage";

        AssertHelper.assertLogMessageEquals(expectedLogMessage, pageAction.getLogMessage());

    }

    @Test
    public void testTeamMemberDetailsOnViewTeamPage() {
        AccountAttributes student = typicalBundle.accounts.get("student1InCourse1");

        String[] submissionParams = createValidParamsForProfile();
        StudentProfileAttributes expectedProfile = getProfileAttributesFrom(student.googleId, submissionParams);
        gaeSimulation.loginAsStudent(student.googleId);

        // adding profile picture for student1InCourse1
        StudentProfileEditSaveAction action = getStudentProfileEditSaveAction(submissionParams);
        RedirectResult result = getRedirectResult(action);
        expectedProfile.googleId = student.googleId;
        assertFalse(result.isError);

        StudentAttributes student1 = typicalBundle.students.get("student1InCourse1");

        gaeSimulation.logoutUser();
        gaeSimulation.loginAsStudent(typicalBundle.accounts.get("student2InCourse1").googleId);
        String[] submissionParam = new String[] {
                Const.ParamsNames.COURSE_ID, student1.course
        };

        StudentCourseDetailsPageAction pageAction = getAction(submissionParam);
        ShowPageResult pageResult = getShowPageResult(pageAction);
        StudentCourseDetailsPageData pageData = (StudentCourseDetailsPageData) pageResult.data;

        List<StudentAttributes> actualStudentsList = pageData.getStudentCourseDetailsPanel().getTeammates();
        boolean isStudentDisplayedOnViewTeam = false;
        for (StudentAttributes stud : actualStudentsList) {
            if (student1.email.equals(stud.email) && student1.name.equals(stud.name)
                    && student1.getPublicProfilePictureUrl().equals(stud.getPublicProfilePictureUrl())) {
                isStudentDisplayedOnViewTeam = true;
            }
        }

        assertTrue(isStudentDisplayedOnViewTeam);
    }

    private StudentProfileAttributes getProfileAttributesFrom(
            String googleId, String[] submissionParams) {
        StudentProfileAttributes spa = StudentProfileAttributes.builder(googleId).build();

        spa.shortName = StringHelper.trimIfNotNull(submissionParams[1]);
        spa.email = StringHelper.trimIfNotNull(submissionParams[3]);
        spa.institute = StringHelper.trimIfNotNull(submissionParams[5]);
        spa.nationality = StringHelper.trimIfNotNull(submissionParams[7]);
        spa.gender = StringHelper.trimIfNotNull(submissionParams[9]);
        spa.moreInfo = StringHelper.trimIfNotNull(submissionParams[11]);
        spa.modifiedDate = null;

        return spa;
    }

    @Override
    protected StudentCourseDetailsPageAction getAction(String... params) {
        return (StudentCourseDetailsPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    private StudentProfileEditSaveAction getStudentProfileEditSaveAction(String[] submissionParams) {
        return (StudentProfileEditSaveAction) gaeSimulation.getActionObject(Const.ActionURIs.STUDENT_PROFILE_EDIT_SAVE,
                submissionParams);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String idOfCourseOfStudent = typicalBundle.students
                .get("student1InCourse1").course;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, idOfCourseOfStudent
        };

        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
        verifyUnaccessibleWithoutLogin(submissionParams);

        idOfCourseOfStudent = typicalBundle.students.get("student2InCourse1").course;
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, idOfCourseOfStudent
        };

        verifyUnaccessibleForStudentsOfOtherCourses(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
    }

}
