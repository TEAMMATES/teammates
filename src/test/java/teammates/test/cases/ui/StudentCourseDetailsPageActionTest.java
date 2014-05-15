package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.StudentsLogic;


public class StudentCourseDetailsPageActionTest extends BaseActionTest {

    DataBundle dataBundle;

    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE;
    }

    @BeforeMethod
    public void methodSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        String iDOfCourseOfStudent = dataBundle.students.get("student1InCourse1").course;
       
        
        String[] submissionParams = new String[]{
                    Const.ParamsNames.COURSE_ID, iDOfCourseOfStudent
                };
        
        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
        
        verifyAccessibleForStudentsOfDifferentCourse(submissionParams);
        
    }
    
    
  
    protected void verifyAccessibleForStudentsOfDifferentCourse(String[] submissionParams) throws Exception{
        
        ______TS("students not of the same course cannot access");
        
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse2 = dataBundle.students.get("student1InCourse2");
        
        gaeSimulation.loginAsStudent(student1InCourse2.googleId);
        verifyCanAccess(submissionParams);
        verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
        
        
    }

    
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
        StudentsLogic studentsLogic = StudentsLogic.inst();
        StudentAttributes student1InCourse2 = dataBundle.students.get("student1InCourse2");

        String originalTeam = student1InCourse2.team;
        student1InCourse2.team = "TeamWithSingleMember";
        studentsLogic.updateStudentCascade(student1InCourse2.email, student1InCourse2);
        ______TS("Student1 In Course2 team status now set to TeamWithSingleMember");

        
        String iDOfCourseOfStudent = student1InCourse2.course;
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, iDOfCourseOfStudent
            };
        
        gaeSimulation.loginAsStudent(student1InCourse2.googleId);
        
        verifyCanAccess(submissionParams); 

        
        //restore the team status
        student1InCourse2.team = originalTeam;
        
        studentsLogic.updateStudentCascade(student1InCourse2.email, student1InCourse2);

        ______TS("Student1 In Course2 team status now now reset to the original status");
    }
    
}



