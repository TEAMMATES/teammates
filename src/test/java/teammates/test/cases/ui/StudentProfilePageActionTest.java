package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.PageData;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentProfilePageAction;

public class StudentProfilePageActionTest extends BaseActionTest {

    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.STUDENT_PROFILE_PAGE;
    }

    @BeforeMethod
    public void methodSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        String[] submissionParams = new String[]{};
        verifyAnyRegisteredUserCanAccess(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        AccountAttributes student = dataBundle.accounts.get("student1InCourse1");
        
        String[] submissionParams = new String[]{};
        
        ______TS("typical success");
        
        gaeSimulation.loginAsStudent(student.googleId);
        StudentProfilePageAction a = getAction(submissionParams);
        ShowPageResult r = (ShowPageResult) a.executeAndPostProcess();
        
        AssertHelper.assertContains("/jsp/studentProfilePage.jsp?error=false&user="+student.googleId, r.getDestinationWithParams());
        assertFalse(r.isError);
        
        PageData data = r.data;
        student.studentProfile.modifiedDate = data.account.studentProfile.modifiedDate;
        student.createdAt = data.account.createdAt;
        assertEquals(student.toString(), data.account.toString());
        
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePage|||studentProfilePage" +
                "|||true|||Student|||"+ student.name +"|||" + student.googleId + "|||" + student.email +
                "|||studentProfile Page Load <br> Profile: " + student.studentProfile.toString() + "|||/page/studentProfilePage" ;
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("masquerade mode");
        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);
        
        a = getAction(addUserIdToParams(student.googleId, submissionParams));
        r = (ShowPageResult) a.executeAndPostProcess();
        
        AssertHelper.assertContains(Const.ViewURIs.STUDENT_PROFILE_PAGE + "?error=false&user="+student.googleId, r.getDestinationWithParams());
        assertFalse(r.isError);
        
        data = r.data;
        student.studentProfile.modifiedDate = data.account.studentProfile.modifiedDate;
        student.createdAt = data.account.createdAt;
        assertEquals(student.toString(), data.account.toString());
        
        expectedLogMessage = "TEAMMATESLOG|||studentProfilePage|||studentProfilePage" +
                "|||true|||Student(M)|||"+ student.name +"|||" + student.googleId + "|||" + student.email +
                "|||studentProfile Page Load <br> Profile: " + student.studentProfile.toString() + "|||/page/studentProfilePage" ;
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("unregistered user");
        
        gaeSimulation.loginUser("random-id-unreg");
        
        a = getAction(submissionParams);
        try {
            a.executeAndPostProcess();
            signalFailureToDetectException(" - Unauthorised Exception");
        } catch(UnauthorizedAccessException ue) {
            assertEquals("User is not registered", ue.getMessage());
        }
        
    }

    private StudentProfilePageAction getAction(String... params) throws Exception{
            return (StudentProfilePageAction) (gaeSimulation.getActionObject(uri, params));
    }

}
