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
		return getInvalidStateInfo().isEmpty();
	}

	/**
	 * @return a {@code List} of strings, one string for each attribute whose
	 *         value is invalid. The string explains why the value is invalid
	 *         and what should values are acceptable. These explanations are
	 *         good enough to show to the user. Returns an empty {@code List} if
	 *         all attributes are valid.
	 */
	public abstract List<String> getInvalidStateInfo();
}
