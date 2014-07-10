package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentProfilePictureEditAction;

public class StudentProfilePictureEditActionTest extends BaseActionTest {
    
    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
    }

    @BeforeMethod
    public void methodSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        String[] submissionParams = createValidParamsForProfilePictureEdit();
        verifyAnyRegisteredUserCanAccess(submissionParams);
        
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        String[] submissionParams = createValidParamsForProfilePictureEdit();
        AccountAttributes student = dataBundle.accounts.get("student1InCourse1");
        gaeSimulation.loginAsStudent(student.googleId);
        StudentProfilePictureEditAction a;
        RedirectResult r;
        
        ______TS("empty parameter - leftx");
        submissionParams[1] = "";
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePictureEdit|||"
                + "studentProfilePictureEdit|||true|||Student|||Student 1 in course 1|||" 
                + student.googleId + "|||" + student.email + "|||"
                + "Servlet Action Failure : One or more of the given coords were empty."
                + "|||" + Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
        String expectedUrl = Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=true&user=" + student.googleId;
        assertEquals(expectedLogMessage, a.getLogMessage());
        assertEquals(expectedUrl, r.getDestinationWithParams());
        
        ______TS("empty parameter - rightx");
        submissionParams[1] = "0";
        submissionParams[3] = "";
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        ______TS("empty parameter - topy");
        submissionParams[3] = "100";
        submissionParams[5] = "";
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        ______TS("empty parameter - bottomy");
        submissionParams[5] = "0";
        submissionParams[7] = "";
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        ______TS("empty parameter - height");
        submissionParams[7] = "100";
        submissionParams[9] = "";
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        ______TS("empty parameter - width");
        submissionParams[9] = "500";
        submissionParams[11] = "";
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        ______TS("zero height");
        submissionParams[11] = "300";
        submissionParams[9] = "0";
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        assertEquals(expectedLogMessage, a.getLogMessage());
        assertEquals(expectedUrl, r.getDestinationWithParams());
        
        ______TS("zero width");
        submissionParams[9] = "500";
        submissionParams[11] = "0";
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        assertEquals(expectedLogMessage, a.getLogMessage());
        assertEquals(expectedUrl, r.getDestinationWithParams());
        
        ______TS("non-existent blobKey");
        submissionParams[11] = "300";
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        expectedLogMessage = "TEAMMATESLOG|||studentProfilePictureEdit|||"
                + "studentProfilePictureEdit|||true|||Student|||Student 1 in course 1|||"
                + student.googleId + "|||student1InCourse1@gmail.com|||"
                + "Servlet Action Failure : Reading and transforming image failed.Could not read blob."
                + "|||" + Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
        
        assertEquals(expectedLogMessage, a.getLogMessage());
        assertEquals(expectedUrl, r.getDestinationWithParams());
    }
    
    private String[] createValidParamsForProfilePictureEdit() {
        return new String[] {
                Const.ParamsNames.PROFILE_PICTURE_LEFTX, "0",
                Const.ParamsNames.PROFILE_PICTURE_RIGHTX, "100",
                Const.ParamsNames.PROFILE_PICTURE_TOPY, "0",
                Const.ParamsNames.PROFILE_PICTURE_BOTTOMY, "100",
                Const.ParamsNames.PROFILE_PICTURE_HEIGHT, "500",
                Const.ParamsNames.PROFILE_PICTURE_WIDTH, "300",
                Const.ParamsNames.BLOB_KEY, "random-blobKey"
        };
    }

    private StudentProfilePictureEditAction getAction(String... params) throws Exception{
            return (StudentProfilePictureEditAction) (gaeSimulation.getActionObject(uri, params));
    }
}
