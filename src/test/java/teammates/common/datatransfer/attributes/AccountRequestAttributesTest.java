package teammates.common.datatransfer.attributes;

import org.testng.annotations.Test;

import teammates.common.util.Const;
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
        AccountRequest accountRequest =
                new AccountRequest("valid@test.com", "Valid Name", "Valid Institute");

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes.valueOf(accountRequest);

        assertNotNull(accountRequestAttributes.getRegistrationKey());
        assertEquals("Valid Name", accountRequestAttributes.getName());
        assertEquals("valid@test.com", accountRequestAttributes.getEmail());
        assertEquals("Valid Institute", accountRequestAttributes.getInstitute());
        assertNull(accountRequestAttributes.getRegisteredAt());
    }

    @Test
    public void testValueOf_registeredWithTypicalData_shouldGenerateAttributesCorrectly() {
        AccountRequest accountRequest =
                new AccountRequest("valid@test.com", "Valid Name", "Valid Institute");
        accountRequest.setRegisteredAt(Const.TIME_REPRESENTS_NOW);

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes.valueOf(accountRequest);

        assertEquals(Const.TIME_REPRESENTS_NOW, accountRequestAttributes.getRegisteredAt());
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildCorrectAttributes() {
        String validEmail = "valid@test.com";
        String validInstitute = "validInstitute";
        String validName = "valid name";

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes
                .builder(validEmail, validInstitute, validName)
                .withRegisteredAt(Const.TIME_REPRESENTS_NOW)
                .build();

        assertEquals(Const.TIME_REPRESENTS_NOW, accountRequestAttributes.getRegisteredAt());
        assertEquals(validEmail, accountRequestAttributes.getEmail());
        assertEquals(validInstitute, accountRequestAttributes.getInstitute());
        assertEquals(validName, accountRequestAttributes.getName());
    }

    @Test
    public void testBuilder_buildNothing_shouldUseDefaultValues() {
        AccountRequestAttributes accountRequestAttributes =
                AccountRequestAttributes.builder("valid@test.com", "valid institute", "valid name").build();

        assertEquals("valid@test.com", accountRequestAttributes.getEmail());
        assertEquals("valid institute", accountRequestAttributes.getInstitute());
        assertEquals("valid name", accountRequestAttributes.getName());
        assertNull(accountRequestAttributes.getRegistrationKey());
        assertNull(accountRequestAttributes.getRegisteredAt());
    }

    @Test
    public void testBuilder_withNullArguments_shouldThrowException() {
        assertThrows(AssertionError.class, () -> {
            AccountRequestAttributes
                    .builder(null, null, null)
                    .build();
        });
    }

    @Test
    public void testValidate() throws Exception {
        AccountRequestAttributes validAccountRequest = getValidAccountRequestAttributesObject();

        assertTrue("valid value", validAccountRequest.isValid());

        String invalidEmail = "invalid-email";
        String emptyName = "";
        AccountRequestAttributes invalidAccountRequest = AccountRequestAttributes
                .builder(invalidEmail, "institute", emptyName)
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
        AccountRequestAttributes a = getValidAccountRequestAttributesObject();
        assertEquals("[AccountRequestAttributes] email: valid@test.com "
                + "name: valid-name institute: valid-institute", a.toString());
    }

    @Test
    public void testEquals() {
        AccountRequestAttributes accountRequest = getValidAccountRequestAttributesObject();

        // When the two account requests have same values
        AccountRequestAttributes similarAccountRequest = getValidAccountRequestAttributesObject();

        assertTrue(accountRequest.equals(similarAccountRequest));

        // When the two account requests are different
        AccountRequestAttributes differentAccountRequest =
                AccountRequestAttributes.builder("test@test.com", "test-institute", "Another Name").build();

        assertFalse(accountRequest.equals(differentAccountRequest));

        // When the other object is of different class
        assertFalse(accountRequest.equals(3));
    }

    @Test
    public void testHashCode() {
        AccountRequestAttributes accountRequest = getValidAccountRequestAttributesObject();

        // When the two account requests have same values, they should have the same hash code
        AccountRequestAttributes accountRequestSimilar = getValidAccountRequestAttributesObject();

        assertTrue(accountRequest.hashCode() == accountRequestSimilar.hashCode());

        // When the two account requests are different, they should have different hash code
        AccountRequestAttributes accountRequestDifferent =
                AccountRequestAttributes.builder("test@test.com", "test-institute", "Another Name").build();

        assertFalse(accountRequest.hashCode() == accountRequestDifferent.hashCode());
    }

    private static AccountRequestAttributes getValidAccountRequestAttributesObject() {
        AccountRequest accountRequest = new AccountRequest("valid@test.com", "valid-name", "valid-institute");
        accountRequest.setRegistrationKey("valid123");
        return AccountRequestAttributes.valueOf(accountRequest);
    }

}
