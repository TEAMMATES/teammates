package teammates.test.cases.storage;

import java.io.IOException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.appengine.api.blobstore.BlobKey;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.api.ProfilesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link ProfilesDb}.
 */
public class ProfilesDbTest extends BaseComponentTestCase {

    private ProfilesDb profilesDb = new ProfilesDb();

    private StudentProfileAttributes typicalProfileWithPicture;
    private StudentProfileAttributes typicalProfileWithoutPicture;
    private String typicalPictureKey;

    @BeforeMethod
    public void createTypicalData() throws Exception {
        // typical picture
        typicalPictureKey = uploadDefaultPictureForProfile("valid.googleId");
        assertTrue(doesFileExistInGcs(new BlobKey(typicalPictureKey)));

        // typical profiles
        profilesDb.saveEntity(StudentProfileAttributes.builder("valid.googleId")
                .withInstitute("TEAMMATES Test Institute 1")
                .withPictureKey(typicalPictureKey)
                .build().toEntity());
        profilesDb.saveEntity(StudentProfileAttributes.builder("valid.googleId2")
                .withInstitute("TEAMMATES Test Institute 1")
                .withPictureKey(typicalPictureKey)
                .build().toEntity());

        // save entity and picture
        typicalProfileWithPicture = profilesDb.getStudentProfile("valid.googleId");
        typicalProfileWithoutPicture = profilesDb.getStudentProfile("valid.googleId2");
    }

    @AfterMethod
    public void deleteTypicalData() {
        // delete entity
        profilesDb.deleteEntity(typicalProfileWithPicture);
        profilesDb.deleteEntity(typicalProfileWithoutPicture);
        verifyAbsentInDatastore(typicalProfileWithPicture);
        verifyAbsentInDatastore(typicalProfileWithoutPicture);

        // delete picture
        profilesDb.deletePicture(new BlobKey(typicalPictureKey));
        assertFalse(doesFileExistInGcs(new BlobKey(typicalPictureKey)));
    }

    @Test
    public void testGetStudentProfile_nonExistentStudentProfile_shouldReturnNull() {
        assertNull(profilesDb.getStudentProfile("nonExistent"));
    }

    @Test
    public void testGetStudentProfile_existentStudentProfile_shouldNotReturnNull() {
        assertNotNull(profilesDb.getStudentProfile(typicalProfileWithPicture.googleId));
        assertNotNull(profilesDb.getStudentProfile(typicalProfileWithoutPicture.googleId));
    }

    @Test
    public void testUpdateOrCreateStudentProfile_nonExistentProfile_shouldCreateNewProfile()
            throws Exception {
        StudentProfileAttributes spa =
                StudentProfileAttributes.builder("non-ExIsTenT")
                        .withShortName("Test")
                        .build();
        profilesDb.updateOrCreateStudentProfile(spa);

        verifyPresentInDatastore(spa);

        // tear down
        profilesDb.deleteEntity(spa);
    }

    @Test
    public void testUpdateOrCreateStudentProfile_nullParameter_shouldThrowAssertionException() throws Exception {
        AssertionError ae = assertThrows(AssertionError.class,
                () -> profilesDb.updateOrCreateStudentProfile(null));

        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    @Test
    public void testUpdateOrCreateStudentProfile_invalidParameter_shouldThrowInvalidParamException() {
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> profilesDb.updateOrCreateStudentProfile(StudentProfileAttributes.builder("").build()));

        assertEquals(getPopulatedEmptyStringErrorMessage(
                FieldValidator.GOOGLE_ID_ERROR_MESSAGE_EMPTY_STRING,
                FieldValidator.GOOGLE_ID_FIELD_NAME, FieldValidator.GOOGLE_ID_MAX_LENGTH),
                ipe.getMessage());
    }

    @Test
    public void testUpdateOrCreateStudentProfile_noChangesToProfile_shouldNotChangeProfileContent()
            throws Exception {
        // update same profile
        profilesDb.updateOrCreateStudentProfile(typicalProfileWithPicture);

        StudentProfileAttributes storedProfile = profilesDb.getStudentProfile(typicalProfileWithPicture.googleId);
        // other fields remain
        verifyPresentInDatastore(typicalProfileWithPicture);
        // picture remains
        assertTrue(doesFileExistInGcs(new BlobKey(storedProfile.pictureKey)));
        // modifiedDate remains
        assertEquals(typicalProfileWithPicture.modifiedDate, storedProfile.modifiedDate);
    }

    @Test
    public void testUpdateOrCreateStudentProfile_withEmptyPictureKey_shouldUpdateSuccessfullyAndNotChangePictureKey()
            throws Exception {
        typicalProfileWithPicture.pictureKey = "";

        profilesDb.updateOrCreateStudentProfile(typicalProfileWithPicture);

        typicalProfileWithPicture.pictureKey = typicalPictureKey;
        verifyPresentInDatastore(typicalProfileWithPicture);
    }

