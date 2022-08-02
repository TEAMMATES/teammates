package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.storage.entity.AccountRequest;

/**
 * Script to migrate old account request entities to new account request entities.
 */
public class DataMigrationForAccountRequestFormUpgrade extends DataMigrationEntitiesBaseScript<AccountRequest> {

    public static void main(String[] args) {
        new DataMigrationForAccountRequestFormUpgrade().doOperationRemotely();
    }

    @Override
    protected Query<AccountRequest> getFilterQuery() {
        return ofy().load().type(AccountRequest.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(AccountRequest accountRequest) {
        return accountRequest.getStatus() == null;
    }

    @Override
    protected void migrateEntity(AccountRequest accountRequest) {
        accountRequest.setHomePageUrl("");
        accountRequest.setComments("");
        if (accountRequest.getRegisteredAt() == null) {
            accountRequest.setStatus(AccountRequestStatus.APPROVED);
        } else {
            accountRequest.setStatus(AccountRequestStatus.REGISTERED);
        }
        accountRequest.setLastProcessedAt(accountRequest.getCreatedAt());

        saveEntityDeferred(accountRequest);
    }

}
