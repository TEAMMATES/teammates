package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.manager.Emails;
import teammates.persistent.Evaluation;
import teammates.persistent.Student;

import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class EmailsTest extends BaseTestCase{
	private LocalServiceTestHelper helper;
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
		Emails emails = new Emails();
		localMailService.setLogMailBody(true);
		localMailService.setLogMailLevel(Level.FINEST);
		emails.sendEmail();
		
		//TODO: complete this
	}
	
	@Test
	public void testGetEmailInfo() throws MessagingException{
		printTestCaseHeader();
		Session session = Session.getDefaultInstance(new Properties(), null);
		MimeMessage message = new MimeMessage(session);
		
		String email = "reciever@gmail.com";
		String from = "sender@gmail.com";

		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				email));

		message.setFrom(new InternetAddress(from));
		String subject = "email subject";
		message.setSubject(subject);
		message.setContent("<h1>email body</h1>", "text/html" );

		assertEquals("[Email sent]to=reciever@gmail.com|from=sender@gmail.com|subject=email subject",Emails.getEmailInfo(message));
	}
	
	@Test
	public void testGenerateEvaluationOpeningEmail(){
		printTestCaseHeader();
		
		Student s = null;
		Evaluation e = null;
		MimeMessage email = Emails.generateEvaluationOpeningEmail(s,e);
		
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
