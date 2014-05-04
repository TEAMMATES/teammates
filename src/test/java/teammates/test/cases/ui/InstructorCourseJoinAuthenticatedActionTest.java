package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.InstructorsDb;
import teammates.ui.controller.InstructorCourseJoinAuthenticatedAction;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseJoinAuthenticatedActionTest extends BaseActionTest {
    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED;
    }

    @BeforeMethod
    public void methodSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, "sampleKey"
        };
        
        verifyOnlyLoggedInUsersCanAccess(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorsDb instrDb = new InstructorsDb();
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        instructor = instrDb.getInstructorForEmail(instructor.courseId, instructor.email);
        
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        ______TS("not enough parameters");
        
        verifyAssumptionFailure();
        
        ______TS("invalid key");
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, "invalidKey"
        };
        
        InstructorCourseJoinAuthenticatedAction a = getAction(submissionParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                + "?message=You+have+used+an+invalid+join+link"
                + "%3A+%2Fpage%2FinstructorCourseJoin%3Fregkey%3DinvalidKey"
                + "&error=true&user=" + instructor.googleId,
                r.getDestinationWithParams());
        assertTrue(r.isError);
        
        ______TS("instructor already registered");
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(instructor.key)
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                + "?message=idOfInstructor1OfCourse1+has+already+joined+this+course"
                + "&persistencecourse=" + instructor.courseId
                + "&error=true&user=" + instructor.googleId,
                r.getDestinationWithParams());
        assertTrue(r.isError);
        
        ______TS("instructor object belongs to another account");
        
        InstructorAttributes instructor2 = dataBundle.instructors.get("instructor2OfCourse1");
        instructor2 = instrDb.getInstructorForGoogleId(instructor2.courseId, instructor2.googleId);
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(instructor2.key)
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                + "?message=The+join+link+used+belongs+to+a+different+user"
                + "+whose+Google+ID+is+idOfInst..fCourse1"
                + "+%28only+part+of+the+Google+ID+is+shown+to+protect+privacy%29."
                + "+If+that+Google+ID+is+owned+by+you%2C+please+logout+and"
                + "+re-login+using+that+Google+account.+If+it+doesn%E2%80%99t"
                + "+belong+to+you%2C+please+%3Ca+href%3D%22mailto"
                + "%3Ateammates%40comp.nus.edu.sg%3Fbody%3D"
                + "Your+name%3A%250AYour+course%3A%250AYour+university%3A%22%3E"
                + "contact+us%3C%2Fa%3E+so+that+we+can+investigate."
                + "&persistencecourse=" + instructor2.courseId
                + "&error=true&user=" + instructor.googleId,
                r.getDestinationWithParams());
        assertTrue(r.isError);
        
        ______TS("typical case");
        
        instructor = new InstructorAttributes("ICJAAT.instr", instructor.courseId, "New Instructor", "ICJAAT.instr@email.com");
        InstructorsLogic.inst().addInstructor(instructor.courseId, instructor.name, instructor.email);
        
        AccountAttributes newInstructorAccount = new AccountAttributes(
                instructor.googleId, instructor.name, false,
                instructor.email, "NUS");
        AccountsLogic.inst().createAccount(newInstructorAccount);
        
        InstructorAttributes newInstructor = instrDb.getInstructorForEmail(instructor.courseId, instructor.email);
        
        gaeSimulation.loginUser(instructor.googleId);
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(newInstructor.key)
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                + "?persistencecourse=idOfTypicalCourse1"
                + "&error=false&user=ICJAAT.instr",
                r.getDestinationWithParams());
        assertFalse(r.isError);
        
        InstructorAttributes retrievedInstructor = instrDb.getInstructorForEmail(instructor.courseId, instructor.email);
        assertEquals(instructor.googleId, retrievedInstructor.googleId);
    
    }
    
    private InstructorCourseJoinAuthenticatedAction getAction(String... params) throws Exception {
        return (InstructorCourseJoinAuthenticatedAction) (gaeSimulation.getActionObject(uri, params));
    }
}
