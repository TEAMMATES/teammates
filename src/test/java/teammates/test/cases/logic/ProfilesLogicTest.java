package teammates.test.cases.logic;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.ProfilesLogic;
import teammates.test.cases.BaseComponentTestCase;

import com.google.appengine.api.blobstore.BlobKey;

public class ProfilesLogicTest extends BaseComponentTestCase {
    
    private static final AccountsLogic accountsLogic = AccountsLogic.inst();
    private static final ProfilesLogic profilesLogic = ProfilesLogic.inst();
    
    @Test
    public void testStudentProfileFunctions() throws Exception {
        
        // 4 functions are tested together as:
        //      => The functions are very simple (one-liners)
        //      => They are fundamentally related and easily tested together
        //      => It saves time during tests
        
        ______TS("get SP");
        StudentProfileAttributes expectedSpa =
                new StudentProfileAttributes("id", "shortName", "personal@email.com",
                                             "institute", "American", "female", "moreInfo", "");
        AccountAttributes accountWithStudentProfile =
                new AccountAttributes("id", "name", true, "test@email.com", "dev", expectedSpa);
        
        accountsLogic.createAccount(accountWithStudentProfile);
        
        StudentProfileAttributes actualSpa = profilesLogic.getStudentProfile(accountWithStudentProfile.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;
        assertEquals(expectedSpa.toString(), actualSpa.toString());
        
        ______TS("update SP");
        
        expectedSpa.pictureKey = "non-empty";
        accountWithStudentProfile.studentProfile.pictureKey = expectedSpa.pictureKey;
        profilesLogic.updateStudentProfile(accountWithStudentProfile.studentProfile);
        
        actualSpa = profilesLogic.getStudentProfile(accountWithStudentProfile.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;
        assertEquals(expectedSpa.toString(), actualSpa.toString());
        
        ______TS("update picture");
        
        expectedSpa.pictureKey = writeFileToGcs(expectedSpa.googleId, "src/test/resources/images/profile_pic.png");
        profilesLogic.updateStudentProfilePicture(expectedSpa.googleId, expectedSpa.pictureKey);
        actualSpa = profilesLogic.getStudentProfile(accountWithStudentProfile.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;
        assertEquals(expectedSpa.toString(), actualSpa.toString());
        
        ______TS("delete profile picture");
        
        profilesLogic.deleteStudentProfilePicture(expectedSpa.googleId);
        assertFalse(doesFileExistInGcs(new BlobKey(expectedSpa.pictureKey)));
        
        actualSpa = profilesLogic.getStudentProfile(accountWithStudentProfile.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;
        expectedSpa.pictureKey = "";
        assertEquals(expectedSpa.toString(), actualSpa.toString());
        
        // remove the account that was created
        accountsLogic.deleteAccountCascade("id");
    }
    
    @Test
    public void testDeletePicture() throws Exception {
        String keyString = writeFileToGcs("accountsLogicTestid", "src/test/resources/images/profile_pic.png");
        BlobKey key = new BlobKey(keyString);
        profilesLogic.deletePicture(key);
        assertFalse(doesFileExistInGcs(key));
    }
    
}
