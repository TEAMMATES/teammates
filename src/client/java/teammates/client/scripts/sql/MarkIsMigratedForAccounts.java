package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.googlecode.objectify.cmd.Query;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import teammates.storage.sqlentity.Account;
import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.HibernateUtil;
// CHECKSTYLE.ON:ImportOrder

/**
 * Marks Datastore Account entities as migrated when a matching SQL Account exists (same googleId).
 * Iterates over SQL accounts, looks up Datastore by googleId, and sets isMigrated=true where not already set.
 */
@SuppressWarnings("PMD")
public class MarkIsMigratedForAccounts extends DatastoreClient {
    /**
     * Batch size to fetch per page.
     */
    protected static final int CONST_SQL_FETCH_BASE_SIZE = 1000;

    /* NOTE
     * Before running this script, enable hibernate.jdbc.fetch_size in HibernateUtil.java
     * for optimized batch-fetching.
     */
    /** Datastore entity class. */
    protected Class<teammates.storage.entity.Account> datastoreEntityClass = teammates.storage.entity.Account.class;

    /** SQL entity class. */
    protected Class<Account> sqlEntityClass = Account.class;

    //    AtomicLong numberOfScannedKey;
    //    AtomicLong numberOfAffectedEntities;
    //    AtomicLong numberOfUpdatedEntities;

    private long entitiesVerified = 0;
    private long entitiesSetToIsMigrated = 0;

