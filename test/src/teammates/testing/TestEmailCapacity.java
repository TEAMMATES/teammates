package teammates.testing;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;

/**
 * Stree Testing of application mailing capacity
 * 
 * @author wangsha
 * 
 */
public class TestEmailCapacity extends BaseTest {

	@BeforeClass
	public static void classSetup() {
		// Nothing to setup
		try {
			SharedLib.markAllEmailsSeen(Config.MAIL_STRESS_TEST_ACCOUNT,
					Config.TEAMMATES_APP_PASSWD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void classTeardown() {
		// Nothing to wrapup
	}

	@Test
	public void testEmailCapacity() throws Exception {
		TMAPI.mailStressTesting(Config.MAIL_STRESS_TEST_ACCOUNT,
				Config.MAIL_STRESS_TEST_SIZE);
		// count mails in inbox
		int count = 0;
		for (int i = 0; i < 10; i++) {
			// do waiting
			waitAWhile(1000 * 30 * i);
			count = SharedLib.mailStressTestCount(Config.MAIL_STRESS_TEST_ACCOUNT,
					Config.TEAMMATES_APP_PASSWD);
			if (count == Config.MAIL_STRESS_TEST_SIZE) {
				break;
			}
			System.out.println("Iteration: " + i + ", count: " + count);
		}

		assertEquals(count, Config.MAIL_STRESS_TEST_SIZE);
	}

}
