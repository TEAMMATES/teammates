package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.StudentsDb;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentCourseJoinAction;

public class StudentCourseJoinActionTest extends BaseActionTest {
    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.STUDENT_COURSE_JOIN;
    }

    @BeforeMethod
    public void methodSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, "sample-key"
        };
        
        verifyOnlyLoggedInUsersCanAccess(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        StudentsDb sDb = new StudentsDb();
        student = sDb.getStudentForGoogleId(student.course, student.googleId);
        
        gaeSimulation.loginAsStudent(student.googleId);
        
        ______TS("not enough parameters");
        
        verifyAssumptionFailure();
        
        ______TS("invalid key");
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, "invalid key"
        };
        
        StudentCourseJoinAction a = getAction(submissionParams);
        ShowPageResult r = (ShowPageResult) a.executeAndPostProcess();

        assertEquals(Const.ViewURIs.STUDENT_COURSE_JOIN_CONFIRMATION
                + "?error=false&user=" + student.googleId,
                r.getDestinationWithParams());
        assertFalse(r.isError);
        
        ______TS("already registered student");
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(student.key)
        };
        
        a = getAction(submissionParams);
        RedirectResult rr = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED
                + "?regkey=" + StringHelper.encrypt(student.key)
                + "&error=false&user=" + student.googleId,
                rr.getDestinationWithParams());
        assertFalse(r.isError);
        
        ______TS("typical case");
        
        AccountAttributes newStudentAccount = new AccountAttributes(
                "newStudnet", "New Student", false,
                "newStudent@gmail.com", "NUS");
        AccountsLogic.inst().createAccount(newStudentAccount);
        
        StudentAttributes newStudent = new StudentAttributes(student.team,
                "New Student", "newStudent@course1.com",
                "This is a new student", student.course);
        StudentsLogic.inst().createStudentCascade(newStudent);
        newStudent = sDb.getStudentForEmail(newStudent.course, newStudent.email);
        
        gaeSimulation.loginUser("newStudent");
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(newStudent.key)
        };
        
        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();

        assertEquals(Const.ViewURIs.STUDENT_COURSE_JOIN_CONFIRMATION
                + "?error=false&user=newStudent",
                r.getDestinationWithParams());
        assertFalse(r.isError);
    }
    
    private StudentCourseJoinAction getAction(String... params) throws Exception {
        return (StudentCourseJoinAction) (gaeSimulation.getActionObject(uri, params));
    }
}
