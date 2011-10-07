package teammates.testing.script;

import teammates.testing.Config;
import teammates.testing.lib.SharedLib;

public class MarkAllEmailsSeen {
	
	/**
	 * Mark all emails as read
	 * 
	 */
	public static void main(String args[]) {
		try {
			SharedLib.markAllEmailsSeen("alice.tmms", Config.TEAMMATES_APP_PASSWD);
			SharedLib.markAllEmailsSeen("benny.tmms", Config.TEAMMATES_APP_PASSWD);
			SharedLib.markAllEmailsSeen("charlie.tmms", Config.TEAMMATES_APP_PASSWD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
