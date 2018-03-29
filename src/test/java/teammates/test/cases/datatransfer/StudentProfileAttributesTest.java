package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.StudentProfile;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.StringHelperExtension;

/**
 * SUT: {@link StudentProfileAttributes}.
 */
public class StudentProfileAttributesTest extends BaseAttributesTest {

    private static final String VALID_GOOGLE_ID = "valid.googleId";
    private StudentProfileAttributes profile;

    @BeforeClass
    public void classSetup() {
        profile = StudentProfileAttributes.builder(VALID_GOOGLE_ID)
                .withShortName("shor")
                .withInstitute("institute")
                .withEmail("valid@email.com")
                .withNationality("Lebanese")
                .withGender("female")
                .withMoreInfo("moreInfo can have a lot more than this...")
                .withPictureKey("profile Pic Key")
                .build();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testBuilderWithNullValuesForRequiredFields() {
        StudentProfileAttributes.builder(null)
                .build();
    }

    @Test
    public void testBuilderWithDefaultOptionalValues() {
        StudentProfileAttributes profileAttributes = StudentProfileAttributes.builder(VALID_GOOGLE_ID).build();
        assertIsDefaultValues(profileAttributes);
    }

    @Test
    public void testDefaultValueForGenderIfNullPassed() {
        StudentProfileAttributes profileAttributes = StudentProfileAttributes.builder(VALID_GOOGLE_ID)
                .withGender(null)
                .build();

        assertIsDefaultValues(profileAttributes);
    }

    private void assertIsDefaultValues(StudentProfileAttributes profileAttributes) {
        assertEquals("other", profileAttributes.gender);
        assertEquals(VALID_GOOGLE_ID, profileAttributes.googleId);
        assertEquals("", profileAttributes.shortName);
        assertEquals("", profileAttributes.email);
        assertEquals("", profileAttributes.institute);
        assertEquals("", profileAttributes.nationality);
        assertEquals("", profileAttributes.moreInfo);
        assertEquals("", profileAttributes.pictureKey);
    }

    @Test
    public void testValueOf() {
        StudentProfile studentProfile = new StudentProfile("id", "Joe", "joe@gmail.com",
                "Teammates Institute", "American", "male",
                new Text("hello"), new BlobKey("key"));
        StudentProfileAttributes profileAttributes = StudentProfileAttributes.valueOf(studentProfile);

        assertEquals(studentProfile.getGoogleId(), profileAttributes.googleId);
        assertEquals(studentProfile.getShortName(), profileAttributes.shortName);
        assertEquals(studentProfile.getEmail(), profileAttributes.email);
        assertEquals(studentProfile.getInstitute(), profileAttributes.institute);
        assertEquals(studentProfile.getNationality(), profileAttributes.nationality);
        assertEquals(studentProfile.getGender(), profileAttributes.gender);
        assertEquals(studentProfile.getMoreInfo().getValue(), profileAttributes.moreInfo);
        assertEquals(studentProfile.getPictureKey().getKeyString(), profileAttributes.pictureKey);

    }

    @Test
    public void testGetEntityTypeAsString() {
        assertEquals("StudentProfile", profile.getEntityTypeAsString());
    }

    @Test
    public void testGetBackupIdentifier() {
        assertEquals("Student profile modified", profile.getBackupIdentifier());
    }

    @Test
    public void testGetIdentificationString() {
        assertEquals(profile.googleId, profile.getIdentificationString());
    }

    @Test
    public void testGetJsonString() throws Exception {
        StudentProfileAttributes spa = StudentProfileAttributes.valueOf(profile.toEntity());
        spa.modifiedDate = TimeHelper.parseInstant("2015-05-21 8:34 AM +0000");
        assertEquals("{\n  \"googleId\": \"valid.googleId\",\n  \"shortName\": \"shor\","
                     + "\n  \"email\": \"valid@email.com\",\n  \"institute\": \"institute\","
                     + "\n  \"nationality\": \"Lebanese\",\n  \"gender\": \"female\","
                     + "\n  \"moreInfo\": \"moreInfo can have a lot more than this...\","
                     + "\n  \"pictureKey\": \"profile Pic Key\","
                     + "\n  \"modifiedDate\": \"2015-05-21T08:34:00Z\"\n}",
                     spa.getJsonString());
    }

    @Test
    public void testGetInvalidityInfo() throws Exception {
        testGetInvalidityInfoForValidProfileWithValues();
        testGetInvalidityInfoForValidProfileWithEmptyValues();
        testInvalidityInfoForInvalidProfile();
    }

    private void testGetInvalidityInfoForValidProfileWithValues() {
        StudentProfileAttributes validProfile = profile.getCopy();

        ______TS("Typical case: valid profile attributes");
        assertTrue("'validProfile' indicated as invalid", validProfile.isValid());
        assertEquals(new ArrayList<String>(), validProfile.getInvalidityInfo());
    }

    private void testGetInvalidityInfoForValidProfileWithEmptyValues() {
        StudentProfileAttributes validProfile = profile.getCopy();

        ______TS("Typical case: valid profile with empty attributes");
        validProfile.shortName = "";
        validProfile.email = "";
        validProfile.nationality = "";
        validProfile.institute = "";

        assertTrue("'validProfile' indicated as invalid", validProfile.isValid());
        assertEquals(new ArrayList<String>(), validProfile.getInvalidityInfo());
    }

    private void testInvalidityInfoForInvalidProfile() throws Exception {
        StudentProfileAttributes invalidProfile = getInvalidStudentProfileAttributes();

        ______TS("Failure case: invalid profile attributes");
        assertFalse("'invalidProfile' indicated as valid", invalidProfile.isValid());
        List<String> expectedErrorMessages = generatedExpectedErrorMessages(invalidProfile);

        AssertHelper.assertSameContentIgnoreOrder(expectedErrorMessages, invalidProfile.getInvalidityInfo());
    }

    @Test
    public void testSanitizeForSaving() {
        StudentProfileAttributes profileToSanitize = getStudentProfileAttributesToSanitize();
        StudentProfileAttributes profileToSanitizeExpected = getStudentProfileAttributesToSanitize();
        profileToSanitize.sanitizeForSaving();

        assertEquals(SanitizationHelper.sanitizeGoogleId(profileToSanitizeExpected.googleId),
                     profileToSanitize.googleId);
        assertEquals(profileToSanitizeExpected.shortName, profileToSanitize.shortName);
        assertEquals(profileToSanitizeExpected.institute, profileToSanitize.institute);
        assertEquals(profileToSanitizeExpected.email, profileToSanitize.email);
        assertEquals(profileToSanitizeExpected.nationality, profileToSanitize.nationality);
        assertEquals(profileToSanitizeExpected.gender, profileToSanitize.gender);
        assertEquals(profileToSanitizeExpected.moreInfo, profileToSanitize.moreInfo);
        assertEquals(profileToSanitizeExpected.pictureKey, profileToSanitize.pictureKey);
    }

    @Override
    @Test
    public void testToEntity() {
        StudentProfile expectedEntity = createStudentProfileFrom(profile);
        StudentProfileAttributes testProfile = StudentProfileAttributes.valueOf(expectedEntity);
        StudentProfile actualEntity = testProfile.toEntity();

        assertEquals(expectedEntity.getShortName(), actualEntity.getShortName());
        assertEquals(expectedEntity.getInstitute(), actualEntity.getInstitute());
        assertEquals(expectedEntity.getEmail(), actualEntity.getEmail());
        assertEquals(expectedEntity.getNationality(), actualEntity.getNationality());
        assertEquals(expectedEntity.getGender(), actualEntity.getGender());
        assertEquals(expectedEntity.getMoreInfo(), actualEntity.getMoreInfo());
        assertEquals(expectedEntity.getModifiedDate().toString(),
                     actualEntity.getModifiedDate().toString());
        assertEquals(expectedEntity.getPictureKey(), actualEntity.getPictureKey());
    }

    @Test
    public void testToString() {
        StudentProfileAttributes spa = StudentProfileAttributes.valueOf(profile.toEntity());
        profile.modifiedDate = spa.modifiedDate;

        // the toString must be unique to the values in the object
        assertEquals(profile.toString(), spa.toString());
    }

    // -------------------------------------------------------------------------------------------------------
    // -------------------------------------- Helper Functions
    // -----------------------------------------------
    // -------------------------------------------------------------------------------------------------------

    private StudentProfile createStudentProfileFrom(
            StudentProfileAttributes profile) {
        return new StudentProfile(profile.googleId, profile.shortName, profile.email,
                                  profile.institute, profile.nationality, profile.gender,
                                  new Text(profile.moreInfo), new BlobKey(profile.pictureKey));
    }

    private List<String> generatedExpectedErrorMessages(StudentProfileAttributes profile) throws Exception {
        List<String> expectedErrorMessages = new ArrayList<>();

        // tests both the constructor and the invalidity info
        expectedErrorMessages.add(
                getPopulatedErrorMessage(
                    FieldValidator.INVALID_NAME_ERROR_MESSAGE, profile.shortName,
                    FieldValidator.PERSON_NAME_FIELD_NAME,
                    FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR,
                    FieldValidator.PERSON_NAME_MAX_LENGTH));
        expectedErrorMessages.add(
                getPopulatedErrorMessage(
                    FieldValidator.EMAIL_ERROR_MESSAGE, profile.email,
                    FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                    FieldValidator.EMAIL_MAX_LENGTH));
        expectedErrorMessages.add(
                getPopulatedErrorMessage(
                    FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, profile.institute,
                    FieldValidator.INSTITUTE_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                    FieldValidator.INSTITUTE_NAME_MAX_LENGTH));
        expectedErrorMessages.add(String.format(FieldValidator.NATIONALITY_ERROR_MESSAGE, profile.nationality));

        return expectedErrorMessages;
    }

    private StudentProfileAttributes getInvalidStudentProfileAttributes() {
        String googleId = StringHelperExtension.generateStringOfLength(46);
        String shortName = "%%";
        String email = "invalid@email@com";
        String institute = StringHelperExtension.generateStringOfLength(FieldValidator.INSTITUTE_NAME_MAX_LENGTH + 1);
        String nationality = "$invalid nationality ";
        String gender = "invalidGender";
        String moreInfo = "Ooops no validation for this one...";
        String pictureKey = "";

        return StudentProfileAttributes.builder(googleId)
                .withShortName(shortName)
                .withEmail(email)
                .withInstitute(institute)
                .withNationality(nationality)
                .withGender(gender)
                .withMoreInfo(moreInfo)
                .withPictureKey(pictureKey)
                .build();
    }

    private StudentProfileAttributes getStudentProfileAttributesToSanitize() {
        String googleId = " test.google@gmail.com ";
        String shortName = "<name>";
        String email = "'toSanitize@email.com'";
        String institute = "institute/\"";
        String nationality = "&\"invalid nationality &";
        String gender = "'\"'invalidGender";
        String moreInfo = "<<script> alert('hi!'); </script>";
        String pictureKey = "testPictureKey";

        return StudentProfileAttributes.builder(googleId)
                .withShortName(shortName)
                .withEmail(email)
                .withInstitute(institute)
                .withNationality(nationality)
                .withGender(gender)
                .withMoreInfo(moreInfo)
                .withPictureKey(pictureKey)
                .build();
    }

}
