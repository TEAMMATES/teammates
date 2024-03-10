package teammates.client.scripts.sql;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import teammates.storage.entity.Account;
import teammates.storage.sqlentity.ReadNotification;

/**
 * Class for verifying account attributes
 */
public class VerifyAccountAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<Account, teammates.storage.sqlentity.Account> {

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

    /**
     * Verify account fields
     */
    public boolean verifyAccountFields(teammates.storage.sqlentity.Account sqlEntity, Account datastoreEntity) {
        if (datastoreEntity instanceof teammates.storage.entity.Account) {
            teammates.storage.entity.Account acc = (teammates.storage.entity.Account) datastoreEntity;
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

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Account sqlEntity, Account datastoreEntity) {
        if (!verifyAccountFields(sqlEntity, datastoreEntity)) {
            return false;
        }

        Map<String, Instant> datastoreReadNotifications = datastoreEntity.getReadNotifications();
        List<ReadNotification> sqlReadNotifications = sqlEntity.getReadNotifications();

        List<Instant> datastoreEndTimes = new ArrayList<Instant>(datastoreReadNotifications.values());
        Collections.sort(datastoreEndTimes);

        List<Instant> sqlEndTimes = new ArrayList<>();
        for (ReadNotification sqlReadNotification : sqlReadNotifications) {
            sqlEndTimes.add(sqlReadNotification.getNotification().getEndTime());
        }
        Collections.sort(sqlEndTimes);

        return datastoreEndTimes.equals(sqlEndTimes);
    }
}
