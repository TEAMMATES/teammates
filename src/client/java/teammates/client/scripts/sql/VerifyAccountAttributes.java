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
        return sqlEntity.getId().toString();
    }

    public static void main(String[] args) {
        VerifyAccountAttributes script = new VerifyAccountAttributes();
        script.doOperationRemotely();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Account sqlEntity, Account datastoreEntity) {
        try {
            // UUID for account is not checked, as datastore ID is google ID
            return sqlEntity.getName() == datastoreEntity.getName()
                && sqlEntity.getGoogleId() == datastoreEntity.getGoogleId()
                && sqlEntity.getEmail() == datastoreEntity.getEmail();
        } catch (IllegalArgumentException iae) {
            return false;
        } 
    }
}
