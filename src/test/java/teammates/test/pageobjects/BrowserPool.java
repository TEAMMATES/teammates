package teammates.test.pageobjects;

import java.util.ArrayList;

/**
 * Manage the pool of {@link Browser} instances.
 * This class is thread-safe.  
 */
public class BrowserPool {
    /* This class is implemented as a Singleton class.
     * The reason we're not implementing this class as static because we want to
     * use wait() and notify().
     */
    
    /** Ideally, should be equal to the number of threads used for testing */
    private static final int CAPACITY = System.getenv("TRAVIS") == null ? 9 + 1 : 1;
    //+1 in case a sequential ui test uses a browser other than the first in pool

    private static BrowserPool instance = null;
    private ArrayList<Browser> pool;

    private BrowserPool() {
        pool = new ArrayList<Browser>(CAPACITY);
    }

    /**
    
     */
    private static synchronized BrowserPool getInstance() {
        if (instance == null) {
            instance = new BrowserPool();
        }
        return instance;
    }

    /**
     * @return a Browser object ready to be used.
     */
    public static Browser getBrowser() {
        Browser b = getInstance().requestInstance(false);
        return b;
    }
    
    /**
     *  Gives 'priority' to sequential ui tests, allowing the browser pool to use
     *  the first browser in pool.
     *  Allocates the first browser to sequential ui tests only,
     *  since it takes a thread by itself and should not spend
     *  time waiting for a free browser.
     */
    public static Browser getBrowser(boolean sequentialUiTest) {
        Browser b = getInstance().requestInstance(sequentialUiTest);
        return b;
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

    private Browser requestInstance(boolean sequentialUiTest) {
        
        if(sequentialUiTest){
            //Set priority of the sequential ui tests thread to max priority.
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        } else {
            Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        }
        
        while (true) {
            //synchronized to ensure thread-safety
            synchronized (this) {
                // Look for instantiated and available object.
                int n=0;
                for (Browser b : pool) {
                    n++;
                    if(System.getenv("TRAVIS") == null && !sequentialUiTest && n==1){
                        continue;
                    }
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
                    e.printStackTrace();
                }
            }
        }
    }
}