    @Test
    public void testUpdateOrCreateStudentProfile_withNonEmptyPictureKey_shouldUpdateSuccessfully() throws Exception {
        typicalProfileWithoutPicture.pictureKey = uploadDefaultPictureForProfile(typicalProfileWithPicture.googleId);

        profilesDb.updateOrCreateStudentProfile(typicalProfileWithoutPicture);

        verifyPresentInDatastore(typicalProfileWithoutPicture);

        // tear down
        profilesDb.deletePicture(new BlobKey(typicalProfileWithoutPicture.pictureKey));
    }

    @Test
    public void testUpdateOrCreateStudentProfile_withSamePictureKey_shouldUpdateProfileButNotPictureKey() throws Exception {
        typicalProfileWithPicture.shortName = "s";
        profilesDb.updateOrCreateStudentProfile(typicalProfileWithPicture);

        // picture should not be deleted
        assertTrue(doesFileExistInGcs(new BlobKey(typicalProfileWithPicture.pictureKey)));
        verifyPresentInDatastore(typicalProfileWithPicture);
    }

    @Test
    public void testDeleteStudentProfile_nonExistentEntity_shouldFailSilently() {
        profilesDb.deleteStudentProfile("test.non-existent");

        assertNull(profilesDb.getStudentProfile("test.non-existent"));
    }

    @Test
    public void testDeleteStudentProfile_profileWithoutPicture_shouldDeleteCorrectly() {
        profilesDb.deleteStudentProfile(typicalProfileWithoutPicture.googleId);

        verifyAbsentInDatastore(typicalProfileWithoutPicture);
    }

    @Test
    public void testDeleteStudentProfile_profileWithPicture_shouldDeleteCorrectly() {
        profilesDb.deleteStudentProfile(typicalProfileWithPicture.googleId);

        // check that profile get deleted and picture get deleted
        verifyAbsentInDatastore(typicalProfileWithPicture);
        assertFalse(doesFileExistInGcs(new BlobKey(typicalProfileWithPicture.pictureKey)));
    }

    @Test
    public void testUpdateStudentProfilePicture() throws Exception {
        // failure test cases
        testUpdateProfilePictureWithNullParameters();
        testUpdateProfilePictureWithEmptyParameters(typicalProfileWithPicture);

        // success test cases
        testUpdateProfilePictureSuccessInitiallyEmpty(typicalProfileWithoutPicture);
        testUpdateProfilePictureSuccessSamePictureKey(typicalProfileWithPicture);
    }

    @Test
    public void testUpdateProfilePicture_nonExistentProfile_shouldCreateProfile() {
        profilesDb.updateStudentProfilePicture("non-eXisTEnt", "random");

        StudentProfileAttributes sp = profilesDb.getStudentProfile("non-eXisTEnt");
        assertNotNull(sp);
        assertEquals("random", sp.pictureKey);
    }

    @Test
    public void testDeletePicture_unknownBlobKey_shouldFailSilently() {
        profilesDb.deletePicture(new BlobKey("unknown"));

        assertFalse(doesFileExistInGcs(new BlobKey("unknown")));
    }

    @Test
    public void testDeletePicture_typicalBlobKey_shouldDeleteSuccessfully() {
        profilesDb.deletePicture(new BlobKey(typicalPictureKey));

        assertFalse(doesFileExistInGcs(new BlobKey(typicalPictureKey)));
    }

    private void testUpdateProfilePictureWithNullParameters() {
        ______TS("null parameters");
        // googleId
        AssertionError ae = assertThrows(AssertionError.class,
                () -> profilesDb.updateStudentProfilePicture(null, "anything"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        // pictureKey
        ae = assertThrows(AssertionError.class,
                () -> profilesDb.updateStudentProfilePicture("anything", null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void testUpdateProfilePictureWithEmptyParameters(StudentProfileAttributes spa) {
        ______TS("empty parameters");

        // googleId
        AssertionError ae = assertThrows(AssertionError.class,
                () -> profilesDb.updateStudentProfilePicture("", "anything"));
        AssertHelper.assertContains("GoogleId is empty", ae.getMessage());

        // picture key
        ae = assertThrows(AssertionError.class,
                () -> profilesDb.updateStudentProfilePicture(spa.googleId, ""));
        AssertHelper.assertContains("PictureKey is empty", ae.getMessage());
    }

    private void testUpdateProfilePictureSuccessInitiallyEmpty(
            StudentProfileAttributes spa) throws IOException {
        ______TS("update picture key - initially empty");

        spa.pictureKey = uploadDefaultPictureForProfile(spa.googleId);
        profilesDb.updateStudentProfilePicture(spa.googleId, spa.pictureKey);

        StudentProfileAttributes updatedProfile = profilesDb.getStudentProfile(spa.googleId);

        assertEquals(spa.pictureKey, updatedProfile.pictureKey);
    }

    private void testUpdateProfilePictureSuccessSamePictureKey(StudentProfileAttributes spa) {
        ______TS("update picture key - same key; does nothing");
        profilesDb.updateStudentProfilePicture(spa.googleId, spa.pictureKey);
    }

    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------- Helper Functions -----------------------------------------------
    //-------------------------------------------------------------------------------------------------------

    private String uploadDefaultPictureForProfile(String googleId)
            throws IOException {
        // we upload a small text file as the actual file does not matter here
        return writeFileToGcs(googleId, "src/test/resources/images/not_a_picture.txt");
    }
}
