package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.Account;

/**
 * Script to append Google IDs that do not contain "@" with "@gmail.com".
 */
public class DataMigrationForGoogleIdToGmail extends GoogleIdMigrationBaseScript {

    public static void main(String[] args) {
        new DataMigrationForGoogleIdToGmail().doOperationRemotely();
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected Query<Account> getFilterQuery() {
        return ofy().load().type(Account.class);
    }

    @Override
    protected boolean isMigrationOfGoogleIdNeeded(Account account) {
        return !account.getGoogleId().contains("@");
    }

    @Override
    protected String generateNewGoogleId(Account oldAccount) {
        return oldAccount.getGoogleId().concat("@gmail.com");
    }
}
