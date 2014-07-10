package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.Action;
import teammates.ui.controller.InstructorCourseStudentDeleteAction;
import teammates.ui.controller.RedirectResult;


public class InstructorCourseStudentDeleteActionTest extends BaseActionTest {

    DataBundle dataBundle;
    
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifyStudentPrivilege(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        
        ______TS("success: delete a student ");
        gaeSimulation.loginAsInstructor(instructor1OfCourse1.googleId);
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
        };
        
        InstructorCourseStudentDeleteAction action = (InstructorCourseStudentDeleteAction) getAction(submissionParams);
        RedirectResult redirectResult = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE, redirectResult.destination);
        assertEquals(false, redirectResult.isError);
        assertEquals(Const.StatusMessages.STUDENT_DELETED, redirectResult.getStatusMessage());
        
        assertEquals("TEAMMATESLOG|||instructorCourseStudentDelete|||instructorCourseStudentDelete|||"
                     + "true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                     + "instr1@course1.com|||Student <span class=\"bold\">student1InCourse1@gmail.com</span> "
                     + "in Course <span class=\"bold\">[idOfTypicalCourse1]</span> deleted.|||"
                     + "/page/instructorCourseStudentDelete", action.getLogMessage());
        
    }
    
    private InstructorCourseStudentDeleteAction getAction(String... params) throws Exception{
        return (InstructorCourseStudentDeleteAction) (gaeSimulation.getActionObject(uri, params));
    }
    
}
