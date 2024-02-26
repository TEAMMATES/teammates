package teammates.client.scripts.sql;

import teammates.storage.entity.Account;

public class VerifyAccountAttributes extends VerifyNonCourseEntityAttributesBaseScript<Account, 
    teammates.storage.sqlentity.Account> {
    
    static String dataStoreIdFieldName = "notificationId";

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
}