    public MarkIsMigratedForAccounts() {
        //        numberOfScannedKey = new AtomicLong();
        //        numberOfAffectedEntities = new AtomicLong();
        //        numberOfUpdatedEntities = new AtomicLong();

        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    private String getLogPrefix() {
        return String.format("%s verifying fields:", sqlEntityClass.getName());
    }

    /**
     * Generate the Datstore id of entity to compare with on Datastore side.
     */
    protected String generateID(Account sqlEntity) {
        return sqlEntity.getGoogleId();
    }

    /**
     * Lookup data store entities.
     */
    protected Map<String, teammates.storage.entity.Account> lookupDataStoreEntities(List<String> datastoreEntitiesIds) {
        return ofy().load().type(teammates.storage.entity.Account.class).ids(datastoreEntitiesIds);
    }

    /**
     * Calculate offset.
     */
    protected int calculateOffset(int pageNum) {
        return (pageNum - 1) * CONST_SQL_FETCH_BASE_SIZE;
    }

    /**
     * Get number of pages in database table.
     */
    protected Query<teammates.storage.entity.Account> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.Account.class);
    }

    /**
     * Get number of pages in database table.
     */
    private int getNumPages() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(sqlEntityClass)));
        long countResults = HibernateUtil.createQuery(countQuery).getSingleResult();
        int numPages = (int) (Math.ceil((double) countResults / (double) CONST_SQL_FETCH_BASE_SIZE));
        log(String.format("Has %d entities with %d pages", countResults, numPages));

        return numPages;
    }

    /**
     * Sort SQL entities by id in ascending order and return entities on page.
     * @param pageNum page in a sorted entities tables
     * @return list of SQL entities on page num
     */
    protected List<Account> lookupSqlEntitiesByPageNumber(int pageNum) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Account> pageQuery = cb.createQuery(sqlEntityClass);

        // sort by id to maintain stable order.
        Root<Account> root = pageQuery.from(sqlEntityClass);
        pageQuery.select(root);
        List<Order> orderList = new LinkedList<>();
        orderList.add(cb.asc(root.get("id")));
        pageQuery.orderBy(orderList);

        // perform query with pagination
        TypedQuery<Account> query = HibernateUtil.createQuery(pageQuery);
        query.setFirstResult(calculateOffset(pageNum));
        query.setMaxResults(CONST_SQL_FETCH_BASE_SIZE);

        return query.getResultList();
    }

    /**
     * Iterates SQL accounts, looks up Datastore by googleId. Records failures when SQL has an account
     * but Datastore does not. Sets isMigrated=true on Datastore accounts that have a matching SQL account.
     */
    protected List<Map.Entry<Account, teammates.storage.entity.Account>> checkAllEntitiesForFailures() {
        // WARNING: failures list might lead to OoM if too many entities,
        // but okay since will fail anyway.
        List<Map.Entry<Account, teammates.storage.entity.Account>> failures = new LinkedList<>();
        int numPages = getNumPages();
        if (numPages == 0) {
            log("No entities available for verification");
            return failures;
        }

        List<teammates.storage.entity.Account> setMigratedAccountBuffer = new ArrayList<>();

        /* Query SQL and compare against datastore */
        for (int currPageNum = 1; currPageNum <= numPages; currPageNum++) {
            log(String.format("Scanning Progress %d %%",
                     (int) ((float) currPageNum / (float) numPages * 100)));

            long startTimeForSql = System.currentTimeMillis();
            List<Account> sqlEntities = lookupSqlEntitiesByPageNumber(currPageNum);
            long endTimeForSql = System.currentTimeMillis();
            log("Querying for SQL for page " + currPageNum + " took "
                    + (endTimeForSql - startTimeForSql) + " milliseconds");

            List<String> datastoreEntitiesIds = sqlEntities.stream()
                    .map(entity -> generateID(entity)).collect(Collectors.toList());

            long startTimeForDatastore = System.currentTimeMillis();
            Map<String, teammates.storage.entity.Account> datastoreEntities = lookupDataStoreEntities(datastoreEntitiesIds);
            long endTimeForDatastore = System.currentTimeMillis();
            log("Querying for Datastore for page " + currPageNum + " took "
                    + (endTimeForDatastore - startTimeForDatastore) + " milliseconds");

            entitiesVerified += sqlEntities.size();
            for (Account sqlEntity : sqlEntities) {
                teammates.storage.entity.Account datastoreEntity = datastoreEntities.get(generateID(sqlEntity));
                if (datastoreEntity == null) {
                    entitiesVerified -= 1;
                    failures.add(new AbstractMap.SimpleEntry<>(sqlEntity, null));
                    continue;
                }

                if (!datastoreEntity.isMigrated()) {
                    datastoreEntity.setMigrated(true);
                    setMigratedAccountBuffer.add(datastoreEntity);
                }
            }

            /* Flushing the buffer */
            if (!setMigratedAccountBuffer.isEmpty()) {
                long startTimeForDatastoreFlushing = System.currentTimeMillis();
                entitiesSetToIsMigrated += setMigratedAccountBuffer.size();
                ofy().save().entities(setMigratedAccountBuffer).now();
                setMigratedAccountBuffer.clear();
                long endTimeForDatastoreFlushing = System.currentTimeMillis();
                log("Flushing for datastore " + (endTimeForDatastoreFlushing - startTimeForDatastoreFlushing)
                        + " milliseconds");
            }
        }

        return failures;
    }

    /**
     * Main function to run to verify isEqual between sql and datastore DBs.
     */
    protected void runCheckAllEntities(Class<Account> sqlEntityClass,
            Class<teammates.storage.entity.Account> datastoreEntityClass) {
        HibernateUtil.beginTransaction();
        long checkStartTime = System.currentTimeMillis();
        List<Map.Entry<Account, teammates.storage.entity.Account>> failedEntities = checkAllEntitiesForFailures();

        System.out.println("========================================");
        if (!failedEntities.isEmpty()) {
            log("Errors detected");
            for (Map.Entry<Account, teammates.storage.entity.Account> failure : failedEntities) {
                log("Sql entity: " + failure.getKey() + " datastore entity: " + failure.getValue());
            }
        } else {
            log("No errors detected");
        }

        long checkEndTime = System.currentTimeMillis();

        log("Entity took " + (checkEndTime - checkStartTime) + " milliseconds to verify");
        log("Verified " + entitiesVerified + " SQL entities successfully");
        log("Number of datastore accounts set to isMigrated " + entitiesSetToIsMigrated);

        HibernateUtil.commitTransaction();
    }

    /**
     * Log a line.
     * @param logLine the line to log
     */
    protected void log(String logLine) {
        System.out.println(String.format("%s %s", getLogPrefix(), logLine));
    }

    /**
     * Run the operation.
     */
    @Override
    protected void doOperation() {
        runCheckAllEntities(this.sqlEntityClass, this.datastoreEntityClass);
    }

    public static void main(String[] args) {
        MarkIsMigratedForAccounts operation = new MarkIsMigratedForAccounts();
        operation.doOperationRemotely();
    }
}
