package teammates.client.scripts.sql;

import java.util.AbstractMap; 
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.HibernateUtil;

/** 
 * Protected methods may be overriden
 */
public abstract class VerifyNonCourseEntityAttributesBaseScript
    <E extends teammates.storage.entity.BaseEntity, 
        T extends teammates.storage.sqlentity.BaseEntity> extends DatastoreClient {

    protected String dataStoreIdFieldName;
    protected Class<E> datastoreEntityClass; 
    protected Class<T> sqlEntityClass; 

    public VerifyNonCourseEntityAttributesBaseScript(String dataStoreIdFieldName, 
        Class<T> sqlEntityClass, Class<E> datastoreEntityClass) {
        this.dataStoreIdFieldName = dataStoreIdFieldName; 
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

    protected E lookupDataStoreEntity(String dataStoreIdFieldName, String idVal, Class<E> datastoreEntityClass) {
        return ofy().load().type(datastoreEntityClass).filter(dataStoreIdFieldName, idVal).first().now();
    }

    protected List<T> lookupSqlEntities(Class<T> sqlEntityClass) {
        HibernateUtil.beginTransaction();
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<T> cr = cb.createQuery(sqlEntityClass);
        Root<T> root = cr.from(sqlEntityClass);

        cr.select(root);

        List<T> sqlEntities = HibernateUtil.createQuery(cr).getResultList();
        HibernateUtil.commitTransaction();
        
        return sqlEntities;
    } 

    /** 
     * Idea: lookup sql side, have all the sql entities for 
     * each sql entity, lookup datastore entity
     * if does not match, return failure 
     */
    protected List<Map.Entry<T, E>> checkAllEntitiesForFailures(Class<T> sqlEntityClass,
        Class<E> datastoreEntityClass) {
        List<T> sqlEntities = lookupSqlEntities(sqlEntityClass); 
        
        List<Map.Entry<T, E>> failures = new LinkedList<>(); 

        for (T sqlEntity : sqlEntities) {
            String entityId = generateID(sqlEntity);
            E datastoreEntity = lookupDataStoreEntity(dataStoreIdFieldName, entityId, datastoreEntityClass); 

            if (datastoreEntity == null) {
                failures.add(new AbstractMap.SimpleEntry<T, E>(sqlEntity, null));
                continue;
            }
            boolean isEqual = sqlEntity.equals(datastoreEntity); 
            if (!isEqual) {
                failures.add(new AbstractMap.SimpleEntry<T,E>(sqlEntity, datastoreEntity)); 
                continue; 
            }
        }
        return failures; 
    }

    /** 
     * Main function to run to verify isEqual between sql and datastore DBs. 
     */
    protected void runCheckAllEntities(Class<T> sqlEntityClass,
        Class<E> datastoreEntityClass) {
        List<Map.Entry<T, E>> failedEntities = checkAllEntitiesForFailures(sqlEntityClass, datastoreEntityClass); 
        System.out.println("========================================");
        if (!failedEntities.isEmpty()) {
            System.err.println("Errors detected for entity: " + sqlEntityClass.getName());
            for (Map.Entry<T, E> failure : failedEntities) {
                System.err.println("Sql entity: " + failure.getKey() + " datastore entity: " + failure.getValue()); 
            }
        } else {
            System.out.println("No errors detected for entity: " + sqlEntityClass.getName()); 
        }
    }

    protected void doOperation() {
        runCheckAllEntities(this.sqlEntityClass, this.datastoreEntityClass); 
    }
}
