package teammates.common.datatransfer.attributes;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.storage.entity.AccountRequest;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountRequestAttributes}.
 */
public class AccountRequestAttributesTest extends BaseTestCase {

    @Test
    public void testValueOf_withTypicalData_shouldGenerateAttributesCorrectly() {
        Instant typicalInstant = Instant.now();
        AccountRequest accountRequest =
                new AccountRequest("valid@test.com", "registrationkey", "Valid Name",
                         "Valid Institute", typicalInstant, typicalInstant);

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes.valueOf(accountRequest);

        assertEquals("registrationkey", accountRequestAttributes.getRegistrationKey());
        assertEquals("Valid Name", accountRequestAttributes.getName());
        assertEquals("valid@test.com", accountRequestAttributes.getEmail());
        assertEquals("Valid Institute", accountRequestAttributes.getInstitute());
        assertEquals(typicalInstant, accountRequestAttributes.getCreatedAt());
        assertEquals(typicalInstant, accountRequestAttributes.getDeletedAt());
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildCorrectAttributes() {
        String validName = "validName";
        String validEmail = "valid@test.com";
        String validInstitute = "validInstitute";
        String validRegKey = "validRegKey123";

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes
                .builder(validEmail)
                .withName(validName)
                .withInstitute(validInstitute)
                .withRegistrationKey(validRegKey)
                .build();

        assertNotNull(accountRequestAttributes.getCreatedAt());
        assertNull(accountRequestAttributes.getDeletedAt());
        assertEquals(validEmail, accountRequestAttributes.getEmail());
        assertEquals(validName, accountRequestAttributes.getName());
        assertEquals(validRegKey, accountRequestAttributes.getRegistrationKey());
        assertEquals(validInstitute, accountRequestAttributes.getInstitute());
    }

    @Test
    public void testBuilder_buildNothing_shouldUseDefaultValues() {
        AccountRequestAttributes accountRequestAttributes =
                AccountRequestAttributes.builder("valid@test.com").build();

        assertEquals("valid@test.com", accountRequestAttributes.getEmail());
        assertNull(accountRequestAttributes.getName());
        assertNull(accountRequestAttributes.getInstitute());
        assertNull(accountRequestAttributes.getRegistrationKey());
        assertNotNull(accountRequestAttributes.getCreatedAt());
        assertNull(accountRequestAttributes.getDeletedAt());
    }

    @Test
    public void testBuilder_withNullArguments_shouldThrowException() {
        assertThrows(AssertionError.class, () -> {
            AccountRequestAttributes
                    .builder(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            AccountRequestAttributes
                    .builder("valid@test.com")
                    .withName(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            AccountRequestAttributes
                    .builder("valid@test.com")
                    .withRegistrationKey(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            AccountRequestAttributes
                    .builder("valid@test.com")
                    .withInstitute(null)
                    .build();
        });
    }

    @Test
    public void testValidate() throws Exception {
        AccountRequestAttributes validAccountRequest = generateValidAccountRequestAttributesObject();

        assertTrue("valid value", validAccountRequest.isValid());

        String invalidEmail = "invalid-email";
        String emptyName = "";
        AccountRequestAttributes invalidAccountRequest = AccountRequestAttributes
                .builder(invalidEmail)
                .withName(emptyName)
                .withInstitute("institute")
                .build();

        assertFalse("invalid value", invalidAccountRequest.isValid());
        String errorMessage =
                getPopulatedErrorMessage(
                    FieldValidator.EMAIL_ERROR_MESSAGE, invalidAccountRequest.getEmail(),
                    FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                    FieldValidator.EMAIL_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedEmptyStringErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                      FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH);
        assertEquals("invalid value", errorMessage, StringHelper.toString(invalidAccountRequest.getInvalidityInfo()));
    }

    @Test
    public void testGetValidityInfo() {
        // already tested in testValidate() above
    }

    @Test
    public void testIsValid() {
        // already tested in testValidate() above
    }

    @Test
    public void testToString() {
        AccountRequestAttributes a = generateValidAccountRequestAttributesObject();
        assertEquals("[AccountRequestAttributes] email: valid@test.comregistrationKey: "
                + "valid123 name: valid-name institute: valid-institute", a.toString());
    }

    @Test
    public void testEquals() {
        AccountRequestAttributes accountRequest = generateValidAccountRequestAttributesObject();

        // When the two account requests have same values
        AccountRequestAttributes similarAccountRequest = generateValidAccountRequestAttributesObject();

        assertTrue(accountRequest.equals(similarAccountRequest));

        // When the two account requests are different
        AccountRequestAttributes differentAccountRequest = AccountRequestAttributes.builder("test@test.com")
                .withName("Another Name")
                .build();

        assertFalse(accountRequest.equals(differentAccountRequest));

        // When the other object is of different class
        assertFalse(accountRequest.equals(3));
    }

    @Test
    public void testHashCode() {
        AccountRequestAttributes accountRequest = generateValidAccountRequestAttributesObject();

        // When the two account requests have same values, they should have the same hash code
        AccountRequestAttributes accountRequestSimilar = generateValidAccountRequestAttributesObject();

        assertTrue(accountRequest.hashCode() == accountRequestSimilar.hashCode());

        // When the two account requests are different, they should have different hash code
        AccountRequestAttributes accountRequestDifferent = AccountRequestAttributes.builder("test@test.com")
                .withName("Another Name")
                .build();

        assertFalse(accountRequest.hashCode() == accountRequestDifferent.hashCode());
    }

    private static AccountRequestAttributes generateValidAccountRequestAttributesObject() {
        return AccountRequestAttributes.builder("valid@test.com")
                .withName("valid-name")
                .withRegistrationKey("valid123")
                .withInstitute("valid-institute")
                .build();
    }

}
