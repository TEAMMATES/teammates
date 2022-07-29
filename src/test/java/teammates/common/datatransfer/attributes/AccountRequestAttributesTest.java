package teammates.common.datatransfer.attributes;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.StringHelperExtension;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.AccountRequest;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountRequestAttributes}.
 */
public class AccountRequestAttributesTest extends BaseTestCase {

    @Test
    public void testValueOf_withTypicalData_shouldGenerateAttributesCorrectly() {
        AccountRequest accountRequest = new AccountRequest("Adam", "TMT, Singapore", "adam@tmt.tmt",
                "https://www.comp.nus.edu.sg/", "Valid comments.");

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes.valueOf(accountRequest);

        verifyNullPureInstituteAndPureCountry(accountRequestAttributes);
        verifyAccountRequestEquals(accountRequestAttributes, accountRequest);
    }

    @Test
    public void testValueOf_registeredWithTypicalData_shouldGenerateAttributesCorrectly() {
        AccountRequest accountRequest = new AccountRequest("Adam", "TMT, Singapore", "adam@tmt.tmt",
                "https://www.comp.nus.edu.sg/", "Valid comments.");
        accountRequest.setStatus(AccountRequestStatus.REGISTERED);
        accountRequest.setLastProcessedAt(Instant.now().minusSeconds(600));
        accountRequest.setRegisteredAt(Instant.now().minusSeconds(300));

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes.valueOf(accountRequest);

        verifyNullPureInstituteAndPureCountry(accountRequestAttributes);
        verifyAccountRequestEquals(accountRequestAttributes, accountRequest);
    }

    @Test
    public void testBuilder_withTypicalDataWithPureInstituteAndPureCountry_shouldBuildCorrectAttributes() {
        String validName = "Adam";
        String validPureInstitute = "TMT";
        String validPureCountry = "Singapore";
        String validEmail = "adam@tmt.tmt";
        String validHomePageUrl = "";
        String validComments = "";

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes
                .builder(validName, validPureInstitute, validPureCountry, validEmail, validHomePageUrl, validComments)
                .withStatus(AccountRequestStatus.SUBMITTED)
                .build();

        assertEquals(validName, accountRequestAttributes.getName());
        assertEquals(validPureInstitute, accountRequestAttributes.getPureInstitute());
        assertEquals(validPureCountry, accountRequestAttributes.getPureCountry());
        assertEquals("TMT, Singapore", accountRequestAttributes.getInstitute());
        assertEquals(validEmail, accountRequestAttributes.getEmail());
        assertEquals(validHomePageUrl, accountRequestAttributes.getHomePageUrl());
        assertEquals(validComments, accountRequestAttributes.getComments());
        assertEquals(AccountRequestStatus.SUBMITTED, accountRequestAttributes.getStatus());
        assertNull(accountRequestAttributes.getCreatedAt());
        assertNull(accountRequestAttributes.getLastProcessedAt());
        assertNull(accountRequestAttributes.getRegisteredAt());
        assertNull(accountRequestAttributes.getRegistrationKey());
    }

    @Test
    public void testBuilder_withTypicalDataWithInstitute_shouldBuildCorrectAttributes() {
        String validName = "Adam";
        String validInstitute = "TMT, Singapore";
        String validEmail = "adam@tmt.tmt";
        String validHomePageUrl = "";
        String validComments = "";
        Instant lastProcessedAt = TimeHelper.parseInstant("2022-07-01T00:10:00Z");
        Instant registeredAt = TimeHelper.parseInstant("2022-07-01T00:12:00Z");

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes
                .builder(validName, validInstitute, validEmail, validHomePageUrl, validComments)
                .withStatus(AccountRequestStatus.REGISTERED)
                .withLastProcessedAt(lastProcessedAt)
                .withRegisteredAt(registeredAt)
                .build();

        assertEquals(validName, accountRequestAttributes.getName());
        assertEquals(validInstitute, accountRequestAttributes.getInstitute());
        assertEquals(validEmail, accountRequestAttributes.getEmail());
        assertEquals(validHomePageUrl, accountRequestAttributes.getHomePageUrl());
        assertEquals(validComments, accountRequestAttributes.getComments());
        assertEquals(AccountRequestStatus.REGISTERED, accountRequestAttributes.getStatus());
        assertEquals(lastProcessedAt, accountRequestAttributes.getLastProcessedAt());
        assertEquals(registeredAt, accountRequestAttributes.getRegisteredAt());
        assertNull(accountRequestAttributes.getPureInstitute());
        assertNull(accountRequestAttributes.getPureCountry());
        assertNull(accountRequestAttributes.getCreatedAt());
        assertNull(accountRequestAttributes.getRegistrationKey());
    }

