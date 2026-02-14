package teammates.client.scripts.sql;

import teammates.storage.entity.AccountRequest;

/**
 * Class for verifying account request attributes.
 */
@SuppressWarnings("PMD")
public class VerifyAccountRequestAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<AccountRequest, teammates.storage.sqlentity.AccountRequest> {

    public VerifyAccountRequestAttributes() {
        super(AccountRequest.class,
                teammates.storage.sqlentity.AccountRequest.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.AccountRequest sqlEntity) {
        return teammates.storage.entity.AccountRequest.generateId(
                sqlEntity.getEmail(), sqlEntity.getInstitute());
    }

    public static void main(String[] args) {
        VerifyAccountRequestAttributes script = new VerifyAccountRequestAttributes();
        script.doOperationRemotely();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.AccountRequest sqlEntity, AccountRequest datastoreEntity) {
        if (datastoreEntity != null) {
            boolean matchingCreatedAtTimestamp;
            boolean matchingRegisteredAtTimestamp;

            if (sqlEntity.getCreatedAt() == null || datastoreEntity.getCreatedAt() == null) {
                matchingCreatedAtTimestamp = sqlEntity.getCreatedAt() == datastoreEntity.getCreatedAt();
            } else {
                matchingCreatedAtTimestamp = sqlEntity.getCreatedAt().equals(datastoreEntity.getCreatedAt());
            }

            if (sqlEntity.getRegisteredAt() == null || datastoreEntity.getRegisteredAt() == null) {
                 matchingRegisteredAtTimestamp = sqlEntity.getRegisteredAt() == datastoreEntity.getRegisteredAt();
            } else {
                 matchingRegisteredAtTimestamp = sqlEntity.getRegisteredAt().equals(datastoreEntity.getRegisteredAt());
            }

            // UUID for account is not checked, as datastore ID is email%institute
            return sqlEntity.getName().equals(datastoreEntity.getName())
                && sqlEntity.getEmail().equals(datastoreEntity.getEmail())
                && sqlEntity.getInstitute().equals(datastoreEntity.getInstitute())
                && matchingCreatedAtTimestamp
                && matchingRegisteredAtTimestamp;
        } else {
            return false;
        }
    }
}
