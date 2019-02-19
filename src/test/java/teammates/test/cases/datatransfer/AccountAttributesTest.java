package teammates.test.cases.datatransfer;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
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

    @Test
    public void testGetInvalidStateInfo() throws Exception {
        ______TS("valid account");

        AccountAttributes account = createValidAccountAttributesObject();
        assertTrue("all valid values", account.isValid());

        ______TS("invalid account");

        account = createInvalidAccountAttributesObject();
        String expectedError =
                getPopulatedEmptyStringErrorMessage(
                    FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                    FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedErrorMessage(
                      FieldValidator.GOOGLE_ID_ERROR_MESSAGE, "invalid google id",
                      FieldValidator.GOOGLE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                      FieldValidator.GOOGLE_ID_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedErrorMessage(
                      FieldValidator.EMAIL_ERROR_MESSAGE, "invalid@email@com",
                      FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                      FieldValidator.EMAIL_MAX_LENGTH) + System.lineSeparator()
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
        Account expectedAccount = new Account(account.googleId, account.name,
                account.isInstructor, account.email, account.institute);

        Account actualAccount = account.toEntity();

        assertEquals(expectedAccount.getGoogleId(), actualAccount.getGoogleId());
        assertEquals(expectedAccount.getName(), actualAccount.getName());
        assertEquals(expectedAccount.getEmail(), actualAccount.getEmail());
        assertEquals(expectedAccount.getInstitute(), actualAccount.getInstitute());
        assertEquals(expectedAccount.isInstructor(), actualAccount.isInstructor());
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

        assertEquals(SanitizationHelper.sanitizeGoogleId(expectedAccount.googleId), actualAccount.googleId);
        assertEquals(SanitizationHelper.sanitizeName(expectedAccount.name), actualAccount.name);
        assertEquals(SanitizationHelper.sanitizeEmail(expectedAccount.email), actualAccount.email);
        assertEquals(SanitizationHelper.sanitizeTitle(expectedAccount.institute), actualAccount.institute);
    }

    @Test
    public void testBuilderWithDefaultValues() {
        AccountAttributes observedAccountAttributes = AccountAttributes.builder().build();

        assertNull(observedAccountAttributes.createdAt);
        assertNull(observedAccountAttributes.getEmail());
        assertNull(observedAccountAttributes.getGoogleId());
        assertNull(observedAccountAttributes.getInstitute());
        assertFalse(observedAccountAttributes.isInstructor());
        assertNull(observedAccountAttributes.getName());
    }

    @Test
    public void testBuilderWithPopulatedFieldValues() {
        String expectedGoogleId = "dummyGoogleId";
        String expectedEmail = "email@example.com";
        String expectedName = "dummyName";
        String expectedInstitute = "dummyInstitute";
        boolean expectedIsInstructor = true; //since false case is covered in default test
        Instant expectedCreatedAt = Instant.ofEpochMilli(98765);

        AccountAttributes observedAccountAttributes = AccountAttributes.builder()
                .withGoogleId(expectedGoogleId)
                .withEmail(expectedEmail)
                .withName(expectedName)
                .withInstitute(expectedInstitute)
                .withIsInstructor(expectedIsInstructor)
                .withCreatedAt(expectedCreatedAt)
                .build();

        assertEquals(expectedGoogleId, observedAccountAttributes.getGoogleId());
        assertEquals(expectedEmail, observedAccountAttributes.getEmail());
        assertEquals(expectedCreatedAt, observedAccountAttributes.createdAt);
        assertEquals(expectedInstitute, observedAccountAttributes.getInstitute());
        assertEquals(expectedIsInstructor, observedAccountAttributes.isInstructor());
        assertEquals(expectedName, observedAccountAttributes.getName());
    }

    @Test
    public void testBuilderWithUnsanitisedFieldValues() {
        AccountAttributes observedAccountAttributes = AccountAttributes.builder()
                .withGoogleId("googleId@gmail.com")
                .withName("  random  name with   extra spaces    ")
                .withEmail("         email@example.com ")
                .withInstitute("    random  institute name      with extra    spaces  ")
                .build();

        assertEquals("googleId", observedAccountAttributes.getGoogleId());
        assertEquals("random name with extra spaces", observedAccountAttributes.getName());
        assertEquals("email@example.com", observedAccountAttributes.getEmail());
        assertEquals("random institute name with extra spaces", observedAccountAttributes.getInstitute());
    }

    @Test
    public void testValueOf() {
        Account genericAccount = new Account("id", "Joe", true, "joe@example.com", "Teammates Institute");

        AccountAttributes observedAccountAttributes = AccountAttributes.valueOf(genericAccount);

        assertEquals(genericAccount.getGoogleId(), observedAccountAttributes.getGoogleId());
        assertEquals(genericAccount.getName(), observedAccountAttributes.getName());
        assertEquals(genericAccount.isInstructor(), observedAccountAttributes.isInstructor());
        assertEquals(genericAccount.getEmail(), observedAccountAttributes.getEmail());
        assertEquals(genericAccount.getInstitute(), observedAccountAttributes.getInstitute());
        assertEquals(genericAccount.getCreatedAt(), observedAccountAttributes.createdAt);
    }

    @Test
    public void testGetBackUpIdentifier() {
        AccountAttributes account = createValidAccountAttributesObject();

        String expectedBackUpIdentifierMessage = "Recently modified account::" + account.googleId;
        assertEquals(expectedBackUpIdentifierMessage, account.getBackupIdentifier());
    }

    @Test
    public void getCopy_typicalData_createsCopyCorrectly() {
        AccountAttributes account = createValidAccountAttributesObject();

        AccountAttributes copy = account.getCopy();

        assertNotSame(account, copy);
        assertFalse(account.isInstructor);

        assertEquals(account.googleId, copy.googleId);
        assertEquals(account.name, copy.name);
        assertEquals(account.institute, copy.institute);
        assertEquals(account.email, copy.email);
    }

    @Test
    public void getCopy_allFieldsNull_createsCopyCorrectly() {
        AccountAttributes account = AccountAttributes.builder()
                .withGoogleId(null)
                .withName(null)
                .withEmail(null)
                .withInstitute(null)
                .withIsInstructor(false)
                .build();

        AccountAttributes copy = account.getCopy();

        assertNotSame(account, copy);
        assertFalse(account.isInstructor);

        assertNull("google id should be null", copy.googleId);
        assertNull("name should be null", copy.name);
        assertNull("institute should be null", copy.institute);
        assertNull("email should be null", copy.email);
    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributeCorrectly() {
        AccountAttributes.UpdateOptions updateOptions =
                AccountAttributes.updateOptionsBuilder("testGoogleId")
                        .withIsInstructor(true)
                        .build();

        assertEquals("testGoogleId", updateOptions.getGoogleId());

        AccountAttributes accountAttributes = AccountAttributes.builder().withIsInstructor(false).build();

        accountAttributes.update(updateOptions);

        assertTrue(accountAttributes.isInstructor());
    }

    @Test
    public void testUpdateOptionsBuilder_withNullInput_shouldFailWithAssertionError() {
        assertThrows(AssertionError.class, () ->
                AccountAttributes.updateOptionsBuilder(null));
    }

    private AccountAttributes createInvalidAccountAttributesObject() {

        String googleId = "invalid google id";
        String name = ""; //invalid name
        boolean isInstructor = false;
        String email = "invalid@email@com";
        String institute = StringHelperExtension.generateStringOfLength(FieldValidator.INSTITUTE_NAME_MAX_LENGTH + 1);

        return AccountAttributes.builder()
                .withGoogleId(googleId)
                .withName(name)
                .withEmail(email)
                .withInstitute(institute)
                .withIsInstructor(isInstructor)
                .build();
    }

    private AccountAttributes createValidAccountAttributesObject() {

        String googleId = "valid.google.id";
        String name = "valid name";
        boolean isInstructor = false;
        String email = "valid@email.com";
        String institute = "valid institute name";

        return AccountAttributes.builder()
                .withGoogleId(googleId)
                .withName(name)
                .withEmail(email)
                .withInstitute(institute)
                .withIsInstructor(isInstructor)
                .build();
    }

    private AccountAttributes createAccountAttributesToSanitize() {
        AccountAttributes unsanitizedAttributes = AccountAttributes.builder().build();
        unsanitizedAttributes.googleId = "    google'Id@gmail.com\t";
        unsanitizedAttributes.name = "'n    \t\t    a me'\n\n";
        unsanitizedAttributes.institute = "Some\t  \\       institute   \n/";
        unsanitizedAttributes.email = "   <my&email>@gmail.com\n";
        unsanitizedAttributes.isInstructor = true;

        return unsanitizedAttributes;
    }

}
