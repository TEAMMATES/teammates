package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        var account = given.account("account");
        persistGivenData(given);

        Account actual = inTransaction(() -> accountsDb.getAccount(account.id()));

        assertNotNull(actual);
        assertEquals(account.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getAccount_accountDoesNotExist_returnsNull() {
        given.account("different-account");
        persistGivenData(given);

        Account actual = inTransaction(() -> accountsDb.getAccount(given.uuid("non-existent-account")));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void getAccountByAuthIdentity_accountExists_returnsAccount() {
        var account = given.account("account", a -> a
                .authIdentity(Provider.TEAMMATES_DEV, "account-subject", "tenant-id"));
        persistGivenData(given);

        Account actual = inTransaction(() -> accountsDb.getAccountByAuthIdentity(
                Provider.TEAMMATES_DEV, "account-subject", "tenant-id"));

        assertNotNull(actual);
        assertEquals(account.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getAccountByAuthIdentity_accountDoesNotExist_returnsNull() {
        given.account("account", a -> a
                .authIdentity(Provider.TEAMMATES_DEV, "account-subject", "tenant-id"));
        persistGivenData(given);

        Account actual = inTransaction(() -> accountsDb.getAccountByAuthIdentity(
                Provider.TEAMMATES_DEV, "non-existent-subject", "tenant-id"));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void getAccountByAuthIdentity_nullTenantId_matchesOnlyNullTenantId() {
        var accountWithNullTenant = given.account("account-with-null-tenant", a -> a
                .authIdentity(Provider.TEAMMATES_DEV, "shared-subject", null));
        given.account("account-with-tenant", a -> a
                .authIdentity(Provider.TEAMMATES_DEV, "shared-subject", "tenant-id"));
        persistGivenData(given);

        Account actual = inTransaction(() -> accountsDb.getAccountByAuthIdentity(
                Provider.TEAMMATES_DEV, "shared-subject", null));

        assertNotNull(actual);
        assertEquals(accountWithNullTenant.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void upsertAccount_accountDoesNotExist_accountIsInserted() {
        var accountId = given.uuid("account");
        Account account = buildDefaultAccount(accountId);

        Account actual = inTransaction(() -> accountsDb.upsertAccount(account));

        assertEquals(accountId, actual.getId());
        verifyPresentInDatabase(Account.class, accountId);
    }

    @Test(groups = GroupNames.DB)
    public void upsertAccount_accountExists_returnsExistingAccount() {
        var existingAccount = given.account("account", a -> a
                .googleId("original-google-id")
                .email("original@example.com")
                .authIdentity(Provider.TEAMMATES_DEV, "shared-subject", null));
        persistGivenData(given);

        Account updatedAccount = new Account(
                "should-not-update-google-id",
                Provider.TEAMMATES_DEV,
                "shared-subject",
                null,
                "Should Not Update Name",
                "should-not-update@example.com");

        Account actual = inTransaction(() -> accountsDb.upsertAccount(updatedAccount));

        assertEquals(existingAccount.id(), actual.getId());
        assertEquals("original@example.com", actual.getEmail());
        assertNotEquals("Should Not Update Name", actual.getName());
        assertEquals(Account.NO_TENANT, actual.getTenantId());
    }

    @Test(groups = GroupNames.DB)
    public void persistAccount_accountIsNew_accountIsPersisted() {
        var accountId = given.uuid("account");
        Account account = buildDefaultAccount(accountId);

        Account actual = inTransaction(() -> accountsDb.persistAccount(account));

        assertEquals(accountId, actual.getId());
        verifyPresentInDatabase(Account.class, accountId);
    }

    @Test(groups = GroupNames.DB)
    public void persistAccount_accountIdExists_throwsException() {
        var account = given.account("account");
        persistGivenData(given);
        Account duplicateAccount = buildDefaultAccount(account.id());

        assertThrowsInTransaction(ConstraintViolationException.class, () -> accountsDb.persistAccount(duplicateAccount));
    }

    @Test(groups = GroupNames.DB)
    public void removeAccount_accountExists_accountIsRemoved() {
        var account = given.account("account");
        persistGivenData(given);

        inTransaction(() -> accountsDb.removeAccount(accountsDb.getAccount(account.id())));

        verifyAbsentInDatabase(Account.class, account.id());
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