    @Test
    public void testBuilder_buildNothingWithPureInstituteAndPureCountry_shouldUseDefaultValues() {
        String validName = "Adam";
        String validPureInstitute = "TMT";
        String validPureCountry = "Singapore";
        String validEmail = "adam@tmt.tmt";
        String validHomePageUrl = "";
        String validComments = "";

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes
                .builder(validName, validPureInstitute, validPureCountry, validEmail, validHomePageUrl, validComments)
                .build();

        assertEquals(validName, accountRequestAttributes.getName());
        assertEquals(validPureInstitute, accountRequestAttributes.getPureInstitute());
        assertEquals(validPureCountry, accountRequestAttributes.getPureCountry());
        assertEquals("TMT, Singapore", accountRequestAttributes.getInstitute());
        assertEquals(validEmail, accountRequestAttributes.getEmail());
        assertEquals(validHomePageUrl, accountRequestAttributes.getHomePageUrl());
        assertEquals(validComments, accountRequestAttributes.getComments());
        assertEquals(AccountRequestStatus.SUBMITTED, accountRequestAttributes.getStatus());
        assertNull(accountRequestAttributes.getCreatedAt());
        assertNull(accountRequestAttributes.getLastProcessedAt());
        assertNull(accountRequestAttributes.getRegisteredAt());
        assertNull(accountRequestAttributes.getRegistrationKey());
    }

    @Test
    public void testBuilder_buildNothingWithInstitute_shouldUseDefaultValues() {
        String validName = "Adam";
        String validInstitute = "TMT, Singapore";
        String validEmail = "adam@tmt.tmt";
        String validHomePageUrl = "";
        String validComments = "";

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes
                .builder(validName, validInstitute, validEmail, validHomePageUrl, validComments)
                .build();

        assertEquals(validName, accountRequestAttributes.getName());
        assertEquals(validInstitute, accountRequestAttributes.getInstitute());
        assertEquals(validEmail, accountRequestAttributes.getEmail());
        assertEquals(validHomePageUrl, accountRequestAttributes.getHomePageUrl());
        assertEquals(validComments, accountRequestAttributes.getComments());
        assertEquals(AccountRequestStatus.SUBMITTED, accountRequestAttributes.getStatus());
        assertNull(accountRequestAttributes.getPureInstitute());
        assertNull(accountRequestAttributes.getPureCountry());
        assertNull(accountRequestAttributes.getCreatedAt());
        assertNull(accountRequestAttributes.getLastProcessedAt());
        assertNull(accountRequestAttributes.getRegisteredAt());
        assertNull(accountRequestAttributes.getRegistrationKey());
    }

