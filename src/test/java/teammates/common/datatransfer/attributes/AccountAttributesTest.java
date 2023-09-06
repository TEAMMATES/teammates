package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
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
                      FieldValidator.EMAIL_MAX_LENGTH);
        assertFalse("all valid values", account.isValid());
        assertEquals("all valid values", expectedError, StringHelper.toString(account.getInvalidityInfo()));

    }

    @Override
    @Test
    public void testToEntity() {
        AccountAttributes account = createValidAccountAttributesObject();
        Account expectedAccount = new Account(account.getGoogleId(), account.getName(),
                account.getEmail(), account.getDescription(), account.getReadNotifications());

        Account actualAccount = account.toEntity();

        assertEquals(expectedAccount.getGoogleId(), actualAccount.getGoogleId());
        assertEquals(expectedAccount.getName(), actualAccount.getName());
        assertEquals(expectedAccount.getEmail(), actualAccount.getEmail());
        assertEquals(expectedAccount.getDescription(), actualAccount.getDescription());
        assertEquals(expectedAccount.getReadNotifications(), actualAccount.getReadNotifications());
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

        assertEquals(SanitizationHelper.sanitizeGoogleId(expectedAccount.getGoogleId()), actualAccount.getGoogleId());
        assertEquals(SanitizationHelper.sanitizeName(expectedAccount.getName()), actualAccount.getName());
        assertEquals(SanitizationHelper.sanitizeEmail(expectedAccount.getEmail()), actualAccount.getEmail());
    }

    @Test
    public void testBuilder_buildNothing_shouldUseDefaultValues() {
        AccountAttributes observedAccountAttributes = AccountAttributes.builder("id").build();

        assertEquals("id", observedAccountAttributes.getGoogleId());
        assertEquals(new HashMap<>(), observedAccountAttributes.getReadNotifications());

        assertNull(observedAccountAttributes.getCreatedAt());
        assertNull(observedAccountAttributes.getEmail());
        assertNull(observedAccountAttributes.getName());
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildCorrectAttributes() {
        String expectedGoogleId = "dummyGoogleId";
        String expectedEmail = "email@example.com";
        String expectedName = "dummyName";

        AccountAttributes observedAccountAttributes = AccountAttributes.builder(expectedGoogleId)
                .withEmail(expectedEmail)
                .withName(expectedName)
                .build();

        assertEquals(expectedGoogleId, observedAccountAttributes.getGoogleId());
        assertEquals(expectedEmail, observedAccountAttributes.getEmail());
        assertNull(observedAccountAttributes.getCreatedAt());
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
    }

    @Test
    public void testValueOf() {
        Account genericAccount = new Account("id", "Joe", "joe@example.com", "A software engineering student.", new HashMap<>());

        AccountAttributes observedAccountAttributes = AccountAttributes.valueOf(genericAccount);

        assertEquals(genericAccount.getGoogleId(), observedAccountAttributes.getGoogleId());
        assertEquals(genericAccount.getName(), observedAccountAttributes.getName());
        assertEquals(genericAccount.getEmail(), observedAccountAttributes.getEmail());
        assertEquals(genericAccount.getDescription(), observedAccountAttributes.getDescription());
        assertEquals(genericAccount.getCreatedAt(), observedAccountAttributes.getCreatedAt());
    }

    @Test
    public void testGetCopy_typicalData_createsCopyCorrectly() {
        AccountAttributes account = createValidAccountAttributesObject();

        AccountAttributes copy = account.getCopy();

        assertNotSame(account, copy);

        assertEquals(account.getGoogleId(), copy.getGoogleId());
        assertEquals(account.getName(), copy.getName());
        assertEquals(account.getEmail(), copy.getEmail());
    }

    @Test
    public void testGetCopy_allFieldsNull_createsCopyCorrectly() {
        AccountAttributes account = AccountAttributes.builder("id").build();

        AccountAttributes copy = account.getCopy();

        assertNotSame(account, copy);

        assertEquals("id", copy.getGoogleId());
        assertNull("name should be null", copy.getName());
        assertNull("email should be null", copy.getEmail());
        assertNull("email should be null", copy.getCreatedAt());
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
        String email = "invalid@email@com";

        return AccountAttributes.builder(googleId)
                .withName(name)
                .withEmail(email)
                .build();
    }

    private AccountAttributes createValidAccountAttributesObject() {

        String googleId = "valid.google.id";
        String name = "valid name";
        String email = "valid@email.com";

        Map<String, Instant> readNotifications = new HashMap<>();
        readNotifications.put("1", Instant.now());

        return AccountAttributes.builder(googleId)
                .withName(name)
                .withEmail(email)
                .withReadNotifications(readNotifications)
                .build();
    }

    private AccountAttributes createAccountAttributesToSanitize() {
        return AccountAttributes.builder("    google'Id@gmail.com\t")
                        .withName("'n    \t\t    a me'\n\n")
                        .withEmail("   <my&email>@gmail.com\n")
                        .build();
    }

}
