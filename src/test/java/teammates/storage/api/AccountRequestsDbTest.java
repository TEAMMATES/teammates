package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.storage.entity.AccountRequest;
import teammates.test.GroupNames;

/**
 * Tests for {@link AccountRequestsDb}.
 */
public class AccountRequestsDbTest extends BaseDbTestcase {
    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

    @Test(groups = GroupNames.DB)
    public void getAccountRequest_accountRequestExists_returnsAccountRequest() {
        var accountRequest = given.accountRequest("account-request");
        persistGivenData(given);

        AccountRequest actual = inTransaction(() -> accountRequestsDb.getAccountRequest(accountRequest.id()));

        assertNotNull(actual);
        assertEquals(accountRequest.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getAccountRequest_accountRequestDoesNotExist_returnsNull() {
        given.accountRequest("different-account-request");
        persistGivenData(given);

        AccountRequest actual = inTransaction(
                () -> accountRequestsDb.getAccountRequest(given.uuid("non-existent-account-request")));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void persistAccountRequest_accountRequestIsNew_accountRequestIsPersisted() {
        var accountRequestId = given.uuid("account-request");
        AccountRequest accountRequest = buildDefaultAccountRequest(accountRequestId);

        AccountRequest actual = inTransaction(() -> accountRequestsDb.persistAccountRequest(accountRequest));

        assertEquals(accountRequestId, actual.getId());
        verifyPresentInDatabase(AccountRequest.class, accountRequestId);
    }

    @Test(groups = GroupNames.DB)
    public void getPendingAccountRequests_accountRequestsExist_returnsOnlyPendingRequestsInCreatedAtOrder() {
        Instant now = Instant.now();
        var olderPendingRequest = given.accountRequest("older-pending-request",
                ar -> ar.pending().createdAt(now.minus(2, ChronoUnit.HOURS)));
        var newerPendingRequest = given.accountRequest("newer-pending-request",
                ar -> ar.pending().createdAt(now.minus(1, ChronoUnit.HOURS)));
        given.accountRequest("approved-request", ar -> ar.approved());
        persistGivenData(given);

        List<AccountRequest> actual = inTransaction(accountRequestsDb::getPendingAccountRequests);

        assertEquals(List.of(newerPendingRequest.id(), olderPendingRequest.id()),
                actual.stream().map(AccountRequest::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getAccountRequestByRegistrationKey_accountRequestExists_returnsAccountRequest() {
        var accountRequest = given.accountRequest("account-request",
                ar -> ar.registrationKey("registration-key"));
        given.accountRequest("another-account-request", ar -> ar.registrationKey("another-registration-key"));
        persistGivenData(given);

        AccountRequest actual = inTransaction(
                () -> accountRequestsDb.getAccountRequestByRegistrationKey("registration-key"));

        assertNotNull(actual);
        assertEquals(accountRequest.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void removeAccountRequest_accountRequestExists_accountRequestIsRemoved() {
        var accountRequest = given.accountRequest("account-request");
        persistGivenData(given);

        inTransaction(() -> accountRequestsDb.removeAccountRequest(
                accountRequestsDb.getAccountRequest(accountRequest.id())));

        verifyAbsentInDatabase(AccountRequest.class, accountRequest.id());
    }

    private static AccountRequest buildDefaultAccountRequest(UUID accountRequestId) {
        AccountRequest accountRequest = new AccountRequest(
                "account-request@example.com",
                "Account Request",
                "TEAMMATES Test Institute",
                AccountRequestStatus.PENDING,
                "");
        accountRequest.setId(accountRequestId);
        accountRequest.setRegistrationKey("registration-key:" + accountRequestId);
        return accountRequest;
    }
}
