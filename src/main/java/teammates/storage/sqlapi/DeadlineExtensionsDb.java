package teammates.storage.sqlapi;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.DeadlineExtension;

/**
 * Handles CRUD operations for deadline extensions.
 *
 * @see DeadlineExtension
 */
public final class DeadlineExtensionsDb extends EntitiesDb<DeadlineExtension> {

    private static final DeadlineExtensionsDb instance = new DeadlineExtensionsDb();

    private DeadlineExtensionsDb() {
        // prevent initialization
    }

    public static DeadlineExtensionsDb inst() {
        return instance;
    }

    /**
     * Creates a deadline extension.
     */
    public DeadlineExtension createDeadlineExtension(DeadlineExtension de)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert de != null;

        if (!de.isValid()) {
            throw new InvalidParametersException(de.getInvalidityInfo());
        }

        if (getDeadlineExtension(de.getId()) != null) {
            throw new EntityAlreadyExistsException(
                    String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, de.toString()));
        }

        persist(de);
        return de;
    }

    /**
     * Gets a deadline extension by {@code id}.
     */
    public DeadlineExtension getDeadlineExtension(Integer id) {
        assert id != null;

        return HibernateUtil.getSessionFactory().getCurrentSession()
                .get(DeadlineExtension.class, id);
    }

    /**
     * Saves an updated {@code DeadlineExtension} to the db.
     *
     * @return updated deadline extension
     * @throws InvalidParametersException  if attributes to update are not valid
     * @throws EntityDoesNotExistException if the deadline extension cannot be found
     */
    public DeadlineExtension updateDeadlineExtension(DeadlineExtension deadlineExtension)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert deadlineExtension != null;

        if (!deadlineExtension.isValid()) {
            throw new InvalidParametersException(deadlineExtension.getInvalidityInfo());
        }

        if (getDeadlineExtension(deadlineExtension.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        return merge(deadlineExtension);
    }

    /**
     * Deletes a deadline extension.
     */
    public void deleteDeadlineExtension(DeadlineExtension de) {
        if (de != null) {
            delete(de);
        }
    }
}
