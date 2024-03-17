package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.HibernateUtil;
// CHECKSTYLE.ON:ImportOrder

/**
 * Protected methods may be overriden.
 * @param <E> Datastore entity
 * @param <T> SQL entity
 */
@SuppressWarnings("PMD")
public abstract class VerifyNonCourseEntityAttributesBaseScript<E extends teammates.storage.entity.BaseEntity,
        T extends teammates.storage.sqlentity.BaseEntity>
        extends DatastoreClient {

    /* NOTE
     * Before running the verification, please enable hibernate.jdbc.fetch_size in HibernateUtil.java
     * for optimized batch-fetching.
    */

    /**
     * Batch size to fetch per page.
     */
    protected static final int CONST_SQL_FETCH_BASE_SIZE = 1000;

    /** Datastore entity class. */
    protected Class<E> datastoreEntityClass;

    /** SQL entity class. */
    protected Class<T> sqlEntityClass;

    private long entitiesVerified = 0;

    public VerifyNonCourseEntityAttributesBaseScript(
            Class<E> datastoreEntityClass, Class<T> sqlEntityClass) {
        this.datastoreEntityClass = datastoreEntityClass;
        this.sqlEntityClass = sqlEntityClass;

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
    protected abstract String generateID(T sqlEntity);

    /**
     * Compares the sqlEntity with the datastoreEntity.
     */
    protected abstract boolean equals(T sqlEntity, E datastoreEntity);

    /**
     * Lookup data store entities.
     */
    protected Map<String, E> lookupDataStoreEntities(List<String> datastoreEntitiesIds) {
        return ofy().load().type(datastoreEntityClass).ids(datastoreEntitiesIds);
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
    private int getNumPages() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(sqlEntityClass)));
        long countResults = HibernateUtil.createQuery(countQuery).getSingleResult().longValue();
        int numPages = (int) (Math.ceil((double) countResults / (double) CONST_SQL_FETCH_BASE_SIZE));
        log(String.format("Has %d entities with %d pages", countResults, numPages));

        return numPages;
    }

    /**
     * Sort SQL entities by id in ascending order and return entities on page.
     * @param pageNum page in a sorted entities tables
     * @return list of SQL entities on page num
     */
    protected List<T> lookupSqlEntitiesByPageNumber(int pageNum) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<T> pageQuery = cb.createQuery(sqlEntityClass);

        // sort by id to maintain stable order.
        Root<T> root = pageQuery.from(sqlEntityClass);
        pageQuery.select(root);
        List<Order> orderList = new LinkedList<>();
        orderList.add(cb.asc(root.get("id")));
        pageQuery.orderBy(orderList);

        // perform query with pagination
        TypedQuery<T> query = HibernateUtil.createQuery(pageQuery);
        query.setFirstResult(calculateOffset(pageNum));
        query.setMaxResults(CONST_SQL_FETCH_BASE_SIZE);

        return query.getResultList();
    }

    /**
     * Lookup sql side, have all the sql entities for each sql entity, lookup
     * datastore entity.
     * If does not match, return failure.
     */
    protected List<Map.Entry<T, E>> checkAllEntitiesForFailures() {
        // WARNING: failures list might lead to OoM if too many entities,
        // but okay since will fail anyway.
        List<Map.Entry<T, E>> failures = new LinkedList<>();
        int numPages = getNumPages();
        if (numPages == 0) {
            log("No entities available for verification");
            return failures;
        }

        for (int currPageNum = 1; currPageNum <= numPages; currPageNum++) {
            log(String.format("Verification Progress %d %%",
                     (int) ((float) currPageNum / (float) numPages * 100)));

            long startTimeForSql = System.currentTimeMillis();
            List<T> sqlEntities = lookupSqlEntitiesByPageNumber(currPageNum);
            long endTimeForSql = System.currentTimeMillis();
            log("Querying for SQL for page " + currPageNum + " took "
                    + (endTimeForSql - startTimeForSql) + " milliseconds");

            List<String> datastoreEntitiesIds = sqlEntities.stream()
                    .map(entity -> generateID(entity)).collect(Collectors.toList());

            long startTimeForDatastore = System.currentTimeMillis();
            Map<String, E> datastoreEntities = lookupDataStoreEntities(datastoreEntitiesIds);
            long endTimeForDatastore = System.currentTimeMillis();
            log("Querying for Datastore for page " + currPageNum + " took "
                    + (endTimeForDatastore - startTimeForDatastore) + " milliseconds");

            long startTimeForEquals = System.currentTimeMillis();
            entitiesVerified += sqlEntities.size();
            for (T sqlEntity : sqlEntities) {
                E datastoreEntity = datastoreEntities.get(generateID(sqlEntity));
                if (datastoreEntity == null) {
                    entitiesVerified -= 1;
                    failures.add(new AbstractMap.SimpleEntry<T, E>(sqlEntity, null));
                    continue;
                }

                boolean isEqual = equals(sqlEntity, datastoreEntity);
                if (!isEqual) {
                    entitiesVerified -= 1;
                    failures.add(new AbstractMap.SimpleEntry<T, E>(sqlEntity, datastoreEntity));
                    continue;
                }
            }
            long endTimeForEquals = System.currentTimeMillis();
            log("Verifying SQL and Datastore for page " + currPageNum + " took "
                    + (endTimeForEquals - startTimeForEquals) + " milliseconds");
        }
        return failures;
    }

    /**
     * Main function to run to verify isEqual between sql and datastore DBs.
     */
    protected void runCheckAllEntities(Class<T> sqlEntityClass,
            Class<E> datastoreEntityClass) {
        HibernateUtil.beginTransaction();
        long checkStartTime = System.currentTimeMillis();
        List<Map.Entry<T, E>> failedEntities = checkAllEntitiesForFailures();

        System.out.println("========================================");
        if (!failedEntities.isEmpty()) {
            log("Errors detected");
            for (Map.Entry<T, E> failure : failedEntities) {
                log("Sql entity: " + failure.getKey() + " datastore entity: " + failure.getValue());
            }
        } else {
            log("No errors detected");
        }

        long checkEndTime = System.currentTimeMillis();
        log("Entity took " + (checkEndTime - checkStartTime) + " milliseconds to verify");
        log("Verified " + entitiesVerified + " SQL entities successfully");
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
    protected void doOperation() {
        runCheckAllEntities(this.sqlEntityClass, this.datastoreEntityClass);
    }
}
