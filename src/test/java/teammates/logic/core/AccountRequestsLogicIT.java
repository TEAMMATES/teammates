package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Institute;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.GroupNames;

/**
 * SUT: {@link AccountRequestsLogic}.
 */
public class AccountRequestsLogicIT extends BaseTestCaseWithDatabaseAccess {

    private AccountRequestsLogic accountRequestsLogic = AccountRequestsLogic.inst();

    @Test(groups = GroupNames.INTEGRATION)
    public void testGetAccountRequest_nonExistentAccountRequest_returnsNull() {
        UUID id = UUID.randomUUID();
        AccountRequest actualAccountRequest = inTransaction(() -> accountRequestsLogic.getAccountRequest(id));
        assertNull(actualAccountRequest);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testGetAccountRequest_existingAccountRequest_getsSuccessfully() {
        AccountRequest expectedAccountRequest =
                new AccountRequest("test@gmail.com", "name", AccountRequestStatus.PENDING, "comments");
        new Institute("institute", "SG").addAccountRequest(expectedAccountRequest);
        UUID id = expectedAccountRequest.getId();
        inTransaction(() -> {
            HibernateUtil.persist(expectedAccountRequest.getInstitute());
            return accountRequestsLogic.createAccountRequest(expectedAccountRequest);
        });
        AccountRequest actualAccountRequest = inTransaction(() -> accountRequestsLogic.getAccountRequest(id));
        assertEquals(expectedAccountRequest, actualAccountRequest);
    }
}
