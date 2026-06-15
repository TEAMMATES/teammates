package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.datatransfer.Provider;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Institute;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.GroupNames;

/**
 * SUT: {@link AccountVerificationRequestsLogic}.
 */
public class AccountVerificationRequestsLogicIT extends BaseTestCaseWithDatabaseAccess {

    private AccountVerificationRequestsLogic accountVerificationRequestsLogic = AccountVerificationRequestsLogic.inst();

    @Test(groups = GroupNames.INTEGRATION)
    public void testGetAccountVerificationRequest_nonExistentAccountVerificationRequest_returnsNull() {
        UUID id = UUID.randomUUID();
        AccountVerificationRequest actualAccountVerificationRequest = inTransaction(() -> accountVerificationRequestsLogic.getAccountVerificationRequest(id));
        assertNull(actualAccountVerificationRequest);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testGetAccountVerificationRequest_existingAccountVerificationRequest_getsSuccessfully() {
        AccountVerificationRequest expectedAccountVerificationRequest =
                new AccountVerificationRequest("test@gmail.com", "name", AccountVerificationRequestStatus.PENDING, "comments");
        new Institute("institute", "SG").addAccountVerificationRequest(expectedAccountVerificationRequest);
        Account account = new Account("test-google-id", Provider.TEAMMATES_DEV,
                UUID.randomUUID().toString(), "tenant-id", "name", "test@gmail.com");
        account.addAccountVerificationRequest(expectedAccountVerificationRequest);
        UUID id = expectedAccountVerificationRequest.getId();
        inTransaction(() -> {
            HibernateUtil.persist(expectedAccountVerificationRequest.getInstitute());
            HibernateUtil.persist(account);
            return accountVerificationRequestsLogic.createAccountVerificationRequest(expectedAccountVerificationRequest);
        });
        AccountVerificationRequest actualAccountVerificationRequest = inTransaction(() -> accountVerificationRequestsLogic.getAccountVerificationRequest(id));
        assertEquals(expectedAccountVerificationRequest, actualAccountVerificationRequest);
    }
}
