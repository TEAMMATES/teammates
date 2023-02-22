
package teammates.storage.sqlapi;

import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.BaseEntity;

/**
 * Base class for all classes performing CRUD operations against the database.
 *
 * @param <E> subclass of BaseEntity
 */
class EntitiesDb<E extends BaseEntity> {

    static final String ERROR_CREATE_ENTITY_ALREADY_EXISTS = "Trying to create an entity that exists: %s";
    static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Entity: ";

    static final Logger log = Logger.getLogger();

    /**
     * Copy the state of the given object onto the persistent object with the same identifier.
     * If there is no persistent instance currently associated with the session, it will be loaded.
     */
    E merge(E entity) {
        assert entity != null;

        E newEntity = HibernateUtil.merge(entity);
        log.info("Entity saves: " + JsonUtils.toJson(entity));
        return newEntity;
    }

    /**
     * Associate {@code entity} with the persistence context.
     */
    void persist(E entity) {
        assert entity != null;

        HibernateUtil.persist(entity);
        log.info("Entity persisted: " + JsonUtils.toJson(entity));
    }

    /**
     * Deletes {@code entity} from persistence context.
     */
    void delete(E entity) {
        assert entity != null;

        HibernateUtil.remove(entity);
        log.info("Entity deleted: " + JsonUtils.toJson(entity));
    }
}