    @Test
    public void testBuilder_withNullArgumentsWithPureInstituteAndPureCountry_shouldThrowException() {
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder(null, "TMT", "Singapore", "adam@tmt.tmt", "", "").build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", null, "Singapore", "adam@tmt.tmt", "", "").build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT", null, "adam@tmt.tmt", "", "").build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT", "Singapore", null, "", "").build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", null, "").build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", null).build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                        .withName(null)
                        .build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                        .withInstitute(null)
                        .build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                        .withEmail(null)
                        .build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                        .withStatus(null)
                        .build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                        .withLastProcessedAt(null)
                        .build());
    }

    @Test
    public void testBuilder_withNullArgumentsWithInstitute_shouldThrowException() {
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder(null, "TMT, Singapore", "adam@tmt.tmt", "", "").build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", null, "adam@tmt.tmt", "", "").build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT, Singapore", null, "", "").build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT, Singapore", "adam@tmt.tmt", null, "").build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT, Singapore", "adam@tmt.tmt", "", null).build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT, Singapore", "adam@tmt.tmt", "", "")
                        .withName(null)
                        .build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT, Singapore", "adam@tmt.tmt", "", "")
                        .withInstitute(null)
                        .build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT, Singapore", "adam@tmt.tmt", "", "")
                        .withEmail(null)
                        .build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT, Singapore", "adam@tmt.tmt", "", "")
                        .withStatus(null)
                        .build());
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.builder("Adam", "TMT, Singapore", "adam@tmt.tmt", "", "")
                        .withLastProcessedAt(null)
                        .build());
    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributesCorrectly() {
        String email = "adam@tmt.tmt";
        String institute = "TMT, Singapore";
        String editedName = "Adams";
        String editedInstitute = "TEAMMATES TEST, Singapore";
        String editedEmail = "adam@tmt.tmt.nus";
        Instant lastProcessedAt = TimeHelper.parseInstant("2022-07-01T00:10:00Z");
        Instant registeredAt = TimeHelper.parseInstant("2022-07-01T00:12:00Z");

        AccountRequestAttributes.UpdateOptions updateOptions =
                AccountRequestAttributes.updateOptionsBuilder(email, institute)
                        .withName(editedName)
                        .withInstitute(editedInstitute)
                        .withEmail(editedEmail)
                        .withStatus(AccountRequestStatus.REGISTERED)
                        .withLastProcessedAt(lastProcessedAt)
                        .withRegisteredAt(registeredAt)
                        .build();

        assertEquals(email, updateOptions.getEmail());
        assertEquals(institute, updateOptions.getInstitute());

        AccountRequestAttributes accountRequestAttributes = getValidAccountRequestAttributesObject();
        Instant originalCreatedAt = accountRequestAttributes.getCreatedAt();
        String originalHomePageUrl = accountRequestAttributes.getHomePageUrl();
        String originalComments = accountRequestAttributes.getComments();
        String originalRegistrationKey = accountRequestAttributes.getRegistrationKey();

        accountRequestAttributes.update(updateOptions);

        assertEquals(editedName, accountRequestAttributes.getName());
        assertEquals(editedInstitute, accountRequestAttributes.getInstitute());
        assertEquals(editedEmail, accountRequestAttributes.getEmail());
        assertEquals(AccountRequestStatus.REGISTERED, accountRequestAttributes.getStatus());
        assertEquals(lastProcessedAt, accountRequestAttributes.getLastProcessedAt());
        assertEquals(registeredAt, accountRequestAttributes.getRegisteredAt());
        assertEquals(originalHomePageUrl, accountRequestAttributes.getHomePageUrl());
        assertEquals(originalComments, accountRequestAttributes.getComments());
        assertEquals(originalCreatedAt, accountRequestAttributes.getCreatedAt());
        assertEquals(originalRegistrationKey, accountRequestAttributes.getRegistrationKey());
        assertNull(accountRequestAttributes.getPureInstitute());
        assertNull(accountRequestAttributes.getPureCountry());
    }

    @Test
    public void testUpdateOptionsBuilder_withNullInput_shouldFailWithAssertionError() {
        assertThrows(AssertionError.class, () -> AccountRequestAttributes.updateOptionsBuilder(null, "TMT, Singapore"));
        assertThrows(AssertionError.class, () -> AccountRequestAttributes.updateOptionsBuilder("adam@tmt.tmt", null));
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.updateOptionsBuilder("adam@tmt.tmt", "TMT, Singapore")
                        .withName(null));
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.updateOptionsBuilder("adam@tmt.tmt", "TMT, Singapore")
                        .withInstitute(null));
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.updateOptionsBuilder("adam@tmt.tmt", "TMT, Singapore")
                        .withEmail(null));
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.updateOptionsBuilder("adam@tmt.tmt", "TMT, Singapore")
                        .withStatus(null));
        assertThrows(AssertionError.class, () ->
                AccountRequestAttributes.updateOptionsBuilder("adam@tmt.tmt", "TMT, Singapore")
                        .withLastProcessedAt(null));
    }

    @Test
    public void testValidate() throws Exception {
        ______TS("valid account request attributes, null pure institute and null pure country");

        AccountRequestAttributes validAccountRequest = getValidAccountRequestAttributesObject();

        assertTrue(validAccountRequest.isValid());

        ______TS("valid account request attributes, non-null pure institute and pure country");

        validAccountRequest = AccountRequestAttributes
                .builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "valid url", "valid comments")
                .build();

        assertTrue(validAccountRequest.isValid());

        ______TS("invalid account request attributes, null pure institute and null pure country");

        String emptyName = "";
        String invalidEmail = "invalid-email";
        String invalidInstitute = "{TMT, Singapore";
        String longUrl = StringHelperExtension
                .generateRandomAsciiStringOfLength(FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH + 1);
        String longComments = StringHelperExtension
                .generateRandomAsciiStringOfLength(FieldValidator.ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH + 1);
        AccountRequestAttributes invalidAccountRequest = AccountRequestAttributes
                .builder(emptyName, invalidInstitute, invalidEmail, longUrl, longComments)
                .build();

        assertFalse(invalidAccountRequest.isValid());
        String errorMessage = FieldValidator.PERSON_NAME_FIELD_NAME + ": "
                + getPopulatedEmptyStringErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH)
                + System.lineSeparator() + FieldValidator.INSTITUTE_NAME_FIELD_NAME + ": "
                + getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, invalidInstitute,
                FieldValidator.INSTITUTE_NAME_FIELD_NAME, FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR)
                + System.lineSeparator() + FieldValidator.EMAIL_FIELD_NAME + ": "
                + getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, invalidEmail,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT, FieldValidator.EMAIL_MAX_LENGTH)
                + System.lineSeparator() + FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_FIELD_NAME + ": "
                + getPopulatedErrorMessage(FieldValidator.SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, longUrl,
                FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH)
                + System.lineSeparator() + FieldValidator.ACCOUNT_REQUEST_COMMENTS_FIELD_NAME + ": "
                + getPopulatedErrorMessage(FieldValidator.SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, longComments,
                FieldValidator.ACCOUNT_REQUEST_COMMENTS_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH);
        assertEquals(errorMessage, StringHelper.toString(invalidAccountRequest.getInvalidityInfo()));

        ______TS("invalid account request attributes, non-null pure institute and pure country");

        String invalidPureInstitute = "invalid%pure institute";
        String longPureCountry = StringHelperExtension
                .generateRandomAsciiStringOfLength(FieldValidator.ACCOUNT_REQUEST_COUNTRY_NAME_MAX_LENGTH + 1);
        invalidAccountRequest = AccountRequestAttributes
                .builder("Adam", invalidPureInstitute, longPureCountry, "adam@tmt.tmt", "", "")
                .build();

        assertFalse(invalidAccountRequest.isValid());
        errorMessage = FieldValidator.ACCOUNT_REQUEST_INSTITUTE_NAME_FIELD_NAME + ": "
                + getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, invalidPureInstitute,
                FieldValidator.ACCOUNT_REQUEST_INSTITUTE_NAME_FIELD_NAME,
                FieldValidator.REASON_CONTAINS_INVALID_CHAR,
                FieldValidator.ACCOUNT_REQUEST_INSTITUTE_NAME_MAX_LENGTH)
                + System.lineSeparator() + FieldValidator.ACCOUNT_REQUEST_COUNTRY_NAME_FIELD_NAME + ": "
                + getPopulatedErrorMessage(FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, longPureCountry,
                FieldValidator.ACCOUNT_REQUEST_COUNTRY_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.ACCOUNT_REQUEST_COUNTRY_NAME_MAX_LENGTH)
                + System.lineSeparator() + FieldValidator.INSTITUTE_NAME_FIELD_NAME + ": "
                // even though country exceeds its length, the combined institute with country is still within the limit
                + getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                invalidPureInstitute + ", " + longPureCountry,
                FieldValidator.INSTITUTE_NAME_FIELD_NAME, FieldValidator.REASON_CONTAINS_INVALID_CHAR);
        assertEquals(errorMessage, StringHelper.toString(invalidAccountRequest.getInvalidityInfo()));
    }

    @Test
    public void testCanRegistrationKeyBeUseToJoin() {
        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes
                .builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                .withStatus(AccountRequestStatus.APPROVED)
                .build();
        assertTrue(accountRequestAttributes.canRegistrationKeyBeUseToJoin());

        accountRequestAttributes = AccountRequestAttributes
                .builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                .withStatus(AccountRequestStatus.APPROVED)
                .withRegisteredAt(Instant.now().minusSeconds(600))
                .build();
        assertFalse(accountRequestAttributes.canRegistrationKeyBeUseToJoin());

        accountRequestAttributes = AccountRequestAttributes
                .builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                .build();
        assertFalse(accountRequestAttributes.canRegistrationKeyBeUseToJoin());

        accountRequestAttributes = AccountRequestAttributes
                .builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                .withStatus(AccountRequestStatus.REJECTED)
                .build();
        assertFalse(accountRequestAttributes.canRegistrationKeyBeUseToJoin());

        accountRequestAttributes = AccountRequestAttributes
                .builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                .withStatus(AccountRequestStatus.REGISTERED)
                .build();
        assertFalse(accountRequestAttributes.canRegistrationKeyBeUseToJoin());
    }

    @Test
    public void testHasRegistrationKeyBeenUsedToJoin() {
        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes
                .builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                .withStatus(AccountRequestStatus.REGISTERED)
                .build();
        assertTrue(accountRequestAttributes.hasRegistrationKeyBeenUsedToJoin());

        accountRequestAttributes = AccountRequestAttributes
                .builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                .withRegisteredAt(Instant.now().minusSeconds(600))
                .build();
        assertTrue(accountRequestAttributes.hasRegistrationKeyBeenUsedToJoin());

        accountRequestAttributes = AccountRequestAttributes
                .builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                .withStatus(AccountRequestStatus.APPROVED)
                .build();
        assertFalse(accountRequestAttributes.hasRegistrationKeyBeenUsedToJoin());

        accountRequestAttributes = AccountRequestAttributes
                .builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                .withStatus(AccountRequestStatus.REJECTED)
                .build();
        assertFalse(accountRequestAttributes.hasRegistrationKeyBeenUsedToJoin());

        accountRequestAttributes = AccountRequestAttributes
                .builder("Adam", "TMT", "Singapore", "adam@tmt.tmt", "", "")
                .withStatus(AccountRequestStatus.SUBMITTED)
                .build();
        assertFalse(accountRequestAttributes.hasRegistrationKeyBeenUsedToJoin());
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
        AccountRequestAttributes ar = getValidAccountRequestAttributesObject();
        String expected = "[AccountRequestAttributes] name= Adam, pureInstitute= null, pureCountry= null"
                + ", institute= TMT, Singapore, email= adam@tmt.tmt, homePageUrl= , comments= , status= SUBMITTED"
                + ", createdAt= " + ar.getCreatedAt() + ", lastProcessedAt= " + ar.getLastProcessedAt()
                + ", registeredAt= " + ar.getLastProcessedAt();
        assertEquals(expected, ar.toString());
    }

    @Test
    public void testEquals() {
        AccountRequestAttributes accountRequest = getValidAccountRequestAttributesObject();

        // When the two account requests have same values
        AccountRequestAttributes similarAccountRequest = getValidAccountRequestAttributesObject();

        assertTrue(accountRequest.equals(similarAccountRequest));

        // When the two account requests are different
        AccountRequestAttributes differentAccountRequest =
                AccountRequestAttributes.builder("test@test.com", "test-institute", "Another Name", "", "").build();

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
                AccountRequestAttributes.builder("test@test.com", "test-institute", "Another Name", "", "").build();

        assertFalse(accountRequest.hashCode() == accountRequestDifferent.hashCode());
    }

    @Test
    public void testGenerateInstitute() {
        assertEquals("TMT, Singapore", AccountRequestAttributes.generateInstitute("TMT", "Singapore"));
        assertThrows(AssertionError.class, () -> AccountRequestAttributes.generateInstitute(null, "Singapore"));
        assertThrows(AssertionError.class, () -> AccountRequestAttributes.generateInstitute("TMT", null));
    }

    private static AccountRequestAttributes getValidAccountRequestAttributesObject() {
        AccountRequest accountRequest = new AccountRequest("Adam", "TMT, Singapore", "adam@tmt.tmt", "", "");
        accountRequest.setRegistrationKey("valid123");
        return AccountRequestAttributes.valueOf(accountRequest);
    }

    private void verifyNullPureInstituteAndPureCountry(AccountRequestAttributes ara) {
        assertNull(ara.getPureInstitute());
        assertNull(ara.getPureCountry());
    }

    private void verifyAccountRequestEquals(AccountRequestAttributes ara, AccountRequest accountRequest) {
        assertEquals(accountRequest.getName(), ara.getName());
        assertEquals(accountRequest.getInstitute(), ara.getInstitute());
        assertEquals(accountRequest.getEmail(), ara.getEmail());
        assertEquals(accountRequest.getHomePageUrl(), ara.getHomePageUrl());
        assertEquals(accountRequest.getComments(), ara.getComments());
        assertEquals(accountRequest.getRegistrationKey(), ara.getRegistrationKey());
        assertEquals(accountRequest.getStatus(), ara.getStatus());
        assertEquals(accountRequest.getCreatedAt(), ara.getCreatedAt());
        assertEquals(accountRequest.getLastProcessedAt(), ara.getLastProcessedAt());
        assertEquals(accountRequest.getRegisteredAt(), ara.getRegisteredAt());
    }

}
