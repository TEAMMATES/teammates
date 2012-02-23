package teammates.testing.old;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.config.Config;
import teammates.testing.lib.TMAPI;

/**
 * Test all the footers if it includes the version and the build.
 * @author Kalpit
 */
public class TestFooter extends BaseTest {

	public static String footer = "Best Viewed In Firefox, Chrome, Safari and Internet Explore 8+. For Enquires:";
	public static int maxVersionLength = 10;
	@BeforeClass
	public static void classSetup() throws Exception {
		setupScenario();
		TMAPI.cleanup();
		setupSelenium();
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}

	/**
	 * Successfully test the footer on the Main Site
	 */
	@Test
	public void testOnTheMainSite() throws Exception {
		cout("Test: Footer on Main Page.");
		assertNotNull(getElementText(By.id("contentFooter")));
		assertTrue((getElementText(By.id("contentFooter"))).contains("[TEAMMATES Version"));
		assertTrue((getElementText(By.id("contentFooter"))).contains(footer));
		//difference of index of 'n' in Version and the ']' in the format [TEAMMATES Version 1]
		int pos = ((getElementText(By.id("contentFooter"))).indexOf('n'))-((getElementText(By.id("contentFooter"))).indexOf(']'));
		assertTrue(pos<maxVersionLength);
		
		justWait();
	}
	
	/**
	 * Successfully login as the Coordinator and test on coordinator.jsp
	 */
	@Test
	public void testOnTheCoordinatorSite() throws Exception {
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
		cout("Test: Footer on Coordinator.jsp.");
		assertNotNull(getElementText(By.id("contentFooter")));
		assertTrue((getElementText(By.id("contentFooter"))).contains("[TEAMMATES Version"));
		assertTrue((getElementText(By.id("contentFooter"))).contains(footer));
		//difference of index of 'n' in Version and the ']' in the format [TEAMMATES Version 1]
		int pos = ((getElementText(By.id("contentFooter"))).indexOf('n'))-((getElementText(By.id("contentFooter"))).indexOf(']'));
		assertTrue(pos<maxVersionLength);
		
		justWait();
		logout();
	}
	
	/**
	 * Successfully login as the Student and test on student.jsp
	 */
	@Test
	public void testOnTheStudentSite() throws Exception {
		studentLogin(sc.students.get(0).email, Config.inst().TEAMMATES_APP_PASSWD);
		cout("Test: Footer on Student.jsp.");
		assertNotNull(getElementText(By.id("contentFooter")));
		assertTrue((getElementText(By.id("contentFooter"))).contains("[TEAMMATES Version"));
		assertTrue((getElementText(By.id("contentFooter"))).contains(footer));
		//difference of index of 'n' in Version and the ']' in the format [TEAMMATES Version 1]
		int pos = ((getElementText(By.id("contentFooter"))).indexOf('n'))-((getElementText(By.id("contentFooter"))).indexOf(']'));
		assertTrue(pos<maxVersionLength);

		justWait();
		logout();
	}
}