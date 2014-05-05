package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
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
import teammates.ui.controller.InstructorCourseJoinAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

public class InstructorCourseJoinActionTest extends BaseActionTest {
    DataBundle dataBundle;
    String invalidEncryptedKey = StringHelper.encrypt("invalidKey");
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN;
    }

    @BeforeMethod
    public void methodSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidEncryptedKey
        };
        
        verifyOnlyLoggedInUsersCanAccess(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorsDb instrDb = new InstructorsDb();
        instructor = instrDb.getInstructorForGoogleId(instructor.courseId, instructor.googleId);
        
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        ______TS("not enough parameters");
        
        verifyAssumptionFailure();
        
        ______TS("invalid key");
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidEncryptedKey
        };
        
        InstructorCourseJoinAction a = getAction(submissionParams);
        ShowPageResult r = (ShowPageResult) a.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_JOIN_CONFIRMATION
                + "?error=false&user=" + instructor.googleId,
                r.getDestinationWithParams());
        assertFalse(r.isError);
        
        ______TS("already registered instructor");
        
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(instructor.key)
        };
        
        a = getAction(submissionParams);
        RedirectResult rr = (RedirectResult) a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED
                + "?regkey=" + StringHelper.encrypt(instructor.key)
                + "&error=false&user=" + instructor.googleId,
                rr.getDestinationWithParams());
        assertFalse(r.isError);
        
        ______TS("typical case");
        
        instructor = new InstructorAttributes("ICJAT.instr", instructor.courseId, "New Instructor", "ICJAT.instr@email.com");
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
        r = (ShowPageResult) a.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_JOIN_CONFIRMATION
                + "?error=false&user=ICJAT.instr",
                r.getDestinationWithParams());
        assertFalse(r.isError);
    }
    
    private InstructorCourseJoinAction getAction(String... params) throws Exception {
        return (InstructorCourseJoinAction) (gaeSimulation.getActionObject(uri, params));
    }
}
