package teammates.storage.datastore;

import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import teammates.common.util.Utils;

//TODO: we might not really need this class. To be reconsidered.
/**
 * Represents the Datastore.
 * Code taken from:
 * http://stackoverflow.com/questions/4185382/how-to-use-jdo-persistence-manager
 */
public class Datastore {
    private static PersistenceManagerFactory PMF = null;
    private static Logger log = Utils.getLogger();
    private static final ThreadLocal<PersistenceManager> PER_THREAD_PM = new ThreadLocal<PersistenceManager>();

    public static void initialize() {
        if (PMF == null) {
            PMF = JDOHelper
                    .getPersistenceManagerFactory("transactions-optional");
        } else {
            log.warning("Trying to initialize Datastore again");
        }
    }

    public static PersistenceManager getPersistenceManager() {

        PersistenceManager pm = PER_THREAD_PM.get();
        if (pm == null) {
            pm = PMF.getPersistenceManager();
            PER_THREAD_PM.set(pm);

        } else if (pm.isClosed()) {

            PER_THREAD_PM.remove();
            pm = PMF.getPersistenceManager();
            PER_THREAD_PM.set(pm);

        }
        return pm;
    }

    public static void finishRequest() {

        PersistenceManager pm = PER_THREAD_PM.get();
        
        if (pm == null) {
            return;
        }
        
        PER_THREAD_PM.remove();

        if (!pm.isClosed()) {
            Transaction tx = pm.currentTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }

    }
}
