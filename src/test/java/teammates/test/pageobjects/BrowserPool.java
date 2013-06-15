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
	private static final int CAPACITY = 5;

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
		Browser b = getInstance().requestInstance();
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
			pool.notify();
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
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
