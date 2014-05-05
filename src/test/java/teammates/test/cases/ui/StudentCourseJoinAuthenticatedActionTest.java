package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
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
import teammates.ui.controller.StudentCourseJoinAuthenticatedAction;

public class StudentCourseJoinAuthenticatedActionTest extends BaseActionTest {
    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED;
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
        StudentsDb sDb = new StudentsDb();
        
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        student = sDb.getStudentForGoogleId(student.course, student.googleId);
        
        gaeSimulation.loginAsStudent(student.googleId);
        
        ______TS("not enough parameters");
        
        verifyAssumptionFailure();
        
        ______TS("invalid key");
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, "invalid key"
        };
        
        StudentCourseJoinAuthenticatedAction a = getAction(submissionParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?message=You+have+used+an+invalid+join+link"
                + "%3A+%2Fpage%2FstudentCourseJoin%3Fregkey%3Dinvalid+key"
                + "&error=true&user=" + student.googleId,
                r.getDestinationWithParams());
        assertTrue(r.isError);
        
        ______TS("already registered student");
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(student.key)
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?message=student1InCourse1+has+already+joined+this+course"
                + "&persistencecourse=" + student.course
                + "&error=true&user=" + student.googleId,
                r.getDestinationWithParams());
        assertTrue(r.isError);
        
        ______TS("student object belongs to another account");
        
        StudentAttributes student2 = dataBundle.students.get("student2InCourse1");
        student2 = sDb.getStudentForGoogleId(student2.course, student2.googleId);
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(student2.key)
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?message=The+join+link+used+belongs+to+a+different+user"
                + "+whose+Google+ID+is+stude..ourse1"
                + "+%28only+part+of+the+Google+ID+is+shown+to+protect+privacy%29."
                + "+If+that+Google+ID+is+owned+by+you%2C+please+logout+and"
                + "+re-login+using+that+Google+account.+If+it+doesn%E2%80%99t"
                + "+belong+to+you%2C+please+%3Ca+href%3D%22mailto"
                + "%3Ateammates%40comp.nus.edu.sg%3Fbody%3D"
                + "Your+name%3A%250AYour+course%3A%250AYour+university%3A%22%3E"
                + "contact+us%3C%2Fa%3E+so+that+we+can+investigate."
                + "&persistencecourse=" + student.course
                + "&error=true&user=" + student.googleId,
                r.getDestinationWithParams());
        assertTrue(r.isError);
        
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
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?persistencecourse=idOfTypicalCourse1"
                + "&error=false&user=newStudent",
                r.getDestinationWithParams());
        assertFalse(r.isError);
    }
    
    private StudentCourseJoinAuthenticatedAction getAction(String... params) throws Exception {
        return (StudentCourseJoinAuthenticatedAction) (gaeSimulation.getActionObject(uri, params));
    }
}
