package teammates.test.cases.datatransfer;

import static teammates.common.datatransfer.attributes.AccountAttributes.AccountAttributesBuilder;
import static teammates.common.datatransfer.attributes.AccountAttributes.valueOf;
import static teammates.common.util.Const.EOL;

import java.util.Date;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Account;
import teammates.test.driver.StringHelperExtension;

/**
 * SUT: {@link AccountAttributes}.
 */
public class AccountAttributesTest extends BaseAttributesTest {

    //TODO: test toString() method

    private static final Date DEFAULT_DATE = AccountAttributes.DEFAULT_DATE;
    private static final StudentProfileAttributes DEFAULT_STUDENT_PROFILE_ATTRIBUTES =
            AccountAttributes.DEFAULT_STUDENT_PROFILE_ATTRIBUTES;

    private static final String VALID_GOOGLE_ID = "valid.google.id";
    private static final String VALID_NAME = "valid name";
    private static final String VALID_EMAIL = "valid@email.com";
    private static final String VALID_INSTITUTE = "valid institute";

    @Test
    public void testBuilderWithRequiredValues() {

        AccountAttributes accountAttributes = new AccountAttributesBuilder(
                VALID_GOOGLE_ID, VALID_NAME, VALID_EMAIL, VALID_INSTITUTE)
                .build();

        assertEquals(VALID_GOOGLE_ID, accountAttributes.googleId);
        assertEquals(VALID_NAME, accountAttributes.name);
        assertEquals(VALID_EMAIL, accountAttributes.email);
        assertEquals(VALID_INSTITUTE, accountAttributes.institute);

        assertEquals(VALID_GOOGLE_ID, accountAttributes.studentProfile.googleId);

    }

    @Test
    public void testBuilderWithDefaultOptionalValues() {

        AccountAttributes accountAttributes = new AccountAttributesBuilder(
                VALID_GOOGLE_ID, VALID_NAME, VALID_EMAIL, VALID_INSTITUTE)
                .build();

        assertEquals(DEFAULT_DATE, accountAttributes.createdAt);
        assertEquals(DEFAULT_STUDENT_PROFILE_ATTRIBUTES, accountAttributes.studentProfile);
        assertTrue(accountAttributes.isInstructor);

        assertEquals(accountAttributes.googleId, accountAttributes.studentProfile.googleId);
    }

    @Test
    public void testBuilderWithNullArguments() {
        AccountAttributes accountAttributesWithNullValues = new AccountAttributesBuilder(
                null, null, null, null)
                .withIsInstructor(null)
                .withStudentProfileAttributes(null)
                .withCreatedAt(null)
                .build();
        // No default values for required params
        assertNull(accountAttributesWithNullValues.googleId);
        assertNull(accountAttributesWithNullValues.name);
        assertNull(accountAttributesWithNullValues.email);
        assertNull(accountAttributesWithNullValues.institute);

        // Check default values for optional params
        assertEquals(DEFAULT_DATE, accountAttributesWithNullValues.createdAt);
        assertEquals(DEFAULT_STUDENT_PROFILE_ATTRIBUTES, accountAttributesWithNullValues.studentProfile);
        assertTrue(accountAttributesWithNullValues.isInstructor);
    }

    @Test
    public void testBuilderCopy() {
        AccountAttributes account = new AccountAttributesBuilder(
                VALID_GOOGLE_ID, VALID_NAME, VALID_EMAIL, VALID_INSTITUTE)
                .build();

        AccountAttributes accountCopy = new AccountAttributesBuilder(
                account.googleId, account.name, account.email, account.institute)
                .build();

        assertEquals(account.googleId, accountCopy.googleId);
        assertEquals(account.name, accountCopy.name);
        assertEquals(account.email, accountCopy.email);
        assertEquals(account.institute, accountCopy.institute);
        assertEquals(account.createdAt, accountCopy.createdAt);
        assertEquals(account.studentProfile, accountCopy.studentProfile);
        assertEquals(account.isInstructor, accountCopy.isInstructor);
        assertEquals(account.studentProfile.googleId, accountCopy.studentProfile.googleId);
    }

