package teammates.storage.api;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelperExtension;
import teammates.common.util.TimeHelper;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;

/**
 * SUT: {@link AccountRequestsDb}.
 */
public class AccountRequestsDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

    @Test
    public void testCreateAccountRequest() throws Exception {
        ______TS("typical success case - 1");

        AccountRequestAttributes accountRequest = AccountRequestAttributes
                .builder("Adams", "TMT, Singapore", "adams@tmt.tmt", "https://www.google.com/", "My comments")
                .build();

        accountRequest = accountRequestsDb.createEntity(accountRequest);
        verifyPresentInDatabase(accountRequest);

        ______TS("typical success case - 2");

        accountRequest = AccountRequestAttributes
                .builder("Baker", "TMT, Singapore", "baker@tmt.tmt", "https://www.google.com/", "My comments")
                .build();

        accountRequest = accountRequestsDb.createEntity(accountRequest);
        verifyPresentInDatabase(accountRequest);

        ______TS("failure: duplicate account request - 1");

        AccountRequestAttributes duplicateAccountRequest1 = AccountRequestAttributes
                .builder("adams", "TMT, Singapore", "adams@tmt.tmt", "", "")
                .build();

        assertThrows(EntityAlreadyExistsException.class, () -> accountRequestsDb.createEntity(duplicateAccountRequest1));

        ______TS("failure: duplicate account request - 2");

        AccountRequestAttributes duplicateAccountRequest2 = AccountRequestAttributes
                .builder("baker", "TMT, Singapore", "baker@tmt.tmt", "https://www.comp.nus.edu.sg", "")
                .build();

        assertThrows(EntityAlreadyExistsException.class, () -> accountRequestsDb.createEntity(duplicateAccountRequest2));

        ______TS("failure: invalid parameter - 1");

        AccountRequestAttributes invalidAccountRequest1 = AccountRequestAttributes
                .builder("Valid Name", "Valid Institute", "Invalid Email", "", "")
                .build();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsDb.createEntity(invalidAccountRequest1));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, "Invalid Email",
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: invalid parameter - 2");

        AccountRequestAttributes invalidAccountRequest2 = AccountRequestAttributes
                .builder("Valid Name", "Valid Institute, Invalid%Country", "valid_email@tmt.tmt", "", "")
                .build();

        ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsDb.createEntity(invalidAccountRequest2));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.INVALID_NAME_ERROR_MESSAGE, "Valid Institute, Invalid%Country",
                        FieldValidator.INSTITUTE_NAME_FIELD_NAME, FieldValidator.REASON_CONTAINS_INVALID_CHAR),
                ipe.getMessage());

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> accountRequestsDb.createEntity(null));
    }

    @Test
    public void testUpdateAccountRequest() throws Exception {
        AccountRequestAttributes originalAccountRequest = accountRequestsDb.createEntity(AccountRequestAttributes
                .builder("Clark", "TMT, Singapore", "clark@tmt.tmt", "", "Comments")
                .build());

        ______TS("typical success case - 1");

        Instant lastProcessedAt = Instant.now();
        AccountRequestAttributes.UpdateOptions updateOptions = AccountRequestAttributes
                .updateOptionsBuilder("clark@tmt.tmt", "TMT, Singapore")
                .withName("Clark Edited")
                .withInstitute("TMT, Singapore")
                .withEmail("clark_edited@tmt.tmt")
                .withLastProcessedAt(lastProcessedAt)
                .build();
        accountRequestsDb.updateAccountRequest(updateOptions);

        AccountRequestAttributes actualAccountRequest = accountRequestsDb
                .getAccountRequest("clark_edited@tmt.tmt", "TMT, Singapore");
        assertEquals("Clark Edited", actualAccountRequest.getName());
        assertEquals("TMT, Singapore", actualAccountRequest.getInstitute());
        assertEquals("clark_edited@tmt.tmt", actualAccountRequest.getEmail());
        assertEquals(originalAccountRequest.getHomePageUrl(), actualAccountRequest.getHomePageUrl());
        assertEquals(originalAccountRequest.getComments(), actualAccountRequest.getComments());
        assertEquals(originalAccountRequest.getStatus(), actualAccountRequest.getStatus());
        assertEquals(originalAccountRequest.getCreatedAt(), actualAccountRequest.getCreatedAt());
        assertEquals(lastProcessedAt, actualAccountRequest.getLastProcessedAt());
        assertEquals(originalAccountRequest.getRegisteredAt(), actualAccountRequest.getRegisteredAt());
        assertEquals(originalAccountRequest.getRegistrationKey(), actualAccountRequest.getRegistrationKey());

        ______TS("typical success case - 2");

        originalAccountRequest = actualAccountRequest;

        Instant registeredAt = Instant.now();
        updateOptions = AccountRequestAttributes
                .updateOptionsBuilder("clark_edited@tmt.tmt", "TMT, Singapore")
                .withStatus(AccountRequestStatus.REGISTERED)
                .withRegisteredAt(registeredAt)
                .build();
        accountRequestsDb.updateAccountRequest(updateOptions);

        actualAccountRequest = accountRequestsDb.getAccountRequest("clark_edited@tmt.tmt", "TMT, Singapore");
        assertEquals(originalAccountRequest.getName(), actualAccountRequest.getName());
        assertEquals(originalAccountRequest.getInstitute(), actualAccountRequest.getInstitute());
        assertEquals(originalAccountRequest.getEmail(), actualAccountRequest.getEmail());
        assertEquals(originalAccountRequest.getHomePageUrl(), actualAccountRequest.getHomePageUrl());
        assertEquals(originalAccountRequest.getComments(), actualAccountRequest.getComments());
        assertEquals(AccountRequestStatus.REGISTERED, actualAccountRequest.getStatus());
        assertEquals(originalAccountRequest.getCreatedAt(), actualAccountRequest.getCreatedAt());
        assertEquals(originalAccountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());
        assertEquals(registeredAt, actualAccountRequest.getRegisteredAt());
        assertEquals(originalAccountRequest.getRegistrationKey(), actualAccountRequest.getRegistrationKey());

        ______TS("failure: account request not found");

        AccountRequestAttributes.UpdateOptions updateOptionsNotFound = AccountRequestAttributes
                .updateOptionsBuilder("clark@tmt.tmt", "TMT, Singapore")
                .withStatus(AccountRequestStatus.REJECTED)
                .withLastProcessedAt(Instant.now())
                .build();

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsDb.updateAccountRequest(updateOptionsNotFound));

        ______TS("failure: account request to update to already exists");

        accountRequestsDb.createEntity(AccountRequestAttributes
                .builder("David", "TMT, Singapore", "david@tmt.tmt", "", "")
                .build());

        AccountRequestAttributes.UpdateOptions updateOptionsAlreadyExists = AccountRequestAttributes
                .updateOptionsBuilder("clark_edited@tmt.tmt", "TMT, Singapore")
                .withEmail("david@tmt.tmt")
                .build();

        assertThrows(EntityAlreadyExistsException.class,
                () -> accountRequestsDb.updateAccountRequest(updateOptionsAlreadyExists));

        ______TS("failure: new account request is invalid");

        String longEmail = StringHelperExtension.generateStringOfLength(FieldValidator.EMAIL_MAX_LENGTH - 7) + "@tmt.tmt";
        AccountRequestAttributes.UpdateOptions updateOptionsInvalid = AccountRequestAttributes
                .updateOptionsBuilder("clark_edited@tmt.tmt", "TMT, Singapore")
                .withName("|")
                .withInstitute("")
                .withEmail(longEmail)
                .build();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsDb.updateAccountRequest(updateOptionsInvalid));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.INVALID_NAME_ERROR_MESSAGE, "|",
                        FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR),
                ipe.getMessage());
        AssertHelper.assertContains(
                getPopulatedEmptyStringErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                        FieldValidator.INSTITUTE_NAME_FIELD_NAME, FieldValidator.INSTITUTE_NAME_MAX_LENGTH),
                ipe.getMessage());
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, longEmail, FieldValidator.EMAIL_FIELD_NAME,
                        FieldValidator.REASON_TOO_LONG, FieldValidator.EMAIL_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> accountRequestsDb.updateAccountRequest(null));

        ______TS("success: single field update");

        assertNotEquals("clark@tmt.tmt", actualAccountRequest.getEmail());
        updateOptions = AccountRequestAttributes
                .updateOptionsBuilder(actualAccountRequest.getEmail(), actualAccountRequest.getInstitute())
                .withEmail("clark@tmt.tmt")
                .build();
        AccountRequestAttributes updatedAccountRequest = accountRequestsDb.updateAccountRequest(updateOptions);
        actualAccountRequest = accountRequestsDb.getAccountRequest("clark@tmt.tmt", "TMT, Singapore");
        assertEquals("clark@tmt.tmt", updatedAccountRequest.getEmail());
        assertEquals("clark@tmt.tmt", actualAccountRequest.getEmail());

        assertNotEquals("NUS, Singapore", actualAccountRequest.getInstitute());
        updateOptions = AccountRequestAttributes
                .updateOptionsBuilder(actualAccountRequest.getEmail(), actualAccountRequest.getInstitute())
                .withInstitute("NUS, Singapore")
                .build();
        updatedAccountRequest = accountRequestsDb.updateAccountRequest(updateOptions);
        actualAccountRequest = accountRequestsDb.getAccountRequest("clark@tmt.tmt", "NUS, Singapore");
        assertEquals("NUS, Singapore", updatedAccountRequest.getInstitute());
        assertEquals("NUS, Singapore", actualAccountRequest.getInstitute());

        assertNotEquals("clark", actualAccountRequest.getName());
        updateOptions = AccountRequestAttributes
                .updateOptionsBuilder(actualAccountRequest.getEmail(), actualAccountRequest.getInstitute())
                .withName("clark")
                .build();
        updatedAccountRequest = accountRequestsDb.updateAccountRequest(updateOptions);
        actualAccountRequest = accountRequestsDb.getAccountRequest("clark@tmt.tmt", "NUS, Singapore");
        assertEquals("clark", updatedAccountRequest.getName());
        assertEquals("clark", actualAccountRequest.getName());

        assertNotEquals(AccountRequestStatus.SUBMITTED, actualAccountRequest.getStatus());
        updateOptions = AccountRequestAttributes
                .updateOptionsBuilder(actualAccountRequest.getEmail(), actualAccountRequest.getInstitute())
                .withStatus(AccountRequestStatus.SUBMITTED)
                .build();
        updatedAccountRequest = accountRequestsDb.updateAccountRequest(updateOptions);
        actualAccountRequest = accountRequestsDb.getAccountRequest("clark@tmt.tmt", "NUS, Singapore");
        assertEquals(AccountRequestStatus.SUBMITTED, updatedAccountRequest.getStatus());
        assertEquals(AccountRequestStatus.SUBMITTED, actualAccountRequest.getStatus());

        registeredAt = Instant.now();
        assertNotEquals(registeredAt, actualAccountRequest.getRegisteredAt());
        updateOptions = AccountRequestAttributes
                .updateOptionsBuilder(actualAccountRequest.getEmail(), actualAccountRequest.getInstitute())
                .withRegisteredAt(registeredAt)
                .build();
        updatedAccountRequest = accountRequestsDb.updateAccountRequest(updateOptions);
        actualAccountRequest = accountRequestsDb.getAccountRequest("clark@tmt.tmt", "NUS, Singapore");
        assertEquals(registeredAt, updatedAccountRequest.getRegisteredAt());
        assertEquals(registeredAt, actualAccountRequest.getRegisteredAt());

        ______TS("lastProcessedAt is still updated when no other fields have changed");

        originalAccountRequest = actualAccountRequest;

        lastProcessedAt = Instant.now();
        updateOptions = AccountRequestAttributes
                .updateOptionsBuilder(actualAccountRequest.getEmail(), actualAccountRequest.getInstitute())
                .withName(originalAccountRequest.getName())
                .withInstitute(originalAccountRequest.getInstitute())
                .withEmail(originalAccountRequest.getEmail())
                .withStatus(originalAccountRequest.getStatus())
                .withRegisteredAt(originalAccountRequest.getRegisteredAt())
                .withLastProcessedAt(lastProcessedAt)
                .build();

        updatedAccountRequest = accountRequestsDb.updateAccountRequest(updateOptions);
        assertEquals(lastProcessedAt, updatedAccountRequest.getLastProcessedAt());

        actualAccountRequest = accountRequestsDb.getAccountRequest("clark@tmt.tmt", "NUS, Singapore");
        assertEquals(lastProcessedAt, actualAccountRequest.getLastProcessedAt());
    }

    @Test
    public void testDeleteAccountRequest() throws Exception {
        AccountRequestAttributes accountRequest = accountRequestsDb.createEntity(AccountRequestAttributes
                .builder("Evans", "TMT, Singapore", "evans@tmt.tmt", "", "")
                .build());

        ______TS("typical success case");

        verifyPresentInDatabase(accountRequest);
        accountRequestsDb.deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verifyAbsentInDatabase(accountRequest);

        ______TS("success: delete the same account request again");

        accountRequestsDb.deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verifyAbsentInDatabase(accountRequest);

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () ->
                accountRequestsDb.deleteAccountRequest(null, accountRequest.getInstitute()));
        assertThrows(AssertionError.class, () ->
                accountRequestsDb.deleteAccountRequest(accountRequest.getEmail(), null));
    }

    @Test
    public void testGetAccountRequestForRegistrationKey() throws Exception {
        AccountRequestAttributes accountRequest = accountRequestsDb.createEntity(AccountRequestAttributes
                .builder("Frank", "TMT, Singapore", "frank@tmt.tmt", "", "")
                .build());

        ______TS("typical success case");

        AccountRequestAttributes actualAccountRequest =
                accountRequestsDb.getAccountRequestForRegistrationKey(accountRequest.getRegistrationKey());
        assertEquals(accountRequest, actualAccountRequest);

        ______TS("account request not found");

        assertNull(accountRequestsDb.getAccountRequestForRegistrationKey("not-found"));

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> accountRequestsDb.getAccountRequestForRegistrationKey(null));
    }

    @Test
    public void testGetAccountRequest() throws Exception {
        AccountRequestAttributes accountRequest = accountRequestsDb.createEntity(AccountRequestAttributes
                .builder("Ghosh", "TMT, Singapore", "ghosh@tmt.tmt", "", "")
                .build());

        ______TS("typical success case");

        AccountRequestAttributes actualAccountRequest =
                accountRequestsDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        assertEquals(accountRequest, actualAccountRequest);

        ______TS("account request not found");

        assertNull(accountRequestsDb.getAccountRequest("Ghosh@tmt.tmt", accountRequest.getInstitute()));

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> accountRequestsDb.getAccountRequest(null, accountRequest.getInstitute()));
        assertThrows(AssertionError.class, () -> accountRequestsDb.getAccountRequest(accountRequest.getEmail(), null));
    }

    @Test
    public void testHasExistingEntities() throws Exception {
        AccountRequestAttributes accountRequest = AccountRequestAttributes
                .builder("Hills", "TMT, Singapore", "hills@tmt.tmt", "", "")
                .build();

        ______TS("false before entity creation");
        assertFalse(accountRequestsDb.hasExistingEntities(accountRequest));

        ______TS("true after entity creation");
        accountRequestsDb.createEntity(accountRequest);
        assertTrue(accountRequestsDb.hasExistingEntities(accountRequest));
    }

    @Test
    public void testGetAccountRequestsWithStatusSubmitted() throws Exception {
        AccountRequestAttributes accountRequest1 = accountRequestsDb.createEntity(
                AccountRequestAttributes
                        .builder("Person 1", "TMT, Singapore", "person_1@tmt.tmt", "", "")
                        .withStatus(AccountRequestStatus.SUBMITTED)
                        .build());
        accountRequestsDb.createEntity(
                AccountRequestAttributes
                        .builder("Person 2", "TMT, Singapore", "person_2@tmt.tmt", "", "")
                        .withStatus(AccountRequestStatus.APPROVED)
                        .build());
        AccountRequestAttributes accountRequest3 = accountRequestsDb.createEntity(
                AccountRequestAttributes
                        .builder("Person 3", "TMT, Singapore", "person_3@tmt.tmt", "", "")
                        .withStatus(AccountRequestStatus.SUBMITTED)
                        .build());
        accountRequestsDb.createEntity(
                AccountRequestAttributes
                        .builder("Person 4", "TMT, Singapore", "person_4@tmt.tmt", "", "")
                        .withStatus(AccountRequestStatus.REGISTERED)
                        .build());
        accountRequestsDb.createEntity(
                AccountRequestAttributes
                        .builder("Person 5", "TMT, Singapore", "person_5@tmt.tmt", "", "")
                        .withStatus(AccountRequestStatus.REJECTED)
                        .build());

        List<AccountRequestAttributes> actual = accountRequestsDb.getAccountRequestsWithStatusSubmitted();

        assertEquals(2, actual.size());
        assertTrue(actual.contains(accountRequest1));
        assertTrue(actual.contains(accountRequest3));
    }

    @Test
    public void testGetAccountRequestsSubmittedWithinPeriod() throws Exception {
        AccountRequestAttributes accountRequest1 = AccountRequestAttributes
                .builder("Person 6", "TMT, Singapore", "person_6@tmt.tmt", "", "")
                .build();
        accountRequest1.setCreatedAt(TimeHelper.parseInstant("2022-07-26T17:50:11Z"));
        accountRequest1 = accountRequestsDb.createEntity(accountRequest1);

        AccountRequestAttributes accountRequest2 = AccountRequestAttributes
                .builder("Person 7", "TMT, Singapore", "person_7@tmt.tmt", "", "")
                .build();
        accountRequest2.setCreatedAt(TimeHelper.parseInstant("2022-07-15T10:23:37Z"));
        accountRequestsDb.createEntity(accountRequest2);

        AccountRequestAttributes accountRequest3 = AccountRequestAttributes
                .builder("Person 8", "TMT, Singapore", "person_8@tmt.tmt", "", "")
                .build();
        accountRequest3.setCreatedAt(TimeHelper.parseInstant("2022-07-26T23:59:59Z"));
        accountRequest3 = accountRequestsDb.createEntity(accountRequest3);

        AccountRequestAttributes accountRequest4 = AccountRequestAttributes
                .builder("Person 9", "TMT, Singapore", "person_9@tmt.tmt", "", "")
                .build();
        accountRequest4.setCreatedAt(TimeHelper.parseInstant("2022-07-27T00:00:00Z"));
        accountRequestsDb.createEntity(accountRequest4);

        AccountRequestAttributes accountRequest5 = AccountRequestAttributes
                .builder("Person 10", "TMT, Singapore", "person_10@tmt.tmt", "", "")
                .build();
        accountRequest5.setCreatedAt(TimeHelper.parseInstant("2023-07-28T12:34:56Z"));
        accountRequestsDb.createEntity(accountRequest5);

        List<AccountRequestAttributes> actual = accountRequestsDb.getAccountRequestsSubmittedWithinPeriod(
                TimeHelper.parseInstant("2022-07-16T00:00:00Z"), TimeHelper.parseInstant("2022-07-27T00:00:00Z"));

        assertEquals(2, actual.size());
        assertTrue(actual.contains(accountRequest1));
        assertTrue(actual.contains(accountRequest3));
    }

    @AfterMethod
    public void removeAllAccountRequests() {
        List<AccountRequestAttributes> allAccountRequests = accountRequestsDb.getAllAccountRequests();
        allAccountRequests.forEach(ar -> accountRequestsDb.deleteAccountRequest(ar.getEmail(), ar.getInstitute()));
    }

}
