package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Account;

/**
 * Class for verifying account attributes.
 */
@SuppressWarnings("PMD")
public class VerifyAccountAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<Account, teammates.storage.sqlentity.Account> {

    private static final String READ_NOTIFICATION_FIELD = "readNotifications";

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
     * Verify account fields.
     */
    public boolean verifyAccountFields(teammates.storage.sqlentity.Account sqlEntity, Account datastoreEntity) {
        try {
            // UUID for account is not checked, as datastore ID is google ID
            return sqlEntity.getName().equals(datastoreEntity.getName())
                    && sqlEntity.getGoogleId().equals(datastoreEntity.getGoogleId())
                    && sqlEntity.getEmail().equals(datastoreEntity.getEmail());
        } catch (IllegalArgumentException iae) {
            return false;
        }

    }

    @Override
    protected List<teammates.storage.sqlentity.Account> lookupSqlEntitiesByPageNumber(int pageNum) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.Account> pageQuery = cb.createQuery(sqlEntityClass);

        // sort by id to maintain stable order.
        Root<teammates.storage.sqlentity.Account> root = pageQuery.from(sqlEntityClass);
        pageQuery.select(root);
        List<Order> orderList = new LinkedList<>();
        orderList.add(cb.asc(root.get("id")));
        pageQuery.orderBy(orderList);

        // perform query with pagination
        TypedQuery<teammates.storage.sqlentity.Account> query = HibernateUtil.createQuery(pageQuery);
        query.setFirstResult(calculateOffset(pageNum));
        query.setMaxResults(CONST_SQL_FETCH_BASE_SIZE);

        // Fetch read notifications eagerly with one join
        root.fetch(READ_NOTIFICATION_FIELD, JoinType.LEFT);
        return query.getResultList();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Account sqlEntity, Account datastoreEntity) {
        if (!verifyAccountFields(sqlEntity, datastoreEntity)) {
            return false;
        }

        return true;

        // Not verifying read notification as current datastore implementation does not remove notifications
        // that have been deleted from account entities. During migration, the notification will not be
        // migrated since it is deleted and read notification will fail during migration (foreign key error)
        // causing the verification to fail

        // Map<String, Instant> datastoreReadNotifications = datastoreEntity.getReadNotifications();
        // List<ReadNotification> sqlReadNotifications = sqlEntity.getReadNotifications();

        // List<Instant> datastoreEndTimes = new ArrayList<>(datastoreReadNotifications.values());
        // Collections.sort(datastoreEndTimes);

        // List<Instant> sqlEndTimes = new ArrayList<>();
        // for (ReadNotification sqlReadNotification : sqlReadNotifications) {
        //     sqlEndTimes.add(sqlReadNotification.getNotification().getEndTime());
        // }
        // Collections.sort(sqlEndTimes);

        // return datastoreEndTimes.equals(sqlEndTimes);
    }
}
