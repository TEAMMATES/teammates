package teammates.test.cases.ui;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.Action;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentCourseDetailsPageAction;
import teammates.ui.controller.StudentCourseDetailsPageData;
import teammates.ui.controller.StudentProfileEditSaveAction;

public class StudentCourseDetailsPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() {

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");

        String idOfCourseOfStudent = student1InCourse1.course;
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, idOfCourseOfStudent
        };

        ______TS("Invalid parameters");
        // parameters missing.
        verifyAssumptionFailure(new String[] {});

        ______TS("Typical case, student in the same course");
        String studentId = student1InCourse1.googleId;
        StudentCourseDetailsPageAction pageAction = getAction(submissionParams);
        ShowPageResult pageResult = getShowPageResult(pageAction);

        assertEquals(Const.ViewURIs.STUDENT_COURSE_DETAILS + "?error=false&user=student1InCourse1",
                     pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        StudentCourseDetailsPageData pageData = (StudentCourseDetailsPageData) pageResult.data;

        assertEquals(student1InCourse1.course, pageData.getStudentCourseDetailsPanel().getCourseId());
        assertEquals(studentId, pageData.account.googleId);
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
        studentId = student1InCourse1.googleId;
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse2"
        };
        
        Action redirectAction = getAction(submissionParams);
        RedirectResult redirectResult = this.getRedirectResult(redirectAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE + "?error=true&user=student1InCourse1",
                     redirectResult.getDestinationWithParams());
        
        assertTrue(redirectResult.isError);
        assertEquals("You are not registered in the course idOfTypicalCourse2", redirectResult.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||studentCourseDetailsPage|||studentCourseDetailsPage|||true|||"
                             + "Student|||Student 1 in course 1|||student1InCourse1|||"
                             + "student1InCourse1@gmail.tmt|||studentCourseDetails Page Load<br>"
                             + "Viewing team details for <span class=\"bold\">[idOfTypicalCourse1] "
                             + "Typical Course 1 with 2 Evals</span>|||/page/studentCourseDetailsPage";

        AssertHelper.assertLogMessageEquals(expectedLogMessage, pageAction.getLogMessage());
        
        
    }

    @Test
    public void testTeamMemberDetailsOnViewTeamPage() {
        AccountAttributes student = dataBundle.accounts.get("student1InCourse1");
        
        String[] submissionParams = createValidParamsForProfile();
        StudentProfileAttributes expectedProfile = getProfileAttributesFrom(submissionParams);
        gaeSimulation.loginAsStudent(student.googleId);
        
        // adding profile picture for student1InCourse1
        StudentProfileEditSaveAction action = getStudentProfileEditSaveAction(submissionParams);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();
        expectedProfile.googleId = student.googleId;
        assertFalse(result.isError);
        
        StudentAttributes student1 = dataBundle.students.get("student1InCourse1");
        
        gaeSimulation.logoutUser();
        gaeSimulation.loginAsStudent(dataBundle.accounts.get("student2InCourse1").googleId);
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
            String[] submissionParams) {
        StudentProfileAttributes spa = new StudentProfileAttributes();
        
        spa.shortName = StringHelper.trimIfNotNull(submissionParams[1]);
        spa.email = StringHelper.trimIfNotNull(submissionParams[3]);
        spa.institute = StringHelper.trimIfNotNull(submissionParams[5]);
        spa.nationality = StringHelper.trimIfNotNull(submissionParams[7]);
        spa.gender = StringHelper.trimIfNotNull(submissionParams[9]);
        spa.moreInfo = StringHelper.trimIfNotNull(submissionParams[11]);
        spa.modifiedDate = null;
        
        return spa;
    }
    
    private StudentCourseDetailsPageAction getAction(String... params) {
        return (StudentCourseDetailsPageAction) gaeSimulation.getActionObject(uri, params);
    }
    
    private StudentProfileEditSaveAction getStudentProfileEditSaveAction(String[] submissionParams) {
        return (StudentProfileEditSaveAction) gaeSimulation.getActionObject(Const.ActionURIs.STUDENT_PROFILE_EDIT_SAVE,
                submissionParams);
    }

}
