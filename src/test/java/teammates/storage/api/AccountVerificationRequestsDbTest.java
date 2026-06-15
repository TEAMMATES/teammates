package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Institute;
import teammates.test.GroupNames;

/**
 * Tests for {@link AccountVerificationRequestsDb}.
 */
public class AccountVerificationRequestsDbTest extends BaseDbTestcase {
    private final AccountVerificationRequestsDb accountVerificationRequestsDb = AccountVerificationRequestsDb.inst();

    @Test(groups = GroupNames.DB)
    public void getAccountVerificationRequest_accountVerificationRequestExists_returnsAccountVerificationRequest() {
        var accountVerificationRequest = given.accountVerificationRequest("account-request");
        persistGivenData(given);

        AccountVerificationRequest actual = inTransaction(
                () -> accountVerificationRequestsDb.getAccountVerificationRequest(accountVerificationRequest.id()));

        assertNotNull(actual);
        assertEquals(accountVerificationRequest.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getAccountVerificationRequest_accountVerificationRequestDoesNotExist_returnsNull() {
        given.accountVerificationRequest("different-account-request");
        persistGivenData(given);

        AccountVerificationRequest actual = inTransaction(
                () -> accountVerificationRequestsDb.getAccountVerificationRequest(
                        given.uuid("non-existent-account-request")));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void persistAccountVerificationRequest_accountVerificationRequestIsNew_accountVerificationRequestIsPersisted() {
        var institute = given.institute("institute");
        var account = given.account("account");
        persistGivenData(given);
        var accountVerificationRequestId = given.uuid("account-request");
        AccountVerificationRequest accountVerificationRequest =
                buildDefaultAccountVerificationRequest(accountVerificationRequestId);

        AccountVerificationRequest actual = inTransaction(() -> {
            getEntity(Institute.class, institute.id()).addAccountVerificationRequest(accountVerificationRequest);
            getEntity(Account.class, account.id()).addAccountVerificationRequest(accountVerificationRequest);
            return accountVerificationRequestsDb.persistAccountVerificationRequest(accountVerificationRequest);
        });

        assertEquals(accountVerificationRequestId, actual.getId());
        verifyPresentInDatabase(AccountVerificationRequest.class, accountVerificationRequestId);
    }

    @Test(groups = GroupNames.DB)
    public void getPendingAccountVerificationRequests_accountVerificationRequestsExist_returnsOnlyPending() {
        Instant now = Instant.now();
        var olderPendingRequest = given.accountVerificationRequest("older-pending-request",
                ar -> ar.pending().createdAt(now.minus(2, ChronoUnit.HOURS)));
        var newerPendingRequest = given.accountVerificationRequest("newer-pending-request",
                ar -> ar.pending().createdAt(now.minus(1, ChronoUnit.HOURS)));
        given.accountVerificationRequest("approved-request", ar -> ar.approved());
        persistGivenData(given);

        List<AccountVerificationRequest> actual =
                inTransaction(accountVerificationRequestsDb::getPendingAccountVerificationRequests);

        assertEquals(List.of(newerPendingRequest.id(), olderPendingRequest.id()),
                actual.stream().map(AccountVerificationRequest::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void removeAccountVerificationRequest_accountVerificationRequestExists_accountVerificationRequestIsRemoved() {
        var accountVerificationRequest = given.accountVerificationRequest("account-request");
        persistGivenData(given);

        inTransaction(() -> accountVerificationRequestsDb.removeAccountVerificationRequest(
                accountVerificationRequestsDb.getAccountVerificationRequest(accountVerificationRequest.id())));

        verifyAbsentInDatabase(AccountVerificationRequest.class, accountVerificationRequest.id());
    }

    @Test(groups = GroupNames.DB)
    public void getApprovedRequestsByAccountId_approvedAndOtherStatuses_returnsOnlyApproved() {
        var account = given.account("account");
        var approvedRequest = given.accountVerificationRequest("approved-request",
                ar -> ar.account(account.alias()).approved());
        given.accountVerificationRequest("pending-request",
                ar -> ar.account(account.alias()).pending());
        persistGivenData(given);

        List<AccountVerificationRequest> actual = inTransaction(
                () -> accountVerificationRequestsDb.getApprovedRequestsByAccountId(account.id()));

        assertEquals(1, actual.size());
        assertEquals(approvedRequest.id(), actual.get(0).getId());
    }

    @Test(groups = GroupNames.DB)
    public void getApprovedRequestsByAccountId_noApprovedRequests_returnsEmpty() {
        var account = given.account("account");
        given.accountVerificationRequest("pending-request",
                ar -> ar.account(account.alias()).pending());
        persistGivenData(given);

        List<AccountVerificationRequest> actual = inTransaction(
                () -> accountVerificationRequestsDb.getApprovedRequestsByAccountId(account.id()));

        assertEquals(0, actual.size());
    }

    @Test(groups = GroupNames.DB)
    public void hasApprovedRequestForAccountAndInstitute_approvedRequestExists_returnsTrue() {
        var account = given.account("account");
        var institute = given.institute("institute");
        given.accountVerificationRequest("approved-request",
                ar -> ar.account(account.alias()).institute(institute.alias()).approved());
        persistGivenData(given);

        boolean actual = inTransaction(
                () -> accountVerificationRequestsDb.hasApprovedRequestForAccountAndInstitute(
                        account.id(), institute.id()));

        assertEquals(true, actual);
    }

    @Test(groups = GroupNames.DB)
    public void hasApprovedRequestForAccountAndInstitute_pendingRequestExists_returnsFalse() {
        var account = given.account("account");
        var institute = given.institute("institute");
        given.accountVerificationRequest("pending-request",
                ar -> ar.account(account.alias()).institute(institute.alias()).pending());
        persistGivenData(given);

        boolean actual = inTransaction(
                () -> accountVerificationRequestsDb.hasApprovedRequestForAccountAndInstitute(
                        account.id(), institute.id()));

        assertEquals(false, actual);
    }

    @Test(groups = GroupNames.DB)
    public void hasApprovedRequestForAccountAndInstitute_differentInstitute_returnsFalse() {
        var account = given.account("account");
        var institute = given.institute("institute");
        var otherInstitute = given.institute("other-institute");
        given.accountVerificationRequest("approved-request",
                ar -> ar.account(account.alias()).institute(institute.alias()).approved());
        persistGivenData(given);

        boolean actual = inTransaction(
                () -> accountVerificationRequestsDb.hasApprovedRequestForAccountAndInstitute(
                        account.id(), otherInstitute.id()));

        assertEquals(false, actual);
    }

    @Test(groups = GroupNames.DB)
    public void getCreatedAtTimestampsForTimeRange_accountVerificationRequestsExist_returnsTimestampsInRange() {
        given.accountVerificationRequest("account-request-1");
        given.accountVerificationRequest("account-request-2");
        persistGivenData(given);

        Instant start = Instant.now().minus(1, ChronoUnit.HOURS);
        Instant end = Instant.now().plus(1, ChronoUnit.HOURS);

        List<Instant> actual = inTransaction(
                () -> accountVerificationRequestsDb.getCreatedAtTimestampsForTimeRange(start, end));

        assertEquals(2, actual.size());
    }

    private static AccountVerificationRequest buildDefaultAccountVerificationRequest(UUID accountVerificationRequestId) {
        AccountVerificationRequest accountVerificationRequest = new AccountVerificationRequest(
                "account-request@example.com",
                "Account Verification Request",
                AccountVerificationRequestStatus.PENDING,
                "");
        accountVerificationRequest.setId(accountVerificationRequestId);
        return accountVerificationRequest;
    }
}
