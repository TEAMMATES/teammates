/**
 * 
 */
package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.storage.entity.StudentProfile;
import teammates.test.cases.BaseTestCase;
import teammates.test.util.TestHelper;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;

public class StudentProfileAttributesTest extends BaseTestCase {

    private static StudentProfileAttributes profile;
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        profile = new StudentProfileAttributes();
        profile.googleId = "valid.googleId";
        profile.shortName = "shor";
        profile.institute = "institute";
        profile.email = "valid@email.com";
        profile.nationality = "nationality";
        profile.gender = "female";
        profile.moreInfo = "moreInfo can have a lot more than this...";
        profile.pictureKey = "profile Pic Key";
    }
    
    @Test
    public void testGetEntityTypeAsString() {
        assertEquals("StudentProfile", profile.getEntityTypeAsString());
    }
    
    @Test
    public void testeGetIdentificationString() {
        assertEquals(profile.googleId, profile.getIdentificationString());
    }
    
    @Test
    public void testGetInvalidityInfo() {        
        testGetInvalidityInfoForValidProfileWithValues();
        testGetInvalidtyInfoForValidProfileWithEmptyValues();
        testInvalidityInfoForInvalidProfile();
    }

    protected void testGetInvalidityInfoForValidProfileWithValues() {
        StudentProfileAttributes validProfile = createNewProfileAttributesFrom(profile);
        
        ______TS("Valid Profile Attributes");
        assertTrue("'validProfile' indicated as invalid", validProfile.isValid());
        assertEquals(new ArrayList<String>(), validProfile.getInvalidityInfo());
    }

    protected void testGetInvalidtyInfoForValidProfileWithEmptyValues() {
        StudentProfileAttributes validProfile = createNewProfileAttributesFrom(profile);
        
        ______TS("Valid profile with empty attributes");
        validProfile.shortName = "";
        validProfile.email = "";
        validProfile.nationality = "";
        validProfile.institute = "";
        
        assertTrue("'validProfile' indicated as invalid", validProfile.isValid());
        assertEquals(new ArrayList<String>(), validProfile.getInvalidityInfo());
    }

    protected void testInvalidityInfoForInvalidProfile() {
        StudentProfileAttributes invalidProfile = getInvalidStudentProfileAttributes();
        
        ______TS("Invalid Profile Attributes");
        assertFalse("'invalidProfile' indicated as valid", invalidProfile.isValid());
        List<String> expectedErrorMessages = generatedExpectedErrorMessages();
        
        TestHelper.isSameContentIgnoreOrder(expectedErrorMessages, invalidProfile.getInvalidityInfo());
    }
    
    @Test
    public void testSanitizeForSaving() {
        StudentProfileAttributes profileToSanitize = getStudentProfileAttributesToSanitize();
        StudentProfileAttributes profileToSanitizeExpected = getStudentProfileAttributesToSanitize();
        profileToSanitize.sanitizeForSaving();
        
        assertEquals(Sanitizer.sanitizeGoogleId(profileToSanitizeExpected.googleId), profileToSanitize.googleId);
        assertEquals(Sanitizer.sanitizeForHtml(profileToSanitizeExpected.shortName), profileToSanitize.shortName);
        assertEquals(Sanitizer.sanitizeForHtml(profileToSanitizeExpected.institute), profileToSanitize.institute);
        assertEquals(Sanitizer.sanitizeForHtml(profileToSanitizeExpected.email), profileToSanitize.email);
        assertEquals(Sanitizer.sanitizeForHtml(profileToSanitizeExpected.nationality), profileToSanitize.nationality);
        assertEquals(Sanitizer.sanitizeForHtml(profileToSanitizeExpected.gender), profileToSanitize.gender);
        assertEquals(Sanitizer.sanitizeForHtml(profileToSanitizeExpected.moreInfo), profileToSanitize.moreInfo);
    }
    
    @Test
    public void testToEntity() {
        StudentProfile expectedEntity = createStudentProfileFrom(profile);
        StudentProfileAttributes testProfile = new StudentProfileAttributes(expectedEntity);
        StudentProfile actualEntity = (StudentProfile) testProfile.toEntity();
        
        assertEquals(expectedEntity.getShortName(), actualEntity.getShortName());
        assertEquals(expectedEntity.getInstitute(), actualEntity.getInstitute());
        assertEquals(expectedEntity.getEmail(), actualEntity.getEmail());
        assertEquals(expectedEntity.getNationality(), actualEntity.getNationality());
        assertEquals(expectedEntity.getGender(), actualEntity.getGender());
        assertEquals(expectedEntity.getMoreInfo(), actualEntity.getMoreInfo());
        assertEquals(expectedEntity.getModifiedDate().toString(), actualEntity.getModifiedDate().toString());
        assertEquals(expectedEntity.getPictureKey(), actualEntity.getPictureKey());
    }
    
    @Test
    public void testToString() {
        StudentProfileAttributes spa = new StudentProfileAttributes((StudentProfile) profile.toEntity());
        profile.modifiedDate = spa.modifiedDate;
        
        // the toString must be unique to the values in the object
        assertEquals(profile.toString(), spa.toString());
    }
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
    
    
    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------- Helper Functions -----------------------------------------------
    //-------------------------------------------------------------------------------------------------------
    
    private StudentProfile createStudentProfileFrom(StudentProfileAttributes profile) {
        return new StudentProfile(profile.googleId, profile.shortName, profile.email, profile.institute, 
                profile.nationality, profile.gender, new Text(profile.moreInfo), new BlobKey(profile.pictureKey));
    }

    private StudentProfileAttributes createNewProfileAttributesFrom(StudentProfileAttributes profile) {
        return new StudentProfileAttributes(profile.googleId, profile.shortName, profile.email, profile.institute,
                profile.nationality, profile.gender, profile.moreInfo, profile.pictureKey);
    }

    private List<String> generatedExpectedErrorMessages() {
        List<String> expectedErrorMessages = new ArrayList<String>();

        //tests both the constructor and the invalidity info
        expectedErrorMessages.add(String.format(FieldValidator.GOOGLE_ID_ERROR_MESSAGE, profile.googleId, FieldValidator.REASON_TOO_LONG));
        expectedErrorMessages.add(String.format(FieldValidator.PERSON_NAME_ERROR_MESSAGE, profile.shortName, FieldValidator.REASON_CONTAINS_INVALID_CHAR));
        expectedErrorMessages.add(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, profile.email, FieldValidator.REASON_INCORRECT_FORMAT));
        expectedErrorMessages.add(String.format(FieldValidator.INSTITUTE_NAME_ERROR_MESSAGE, profile.institute, FieldValidator.REASON_TOO_LONG));
        expectedErrorMessages.add(String.format(FieldValidator.NATIONALITY_ERROR_MESSAGE, profile.nationality, FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR));
        expectedErrorMessages.add(String.format(FieldValidator.GENDER_ERROR_MESSAGE, profile.gender));
        return expectedErrorMessages;
    }
    
    private StudentProfileAttributes getInvalidStudentProfileAttributes() {
        
        String googleId = StringHelper.generateStringOfLength(46);
        String shortName = "%%";
        String email = "invalid@email@com";
        String institute = StringHelper.generateStringOfLength(FieldValidator.INSTITUTE_NAME_MAX_LENGTH+1);
        String nationality = "$invalid nationality ";
        String gender = "invalidGender";
        String moreInfo = "Ooops no validation for this one...";
        String pictureKey = "";
        
        return new StudentProfileAttributes(googleId, shortName, email, institute, 
                nationality, gender, moreInfo, pictureKey);
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
        
        return new StudentProfileAttributes(googleId, shortName, email, institute, 
                nationality, gender, moreInfo, pictureKey);
    }

}
