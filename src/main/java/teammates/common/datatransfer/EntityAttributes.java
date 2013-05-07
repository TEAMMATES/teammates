package teammates.common.datatransfer;

/**
 * Base class for Attribute classes (Attribute classes represent attributes
 * of persistable entities). 
 */
public abstract class EntityAttributes {
	
	/**
	 * @return true if the attributes represent a valid state for the entity.
	 */
	public boolean isValid() {
		return getInvalidStateInfo().isEmpty();
	}
	
	/**
	 * @return a string explaining which attributes values are invalid. Returns
	 * an empty string if all attributes are valid.
	 */
	public abstract String getInvalidStateInfo();
}
