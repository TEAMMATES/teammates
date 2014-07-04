package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.EntitiesDb;
import teammates.storage.api.ProfilesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.common.util.GoogleCloudStorageHelper;

public class ProfilesDbTest extends BaseComponentTestCase {
    
    private ProfilesDb profilesDb = new ProfilesDb();
    private AccountsDb accountsDb = new AccountsDb();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(ProfilesDb.class);
    }
    
    @Test 
    public void testGetStudentProfile() throws Exception{
        
        ______TS("success case");
        // implicitly tested in update
        
        ______TS("non-existent account");
        assertNull(profilesDb.getStudentProfile("non-eXisTent"));
    }
    
    @Test
    public void testUpdateStudentProfile() throws Exception {
        ______TS("null parameter");
        try {
            profilesDb.updateStudentProfile(null);
            signalFailureToDetectException(" - Assertion Error");
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
        
        ______TS("invalid paramters case");
        try {
            profilesDb.updateStudentProfile(new StudentProfileAttributes());
            signalFailureToDetectException(" - InvalidParametersException");
        } catch (InvalidParametersException ipe) {
            assertEquals(String.format(FieldValidator.GOOGLE_ID_ERROR_MESSAGE, "", FieldValidator.REASON_EMPTY),
                    ipe.getMessage());
        }
        
        ______TS("non-existent account");
        AccountAttributes a = createNewAccount();
        
        try {
            a.studentProfile.googleId = "non-ExIsTenT";
            profilesDb.updateStudentProfile(a.studentProfile);
            signalFailureToDetectException(" - EntityDoesNotExistException");
        } catch (EntityDoesNotExistException edne) {
            AssertHelper.assertContains(
                EntitiesDb.ERROR_UPDATE_NON_EXISTENT_STUDENT_PROFILE + a.studentProfile.googleId, 
                edne.getMessage());
            a.studentProfile.googleId = a.googleId;
        }
        
        ______TS("typical success case, no picture");
        a.studentProfile.moreInfo = "This is more than enough info...";
        a.studentProfile.email = "e@email.com";
        
        profilesDb.updateStudentProfile(a.studentProfile);
        StudentProfileAttributes updatedProfile = profilesDb.getStudentProfile(a.studentProfile.googleId);
        
        assertEquals(a.studentProfile.moreInfo, updatedProfile.moreInfo);
        assertEquals(a.studentProfile.email, updatedProfile.email);
        
        ______TS("success case: add picture (initially empty)");
        a.studentProfile.pictureKey = GoogleCloudStorageHelper.writeFileToGcs(a.googleId, "src/test/resources/images/profile_pic.png", "");
        profilesDb.updateStudentProfile(a.studentProfile);
        
        updatedProfile = profilesDb.getStudentProfile(a.studentProfile.googleId);
        
        assertEquals(a.studentProfile.pictureKey, updatedProfile.pictureKey);
        
        ______TS("success case: same profile");
        profilesDb.updateStudentProfile(a.studentProfile);
        
        ______TS("success case: same pictureKey");
        a.studentProfile.shortName = "s";
        profilesDb.updateStudentProfile(a.studentProfile);
        
        ______TS("success case: change picture");
        a.studentProfile.pictureKey = GoogleCloudStorageHelper.writeFileToGcs(a.googleId, "src/test/resources/images/profile_pic_updated.png", "1");
        profilesDb.updateStudentProfile(a.studentProfile);
        assertFalse(GoogleCloudStorageHelper.doesFileExistInGcs(a.googleId, true));
        
        updatedProfile = profilesDb.getStudentProfile(a.studentProfile.googleId);
        
        assertEquals(a.studentProfile.pictureKey, updatedProfile.pictureKey);        
    }
    
    @Test
    public void testUpdateStudentProfilePicture() throws Exception {
        AccountAttributes a = createNewAccount();

        ______TS("null parameters");
        // googleId
        try {
            profilesDb.updateStudentProfilePicture(null, "anything");
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
        
        // pictureKey
        try {
            profilesDb.updateStudentProfilePicture("anything", null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
        
        ______TS("empty parameters");
        
        // googleId
        try {
            profilesDb.updateStudentProfilePicture("", "anything");
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            AssertHelper.assertContains("GoogleId is empty", ae.getMessage());
        }
        
        // picture key        
        try {
            profilesDb.updateStudentProfilePicture(a.googleId, "");
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            AssertHelper.assertContains("PictureKey is empty", ae.getMessage());
        }
        
        ______TS("non-existent googleId");
        
        try {
            profilesDb.updateStudentProfilePicture("non-eXisTEnt", "random");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            AssertHelper.assertContains(EntitiesDb.ERROR_UPDATE_NON_EXISTENT_STUDENT_PROFILE + "non-eXisTEnt", 
                    edne.getMessage());
        }
        
        
        ______TS("update picture key - initially empty");
        
        a.studentProfile.pictureKey = GoogleCloudStorageHelper.writeFileToGcs(a.googleId, "src/test/resources/images/profile_pic_default.png", "");
        profilesDb.updateStudentProfilePicture(a.googleId, a.studentProfile.pictureKey);
        
        StudentProfileAttributes updatedProfile = profilesDb.getStudentProfile(a.studentProfile.googleId);
        
        assertEquals(a.studentProfile.pictureKey, updatedProfile.pictureKey);
        
        ______TS("update picture key - same key; does nothing");
        
        profilesDb.updateStudentProfilePicture(a.googleId, a.studentProfile.pictureKey);
        
        ______TS("update only pictureKey");
        a.studentProfile.pictureKey = GoogleCloudStorageHelper.writeFileToGcs(a.googleId, "src/test/resources/images/profile_pic.png", "1");
        profilesDb.updateStudentProfilePicture(a.googleId, a.studentProfile.pictureKey);
        updatedProfile = profilesDb.getStudentProfile(a.studentProfile.googleId);
        
        assertFalse(GoogleCloudStorageHelper.doesFileExistInGcs(a.googleId, true));
        assertEquals(a.studentProfile.pictureKey, updatedProfile.pictureKey);
        
        ______TS("delete picture");
        
        profilesDb.deleteStudentProfilePicture(a.googleId);
        updatedProfile = profilesDb.getStudentProfile(a.studentProfile.googleId);
        
        assertFalse(GoogleCloudStorageHelper.doesFileExistInGcs(a.googleId + "1", true));
        assertEquals("", updatedProfile.pictureKey);
        
        ______TS("delete picture, currently empty - fails silently");
        profilesDb.deleteStudentProfilePicture(a.googleId);
    }

    private AccountAttributes createNewAccount() throws Exception {
        AccountAttributes a = new AccountAttributes();
        a.googleId = "valid.googleId";
        a.name = "Valid Fresh Account";
        a.isInstructor = false;
        a.email = "valid@email.com";
        a.institute = "National University of Singapore";
        a.studentProfile = new StudentProfileAttributes();
        a.studentProfile.googleId = a.googleId;
        a.studentProfile.institute = "National University of Singapore";
        
        accountsDb.createAccount(a);
        return a;
    }

}
