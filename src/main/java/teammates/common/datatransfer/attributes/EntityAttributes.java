package teammates.common.datatransfer.attributes;

import java.util.List;

import teammates.storage.entity.BaseEntity;

/**
 * Base class for Attribute classes (Attribute classes represent attributes of
 * persistable entities).
 */
public abstract class EntityAttributes<E extends BaseEntity> {

    /**
     * Returns true if the attributes represent a valid state for the entity.
     */
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
    }

    /**
     * Returns a {@code List} of strings, one string for each attribute whose
     *         value is invalid. The string explains why the value is invalid
     *         and what should values are acceptable. These explanations are
     *         good enough to show to the user. Returns an empty {@code List} if
     *         all attributes are valid.
     */
    public abstract List<String> getInvalidityInfo();

    /**
     * Returns a {@code Object} corresponding to the attributes defined by {@code this}
     *            {@link EntityAttributes} class.
     */
    public abstract E toEntity();

    /**
     * Returns an abridged string which can sufficiently identify the entity
     *            this class represents for use in error messages / exceptions.
     */
    public abstract String getIdentificationString();

    /**
     * Returns the type of entity this Attribute class represents as a human
     *            readable string.
     */
    public abstract String getEntityTypeAsString();

    /**
     * Returns the identifier used for logging to perform backup.
     */
    public abstract String getBackupIdentifier();

    /**
     * Returns the entity object as a JSON-formatted string.
     */
    public abstract String getJsonString();

    /**
     * Perform any sanitization that needs to be done before saving.
     * e.g. trim strings
     */
    public abstract void sanitizeForSaving();

    /**
     * Adds {@code error} to {@code errors} if {@code error} is a non-empty string.
     *
     * @param error An error message, possibly empty.
     * @param errors A List of errors, to add {@code error} to.
     */
    public void addNonEmptyError(String error, List<String> errors) {
        if (error.isEmpty()) {
            return;
        }

        errors.add(error);
    }
}
