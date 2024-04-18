package teammates.it.sqllogic.core;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.sqllogic.core.AccountRequestsLogic;
import teammates.storage.sqlapi.AccountRequestsDb;
import teammates.storage.sqlentity.AccountRequest;

/**
 * SUT: {@link AccountRequestsLogic}.
 */
public class AccountRequestsLogicIT extends BaseTestCaseWithSqlDatabaseAccess {

    private AccountRequestsLogic accountRequestsLogic = AccountRequestsLogic.inst();

    @Test
    public void testGetAccountRequest_nonExistentAccountRequest_returnsNull() {
        UUID id = UUID.randomUUID();
        AccountRequest actualAccountRequest = accountRequestsLogic.getAccountRequest(id);
        assertNull(actualAccountRequest);
    }

    @Test
    public void testGetAccountRequest_existingAccountRequest_getsSuccessfully() throws InvalidParametersException {
        AccountRequest expectedAccountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        UUID id = expectedAccountRequest.getId();
        accountRequestsLogic.createAccountRequest(expectedAccountRequest);
        AccountRequest actualAccountRequest = accountRequestsLogic.getAccountRequest(id);
        assertEquals(expectedAccountRequest, actualAccountRequest);
    }

    @Test
    public void testResetAccountRequest()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {

        ______TS("success: create account request and update registeredAt field");

        String name = "name lee";
        String email = "email@gmail.com";
        String institute = "institute";
        AccountRequestStatus status = AccountRequestStatus.PENDING;
        String comments = "comments";

        AccountRequest toReset = accountRequestsLogic.createAccountRequest(name, email, institute, status, comments);
        AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

        toReset.setRegisteredAt(Instant.now());
        UUID id = toReset.getId();
        toReset = accountRequestsDb.getAccountRequest(id);

        assertNotNull(toReset);
        assertNotNull(toReset.getRegisteredAt());

        ______TS("success: reset account request that already exists");

        AccountRequest resetted = accountRequestsLogic.resetAccountRequest(id);

        assertNull(resetted.getRegisteredAt());

        ______TS("success: test delete account request");

        accountRequestsLogic.deleteAccountRequest(toReset.getId());

        assertNull(accountRequestsLogic.getAccountRequest(toReset.getId()));

        ______TS("failure: reset account request that does not exist");

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsLogic.resetAccountRequest(id));
    }
}
