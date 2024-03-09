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

    private String getLogPrefix() {
        return String.format("%s verifying fields:", sqlEntityClass.getName());
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
        System.out.println(String.format("%s has %d entities with %d pages", getLogPrefix(), countResults, numPages));

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

    protected int deleteAllSqlEntities(List<T> sqlEntities) {
        HibernateUtil.beginTransaction();
        for (T sqlEntity : sqlEntities) {
            HibernateUtil.remove(sqlEntity);
        }

        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
        HibernateUtil.commitTransaction();

        return sqlEntities.size();
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
            System.out.println(String.format("%s No entities available for verification", getLogPrefix()));
            return failures;
        }

        for (int currPageNum = 1; currPageNum <= numPages; currPageNum++) {

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

    protected void deleteAllEntities() {
        int numPages = getNumPages();
        if (numPages == 0) {
            System.out.println(String.format("%s No entities available for deletion", getLogPrefix()));
            return;
        }

        double numEntitiesDeleted = 0;
        for (int currPageNum = 1; currPageNum <= numPages; currPageNum++) {
            System.out.println(String.format("%s Deletion Progress %d %% of %s",
                    getLogPrefix(),
                    (100 * (int) ((float) currPageNum / (float) numPages)),
                    sqlEntityClass.getName()));

            List<T> sqlEntities = lookupSqlEntitiesByPageNumber(currPageNum);
            numEntitiesDeleted += deleteAllSqlEntities(sqlEntities);
        }
        System.out.println(String.format("%s Deleted %d entities",
            getLogPrefix(),
            numEntitiesDeleted
        ));
    }

    /**
     * Main function to run to verify isEqual between sql and datastore DBs.
     */
    protected void runCheckAllEntities(Class<T> sqlEntityClass,
            Class<E> datastoreEntityClass) {
        HibernateUtil.beginTransaction();
        List<Map.Entry<T, E>> failedEntities = checkAllEntitiesForFailures();
        deleteAllEntities();

        System.out.println("========================================");
        if (!failedEntities.isEmpty()) {
            System.err.println(String.format("%s Errors detected", getLogPrefix()));
            for (Map.Entry<T, E> failure : failedEntities) {
                System.err.println("Sql entity: " + failure.getKey() + " datastore entity: " + failure.getValue());
            }
        } else {
            System.out.println(String.format("%s No errors detected", getLogPrefix()));
        }
        HibernateUtil.commitTransaction();
    }

    protected void doOperation() {
        runCheckAllEntities(this.sqlEntityClass, this.datastoreEntityClass);
    }
}
