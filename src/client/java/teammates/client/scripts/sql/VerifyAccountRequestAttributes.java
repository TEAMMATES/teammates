package teammates.client.scripts.sql;

import teammates.storage.entity.AccountRequest;

public class VerifyAccountRequestAttributes extends VerifyNonCourseEntityAttributesBaseScript<AccountRequest, 
    teammates.storage.sqlentity.AccountRequest> {
    
    public VerifyAccountRequestAttributes() {
        super(AccountRequest.class, 
            teammates.storage.sqlentity.AccountRequest.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.AccountRequest sqlEntity) {
        return sqlEntity.getId().toString();
    }

    public static void main(String[] args) {
        VerifyAccountRequestAttributes script = new VerifyAccountRequestAttributes();
        script.doOperationRemotely();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.AccountRequest sqlEntity, AccountRequest datastoreEntity) {
        try {
            // UUID for account is not checked, as datastore ID is email%institute
            return sqlEntity.getRegistrationKey() == datastoreEntity.getRegistrationKey()
                && sqlEntity.getName() == datastoreEntity.getName()
                && sqlEntity.getEmail() == datastoreEntity.getEmail()
                && sqlEntity.getInstitute() == datastoreEntity.getInstitute()
                && sqlEntity.getRegisteredAt() == datastoreEntity.getRegisteredAt();
        } catch (IllegalArgumentException iae) {
            return false;
        } 
    }
}
