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
            // UUID for account is not checked, as datastore ID is email%institute
            if (!sqlEntity.getName().equals(datastoreEntity.getName())) {
                return false;
            }
            if (!sqlEntity.getEmail().equals(datastoreEntity.getEmail())) {
                return false;
            }
            if (!sqlEntity.getInstitute().equals(datastoreEntity.getInstitute())) {
                return false;
            }
            // only need to check getRegisteredAt() as the other fields must not be null.
            if (sqlEntity.getRegisteredAt() == null) {
                if (datastoreEntity.getRegisteredAt() != null) {
                    return false;
                }
            } else if (!sqlEntity.getRegisteredAt().equals(datastoreEntity.getRegisteredAt())) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
