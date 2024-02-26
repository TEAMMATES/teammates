package teammates.client.scripts.sql;

import teammates.storage.entity.Account;

public class VerifyAccountAttributes extends VerifyNonCourseEntityAttributesBaseScript<Account, 
    teammates.storage.sqlentity.Account> {
    
    public VerifyAccountAttributes() {
        super(Account.class, 
            teammates.storage.sqlentity.Account.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Account sqlEntity) {
        return sqlEntity.getGoogleId();
    }

    public static void main(String[] args) {
        VerifyAccountAttributes script = new VerifyAccountAttributes();
        script.doOperationRemotely();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Account sqlEntity, Account datastoreEntity) {
        if (datastoreEntity instanceof teammates.storage.entity.Account) {
            teammates.storage.entity.Account acc =
                (teammates.storage.entity.Account) datastoreEntity;
            try {
                // UUID for account is not checked, as datastore ID is google ID
                return sqlEntity.getName().equals(acc.getName())
                    && sqlEntity.getGoogleId().equals(acc.getGoogleId())
                    && sqlEntity.getEmail().equals(acc.getEmail());
            } catch (IllegalArgumentException iae) {
                return false;
            }
        } else {
            return false;
        }
    }
}
