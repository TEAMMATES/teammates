package teammates.client.scripts.sql;

import java.util.AbstractMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.HibernateUtil;

/**
 * Protected methods may be overriden
 */
public abstract class VerifyNonCourseEntityAttributesBaseScript<E extends teammates.storage.entity.BaseEntity, T extends teammates.storage.sqlentity.BaseEntity>
        extends DatastoreClient {

    protected Class<E> datastoreEntityClass;
    protected Class<T> sqlEntityClass;

    private String getLogPrefix() {
        return String.format("%s verifying fields:", sqlEntityClass.getName());
    }

    public VerifyNonCourseEntityAttributesBaseScript(
            Class<E> datastoreEntityClass, Class<T> sqlEntityClass) {
        this.datastoreEntityClass = datastoreEntityClass;
        this.sqlEntityClass = sqlEntityClass;

        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    /**
     * Generate the Datstore id of entity to compare with on Datastore side.
     */
    protected abstract String generateID(T sqlEntity);

    /**
     * Compares the sqlEntity with the datastoreEntity
     */
    protected abstract boolean equals(T sqlEntity, E datastoreEntity);

    protected E lookupDataStoreEntity(String datastoreEntityId) {
        return ofy().load().type(datastoreEntityClass).id(datastoreEntityId).now();
    }

    private static int SQL_FETCH_BATCH_SIZE = 500;

    private int calculateOffset(int pageNum) {
        return (pageNum - 1) * SQL_FETCH_BATCH_SIZE;
    }

    /**
     * Get number of pages in database table.
     */
    private int getNumPages() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(sqlEntityClass)));
        long countResults = HibernateUtil.createQuery(countQuery).getSingleResult().longValue();
        int numPages = (int) (Math.ceil((double) countResults / (double) SQL_FETCH_BATCH_SIZE));
        log(String.format("Has %d entities with %d pages", countResults, numPages));

        return numPages;
    }

    private List<T> lookupSqlEntitiesByPageNumber(int pageNum) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<T> pageQuery = cb.createQuery(sqlEntityClass);

        // sort by createdAt to maintain stable order.
        Root<T> root = pageQuery.from(sqlEntityClass);
        pageQuery.select(root);
        List<Order> orderList = new LinkedList<>();
        orderList.add(cb.asc(root.get("createdAt")));
        pageQuery.orderBy(orderList);

        // perform query with pagination
        TypedQuery<T> query = HibernateUtil.createQuery(pageQuery);
        query.setFirstResult(calculateOffset(pageNum));
        query.setMaxResults(SQL_FETCH_BATCH_SIZE);

        return query.getResultList();
    }

    /**
     * Idea: lookup sql side, have all the sql entities for
     * each sql entity, lookup datastore entity
     * if does not match, return failure
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
                    (100 * (int) ((float) currPageNum / (float) numPages))
                ));

            List<T> sqlEntities = lookupSqlEntitiesByPageNumber(currPageNum);

            for (T sqlEntity : sqlEntities) {
                String entityId = generateID(sqlEntity);
                E datastoreEntity = lookupDataStoreEntity(entityId);

                if (datastoreEntity == null) {
                    failures.add(new AbstractMap.SimpleEntry<T, E>(sqlEntity, null));
                    continue;
                }

                boolean isEqual = equals(sqlEntity, datastoreEntity);
                if (!isEqual) {
                    failures.add(new AbstractMap.SimpleEntry<T, E>(sqlEntity, datastoreEntity));
                    continue;
                }
            }
        }

        return failures;
    }

    /**
     * Main function to run to verify isEqual between sql and datastore DBs.
     */
    protected void runCheckAllEntities(Class<T> sqlEntityClass,
            Class<E> datastoreEntityClass) {
        HibernateUtil.beginTransaction();
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
        HibernateUtil.commitTransaction();
    }

    protected void log(String logLine) {
        System.out.println(String.format("%s %s", getLogPrefix(), logLine));
    }

    protected void doOperation() {
        runCheckAllEntities(this.sqlEntityClass, this.datastoreEntityClass);
    }
}
