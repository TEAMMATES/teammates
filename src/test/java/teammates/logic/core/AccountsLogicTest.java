package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.Provider;
import teammates.storage.api.AccountsDb;
import teammates.storage.entity.Account;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountsLogic}.
 */
public class AccountsLogicTest extends BaseTestCase {

    private AccountsLogic accountsLogic = AccountsLogic.inst();

    private AccountsDb accountsDb;

    @BeforeMethod
    public void setUpMethod() {
        accountsDb = mock(AccountsDb.class);
        UsersLogic usersLogic = mock(UsersLogic.class);
        accountsLogic.initLogicDependencies(accountsDb, usersLogic);
    }

    @Test
    public void testDeleteAccount_accountExists_success() {
        Account account = getTypicalAccount();

        when(accountsLogic.getAccount(account.getId())).thenReturn(account);

        accountsLogic.deleteAccount(account.getId());

        verify(accountsDb, times(1)).removeAccount(account);
    }

    @Test
    public void testCreateOrGetAccountForEmail_accountExists_success() {
        Account account = getTypicalAccount();
        String email = account.getEmail();
        Provider provider = account.getProvider();
        String subject = account.getSubject();
        String tenantId = account.getTenantId();

        when(accountsDb.getAccountByGoogleId(email)).thenReturn(account);

        Account result = accountsLogic.createOrGetAccount(provider, subject, tenantId, email);

        assertEquals(result, account);
    }

    @Test
    public void testCreateOrGetAccountForEmail_accountDoesNotExist_success() {
        String email = "nonexistent@example.com";
        Provider provider = Provider.TEAMMATES_DEV;
        String subject = "nonexistentSubject";
        String tenantId = "nonexistentTenantId";

        when(accountsDb.getAccountByGoogleId(email)).thenReturn(null);
        when(accountsDb.persistAccount(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountsLogic.createOrGetAccount(provider, subject, tenantId, email);

        verify(accountsDb, times(1)).persistAccount(result);
        assertNotNull(result);
        assertEquals(result.getEmail(), email);
    }
}
