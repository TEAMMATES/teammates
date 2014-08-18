package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.PageData;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentProfilePageAction;

public class StudentProfilePageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_PROFILE_PAGE;
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
        assertEquals("", r.getStatusMessage());
        
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
        
        submissionParams = new String[]{
                Const.ParamsNames.STUDENT_PROFILE_PHOTOEDIT, "false",
                Const.ParamsNames.USER_ID, student.googleId
        };
        
        a = getAction(addUserIdToParams(student.googleId, submissionParams));
        r = (ShowPageResult) a.executeAndPostProcess();
        
        AssertHelper.assertContains(Const.ViewURIs.STUDENT_PROFILE_PAGE + "?error=false&user="+student.googleId, r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());
        
        data = r.data;
        student.studentProfile.modifiedDate = data.account.studentProfile.modifiedDate;
        student.createdAt = data.account.createdAt;
        assertEquals(student.toString(), data.account.toString());
        
        expectedLogMessage = "TEAMMATESLOG|||studentProfilePage|||studentProfilePage" +
                "|||true|||Student(M)|||"+ student.name +"|||" + student.googleId + "|||" + student.email +
                "|||studentProfile Page Load <br> Profile: " + student.studentProfile.toString() + "|||/page/studentProfilePage" ;
        assertEquals(expectedLogMessage, a.getLogMessage());
        
    }

    private StudentProfilePageAction getAction(String... params) throws Exception{
            return (StudentProfilePageAction) (gaeSimulation.getActionObject(uri, params));
    }

}
