package teammates.client.scripts.sql;

import com.googlecode.objectify.cmd.Query;

/**
 * DataMigrationForAccountSql
 */
public class DataMigrationForAccountSql extends
        DataMigrationEntitiesBaseScriptSql<teammates.storage.entity.Account, teammates.storage.sqlentity.Account> {
    public static void main(String[] args) {
        SeedDb seedDb = new SeedDb();
        try {
            seedDb.seedSetup();
            // seedDb.setupDbLayer();
            // seedDb.setupObjectify();
            // seedDb.persistTypicalDataBundle();

            // seedDb.verify();
            DataMigrationForAccountSql script = new DataMigrationForAccountSql();
            script.doOperation();

            seedDb.seedTearDown();
            // seedDb.tearDownObjectify();
            // seedDb.tearDownLocalDatastoreHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Query<teammates.storage.entity.Account> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.Account.class);
    }

    @Override
    protected boolean isPreview() {
        return false;
    }

    @Override
    protected boolean isMigrationNeeded(teammates.storage.entity.Account account) {
        return true;
    }

    @Override
    protected void migrateEntity(teammates.storage.entity.Account oldAccount) {
        teammates.storage.sqlentity.Account newAccount = new teammates.storage.sqlentity.Account(
                oldAccount.getGoogleId(),
                oldAccount.getName(),
                oldAccount.getEmail());
        saveEntityDeferred(newAccount);
    }
}