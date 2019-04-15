package teammates.client.scripts;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.Account;

/**
 * Script to migrate a googleId to a new googleId for a single account.
 */
public class SingleAccountGoogleIdMigrationScript extends GoogleIdMigrationBaseScript {

    // if the user uses his own email address to register Google Services, the googleId is his own email address
    // if the user registers a Google Account (e.g. 'alice@gmail.com'),
    // the googleId is the string before '@gmail.com' (e.g. 'alice')
    private String fromAccountGoogleId = "alice@gmail.tmt";
    private String toAccountGoogleId = "bob@gmail.tmt";

    public static void main(String[] args) throws Exception {
        new SingleAccountGoogleIdMigrationScript().doOperationRemotely();
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected Query<Account> getFilterQuery() {
        return ofy().load().type(Account.class).filterKey(Key.create(Account.class, fromAccountGoogleId));
    }

    @Override
    protected boolean isMigrationOfGoogleIdNeeded(Account account) {
        return account.getGoogleId().equals(fromAccountGoogleId);
    }

    @Override
    protected String generateNewGoogleId(Account oldAccount) {
        return toAccountGoogleId;
    }
}
