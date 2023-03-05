package teammates.sqllogic.core;

import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Account;

/**
 * Handles operations related to accounts.
 *
 * @see Account
 * @see AccountsDb
 */
public final class AccountsLogic {

    private static final AccountsLogic instance = new AccountsLogic();

    private AccountsDb accountsDb;

    private AccountsLogic() {
        // prevent initialization
    }

    public static AccountsLogic inst() {
        return instance;
    }

    void initLogicDependencies(AccountsDb accountsDb) {
        this.accountsDb = accountsDb;
    }

    /**
     * Gets an account.
     */
    public Account getAccount(UUID id) {
        assert id != null;
        return accountsDb.getAccount(id);
    }

    /**
     * Creates an account.
     *
     * @return the created account
     * @throws InvalidParametersException if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the database.
     */
    public Account createAccount(Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert account != null;
        return accountsDb.createAccount(account);
    }
}