    @Test
    public void testGetInvalidStateInfo() throws Exception {
        ______TS("valid account");

        AccountAttributes account = createValidAccountAttributesObject();
        assertTrue("all valid values", account.isValid());

        ______TS("null studentProfile");

        account.studentProfile = null;
        try {
            account.isValid();
            signalFailureToDetectException(" - AssertionError");
        } catch (AssertionError ae) {
            assertEquals("Non-null value expected for studentProfile", ae.getMessage());
        }

        ______TS("invalid account");

        account = createInvalidAccountAttributesObject();
        String expectedError =
                getPopulatedErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, "",
                        FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_EMPTY,
                        FieldValidator.PERSON_NAME_MAX_LENGTH) + EOL
                        + getPopulatedErrorMessage(
                        FieldValidator.GOOGLE_ID_ERROR_MESSAGE, "invalid google id",
                        FieldValidator.GOOGLE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.GOOGLE_ID_MAX_LENGTH) + EOL
                        + getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, "invalid@email@com",
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH) + EOL
                        + getPopulatedErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        FieldValidator.INSTITUTE_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                        FieldValidator.INSTITUTE_NAME_MAX_LENGTH);
        assertFalse("all valid values", account.isValid());
        assertEquals("all valid values", expectedError, StringHelper.toString(account.getInvalidityInfo()));

    }

    @Test
    public void testGetEntityTypeAsString() {
        AccountAttributes account = createValidAccountAttributesObject();
        assertEquals("Account", account.getEntityTypeAsString());
    }

    @Override
    @Test
    public void testToEntity() {
        AccountAttributes account = createValidAccountAttributesObject();
        Account expectedAccount =
                new Account(account.googleId, account.name, account.isInstructor, account.email,
                        account.institute, new StudentProfileAttributes().toEntity());
        Account actualAccount = valueOf(expectedAccount).toEntity();

        assertEquals(expectedAccount.getGoogleId(), actualAccount.getGoogleId());
        assertEquals(expectedAccount.getName(), actualAccount.getName());
        assertEquals(expectedAccount.getEmail(), actualAccount.getEmail());
        assertEquals(expectedAccount.getInstitute(), actualAccount.getInstitute());
        assertEquals(expectedAccount.isInstructor(), actualAccount.isInstructor());
        String expectedProfile = new StudentProfileAttributes(expectedAccount.getStudentProfile()).toString();
        String actualProfile = new StudentProfileAttributes(actualAccount.getStudentProfile()).toString();
        assertEquals(expectedProfile, actualProfile);
    }

    @Test
    public void testToString() {
        AccountAttributes account = createValidAccountAttributesObject();
        AccountAttributes account1 = createValidAccountAttributesObject();
        AccountAttributes account2 = createInvalidAccountAttributesObject();

        assertEquals(account.toString(), account1.toString());
        assertFalse("different accounts have different toString() values",
                account1.toString().equals(account2.toString()));
    }

    @Test
    public void testGetIdentificationString() {
        AccountAttributes account = createValidAccountAttributesObject();
        assertEquals(account.googleId, account.getIdentificationString());
    }

    @Test
    public void testSanitizeForSaving() {
        AccountAttributes actualAccount = createAccountAttributesToSanitize();
        AccountAttributes expectedAccount = createAccountAttributesToSanitize();
        actualAccount.sanitizeForSaving();

        assertEquals(SanitizationHelper.sanitizeForHtml(expectedAccount.googleId), actualAccount.googleId);
        assertEquals(SanitizationHelper.sanitizeForHtml(expectedAccount.name), actualAccount.name);
        assertEquals(SanitizationHelper.sanitizeForHtml(expectedAccount.email), actualAccount.email);
        assertEquals(SanitizationHelper.sanitizeForHtml(expectedAccount.institute), actualAccount.institute);
        expectedAccount.studentProfile.sanitizeForSaving();
        assertEquals(expectedAccount.studentProfile.toString(), actualAccount.studentProfile.toString());
    }

    @Test
    public void testLegacyAccountEntityToAttributes() {
        Account a = new Account("test.googleId", "name", true, "email@e.com", "institute");
        a.setStudentProfile(null);

        AccountAttributes attr = valueOf(a);

        assertEquals(a.getGoogleId(), attr.googleId);
        assertEquals(a.getEmail(), attr.email);
        assertEquals(a.getInstitute(), attr.institute);
        assertEquals(a.getName(), attr.name);
        assertEquals(null, a.getStudentProfile());
        assertEquals(DEFAULT_STUDENT_PROFILE_ATTRIBUTES, attr.studentProfile);

    }

    private AccountAttributes createInvalidAccountAttributesObject() {
        String googleId = "invalid google id";
        String name = ""; //invalid name
        boolean isInstructor = false;
        String email = "invalid@email@com";
        String institute = StringHelperExtension.generateStringOfLength(FieldValidator.INSTITUTE_NAME_MAX_LENGTH + 1);
        StudentProfileAttributes studentProfile = new StudentProfileAttributes();

        return new AccountAttributesBuilder(googleId, name, email, institute)
                .withIsInstructor(isInstructor)
                .withStudentProfileAttributes(studentProfile)
                .build();
    }

    private AccountAttributes createValidAccountAttributesObject() {
        return new AccountAttributesBuilder(VALID_GOOGLE_ID, VALID_NAME, VALID_EMAIL, VALID_INSTITUTE)
                .withIsInstructor(false)
                .build();
    }

    private AccountAttributes createAccountAttributesToSanitize() {
        AccountAttributes account = new AccountAttributes();
        account.googleId = "googleId@gmail.com";
        account.name = "'name'";
        account.institute = "\\/";
        account.email = "&<email>&";
        account.isInstructor = true;

        String shortName = "<name>";
        String personalEmail = "'toSanitize@email.com'";
        String profileInstitute = "";
        String nationality = "&\"invalid nationality &";
        String gender = "'\"'other";
        String moreInfo = "<<script> alert('hi!'); </script>";
        String pictureKey = "";

        account.studentProfile = new StudentProfileAttributes(account.googleId, shortName, personalEmail,
                profileInstitute, nationality, gender, moreInfo, pictureKey);

        return new AccountAttributesBuilder(
                account.googleId, account.name, account.email, account.institute)
                .withIsInstructor(account.isInstructor())
                .withStudentProfileAttributes(account.studentProfile)
                .build();
    }

}
