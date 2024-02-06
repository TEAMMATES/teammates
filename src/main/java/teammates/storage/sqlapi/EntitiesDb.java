
package teammates.storage.sqlapi;

import com.google.common.base.Objects;

import teammates.common.util.HibernateUtil;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.BaseEntity;

/**
 * Base class for all classes performing CRUD operations against the database.
 */
class EntitiesDb {

    /**
     * Info message when entity is not saved because it does not change.
     */
    static final String OPTIMIZED_SAVING_POLICY_APPLIED =
            "Saving request is not issued because entity %s does not change by the update (%s)";
    /**
     * Error message when trying to update entity that does not exist.
     */
    static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Entity: ";

    static final Logger log = Logger.getLogger();

    /**
     * Copy the state of the given object onto the persistent object with the same identifier.
     * If there is no persistent instance currently associated with the session, it will be loaded.
     */
    protected <T extends BaseEntity> T merge(T entity) {
        assert entity != null;

        T newEntity = HibernateUtil.merge(entity);
        log.info("Entity saved: " + entity.toString());
        return newEntity;
    }

    /**
     * Associate {@code entity} with the persistence context.
     */
    protected void persist(BaseEntity entity) {
        assert entity != null;

        HibernateUtil.persist(entity);
        log.info("Entity persisted: " + entity.toString());
    }

    /**
     * Deletes {@code entity} from persistence context.
     */
    protected void delete(BaseEntity entity) {
        assert entity != null;

        HibernateUtil.remove(entity);
        log.info("Entity deleted: " + entity.toString());
    }

    /**
     * Checks whether two values are the same.
     */
    <T> boolean hasSameValue(T oldValue, T newValue) {
        return Objects.equal(oldValue, newValue);
    }
}
