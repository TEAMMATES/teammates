
package teammates.storage.sqlapi;

import teammates.common.util.HibernateUtil;
<<<<<<< HEAD
import teammates.common.util.JsonUtils;
=======
>>>>>>> v9-migration
import teammates.common.util.Logger;
import teammates.storage.sqlentity.BaseEntity;

/**
 * Base class for all classes performing CRUD operations against the database.
 *
 * @param <E> subclass of BaseEntity
 */
class EntitiesDb<E extends BaseEntity> {

<<<<<<< HEAD
    static final String ERROR_CREATE_ENTITY_ALREADY_EXISTS = "Trying to create an entity that exists: %s";
    static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Entity: ";

=======
>>>>>>> v9-migration
    static final Logger log = Logger.getLogger();

    /**
     * Copy the state of the given object onto the persistent object with the same identifier.
     * If there is no persistent instance currently associated with the session, it will be loaded.
     */
<<<<<<< HEAD
    E merge(E entity) {
        assert entity != null;

        E newEntity = HibernateUtil.getSessionFactory().getCurrentSession().merge(entity);
        log.info("Entity saves: " + JsonUtils.toJson(entity));
=======
    protected <T extends E> T merge(T entity) {
        assert entity != null;

        T newEntity = HibernateUtil.merge(entity);
        log.info("Entity saved: " + entity.toString());
>>>>>>> v9-migration
        return newEntity;
    }

    /**
     * Associate {@code entity} with the persistence context.
     */
<<<<<<< HEAD
    void persist(E entity) {
        assert entity != null;

        HibernateUtil.getSessionFactory().getCurrentSession().persist(entity);
        log.info("Entity persisted: " + JsonUtils.toJson(entity));
=======
    protected void persist(E entity) {
        assert entity != null;

        HibernateUtil.persist(entity);
        log.info("Entity persisted: " + entity.toString());
>>>>>>> v9-migration
    }

    /**
     * Deletes {@code entity} from persistence context.
     */
<<<<<<< HEAD
    void delete(E entity) {
        assert entity != null;

        HibernateUtil.getSessionFactory().getCurrentSession().remove(entity);
        log.info("Entity deleted: " + JsonUtils.toJson(entity));
=======
    protected void delete(E entity) {
        assert entity != null;

        HibernateUtil.remove(entity);
        log.info("Entity deleted: " + entity.toString());
>>>>>>> v9-migration
    }
}
