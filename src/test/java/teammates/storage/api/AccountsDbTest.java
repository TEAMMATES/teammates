package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.testng.annotations.Test;

import teammates.common.datatransfer.Provider;
import teammates.storage.entity.Account;
import teammates.test.GroupNames;

/**
 * Tests for {@link AccountsDb}.
 */
public class AccountsDbTest extends BaseDbTestcase {
    private final AccountsDb accountsDb = AccountsDb.inst();

    @Test(groups = GroupNames.DB)
    public void getAccount_accountExists_returnsAccount() {
        UUID accountId = given.account("account");
        persistGivenData(given);

        Account actual = inTransaction(() -> accountsDb.getAccount(accountId));

        assertNotNull(actual);
        assertEquals(accountId, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getAccount_accountDoesNotExist_returnsNull() {
        given.account("different-account");
        persistGivenData(given);

        Account actual = inTransaction(() -> accountsDb.getAccount(given.uuid("non-existent-account")));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void persistAccount_accountIsNew_accountIsPersisted() {
        UUID accountId = given.uuid("account");
        Account account = buildDefaultAccount(accountId);

        Account actual = inTransaction(() -> accountsDb.persistAccount(account));

        assertEquals(accountId, actual.getId());
        verifyPresentInDatabase(Account.class, accountId);
    }

    @Test(groups = GroupNames.DB)
    public void persistAccount_accountIdExists_throwsException() {
        UUID accountId = given.account("account");
        persistGivenData(given);
        Account account = buildDefaultAccount(accountId);

        assertThrowsInTransaction(ConstraintViolationException.class, () -> accountsDb.persistAccount(account));
    }

    @Test(groups = GroupNames.DB)
    public void removeAccount_accountExists_accountIsRemoved() {
        UUID accountId = given.account("account");
        persistGivenData(given);

        inTransaction(() -> accountsDb.removeAccount(accountsDb.getAccount(accountId)));

        verifyAbsentInDatabase(Account.class, accountId);
    }

    private static Account buildDefaultAccount(UUID accountId) {
        Account account = new Account(
                accountId.toString(),
                Provider.TEAMMATES_DEV,
                "subject",
                "tenant-id",
                "Account Name",
                "account@example.com");
        account.setId(accountId);
        return account;
    }
}
