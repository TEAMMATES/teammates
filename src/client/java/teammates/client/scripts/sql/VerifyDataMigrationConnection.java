package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.UsageStatistics;
import teammates.storage.sqlentity.Notification;
// CHECKSTYLE.ON:ImportOrder

/**
 * Verification of the data migration connection.
 */
@SuppressWarnings("PMD")
public class VerifyDataMigrationConnection extends DatastoreClient {

    private VerifyDataMigrationConnection() {
        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    public static void main(String[] args) throws Exception {
        new VerifyDataMigrationConnection().doOperationRemotely();
    }

    /**
     * Verifies the SQL connection.
     */
    protected void verifySqlConnection() {
        // Assert count of dummy request is 0
        Long testAccountRequestCount = countPostgresEntities(teammates.storage.sqlentity.AccountRequest.class);
        System.out.println(String.format("Num of account request in SQL: %d", testAccountRequestCount));

        // Write 1 dummy account request
        System.out.println("Writing 1 dummy account request to SQL");
        teammates.storage.sqlentity.AccountRequest newEntity = new teammates.storage.sqlentity.AccountRequest(
                "dummy-teammates-account-request-email@gmail.com",
                "dummy-teammates-account-request",
                "dummy-teammates-institute",
                AccountRequestStatus.PENDING,
                "dummy-comments");
        HibernateUtil.beginTransaction();
        HibernateUtil.persist(newEntity);
        HibernateUtil.commitTransaction();

        // Assert count of dummy request is 1
        testAccountRequestCount = countPostgresEntities(teammates.storage.sqlentity.AccountRequest.class);
        System.out.println(String.format("Num of account request in SQL after inserting: %d", testAccountRequestCount));

        // Delete dummy account request
        HibernateUtil.beginTransaction();
        HibernateUtil.remove(newEntity);
        HibernateUtil.commitTransaction();

        // Assert count of dummy request is 0
        testAccountRequestCount = countPostgresEntities(teammates.storage.sqlentity.AccountRequest.class);
        System.out.println(String.format("Num of account request in SQL after removing: %d", testAccountRequestCount));

    }

    /**
     * Verifies the number of notifications.
     */
    protected void verifyCountsInDatastore() {
        System.out.println(
                String.format("Num of notifications in Datastore: %d", ofy().load().type(Notification.class).count()));
        System.out.println(String.format("Num of usage statistics in Datastore: %d", ofy().load()
                .type(UsageStatistics.class).count()));
    }

    @Override
    protected void doOperation() {
        verifyCountsInDatastore();
        verifySqlConnection();
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
}
