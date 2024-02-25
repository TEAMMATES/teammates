package teammates.client.scripts.sql;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.BaseEntity;

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


    @Override
    protected void doOperation() {
        HashMap<
            Class<? extends BaseEntity>, Class<? extends teammates.storage.sqlentity.BaseEntity>> entities = 
                new HashMap<Class<? extends BaseEntity>, Class<? extends teammates.storage.sqlentity.BaseEntity>>();

        entities.put(teammates.storage.entity.Account.class, teammates.storage.sqlentity.Account.class);
        entities.put(teammates.storage.entity.AccountRequest.class, teammates.storage.sqlentity.AccountRequest.class);
        entities.put(teammates.storage.entity.UsageStatistics.class, teammates.storage.sqlentity.UsageStatistics.class);
        entities.put(teammates.storage.entity.Notification.class, teammates.storage.sqlentity.Notification.class);
        
        

        for (Map.Entry<Class<? extends BaseEntity>, Class<? extends teammates.storage.sqlentity.BaseEntity>> entry : entities.entrySet()) {
             // fetch number of entities in datastore
            Class<? extends BaseEntity> objectifyClass = entry.getKey();
            Class<? extends teammates.storage.sqlentity.BaseEntity> sqlClass = entry.getValue();

            int objectifyEntityCount = ofy().load().type(objectifyClass).count();
            // fetch number of entities in postgres
           
            Long postgresEntityCount = countPostgresEntities(sqlClass);


            System.out.println("========================================");
            System.out.println(objectifyClass.getName());
            System.out.println("Objectify count: " + objectifyEntityCount);
            System.out.println("Postgres count: " + postgresEntityCount);
            
        }
        ofy().load().type(teammates.storage.entity.UsageStatistics.class);
    }
}
