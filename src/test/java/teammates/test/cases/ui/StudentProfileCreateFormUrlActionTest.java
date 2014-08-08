package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.StudentProfileCreateFormUrlAction;
import teammates.ui.controller.StudentProfileCreateFormUrlAjaxPageData;

public class StudentProfileCreateFormUrlActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_PROFILE_CREATEUPLOADFORMURL;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        AccountAttributes student = dataBundle.accounts.get("student1InCourse1");
        
        String[] submissionParams = new String[]{};
        
        ______TS("typical success");
        
        gaeSimulation.loginAsStudent(student.googleId);
        StudentProfileCreateFormUrlAction a = getAction(submissionParams);
        AjaxResult r = (AjaxResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());
        
        StudentProfileCreateFormUrlAjaxPageData data = (StudentProfileCreateFormUrlAjaxPageData) r.data;
        
        
        String expectedLogMessage = "TEAMMATESLOG|||studentProfileCreateFormUrl|||studentProfileCreateFormUrl" +
                "|||true|||Student|||"+ student.name +"|||" + student.googleId + "|||" + student.email +
                "|||Created Url successfully: " + data.formUrl + "|||/page/studentProfileCreateFormUrl";
        
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("masquerade mode");
        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);
        
        submissionParams = new String[]{
                Const.ParamsNames.USER_ID, student.googleId
        };
        
        a = getAction(addUserIdToParams(student.googleId, submissionParams));
        r = (AjaxResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());
        
        data = (StudentProfileCreateFormUrlAjaxPageData) r.data;
        
        assertFalse(r.isError);
        
        expectedLogMessage = "TEAMMATESLOG|||studentProfileCreateFormUrl|||studentProfileCreateFormUrl" +
                "|||true|||Student(M)|||"+ student.name +"|||" + student.googleId + "|||" + student.email +
                "|||Created Url successfully: " + data.formUrl + "|||/page/studentProfileCreateFormUrl";
        
        assertEquals(expectedLogMessage, a.getLogMessage());
        
    }

    private StudentProfileCreateFormUrlAction getAction(String... params) throws Exception{
            return (StudentProfileCreateFormUrlAction) (gaeSimulation.getActionObject(uri, params));
    }

}
