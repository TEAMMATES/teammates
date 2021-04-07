package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelperExtension;
import teammates.storage.entity.StudentProfile;
import teammates.test.AssertHelper;

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
                .withGender(StudentProfileAttributes.Gender.FEMALE)
                .withMoreInfo("moreInfo can have a lot more than this...")
                .build();
    }

    @Test
    public void testBuilder_withNothingPassed_shouldUseDefaultValues() {
        StudentProfileAttributes profileAttributes = StudentProfileAttributes.builder(VALID_GOOGLE_ID).build();

        assertEquals(StudentProfileAttributes.Gender.OTHER, profileAttributes.gender);
        assertEquals(VALID_GOOGLE_ID, profileAttributes.googleId);
        assertEquals("", profileAttributes.shortName);
        assertEquals("", profileAttributes.email);
        assertEquals("", profileAttributes.institute);
        assertEquals("", profileAttributes.nationality);
        assertEquals("", profileAttributes.moreInfo);
    }

    @Test
    public void testBuilder_withNullValuePassed_shouldThrowException() {

        assertThrows(AssertionError.class, () -> StudentProfileAttributes.builder(null).build());

        assertThrows(AssertionError.class, () -> {
            StudentProfileAttributes.builder(VALID_GOOGLE_ID)
                    .withShortName(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            StudentProfileAttributes.builder(VALID_GOOGLE_ID)
                    .withInstitute(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            StudentProfileAttributes.builder(VALID_GOOGLE_ID)
                    .withEmail(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            StudentProfileAttributes.builder(VALID_GOOGLE_ID)
                    .withNationality(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            StudentProfileAttributes.builder(VALID_GOOGLE_ID)
                    .withGender(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            StudentProfileAttributes.builder(VALID_GOOGLE_ID)
                    .withMoreInfo(null)
                    .build();
        });
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildCorrectAttribute() {
        StudentProfileAttributes studentProfileAttributes = StudentProfileAttributes.builder(VALID_GOOGLE_ID)
                .withShortName("shor")
                .withInstitute("institute")
                .withEmail("valid@email.com")
                .withNationality("Lebanese")
                .withGender(StudentProfileAttributes.Gender.FEMALE)
                .withMoreInfo("moreInfo can have a lot more than this...")
                .build();

        assertEquals(VALID_GOOGLE_ID, studentProfileAttributes.getGoogleId());
        assertEquals("shor", studentProfileAttributes.getShortName());
        assertEquals("institute", studentProfileAttributes.getInstitute());
        assertEquals("valid@email.com", studentProfileAttributes.getEmail());
        assertEquals("Lebanese", studentProfileAttributes.getNationality());
        assertEquals(StudentProfileAttributes.Gender.FEMALE, studentProfileAttributes.getGender());
        assertEquals("moreInfo can have a lot more than this...", studentProfileAttributes.getMoreInfo());
    }

    @Test
    public void testValueOf_withAllFieldPopulatedStudentProfile_shouldGenerateAttributesCorrectly() {
        StudentProfile studentProfile = new StudentProfile("id", "Joe", "joe@gmail.com",
                "Teammates Institute", "American", StudentProfileAttributes.Gender.MALE.name().toLowerCase(),
                "hello");
        StudentProfileAttributes profileAttributes = StudentProfileAttributes.valueOf(studentProfile);

        assertEquals(studentProfile.getGoogleId(), profileAttributes.googleId);
        assertEquals(studentProfile.getShortName(), profileAttributes.shortName);
        assertEquals(studentProfile.getEmail(), profileAttributes.email);
        assertEquals(studentProfile.getInstitute(), profileAttributes.institute);
        assertEquals(studentProfile.getNationality(), profileAttributes.nationality);
        assertEquals(studentProfile.getGender(), profileAttributes.gender.name().toLowerCase());
        assertEquals(studentProfile.getMoreInfo(), profileAttributes.moreInfo);

    }

    @Test
    public void testValueOf_withSomeFieldsPopulatedAsNull_shouldUseDefaultValues() {
        StudentProfile studentProfile = new StudentProfile("id", null, null,
                null, null, null, null);
        StudentProfileAttributes profileAttributes = StudentProfileAttributes.valueOf(studentProfile);

        assertEquals(studentProfile.getGoogleId(), profileAttributes.googleId);
        assertEquals("", profileAttributes.shortName);
        assertEquals("", profileAttributes.email);
        assertEquals("", profileAttributes.institute);
        assertEquals("", profileAttributes.nationality);
        assertEquals(StudentProfileAttributes.Gender.OTHER, profileAttributes.gender);
        assertEquals("", profileAttributes.moreInfo);
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
        assertEquals(new ArrayList<>(), validProfile.getInvalidityInfo());
    }

    private void testGetInvalidityInfoForValidProfileWithEmptyValues() {
        StudentProfileAttributes validProfile = profile.getCopy();

        ______TS("Typical case: valid profile with empty attributes");
        validProfile.shortName = "";
        validProfile.email = "";
        validProfile.nationality = "";
        validProfile.institute = "";

        assertTrue("'validProfile' indicated as invalid", validProfile.isValid());
        assertEquals(new ArrayList<>(), validProfile.getInvalidityInfo());
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
    }

    @Test
    public void testToString() {
        StudentProfileAttributes spa = StudentProfileAttributes.valueOf(profile.toEntity());
        profile.modifiedDate = spa.modifiedDate;

        // the toString must be unique to the values in the object
        assertEquals(profile.toString(), spa.toString());
    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributeCorrectly() {
        StudentProfileAttributes.UpdateOptions updateOptions =
                StudentProfileAttributes.updateOptionsBuilder("testGoogleId")
                        .withShortName("testName")
                        .withEmail("test@email.com")
                        .withInstitute("NUS")
                        .withNationality("Singapore")
                        .withGender(StudentProfileAttributes.Gender.MALE)
                        .withMoreInfo("more info")
                        .build();

        assertEquals("testGoogleId", updateOptions.getGoogleId());

        StudentProfileAttributes profileAttributes = StudentProfileAttributes.builder("id").build();

        profileAttributes.update(updateOptions);

        assertEquals("testName", profileAttributes.shortName);
        assertEquals("test@email.com", profileAttributes.email);
        assertEquals("NUS", profileAttributes.institute);
        assertEquals("Singapore", profileAttributes.nationality);
        assertEquals(StudentProfileAttributes.Gender.MALE, profileAttributes.gender);
        assertEquals("more info", profileAttributes.moreInfo);
    }

    @Test
    public void testUpdateOptionsBuilder_withNullInput_shouldFailWithAssertionError() {
        assertThrows(AssertionError.class, () ->
                StudentProfileAttributes.updateOptionsBuilder(null));

        assertThrows(AssertionError.class, () ->
                StudentProfileAttributes.updateOptionsBuilder("validId")
                        .withShortName(null));

        assertThrows(AssertionError.class, () ->
                StudentProfileAttributes.updateOptionsBuilder("validId")
                        .withEmail(null));

        assertThrows(AssertionError.class, () ->
                StudentProfileAttributes.updateOptionsBuilder("validId")
                        .withInstitute(null));

        assertThrows(AssertionError.class, () ->
                StudentProfileAttributes.updateOptionsBuilder("validId")
                        .withNationality(null));

        assertThrows(AssertionError.class, () ->
                StudentProfileAttributes.updateOptionsBuilder("validId")
                        .withGender(null));

        assertThrows(AssertionError.class, () ->
                StudentProfileAttributes.updateOptionsBuilder("validId")
                        .withMoreInfo(null));
    }

    @Test
    public void testEquals() {
        // When the two student profiles are copies of each other
        StudentProfileAttributes studentProfileCopy = profile.getCopy();

        assertTrue(profile.equals(studentProfileCopy));

        // When the two student profiles have same values but created at different time
        StudentProfileAttributes studentProfileSimilar = StudentProfileAttributes.builder(VALID_GOOGLE_ID)
                .withShortName("shor")
                .withInstitute("institute")
                .withEmail("valid@email.com")
                .withNationality("Lebanese")
                .withGender(StudentProfileAttributes.Gender.FEMALE)
                .withMoreInfo("moreInfo can have a lot more than this...")
                .build();

        assertTrue(profile.equals(studentProfileSimilar));

        // When the two student profiles are different
        StudentProfileAttributes studentProfileDifferent = getStudentProfileAttributesToSanitize();

        assertFalse(profile.equals(studentProfileDifferent));

        // When the other object is of different class
        assertFalse(profile.equals(3));
    }

    @Test
    public void testHashCode() {
        // When the two student profiles are copies of each other, they should have the same hash code
        StudentProfileAttributes studentProfileCopy = profile.getCopy();

        assertTrue(profile.hashCode() == studentProfileCopy.hashCode());

        // When the two student profiles have same values but created at different time,
        // they should still have the same hash code
        StudentProfileAttributes studentProfileSimilar = StudentProfileAttributes.builder(VALID_GOOGLE_ID)
                .withShortName("shor")
                .withInstitute("institute")
                .withEmail("valid@email.com")
                .withNationality("Lebanese")
                .withGender(StudentProfileAttributes.Gender.FEMALE)
                .withMoreInfo("moreInfo can have a lot more than this...")
                .build();

        assertTrue(profile.hashCode() == studentProfileSimilar.hashCode());

        // When the two student profiles are different, they should have different hash code
        StudentProfileAttributes studentProfileDifferent = getStudentProfileAttributesToSanitize();

        assertFalse(profile.hashCode() == studentProfileDifferent.hashCode());
    }

    // -------------------------------------------------------------------------------------------------------
    // -------------------------------------- Helper Functions
    // -----------------------------------------------
    // -------------------------------------------------------------------------------------------------------

    private StudentProfile createStudentProfileFrom(
            StudentProfileAttributes profile) {
        return new StudentProfile(profile.googleId, profile.shortName, profile.email,
                                  profile.institute, profile.nationality, profile.gender.name().toLowerCase(),
                                  profile.moreInfo);
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
        StudentProfileAttributes.Gender gender = StudentProfileAttributes.Gender.MALE;
        String moreInfo = "Ooops no validation for this one...";

        return StudentProfileAttributes.builder(googleId)
                .withShortName(shortName)
                .withEmail(email)
                .withInstitute(institute)
                .withNationality(nationality)
                .withGender(gender)
                .withMoreInfo(moreInfo)
                .build();
    }

    private StudentProfileAttributes getStudentProfileAttributesToSanitize() {
        String googleId = " test.google@gmail.com ";
        String shortName = "<name>";
        String email = "'toSanitize@email.com'";
        String institute = "institute/\"";
        String nationality = "&\"invalid nationality &";
        StudentProfileAttributes.Gender gender = StudentProfileAttributes.Gender.OTHER;
        String moreInfo = "<<script> alert('hi!'); </script>";

        return StudentProfileAttributes.builder(googleId)
                .withShortName(shortName)
                .withEmail(email)
                .withInstitute(institute)
                .withNationality(nationality)
                .withGender(gender)
                .withMoreInfo(moreInfo)
                .build();
    }

}
