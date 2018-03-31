package teammates.test.pageobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage the pool of {@link Browser} instances.
 * This class is thread-safe.
 */
public final class BrowserPool {
    /* This class is implemented as a Singleton class.
     * The reason we're not implementing this class as static because we want to
     * use wait() and notify().
     */

    /** Ideally, should be equal to the number of threads used for testing. */
    private static final int CAPACITY = System.getenv("CI") == null ? 9 + 1 : 2;
    //+1 in case a sequential ui test uses a browser other than the first in pool

    private static BrowserPool instance;
    private List<Browser> pool;

    private BrowserPool() {
        pool = new ArrayList<>(CAPACITY);
    }

    private static synchronized BrowserPool getInstance() {
        if (instance == null) {
            instance = new BrowserPool();
        }
        return instance;
    }

    /**
     * Returns a Browser object ready to be used.
     */
    public static Browser getBrowser() {
        return getInstance().requestInstance();
    }

    /**
     * Releases a Browser instance back to the pool, ready to be reused.
     */
    public static void release(Browser browser) {
        BrowserPool pool = getInstance();
        //synchronized to ensure thread-safety
        synchronized (pool) {
            browser.isInUse = false;
            pool.notifyAll();
        }
    }

    private Browser requestInstance() {

        while (true) {
            //synchronized to ensure thread-safety
            synchronized (this) {
                // Look for instantiated and available object.
                for (Browser b : pool) {
                    if (!b.isInUse) {
                        b.isInUse = true;
                        return b;
                    }
                }

                // If less than capacity, create new object
                if (pool.size() < CAPACITY) {
                    Browser b = new Browser();
                    b.isInUse = true;
                    pool.add(b);
                    return b;
                }

                // Wait if no more free objects and no more capacity.
                try {
                    this.wait(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
