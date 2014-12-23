package teammates.common.datatransfer;

import java.util.List;

/**
 * Base class for Attribute classes (Attribute classes represent attributes of
 * persistable entities).
 */
public abstract class EntityAttributes {

    /**
     * @return true if the attributes represent a valid state for the entity.
     */
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
    }

    /**
     * @return a {@code List} of strings, one string for each attribute whose
     *         value is invalid. The string explains why the value is invalid
     *         and what should values are acceptable. These explanations are
     *         good enough to show to the user. Returns an empty {@code List} if
     *         all attributes are valid.
     */
    public abstract List<String> getInvalidityInfo();
    
    /**
     * @return a {@code Object} corresponding to the attributes defined by {@code this}
     *            {@link EntityAttributes} class.
     */
    public abstract Object toEntity();
    
    /**
     * @return an abridged string which can sufficiently identify the entity
     *            this class represents for use in error messages / exceptions.
     */
    public abstract String getIdentificationString();
    
    /**
     * @return the type of entity this Attribute class represents as a human
     *            readable string.
     */
    public abstract String getEntityTypeAsString();

    /**
     * @return the identifier used for logging to perform backup 
     */
    public abstract String getBackupIdentifier();
    
    /**
     * @return the entity object as a Json formatted string
     */
    public abstract String getJsonString();
    
    /**
     * Perform any sanitization that needs to be done before saving. 
     * e.g. trim strings
     */
    public abstract void sanitizeForSaving() ;
}
