package teammates.test.cases.storage;

import java.io.IOException;

import org.testng.annotations.Test;

import com.google.appengine.api.blobstore.BlobKey;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.EntitiesDb;
import teammates.storage.api.ProfilesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link ProfilesDb}.
 */
public class ProfilesDbTest extends BaseComponentTestCase {

    private ProfilesDb profilesDb = new ProfilesDb();
    private AccountsDb accountsDb = new AccountsDb();

    @Test
    public void testGetStudentProfile() {

        ______TS("success case");
        // implicitly tested in update

        ______TS("non-existent account");
        assertNull(profilesDb.getStudentProfile("non-eXisTent"));
    }

    @Test
    public void testUpdateStudentProfile() throws Exception {
        AccountAttributes a = createNewAccount();

        // failure cases
        testUpdateProfileWithNullParameter();
        testUpdateProfileWithInvalidParameters();
        testUpdatingNonExistentProfile(a);

        // success cases
        testUpdateProfileSuccessWithNoPictureKey(a);
        testUpdateProfileSuccessInitiallyEmptyPictureKey(a);
        testUpdateProfileSuccessNoChangesToProfile(a);
        testUpdateProfileSuccessWithSamePictureKey(a);
    }

    private void testUpdateProfileWithNullParameter()
            throws InvalidParametersException, EntityDoesNotExistException {
        ______TS("null parameter");
        try {
            profilesDb.updateStudentProfile(null);
            signalFailureToDetectException(" - Assertion Error");
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }

    private void testUpdateProfileWithInvalidParameters()
            throws Exception {
        ______TS("invalid paramters case");
        try {
            profilesDb.updateStudentProfile(StudentProfileAttributes.builder("").build());
            signalFailureToDetectException(" - InvalidParametersException");
        } catch (InvalidParametersException ipe) {
            assertEquals(getPopulatedEmptyStringErrorMessage(
                             FieldValidator.GOOGLE_ID_ERROR_MESSAGE_EMPTY_STRING,
                             FieldValidator.GOOGLE_ID_FIELD_NAME, FieldValidator.GOOGLE_ID_MAX_LENGTH),
                         ipe.getMessage());
        }
    }

    private void testUpdatingNonExistentProfile(AccountAttributes a)
            throws Exception {
        ______TS("non-existent account");

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
    }

    private void testUpdateProfileSuccessNoChangesToProfile(AccountAttributes a)
            throws Exception {
        ______TS("success case: same profile");
        profilesDb.updateStudentProfile(a.studentProfile);

        // picture should not be deleted
        assertTrue(doesFileExistInGcs(new BlobKey(a.studentProfile.pictureKey)));
    }

    private void testUpdateProfileSuccessWithNoPictureKey(AccountAttributes a)
            throws Exception {
        ______TS("typical success case, no picture");
        a.studentProfile.moreInfo = "This is more than enough info...";
        a.studentProfile.email = "e@email.com";

        profilesDb.updateStudentProfile(a.studentProfile);
        StudentProfileAttributes updatedProfile = profilesDb.getStudentProfile(a.studentProfile.googleId);

        assertEquals(a.studentProfile.moreInfo, updatedProfile.moreInfo);
        assertEquals(a.studentProfile.email, updatedProfile.email);
    }

    private void testUpdateProfileSuccessInitiallyEmptyPictureKey(AccountAttributes a)
            throws Exception,
            EntityDoesNotExistException {
        ______TS("success case: add picture (initially empty)");
        a.studentProfile.pictureKey = uploadDefaultPictureForProfile(a.googleId);
        profilesDb.updateStudentProfile(a.studentProfile);

        StudentProfileAttributes updatedProfile = profilesDb.getStudentProfile(a.studentProfile.googleId);

        assertEquals(a.studentProfile.pictureKey, updatedProfile.pictureKey);
    }

    private void testUpdateProfileSuccessWithSamePictureKey(AccountAttributes a)
            throws Exception {
        ______TS("success case: same pictureKey");
        a.studentProfile.shortName = "s";
        profilesDb.updateStudentProfile(a.studentProfile);

        // picture should not be deleted
        assertTrue(doesFileExistInGcs(new BlobKey(a.studentProfile.pictureKey)));
    }

    @Test
    public void testUpdateStudentProfilePicture() throws Exception {
        AccountAttributes a = createNewAccount();

        // failure test cases
        testUpdateProfilePictureWithNullParameters();
        testUpdateProfilePictureWithEmptyParameters(a);
        testUpdateProfilePictureOnNonExistentProfile();

        // success test cases
        testUpdateProfilePictureSuccessInitiallyEmpty(a);
        testUpdateProfilePictureSuccessSamePictureKey(a);
    }

    private void testUpdateProfilePictureWithNullParameters()
            throws EntityDoesNotExistException {
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
    }

    private void testUpdateProfilePictureWithEmptyParameters(AccountAttributes a)
            throws EntityDoesNotExistException {
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
    }

    private void testUpdateProfilePictureOnNonExistentProfile() {
        ______TS("non-existent profile");

        try {
            profilesDb.updateStudentProfilePicture("non-eXisTEnt", "random");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            AssertHelper.assertContains(EntitiesDb.ERROR_UPDATE_NON_EXISTENT_STUDENT_PROFILE + "non-eXisTEnt",
                    edne.getMessage());
        }
    }

    private void testUpdateProfilePictureSuccessInitiallyEmpty(
            AccountAttributes a) throws IOException,
            EntityDoesNotExistException {
        ______TS("update picture key - initially empty");

        a.studentProfile.pictureKey = uploadDefaultPictureForProfile(a.googleId);
        profilesDb.updateStudentProfilePicture(a.googleId, a.studentProfile.pictureKey);

        StudentProfileAttributes updatedProfile = profilesDb.getStudentProfile(a.studentProfile.googleId);

        assertEquals(a.studentProfile.pictureKey, updatedProfile.pictureKey);
    }

    private void testUpdateProfilePictureSuccessSamePictureKey(
            AccountAttributes a) throws EntityDoesNotExistException {
        ______TS("update picture key - same key; does nothing");
        profilesDb.updateStudentProfilePicture(a.googleId, a.studentProfile.pictureKey);
    }

    @Test
    public void testDeleteProfilePicture() throws Exception {
        AccountAttributes a = createNewAccount();
        a.studentProfile.pictureKey = uploadDefaultPictureForProfile(a.googleId);
        testDeletePictureSuccess(a);
        testDeleteProfilePictureForProfileWithNoPicture(a);
    }

    private void testDeleteProfilePictureForProfileWithNoPicture(
            AccountAttributes a) throws EntityDoesNotExistException {
        ______TS("delete picture, currently empty - fails silently");
        profilesDb.deleteStudentProfilePicture(a.googleId);
    }

    private void testDeletePictureSuccess(AccountAttributes a)
            throws EntityDoesNotExistException {
        ______TS("delete picture");

        profilesDb.deleteStudentProfilePicture(a.googleId);
        StudentProfileAttributes updatedProfile = profilesDb.getStudentProfile(a.studentProfile.googleId);

        assertFalse(doesFileExistInGcs(new BlobKey(updatedProfile.pictureKey)));
        assertEquals("", updatedProfile.pictureKey);
    }

    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------- Helper Functions -----------------------------------------------
    //-------------------------------------------------------------------------------------------------------

    private String uploadDefaultPictureForProfile(String googleId)
            throws IOException {
        // we upload a small text file as the actual file does not matter here
        return writeFileToGcs(googleId, "src/test/resources/images/not_a_picture.txt");
    }

    private AccountAttributes createNewAccount() throws Exception {
        AccountAttributes a = AccountAttributes.builder()
                .withGoogleId("valid.googleId")
                .withEmail("valid@email.com")
                .withName("Valid Fresh Account")
                .withInstitute("TEAMMATES Test Institute 1")
                .withIsInstructor(false)
                .build();

        a.studentProfile = StudentProfileAttributes.builder(a.googleId)
            .withInstitute("TEAMMATES Test Institute 1")
            .build();

        accountsDb.createAccount(a);
        return a;
    }

}
