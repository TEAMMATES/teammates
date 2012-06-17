package teammates.testing.testcases;

import java.io.IOException;
import java.util.logging.Level;

import javax.mail.MessagingException;
import javax.servlet.ServletException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Datastore;
import teammates.manager.Emails;

import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class EmailsTest extends BaseTestCase{
	private static LocalServiceTestHelper helper;
	private LocalMailServiceTestConfig localMailService;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		setGeneralLoggingLevel(Level.WARNING);
		setLogLevelOfClass(Emails.class, Level.FINE);
		setConsoleLoggingLevel(Level.FINE);
	}
	
	@Before
	public void caseSetUp() throws ServletException, IOException {
		localMailService = new LocalMailServiceTestConfig();
		helper = new LocalServiceTestHelper(localMailService);
		helper.setUp();
	}
	
	@Test
	public void testSendEmail() throws MessagingException, IOException{
		String buildFile = System.getProperty("user.dir")+"\\src\\main\\webapp\\WEB-INF\\classes\\"+"build.properties";
		Emails emails = new Emails(buildFile);
		localMailService.setLogMailBody(true);
		localMailService.setLogMailLevel(Level.FINEST);
		emails.sendEmail();
		
		//TODO: complete this
	}
	

	@After
	public void caseTearDown() {
		helper.tearDown();
	}

	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		setLogLevelOfClass(Emails.class, Level.WARNING);
		setConsoleLoggingLevel(Level.WARNING);
	}
}
