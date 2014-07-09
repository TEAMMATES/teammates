package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentProfileEditSaveAction;

public class StudentProfileEditSaveActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		restoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_PROFILE_EDIT_SAVE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        AccountAttributes student = dataBundle.accounts.get("student1InCourse1");
        String[] submissionParams = createValidParamsForProfile();
        StudentProfileAttributes expectedProfile = getProfileAttributesFrom(submissionParams);
        
        ______TS("typical success case");
        
        gaeSimulation.loginAsStudent(student.googleId);
        
        StudentProfileEditSaveAction a = getAction(submissionParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();
        expectedProfile.googleId = student.googleId;
        
        assertFalse(r.isError);
        AssertHelper.assertContains(Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=false&user=" + student.googleId, r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.STUDENT_PROFILE_EDITED, r.getStatusMessage());
        expectedProfile.modifiedDate = a.account.studentProfile.modifiedDate;
        String expectedLogMessage = "TEAMMATESLOG|||studentProfileEditSave|||studentProfileEditSave" +
                "|||true|||Student|||"+ student.name +"|||" + student.googleId + "|||" + student.email +
                "|||Student Profile for <span class=\"bold\">(" + student.googleId + ")</span> edited.<br>" +
                expectedProfile.toString() + "|||/page/studentProfileEditSave";
        
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("invalid parameters");
        
        submissionParams = createInvalidParamsForProfile();
        expectedProfile = getProfileAttributesFrom(submissionParams);
        expectedProfile.googleId = student.googleId;
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertTrue(r.isError);
        AssertHelper.assertContains(Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=true&user=" + student.googleId, r.getDestinationWithParams());
        List<String> expectedErrorMessages = new ArrayList<String>();
        
        expectedErrorMessages.add(String.format(FieldValidator.INVALID_NAME_ERROR_MESSAGE, submissionParams[1], "a person name", FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR, "a person name"));
        expectedErrorMessages.add(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, submissionParams[3], FieldValidator.REASON_INCORRECT_FORMAT));
        
        AssertHelper.assertContains(expectedErrorMessages, r.getStatusMessage());
        
        expectedLogMessage = "TEAMMATESLOG|||studentProfileEditSave|||studentProfileEditSave" +
                "|||true|||Student|||"+ student.name +"|||" + student.googleId + "|||" + student.email +
                "|||" + Const.ACTION_RESULT_FAILURE + " : " + r.getStatusMessage() + "|||/page/studentProfileEditSave";
        AssertHelper.assertContainsRegex(expectedLogMessage, a.getLogMessage());
        
        ______TS("masquerade mode");
        
        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);
        
        submissionParams = createValidParamsForProfile();
        expectedProfile = getProfileAttributesFrom(submissionParams);
        expectedProfile.googleId = student.googleId;
        
        a = getAction(addUserIdToParams(student.googleId, submissionParams));
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.STUDENT_PROFILE_EDITED, r.getStatusMessage());
        AssertHelper.assertContains(Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=false&user=" + student.googleId, r.getDestinationWithParams());
        
        expectedProfile.modifiedDate = a.account.studentProfile.modifiedDate;
        expectedLogMessage = "TEAMMATESLOG|||studentProfileEditSave|||studentProfileEditSave" +
                "|||true|||Student(M)|||"+ student.name +"|||" + student.googleId + "|||" + student.email +
                "|||Student Profile for <span class=\"bold\">(" + student.googleId + ")</span> edited.<br>" +
                expectedProfile.toString() + "|||/page/studentProfileEditSave";
        
        AssertHelper.assertContainsRegex(expectedLogMessage, a.getLogMessage());
    }

    private StudentProfileAttributes getProfileAttributesFrom(
            String[] submissionParams) {
        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.shortName = submissionParams[1];
        spa.email = submissionParams[3];
        spa.institute = submissionParams[5];
        spa.nationality = submissionParams[7];
        spa.gender = submissionParams[9];
        spa.moreInfo = submissionParams[11];
        spa.modifiedDate = null;
        
        return spa;
    }

    private StudentProfileEditSaveAction getAction(String[] submissionParams) {
        return (StudentProfileEditSaveAction) gaeSimulation.getActionObject(uri, submissionParams);
    }

}
