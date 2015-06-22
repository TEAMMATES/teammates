package teammates.test.cases.ui;

import static org.testng.AssertJUnit.*;

import java.util.List;

import teammates.test.util.TestHelper;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.core.StudentsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.ui.controller.Action;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentCourseDetailsPageAction;
import teammates.ui.controller.StudentCourseDetailsPageData;
import teammates.ui.controller.ShowPageResult;

public class StudentCourseDetailsPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {

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

        assertEquals(Const.ViewURIs.STUDENT_COURSE_DETAILS + "?error=false&user=student1InCourse1" , 
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
          
        assertTrue(TestHelper.isSameContentIgnoreOrder(expectedStudentsList,actualStudentsList));

        // assertEquals(StudentsLogic.inst().getStudentsForTeam(student1InCourse1.team, student1InCourse1),pageData.);
        // above comparison method failed, so use the one below 
        
        List<InstructorAttributes> expectedInstructorsList = InstructorsLogic.inst()
                                                                .getInstructorsForCourse(student1InCourse1.course);
        List<InstructorAttributes> actualInstructorsList = pageData.getStudentCourseDetailsPanel().getInstructors();
        
        assertTrue(TestHelper.isSameContentIgnoreOrder(expectedInstructorsList,actualInstructorsList));

        String expectedLogMessage = "TEAMMATESLOG|||studentCourseDetailsPage|||studentCourseDetailsPage|||true|||"
                                    + "Student|||Student 1 in course 1|||student1InCourse1|||"
                                    + "student1InCourse1@gmail.tmt|||studentCourseDetails Page Load<br>"
                                    + "Viewing team details for <span class=\"bold\">[idOfTypicalCourse1] "
                                    + "Typical Course 1 with 2 Evals</span>|||/page/studentCourseDetailsPage";

        assertEquals(expectedLogMessage, pageAction.getLogMessage());

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

        assertEquals(expectedLogMessage, pageAction.getLogMessage());
        
        
    }

    private StudentCourseDetailsPageAction getAction(String... params)throws Exception {   
        return (StudentCourseDetailsPageAction) (gaeSimulation.getActionObject(uri, params));
    }

}
