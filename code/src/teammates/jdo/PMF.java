package teammates.jdo;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

/**
 * PMF is a singleton wrapper class for a static instance of the
 * PersistenceManager class.
 * 
 * @author Google Code
 * 
 */
public final class PMF {
	private static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	private static PersistenceManager pm = pmfInstance.getPersistenceManager();

	/**
	 * Allow no-construction of static-only object
	 */
	private PMF() {
	}

	/**
	 * Returns a static instance of the PersistenceManager class.
	 * 
	 * @return pmfInstance
	 * @deprecated
	 */
	public static PersistenceManagerFactory get() {
		return pmfInstance;
	}

	/**
	 * Return the only PersistenceManager object of the application.
	 * We don't use more than 1 PM.
	 * @author huy
	 * @return The PM instance, do not do pm.close() unless you know what you're
	 *         doing. If you want to make the data persistent instantly, use
	 *         pm.flush() right after the persistent call
	 * @deprecated
	 */
	public static PersistenceManager getPersistenceManager() {
		return pm;
	}

}