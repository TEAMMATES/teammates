package teammates.it.sqllogic.core;

import java.time.Instant;

import org.testng.annotations.Test;

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
    public void testResetAccountRequest()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {

        ______TS("success: create account request and update registeredAt field");

        String name = "name lee";
        String email = "email@gmail.com";
        String institute = "institute";

        AccountRequest toReset = accountRequestsLogic.createAccountRequest(name, email, institute);
        AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

        toReset.setRegisteredAt(Instant.now());
        toReset = accountRequestsDb.getAccountRequest(email, institute);

        assertNotNull(toReset);
        assertNotNull(toReset.getRegisteredAt());

        ______TS("success: reset account request that already exists");

        AccountRequest resetted = accountRequestsLogic.resetAccountRequest(email, institute);

        assertNull(resetted.getRegisteredAt());

        ______TS("success: test delete account request");

        accountRequestsLogic.deleteAccountRequest(email, institute);

        assertNull(accountRequestsLogic.getAccountRequest(email, institute));

        ______TS("failure: reset account request that does not exist");

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsLogic.resetAccountRequest(name, institute));
    }
}
