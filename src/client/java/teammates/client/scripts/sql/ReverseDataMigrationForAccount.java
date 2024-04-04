package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.HibernateUtil;

// CHECKSTYLE.ON:ImportOrder
/**
 * Protected methods may be overriden.
 */
@SuppressWarnings("PMD")
public class ReverseDataMigrationForAccount
        extends DatastoreClient {

    /*
     * NOTE
     * Before running the verification, please enable hibernate.jdbc.fetch_size in
     * HibernateUtil.java
     * for optimized batch-fetching.
     */

    /**
     * Batch size to fetch per page.
     */
    protected static final int CONST_SQL_FETCH_BASE_SIZE = 1000;

    // Set the start time to be the time of publishing the release.
    // Script will migrate accounts created AFTER this time back to datastore.
    private static final String START_TIME_STRING = "2024-03-29 00:48:00.000 +0800";

    private static final Instant START_TIME = parseStartTime(START_TIME_STRING);

    public ReverseDataMigrationForAccount() {
        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    private static Instant parseStartTime(String startTimeString) {
        if (startTimeString == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");
        return Instant.from(formatter.parse(startTimeString));
    }

    private Map<String, teammates.storage.entity.Account> lookupDataStoreEntities(List<String> datastoreEntitiesIds) {
        return ofy().load().type(teammates.storage.entity.Account.class).ids(datastoreEntitiesIds);
    }

    private String generateID(teammates.storage.sqlentity.Account sqlEntity) {
        return sqlEntity.getGoogleId();
    }

    /**
     * Get Account with {@code createdTime} after {@code startTime}.
     */
    protected List<teammates.storage.sqlentity.Account> getNewAccounts() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.Account> cr = cb.createQuery(
                teammates.storage.sqlentity.Account.class);
        Root<teammates.storage.sqlentity.Account> root = cr.from(teammates.storage.sqlentity.Account.class);
        cr.select(root).where(cb.greaterThan(root.get("createdAt"), START_TIME));

        TypedQuery<teammates.storage.sqlentity.Account> query = HibernateUtil.createQuery(cr);
        return query.getResultList();
    }

    /**
     * Reverse migrate accounts to datastore.
     */
    protected void reverseMigrateToDatastore() {
        // WARNING: failures list might lead to OoM if too many entities,
        // but okay since will fail anyway.
        System.out.println("===================reverseMigrateToDatastore=====================");
        HibernateUtil.beginTransaction();
        List<teammates.storage.sqlentity.Account> sqlEntities = getNewAccounts();
        HibernateUtil.commitTransaction();

        List<teammates.storage.entity.Account> entitiesSavingBuffer = new LinkedList<>();
        int count = 0;
        List<String> datastoreEntitiesIds = sqlEntities.stream()
                .map(entity -> generateID(entity)).collect(Collectors.toList());
        Map<String, teammates.storage.entity.Account> datastoreEntities = lookupDataStoreEntities(datastoreEntitiesIds);
        for (teammates.storage.sqlentity.Account sqlEntity : sqlEntities) {
            teammates.storage.entity.Account datastoreEntity = datastoreEntities.get(generateID(sqlEntity));
            if (datastoreEntity == null) {
                teammates.storage.entity.Account newEntity = new teammates.storage.entity.Account(
                        sqlEntity.getGoogleId(),
                        sqlEntity.getName(), sqlEntity.getEmail(), new HashMap<String, Instant>(), true);
                entitiesSavingBuffer.add(newEntity);
                count++;
            } else {
                continue;
            }

        }
        log("Saving " + count + " account entities to datastore");
        ofy().save().entities(entitiesSavingBuffer).now();
    }

    private String getLogPrefix() {
        return String.format("Account reverse migrating:");
    }

    /**
     * Log a line.
     *
     * @param logLine the line to log
     */
    protected void log(String logLine) {
        System.out.println(String.format("%s %s", getLogPrefix(), logLine));
    }

    /**
     * Run the operation.
     */
    protected void doOperation() {
        reverseMigrateToDatastore();
    }

    public static void main(String[] args) {
        ReverseDataMigrationForAccount script = new ReverseDataMigrationForAccount();
        script.doOperationRemotely();
    }
}
