package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.BeforeMethod;
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

/**
 * SUT: {@link AccountRequestsLogic}.
 */
public class AccountRequestsLogicTest extends BaseLogicTest {

    private final AccountRequestsLogic accountRequestsLogic = AccountRequestsLogic.inst();

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testCreateAccountRequest() throws Exception {
        ______TS("typical success case");

        AccountRequestAttributes accountRequest = AccountRequestAttributes
                .builder("Adams", "TMT, Singapore", "adams@tmt.tmt", "https://www.google.com/", "My comments")
                .build();

        AccountRequestAttributes createdAccountRequest = accountRequestsLogic.createAccountRequest(accountRequest);
        verifyPresentInDatabase(createdAccountRequest);

        assertEquals(accountRequest.getName(), createdAccountRequest.getName());
        assertEquals(accountRequest.getInstitute(), createdAccountRequest.getInstitute());
        assertEquals(accountRequest.getEmail(), createdAccountRequest.getEmail());
        assertEquals(accountRequest.getHomePageUrl(), createdAccountRequest.getHomePageUrl());
        assertEquals(accountRequest.getComments(), createdAccountRequest.getComments());
        assertEquals(AccountRequestStatus.SUBMITTED, createdAccountRequest.getStatus());
        assertNotNull(createdAccountRequest.getCreatedAt());
        assertNull(createdAccountRequest.getLastProcessedAt());
        assertNull(createdAccountRequest.getRegisteredAt());
        assertNotNull(createdAccountRequest.getRegistrationKey());

        ______TS("failure: duplicate account request");

        AccountRequestAttributes duplicateAccountRequest1 = AccountRequestAttributes
                .builder("adams", "TMT, Singapore", "adams@tmt.tmt", "https://www.comp.nus.edu.sg", "")
                .build();

        assertThrows(EntityAlreadyExistsException.class,
                () -> accountRequestsLogic.createAccountRequest(duplicateAccountRequest1));

        ______TS("failure case: invalid parameter");

        String longName = StringHelperExtension.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);
        AccountRequestAttributes invalidAccountRequest1 = AccountRequestAttributes
                .builder(longName, "Valid Institute", "valid_email@tmt.tmt", "", "")
                .build();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsLogic.createAccountRequest(invalidAccountRequest1));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, longName,
                        FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                        FieldValidator.PERSON_NAME_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> accountRequestsLogic.createAccountRequest(null));

        // clean up
        accountRequestsLogic.deleteAccountRequest("adams@tmt.tmt", "TMT, Singapore");
    }

    @Test
    public void testCreateAndApproveAccountRequest() throws Exception {
        ______TS("typical success case");

        AccountRequestAttributes accountRequest = AccountRequestAttributes
                .builder("Adams", "TMT, Singapore", "adams@tmt.tmt", "", "My comments")
                .build();

        AccountRequestAttributes createdAccountRequest =
                accountRequestsLogic.createAndApproveAccountRequest(accountRequest);
        verifyPresentInDatabase(createdAccountRequest);

        assertEquals(accountRequest.getName(), createdAccountRequest.getName());
        assertEquals(accountRequest.getInstitute(), createdAccountRequest.getInstitute());
        assertEquals(accountRequest.getEmail(), createdAccountRequest.getEmail());
        assertEquals(accountRequest.getHomePageUrl(), createdAccountRequest.getHomePageUrl());
        assertEquals(accountRequest.getComments(), createdAccountRequest.getComments());
        assertEquals(AccountRequestStatus.APPROVED, createdAccountRequest.getStatus());
        assertNotNull(createdAccountRequest.getCreatedAt());
        assertNotNull(createdAccountRequest.getLastProcessedAt());
        assertEquals(createdAccountRequest.getCreatedAt(), createdAccountRequest.getLastProcessedAt());
        assertNull(createdAccountRequest.getRegisteredAt());
        assertNotNull(createdAccountRequest.getRegistrationKey());

        ______TS("failure: duplicate account request");

        AccountRequestAttributes duplicateAccountRequest1 = AccountRequestAttributes
                .builder("adams", "TMT, Singapore", "adams@tmt.tmt", "", "")
                .build();

        assertThrows(EntityAlreadyExistsException.class,
                () -> accountRequestsLogic.createAndApproveAccountRequest(duplicateAccountRequest1));

        ______TS("failure case: invalid parameter - 1");

        String longUrl =
                StringHelperExtension.generateStringOfLength(FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH + 1);
        AccountRequestAttributes invalidAccountRequest1 = AccountRequestAttributes
                .builder("Valid Name", "Invalid | Institute", "valid_email@tmt.tmt", longUrl, "")
                .build();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsLogic.createAndApproveAccountRequest(invalidAccountRequest1));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.INVALID_NAME_ERROR_MESSAGE, "Invalid | Institute",
                        FieldValidator.INSTITUTE_NAME_FIELD_NAME, FieldValidator.REASON_CONTAINS_INVALID_CHAR),
                ipe.getMessage());
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, longUrl,
                        FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                        FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure case: invalid parameter - 2");

        String longComments =
                StringHelperExtension.generateStringOfLength(FieldValidator.ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH + 1);
        AccountRequestAttributes invalidAccountRequest2 = AccountRequestAttributes
                .builder("Valid Name", "Valid Institute, Valid Country", "valid_email@tmt.tmt", "", longComments)
                .build();

        ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsLogic.createAndApproveAccountRequest(invalidAccountRequest2));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, longComments,
                        FieldValidator.ACCOUNT_REQUEST_COMMENTS_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                        FieldValidator.ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> accountRequestsLogic.createAndApproveAccountRequest(null));

        // clean up
        accountRequestsLogic.deleteAccountRequest("adams@tmt.tmt", "TMT, Singapore");
    }

    @Test
    public void testUpdateAccountRequest() throws Exception {
        AccountRequestAttributes originalAccountRequest = accountRequestsLogic.createAccountRequest(AccountRequestAttributes
                .builder("Clark", "TMT, Singapore", "clark@tmt.tmt", "", "Comments")
                .build());

        ______TS("typical success case - 1");

        Instant lastProcessedAt = Instant.now().minusSeconds(600);
        Instant registeredAt = Instant.now();
        AccountRequestAttributes.UpdateOptions updateOptions = AccountRequestAttributes
                .updateOptionsBuilder("clark@tmt.tmt", "TMT, Singapore")
                .withInstitute("TEAMMATES TEST, Singapore")
                .withStatus(AccountRequestStatus.REGISTERED)
                .withLastProcessedAt(lastProcessedAt)
                .withRegisteredAt(registeredAt)
                .build();
        AccountRequestAttributes updatedAccountRequest = accountRequestsLogic.updateAccountRequest(updateOptions);

        assertEquals(originalAccountRequest.getName(), updatedAccountRequest.getName());
        assertEquals("TEAMMATES TEST, Singapore", updatedAccountRequest.getInstitute());
        assertEquals(originalAccountRequest.getEmail(), updatedAccountRequest.getEmail());
        assertEquals(originalAccountRequest.getHomePageUrl(), updatedAccountRequest.getHomePageUrl());
        assertEquals(originalAccountRequest.getComments(), updatedAccountRequest.getComments());
        assertEquals(AccountRequestStatus.REGISTERED, updatedAccountRequest.getStatus());
        assertEquals(originalAccountRequest.getCreatedAt(), updatedAccountRequest.getCreatedAt());
        assertEquals(lastProcessedAt, updatedAccountRequest.getLastProcessedAt());
        assertEquals(registeredAt, updatedAccountRequest.getRegisteredAt());
        assertEquals(originalAccountRequest.getRegistrationKey(), updatedAccountRequest.getRegistrationKey());

        AccountRequestAttributes actualAccountRequest = accountRequestsLogic
                .getAccountRequest("clark@tmt.tmt", "TEAMMATES TEST, Singapore");
        assertEquals(originalAccountRequest.getName(), actualAccountRequest.getName());
        assertEquals("TEAMMATES TEST, Singapore", actualAccountRequest.getInstitute());
        assertEquals(originalAccountRequest.getEmail(), actualAccountRequest.getEmail());
        assertEquals(originalAccountRequest.getHomePageUrl(), actualAccountRequest.getHomePageUrl());
        assertEquals(originalAccountRequest.getComments(), actualAccountRequest.getComments());
        assertEquals(AccountRequestStatus.REGISTERED, actualAccountRequest.getStatus());
        assertEquals(originalAccountRequest.getCreatedAt(), actualAccountRequest.getCreatedAt());
        assertEquals(lastProcessedAt, actualAccountRequest.getLastProcessedAt());
        assertEquals(registeredAt, actualAccountRequest.getRegisteredAt());
        assertEquals(originalAccountRequest.getRegistrationKey(), actualAccountRequest.getRegistrationKey());

        ______TS("typical success case - 2");

        originalAccountRequest = actualAccountRequest;

        lastProcessedAt = Instant.now();
        updateOptions = AccountRequestAttributes
                .updateOptionsBuilder("clark@tmt.tmt", "TEAMMATES TEST, Singapore")
                .withStatus(AccountRequestStatus.SUBMITTED)
                .withLastProcessedAt(lastProcessedAt)
                .withRegisteredAt(null)
                .build();
        updatedAccountRequest = accountRequestsLogic.updateAccountRequest(updateOptions);

        assertEquals(originalAccountRequest.getName(), updatedAccountRequest.getName());
        assertEquals(originalAccountRequest.getInstitute(), updatedAccountRequest.getInstitute());
        assertEquals(originalAccountRequest.getEmail(), updatedAccountRequest.getEmail());
        assertEquals(originalAccountRequest.getHomePageUrl(), updatedAccountRequest.getHomePageUrl());
        assertEquals(originalAccountRequest.getComments(), updatedAccountRequest.getComments());
        assertEquals(AccountRequestStatus.SUBMITTED, updatedAccountRequest.getStatus());
        assertEquals(originalAccountRequest.getCreatedAt(), updatedAccountRequest.getCreatedAt());
        assertEquals(lastProcessedAt, updatedAccountRequest.getLastProcessedAt());
        assertNull(updatedAccountRequest.getRegisteredAt());
        assertEquals(originalAccountRequest.getRegistrationKey(), updatedAccountRequest.getRegistrationKey());

        actualAccountRequest = accountRequestsLogic.getAccountRequest("clark@tmt.tmt", "TEAMMATES TEST, Singapore");
        assertEquals(originalAccountRequest.getName(), actualAccountRequest.getName());
        assertEquals(originalAccountRequest.getInstitute(), actualAccountRequest.getInstitute());
        assertEquals(originalAccountRequest.getEmail(), actualAccountRequest.getEmail());
        assertEquals(originalAccountRequest.getHomePageUrl(), actualAccountRequest.getHomePageUrl());
        assertEquals(originalAccountRequest.getComments(), actualAccountRequest.getComments());
        assertEquals(AccountRequestStatus.SUBMITTED, actualAccountRequest.getStatus());
        assertEquals(originalAccountRequest.getCreatedAt(), actualAccountRequest.getCreatedAt());
        assertEquals(lastProcessedAt, actualAccountRequest.getLastProcessedAt());
        assertNull(actualAccountRequest.getRegisteredAt());
        assertEquals(originalAccountRequest.getRegistrationKey(), actualAccountRequest.getRegistrationKey());

        ______TS("lastProcessedAt is still updated when no other fields have changed");

        originalAccountRequest = actualAccountRequest;

        lastProcessedAt = Instant.now();
        updateOptions = AccountRequestAttributes
                .updateOptionsBuilder("clark@tmt.tmt", "TEAMMATES TEST, Singapore")
                .withName(originalAccountRequest.getName())
                .withInstitute(originalAccountRequest.getInstitute())
                .withEmail(originalAccountRequest.getEmail())
                .withStatus(originalAccountRequest.getStatus())
                .withRegisteredAt(originalAccountRequest.getRegisteredAt())
                .withLastProcessedAt(lastProcessedAt)
                .build();

        updatedAccountRequest = accountRequestsLogic.updateAccountRequest(updateOptions);
        assertEquals(lastProcessedAt, updatedAccountRequest.getLastProcessedAt());

        actualAccountRequest = accountRequestsLogic.getAccountRequest("clark@tmt.tmt", "TEAMMATES TEST, Singapore");
        assertEquals(lastProcessedAt, actualAccountRequest.getLastProcessedAt());

        ______TS("failure: account request not found");

        AccountRequestAttributes.UpdateOptions updateOptionsNotFound = AccountRequestAttributes
                .updateOptionsBuilder("clark@tmt.tmt", "TMT, Singapore")
                .withStatus(AccountRequestStatus.APPROVED)
                .withLastProcessedAt(Instant.now())
                .build();

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsLogic.updateAccountRequest(updateOptionsNotFound));

        ______TS("failure: account request to update to already exists");

        accountRequestsLogic.createAccountRequest(AccountRequestAttributes
                .builder("David", "TEAMMATES TEST, Singapore", "david@tmt.tmt", "", "")
                .build());

        AccountRequestAttributes.UpdateOptions updateOptionsAlreadyExists = AccountRequestAttributes
                .updateOptionsBuilder("clark@tmt.tmt", "TEAMMATES TEST, Singapore")
                .withEmail("david@tmt.tmt")
                .build();

        assertThrows(EntityAlreadyExistsException.class,
                () -> accountRequestsLogic.updateAccountRequest(updateOptionsAlreadyExists));

        ______TS("failure: new account request is invalid");

        String invalidEmail = "invalid email@tmt.tmt";
        AccountRequestAttributes.UpdateOptions updateOptionsInvalid = AccountRequestAttributes
                .updateOptionsBuilder("clark@tmt.tmt", "TEAMMATES TEST, Singapore")
                .withEmail(invalidEmail)
                .build();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsLogic.updateAccountRequest(updateOptionsInvalid));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, invalidEmail, FieldValidator.EMAIL_FIELD_NAME,
                        FieldValidator.REASON_INCORRECT_FORMAT, FieldValidator.EMAIL_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> accountRequestsLogic.updateAccountRequest(null));

        // clean up
        accountRequestsLogic.deleteAccountRequest("clark@tmt.tmt", "TEAMMATES TEST, Singapore");
        accountRequestsLogic.deleteAccountRequest("david@tmt.tmt", "TEAMMATES TEST, Singapore");
    }

    @Test
    public void testApproveAccountRequest() throws Exception {
        AccountRequestAttributes accountRequest = accountRequestsLogic.createAccountRequest(AccountRequestAttributes
                .builder("Evans", "TMT, Singapore", "evans@tmt.tmt", "", "Comments")
                .build());

        ______TS("typical success case");

        AccountRequestAttributes approvedAccountRequest =
                accountRequestsLogic.approveAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        AccountRequestAttributes actualAccountRequest =
                accountRequestsLogic.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());

        assertEquals(AccountRequestStatus.APPROVED, approvedAccountRequest.getStatus());
        assertNotEquals(accountRequest.getLastProcessedAt(), approvedAccountRequest.getLastProcessedAt());
        assertEquals(AccountRequestStatus.APPROVED, actualAccountRequest.getStatus());
        assertNotEquals(accountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());
        assertEquals(approvedAccountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());

        ______TS("approve the same account request again: status remains APPROVED, lastProcessedAt is updated");

        AccountRequestAttributes newApprovedAccountRequest =
                accountRequestsLogic.approveAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        AccountRequestAttributes newActualAccountRequest =
                accountRequestsLogic.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());

        assertEquals(AccountRequestStatus.APPROVED, newApprovedAccountRequest.getStatus());
        assertEquals(AccountRequestStatus.APPROVED, newActualAccountRequest.getStatus());
        assertNotEquals(approvedAccountRequest.getLastProcessedAt(), newApprovedAccountRequest.getLastProcessedAt());
        assertNotEquals(actualAccountRequest.getLastProcessedAt(), newActualAccountRequest.getLastProcessedAt());

        ______TS("failure: account request not found");

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsLogic.approveAccountRequest("Evans@tmt.tmt", "TMT, Singapore"));

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> accountRequestsLogic.approveAccountRequest(null, "TMT, Singapore"));
        assertThrows(AssertionError.class, () -> accountRequestsLogic.approveAccountRequest("evans@tmt.tmt", null));

        // clean up
        accountRequestsLogic.deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
    }

    @Test
    public void testRejectAccountRequest() throws Exception {
        AccountRequestAttributes accountRequest = accountRequestsLogic.createAccountRequest(AccountRequestAttributes
                .builder("Evans", "TMT, Singapore", "evans@tmt.tmt", "", "Comments")
                .build());

        ______TS("typical success case");

        AccountRequestAttributes rejectedAccountRequest =
                accountRequestsLogic.rejectAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        AccountRequestAttributes actualAccountRequest =
                accountRequestsLogic.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());

        assertEquals(AccountRequestStatus.REJECTED, rejectedAccountRequest.getStatus());
        assertNotEquals(accountRequest.getLastProcessedAt(), rejectedAccountRequest.getLastProcessedAt());
        assertEquals(AccountRequestStatus.REJECTED, actualAccountRequest.getStatus());
        assertNotEquals(accountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());
        assertEquals(rejectedAccountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());

        ______TS("failure: account request not found");

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsLogic.rejectAccountRequest("evans@tmt.tmt", "TMT"));

        // clean up
        accountRequestsLogic.deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
    }

    @Test
    public void testResetAccountRequest() throws Exception {
        AccountRequestAttributes accountRequest = accountRequestsLogic.createAccountRequest(AccountRequestAttributes
                .builder("Evans", "TMT, Singapore", "evans@tmt.tmt", "", "Comments")
                        .withStatus(AccountRequestStatus.REGISTERED)
                        .withLastProcessedAt(Instant.now().minusSeconds(600))
                        .withRegisteredAt(Instant.now())
                .build());

        ______TS("typical success case");

        AccountRequestAttributes resetAccountRequest =
                accountRequestsLogic.resetAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        AccountRequestAttributes actualAccountRequest =
                accountRequestsLogic.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());

        assertEquals(AccountRequestStatus.SUBMITTED, resetAccountRequest.getStatus());
        assertNotEquals(accountRequest.getLastProcessedAt(), resetAccountRequest.getLastProcessedAt());
        assertNull(resetAccountRequest.getRegisteredAt());
        assertEquals(AccountRequestStatus.SUBMITTED, actualAccountRequest.getStatus());
        assertNotEquals(accountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());
        assertNull(actualAccountRequest.getRegisteredAt());
        assertEquals(resetAccountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());

        ______TS("failure: account request not found");

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsLogic.resetAccountRequest("adams@tmt.tmt", "TMT, Singapore"));

        // clean up
        accountRequestsLogic.deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
    }

    @Test
    public void testDeleteAccountRequest() {
        AccountRequestAttributes accountRequest = dataBundle.accountRequests.get("approvedUnregisteredRequest1");

        ______TS("typical success case");

        verifyPresentInDatabase(accountRequest);
        accountRequestsLogic.deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verifyAbsentInDatabase(accountRequest);

        ______TS("success: delete the same account request again");

        accountRequestsLogic.deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verifyAbsentInDatabase(accountRequest);

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsLogic.deleteAccountRequest(null, accountRequest.getInstitute()));
        assertThrows(AssertionError.class,
                () -> accountRequestsLogic.deleteAccountRequest(accountRequest.getEmail(), null));
    }

    @Test
    public void testGetAccountRequestForRegistrationKey() throws Exception {
        AccountRequestAttributes accountRequest = accountRequestsLogic.createAccountRequest(AccountRequestAttributes
                .builder("Frank", "TMT, Singapore", "frank@tmt.tmt", "", "")
                .build());

        ______TS("typical success case");

        AccountRequestAttributes actualAccountRequest =
                accountRequestsLogic.getAccountRequestForRegistrationKey(accountRequest.getRegistrationKey());
        assertEquals(accountRequest, actualAccountRequest);

        ______TS("account request not found");

        assertNull(accountRequestsLogic.getAccountRequestForRegistrationKey("not-found"));

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> accountRequestsLogic.getAccountRequestForRegistrationKey(null));

        // clean up
        accountRequestsLogic.deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
    }

    @Test
    public void testGetAccountRequest() throws Exception {
        AccountRequestAttributes accountRequest = accountRequestsLogic.createAccountRequest(AccountRequestAttributes
                .builder("Ghosh", "TMT, Singapore", "ghosh@tmt.tmt", "", "")
                .build());

        ______TS("typical success case");

        AccountRequestAttributes actualAccountRequest =
                accountRequestsLogic.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        assertEquals(accountRequest, actualAccountRequest);

        ______TS("account request not found");

        assertNull(accountRequestsLogic.getAccountRequest(accountRequest.getEmail(), "not-found"));

        ______TS("failureL: null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsLogic.getAccountRequest(null, accountRequest.getInstitute()));
        assertThrows(AssertionError.class, () -> accountRequestsLogic.getAccountRequest(accountRequest.getEmail(), null));

        // clean up
        accountRequestsLogic.deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
    }

    @Test
    public void testGetAccountRequestsPendingProcessing() {
        AccountRequestAttributes accountRequest1 = dataBundle.accountRequests.get("submittedRequest1");
        AccountRequestAttributes accountRequest2 = dataBundle.accountRequests.get("submittedRequest2");
        AccountRequestAttributes accountRequest3 = dataBundle.accountRequests.get("submittedRequest3");
        AccountRequestAttributes accountRequest4 = dataBundle.accountRequests.get("submittedRequest4");

        List<AccountRequestAttributes> actual = accountRequestsLogic.getAccountRequestsPendingProcessing();

        assertEquals(4, actual.size());
        assertTrue(actual.contains(accountRequest1));
        assertTrue(actual.contains(accountRequest2));
        assertTrue(actual.contains(accountRequest3));
        assertTrue(actual.contains(accountRequest4));
    }

    @Test
    public void testGetAccountRequestsSubmittedWithinPeriod() {
        AccountRequestAttributes accountRequest1 = dataBundle.accountRequests.get("instructorWithoutCourses");
        AccountRequestAttributes accountRequest2 = dataBundle.accountRequests.get("submittedRequest1");
        AccountRequestAttributes accountRequest3 = dataBundle.accountRequests.get("submittedRequest2");
        AccountRequestAttributes accountRequest4 = dataBundle.accountRequests.get("approvedUnregisteredRequest1");

        List<AccountRequestAttributes> actual = accountRequestsLogic.getAccountRequestsSubmittedWithinPeriod(
                TimeHelper.parseInstant("2012-03-30T00:00:00Z"), TimeHelper.parseInstant("2012-04-02T00:00:00Z"));

        assertEquals(4, actual.size());
        assertTrue(actual.contains(accountRequest1));
        assertTrue(actual.contains(accountRequest2));
        assertTrue(actual.contains(accountRequest3));
        assertTrue(actual.contains(accountRequest4));
    }

}
