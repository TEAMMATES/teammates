package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.storage.entity.AccountRequest;
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
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        UUID id = expectedAccountRequest.getId();
        inTransaction(() -> accountRequestsLogic.createAccountRequest(expectedAccountRequest));
        AccountRequest actualAccountRequest = inTransaction(() -> accountRequestsLogic.getAccountRequest(id));
        assertEquals(expectedAccountRequest, actualAccountRequest);
    }
}
