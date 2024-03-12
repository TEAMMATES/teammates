package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import java.util.HashMap;
import java.util.Map;

import com.google.cloud.datastore.QueryResults;
import com.googlecode.objectify.cmd.Query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Account;
import teammates.storage.entity.BaseEntity;
// CHECKSTYLE.ON:ImportOrder

/**
 * Verify the counts of non-course entities are correct.
 */
@SuppressWarnings("PMD")
public class VerifyNonCourseEntityCounts extends DatastoreClient {
    private VerifyNonCourseEntityCounts() {
        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    public static void main(String[] args) throws Exception {
        new VerifyNonCourseEntityCounts().doOperationRemotely();
    }

    private void printEntityVerification(String className, int datastoreCount, long psqlCount) {
        System.out.println("========================================");
        System.out.println(className);
        System.out.println("Objectify count: " + datastoreCount);
        System.out.println("Postgres count: " + psqlCount);
        System.out.println("Correct number of rows?: " + (datastoreCount == psqlCount));
    }

    private Long countPostgresEntities(Class<? extends teammates.storage.sqlentity.BaseEntity> entity) {
        HibernateUtil.beginTransaction();
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<? extends teammates.storage.sqlentity.BaseEntity> root = cr.from(entity);

        cr.select(cb.count(root));

        Long count = HibernateUtil.createQuery(cr).getSingleResult();
        HibernateUtil.commitTransaction();
        return count;
    }

    private void verifyReadNotification() {
        Query<Account> accountQuery = ofy().load().type(Account.class);
        QueryResults<Account> iterator = accountQuery.iterator();

        int datastoreReadNotifications = 0;

        while (iterator.hasNext()) {
            Account acc = iterator.next();
            datastoreReadNotifications += acc.getReadNotifications().size();
        }

        Long postgresReadNotifications = countPostgresEntities(
                teammates.storage.sqlentity.ReadNotification.class);

        printEntityVerification(teammates.storage.sqlentity.ReadNotification.class.getSimpleName(),
                datastoreReadNotifications, postgresReadNotifications);
    }

    @Override
    protected void doOperation() {
        Map<Class<? extends BaseEntity>, Class<? extends teammates.storage.sqlentity.BaseEntity>> entities =
                new HashMap<Class<? extends BaseEntity>, Class<? extends teammates.storage.sqlentity.BaseEntity>>();

        entities.put(teammates.storage.entity.Account.class, teammates.storage.sqlentity.Account.class);
        entities.put(teammates.storage.entity.AccountRequest.class, teammates.storage.sqlentity.AccountRequest.class);
        entities.put(teammates.storage.entity.UsageStatistics.class, teammates.storage.sqlentity.UsageStatistics.class);
        entities.put(teammates.storage.entity.Notification.class, teammates.storage.sqlentity.Notification.class);

        // Compare datastore "table" to postgres table for each entity
        for (Map.Entry<Class<? extends BaseEntity>, Class<? extends teammates.storage.sqlentity.BaseEntity>> entry : entities
                .entrySet()) {
            Class<? extends BaseEntity> objectifyClass = entry.getKey();
            Class<? extends teammates.storage.sqlentity.BaseEntity> sqlClass = entry.getValue();

            int objectifyEntityCount = ofy().load().type(objectifyClass).count();
            Long postgresEntityCount = countPostgresEntities(sqlClass);

            printEntityVerification(objectifyClass.getSimpleName(), objectifyEntityCount, postgresEntityCount);
        }

        // Read notification did not have its own entity in datastore, therefore has to
        // be counted differently
        verifyReadNotification();
    }
}
