package teammates.storage.sqlapi;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.DeadlineExtension;

/**
 * Handles CRUD operations for deadline extensions.
 * 
 * @see DeadlineExtension
 */
public class DeadlineExtensionsDb extends EntitiesDb<DeadlineExtension> {

    private static final DeadlineExtensionsDb instance = new DeadlineExtensionsDb();

    private DeadlineExtensionsDb() {
        // prevent initialization
    }

    public static DeadlineExtensionsDb inst() {
        return instance;
    }

    /**
     * Gets a deadline extension by {@code deadlineExtensionId}.
     */
    public DeadlineExtension getDeadlineExtension(Integer deadlineExtensionId) {
        assert deadlineExtensionId != null;

        DeadlineExtension de = HibernateUtil.getSessionFactory().getCurrentSession()
                .get(DeadlineExtension.class, deadlineExtensionId);
        return de;
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
    public void deleteDeadlineExtension(Integer deadlineExtensionId) {
        assert deadlineExtensionId != null;

        DeadlineExtension de = getDeadlineExtension(deadlineExtensionId);
        if (de != null) {
            delete(de);
        }
    }
}
