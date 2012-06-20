package teammates.testing.script;

import teammates.testing.config.Config;
import teammates.testing.lib.SharedLib;

public class MarkAllEmailsSeen {
	
	/**
	 * Mark all emails as read
	 * 
	 */
	public static void main(String args[]) {
		try {
			SharedLib.markAllEmailsSeen("alice.tmms", Config.inst().TEAMMATES_APP_PASSWORD);
			SharedLib.markAllEmailsSeen("benny.tmms", Config.inst().TEAMMATES_APP_PASSWORD);
			SharedLib.markAllEmailsSeen("charlie.tmms", Config.inst().TEAMMATES_APP_PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
