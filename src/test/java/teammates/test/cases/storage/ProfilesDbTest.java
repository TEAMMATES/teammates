package teammates.test.cases.storage;

import java.io.IOException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.api.ProfilesDb;
import teammates.test.cases.BaseComponentTestCase;

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
        assertTrue(doesFileExistInGcs(typicalPictureKey));

        // typical profiles
        profilesDb.createEntity(StudentProfileAttributes.builder("valid.googleId")
                .withInstitute("TEAMMATES Test Institute 1")
                .withPictureKey(typicalPictureKey)
                .build());
        profilesDb.createEntity(StudentProfileAttributes.builder("valid.googleId2")
                .withInstitute("TEAMMATES Test Institute 1")
                .withPictureKey(typicalPictureKey)
                .build());

        // save entity and picture
        typicalProfileWithPicture = profilesDb.getStudentProfile("valid.googleId");
        typicalProfileWithoutPicture = profilesDb.getStudentProfile("valid.googleId2");
    }

    @AfterMethod
    public void deleteTypicalData() {
        // delete entity
        profilesDb.deleteStudentProfile(typicalProfileWithPicture.googleId);
        profilesDb.deleteStudentProfile(typicalProfileWithoutPicture.googleId);
        verifyAbsentInDatastore(typicalProfileWithPicture);
        verifyAbsentInDatastore(typicalProfileWithoutPicture);

        // delete picture
        profilesDb.deletePicture(typicalPictureKey);
        assertFalse(doesFileExistInGcs(typicalPictureKey));
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
        StudentProfileAttributes createdSpa = profilesDb.updateOrCreateStudentProfile(
                StudentProfileAttributes.updateOptionsBuilder(spa.googleId)
                        .withShortName(spa.shortName)
                        .build());

        verifyPresentInDatastore(spa);
        assertEquals("non-ExIsTenT", createdSpa.googleId);
        assertEquals("Test", createdSpa.shortName);

        // tear down
        profilesDb.deleteStudentProfile(spa.googleId);
    }

    @Test
    public void testUpdateOrCreateStudentProfile_nullParameter_shouldThrowAssertionException() throws Exception {
        AssertionError ae = assertThrows(AssertionError.class,
                () -> profilesDb.updateOrCreateStudentProfile(null));

        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    @Test
    public void testUpdateOrCreateStudentProfile_invalidParameter_shouldThrowInvalidParamException() throws Exception {
        // cannot access entity with empty googleId
        assertThrows(IllegalArgumentException.class,
                () -> profilesDb.updateOrCreateStudentProfile(
                        StudentProfileAttributes.updateOptionsBuilder("")
                                .build()));

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> profilesDb.updateOrCreateStudentProfile(
                        StudentProfileAttributes.updateOptionsBuilder(typicalProfileWithPicture.googleId)
                                .withEmail("invalid email")
                                .build()));

        assertEquals(getPopulatedErrorMessage(
                FieldValidator.EMAIL_ERROR_MESSAGE, "invalid email",
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                FieldValidator.EMAIL_MAX_LENGTH), ipe.getMessage());
    }

    @Test
    public void testUpdateOrCreateStudentProfile_noChangesToProfile_shouldNotChangeProfileContent()
            throws Exception {
        // update same profile
        profilesDb.updateOrCreateStudentProfile(
                StudentProfileAttributes.updateOptionsBuilder(typicalProfileWithPicture.googleId)
                        .withShortName(typicalProfileWithPicture.shortName)
                        .withGender(typicalProfileWithPicture.gender)
                        .withPictureKey(typicalProfileWithPicture.pictureKey)
                        .withMoreInfo(typicalProfileWithPicture.moreInfo)
                        .withInstitute(typicalProfileWithPicture.institute)
                        .withEmail(typicalProfileWithPicture.email)
                        .withNationality(typicalProfileWithPicture.nationality)
                        .build());

        StudentProfileAttributes storedProfile = profilesDb.getStudentProfile(typicalProfileWithPicture.googleId);
        // other fields remain
        verifyPresentInDatastore(typicalProfileWithPicture);
        // picture remains
        assertTrue(doesFileExistInGcs(storedProfile.pictureKey));
        // modifiedDate remains
        assertEquals(typicalProfileWithPicture.modifiedDate, storedProfile.modifiedDate);
    }

    @Test
    public void testUpdateOrCreateStudentProfile_withNonEmptyPictureKey_shouldUpdateSuccessfully() throws Exception {
        typicalProfileWithoutPicture.pictureKey = uploadDefaultPictureForProfile(typicalProfileWithPicture.googleId);

        StudentProfileAttributes updatedSpa = profilesDb.updateOrCreateStudentProfile(
                StudentProfileAttributes.updateOptionsBuilder(typicalProfileWithoutPicture.googleId)
                        .withPictureKey(typicalProfileWithoutPicture.pictureKey)
                        .build());

        verifyPresentInDatastore(typicalProfileWithoutPicture);
        assertEquals(typicalProfileWithoutPicture.pictureKey, updatedSpa.pictureKey);

        // tear down
        profilesDb.deletePicture(typicalProfileWithoutPicture.pictureKey);
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
        assertFalse(doesFileExistInGcs(typicalProfileWithPicture.pictureKey));
    }

    @Test
    public void testDeletePicture_unknownBlobKey_shouldFailSilently() {
        profilesDb.deletePicture("unknown");

        assertFalse(doesFileExistInGcs("unknown"));
    }

    @Test
    public void testDeletePicture_typicalBlobKey_shouldDeleteSuccessfully() {
        profilesDb.deletePicture(typicalPictureKey);

        assertFalse(doesFileExistInGcs(typicalPictureKey));
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
