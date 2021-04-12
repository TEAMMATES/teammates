package teammates.common.datatransfer.attributes;

import org.testng.annotations.Test;

import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.StringHelperExtension;
import teammates.storage.entity.Account;

/**
 * SUT: {@link AccountAttributes}.
 */
public class AccountAttributesTest extends BaseAttributesTest {

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
    public void testBuilder_buildNothing_shouldUseDefaultValues() {
        AccountAttributes observedAccountAttributes = AccountAttributes.builder("id").build();

        assertEquals("id", observedAccountAttributes.getGoogleId());

        assertNull(observedAccountAttributes.getCreatedAt());
        assertNull(observedAccountAttributes.getEmail());
        assertNull(observedAccountAttributes.getInstitute());
        assertFalse(observedAccountAttributes.isInstructor());
        assertNull(observedAccountAttributes.getName());
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildCorrectAttributes() {
        String expectedGoogleId = "dummyGoogleId";
        String expectedEmail = "email@example.com";
        String expectedName = "dummyName";
        String expectedInstitute = "dummyInstitute";

        AccountAttributes observedAccountAttributes = AccountAttributes.builder(expectedGoogleId)
                .withEmail(expectedEmail)
                .withName(expectedName)
                .withInstitute(expectedInstitute)
                .withIsInstructor(true)
                .build();

        assertEquals(expectedGoogleId, observedAccountAttributes.getGoogleId());
        assertEquals(expectedEmail, observedAccountAttributes.getEmail());
        assertNull(observedAccountAttributes.getCreatedAt());
        assertEquals(expectedInstitute, observedAccountAttributes.getInstitute());
        assertTrue(observedAccountAttributes.isInstructor());
        assertEquals(expectedName, observedAccountAttributes.getName());
    }

    @Test
    public void testBuilder_withNullArguments_shouldThrowException() {
        assertThrows(AssertionError.class, () -> {
            AccountAttributes
                    .builder(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            AccountAttributes
                    .builder("id")
                    .withName(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            AccountAttributes
                    .builder("id")
                    .withEmail(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            AccountAttributes
                    .builder("id")
                    .withInstitute(null)
                    .build();
        });
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
    public void testGetCopy_typicalData_createsCopyCorrectly() {
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
    public void testGetCopy_allFieldsNull_createsCopyCorrectly() {
        AccountAttributes account = AccountAttributes.builder("id").build();

        AccountAttributes copy = account.getCopy();

        assertNotSame(account, copy);
        assertFalse(account.isInstructor());

        assertEquals("id", copy.getGoogleId());
        assertNull("name should be null", copy.getName());
        assertNull("institute should be null", copy.getInstitute());
        assertNull("email should be null", copy.getEmail());
        assertNull("email should be null", copy.getCreatedAt());
    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributeCorrectly() {
        AccountAttributes.UpdateOptions updateOptions =
                AccountAttributes.updateOptionsBuilder("testGoogleId")
                        .withIsInstructor(true)
                        .build();

        assertEquals("testGoogleId", updateOptions.getGoogleId());

        AccountAttributes accountAttributes =
                AccountAttributes.builder("testGoogleId").withIsInstructor(false).build();

        accountAttributes.update(updateOptions);

        assertTrue(accountAttributes.isInstructor());
    }

    @Test
    public void testEquals() {
        AccountAttributes account = createValidAccountAttributesObject();
        AccountAttributes accountCopy = account.getCopy();

        // When the two accounts are exact copy of each other
        assertTrue(account.equals(accountCopy));

        // When the two accounts have same values but created at different time
        AccountAttributes accountSimilar = createValidAccountAttributesObject();

        assertTrue(account.equals(accountSimilar));

        // When the two accounts are different
        AccountAttributes accountDifferent = AccountAttributes.builder("another")
                .withName("Another Name")
                .withEmail("Another Email")
                .withInstitute("Another Institute")
                .withIsInstructor(false)
                .build();

        assertFalse(account.equals(accountDifferent));

        // When the other object is of different class
        assertFalse(account.equals(3));
    }

    @Test
    public void testHashCode() {
        AccountAttributes account = createValidAccountAttributesObject();
        AccountAttributes accountCopy = account.getCopy();
        // When the two accounts are exact copy of each other, they should have the same hash code
        assertTrue(account.hashCode() == accountCopy.hashCode());

        // When the two accounts have same values but created at different time,
        // they should have the same hash code
        AccountAttributes accountSimilar = createValidAccountAttributesObject();
        assertTrue(account.hashCode() == accountSimilar.hashCode());

        // When the two accounts have different values, they should have different hash code
        AccountAttributes accountDifferent = AccountAttributes.builder("another")
                .withName("Another Name")
                .withEmail("Another Email")
                .withInstitute("Another Institute")
                .withIsInstructor(false)
                .build();

        assertFalse(account.hashCode() == accountDifferent.hashCode());
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

        return AccountAttributes.builder(googleId)
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

        return AccountAttributes.builder(googleId)
                .withName(name)
                .withEmail(email)
                .withInstitute(institute)
                .withIsInstructor(isInstructor)
                .build();
    }

    private AccountAttributes createAccountAttributesToSanitize() {
        return AccountAttributes.builder("    google'Id@gmail.com\t")
                        .withName("'n    \t\t    a me'\n\n")
                        .withInstitute("Some\t  \\       institute   \n/")
                        .withEmail("   <my&email>@gmail.com\n")
                        .withIsInstructor(true)
                        .build();
    }

}
