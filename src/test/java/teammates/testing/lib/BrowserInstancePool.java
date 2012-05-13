package teammates.testing.lib;

import java.util.ArrayList;

/**
 * Manage the pool of browser instances.
 * 
 * This class is thread-safe.
 * 
 */
public class BrowserInstancePool {
	private static final int CAPACITY = 4;

	private static BrowserInstancePool instance = null;
	private ArrayList<BrowserInstance> pool = null;

	/**
	 * Singleton method
	 * 
	 * The reason we're not implementing this class as static because we want to
	 * use wait() and notify()
	 * 
	 * @return
	 */
	private static synchronized BrowserInstancePool getInstance() {
		if (instance == null) {
			instance = new BrowserInstancePool();
		}
		return instance;
	}

	/**
	 * Request an initialized instance of BrowserInstance
	 * 
	 * @return
	 */
	public static BrowserInstance getBrowserInstance() {
		return getInstance().requestInstance();
	}

	/**
	 * Kill all instances
	 */
	public static void killAll() {
		getInstance().killAllInstances();
	}

	private void killAllInstances() {
		synchronized (this) {
			for (BrowserInstance bi : this.pool) {
				if (bi != null && !bi.isInUse()) {
					bi.getSelenium().stop();
					this.pool.remove(bi);
				}
			}
		}
	}

	/**
	 * The thread has done using the instance and release them back to the pool
	 * 
	 * @param instance
	 */
	public static void release(BrowserInstance bi) {
		getInstance().releaseInstance(bi);
	}

	private BrowserInstancePool() {
		pool = new ArrayList<BrowserInstance>(CAPACITY);
	}

	private BrowserInstance requestInstance() {
		while (true) {
			synchronized (this) {
				// Look for instantiated and free object
				for (BrowserInstance b : pool) {
					if (!b.isInUse()) {
						b.setInUse(true);
						return b;
					}
				}

				// If less than capacity, create new object
				if (pool.size() < CAPACITY) {
					BrowserInstance bi = new BrowserInstance();
					bi.setupSelenium();
					bi.setInUse(true);
					pool.add(bi);
					return bi;
				}
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void releaseInstance(BrowserInstance bi) {
		synchronized (this) {
			bi.setInUse(false);
			this.notify();
		}
	}
}
