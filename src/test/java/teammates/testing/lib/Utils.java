package teammates.testing.lib;

public class Utils {

	/**
	 * Thread println
	 */
	public static void tprintln(String message) {
		System.out.println("[" + Thread.currentThread().getName() + "]" + message);
	}

}
