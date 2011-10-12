package teammates.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

/**
 * Coordinator ready to publish results.
 * 
 * @author huy
 * 
 */
public class TestCoordPublishResults extends BaseTest {

	@BeforeClass
	public static void classSetup() throws IOException {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(sc.students, sc.course.courseId,
				sc.evaluation.name);
		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation.name);

		setupSelenium();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}

	@Test
	public void testPublishResults() throws Exception {
		cout("Test: Coordinator publishes results");

		// Click Evaluations
		waitAndClick(By.className("t_evaluations"));

		// Click the first Publish available
		waitAndClick(By.className("t_eval_publish"));
		// Click Yes
		// wdClick(By.className("t_yes"));
		// Click yes to confirmation
		Alert alert = driver.switchTo().alert();
		alert.accept();

		waitForElementText(By.id("statusMessage"),
				"The evaluation has been published.");

		// Check for status: PUBLISHED
		assertEquals("PUBLISHED", getElementText(By.className("t_eval_status")));

		waitAWhile(5000);

		System.out.println("Checking students' emails to see if they're sent.");
		// Check if emails have been sent to all participants
		assertTrue(checkResultEmailsSent(sc.students.get(0).email, sc.students.get(0).password,
				sc.course.courseId, sc.evaluation.name));

	}

	@Test
	public void testUnpublishResults() throws Exception {
		cout("Test: Unpublishing results.");
		// Click Evaluations
		waitAndClick(By.className("t_evaluations"));
		justWait();

		// Click the first unpublish available
		waitAndClick(By.className("t_eval_unpublish"));
		// Click Yes
		// Click yes to confirmation
		Alert alert = driver.switchTo().alert();
		alert.accept();

		waitForElementText(By.id("statusMessage"),
				"The evaluation has been unpublished.");

		// Check for status: PUBLISHED
		assertEquals("CLOSED", getElementText(By.className("t_eval_status")));
	}

	public static boolean checkResultEmailsSent(String gmail, String password,
			String courseCode, String evaluationName)
			throws MessagingException, IOException {

		// Publish RESULTS Format
		final String HEADER_EVALUATION_PUBLISH = "TEAMMATES: Evaluation Published: %s %s";
		final String TEAMMATES_APP_URL = "You can view the result here: "
				+ Config.TEAMMATES_LIVE_SITE;
		final String TEAMMATES_APP_SIGNATURE = "If you encounter any problems using the system, email TEAMMATES support";

		Session sessioned = Session.getDefaultInstance(System.getProperties(),
				null);
		Store store = sessioned.getStore("imaps");
		store.connect("imap.gmail.com", gmail, password);

		// Retrieve the "Inbox"
		Folder inbox = store.getFolder("inbox");
		// Reading the Email Index in Read / Write Mode
		inbox.open(Folder.READ_WRITE);
		FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
		Message messages[] = inbox.search(ft);
		System.out.println(messages.length + " unread message");

		// Loop over all of the messages
		for (int i = messages.length - 1; i >= 0; i--) {
			Message message = messages[i];
			System.out.println(message.getSubject());

			System.out.println(String.format(HEADER_EVALUATION_PUBLISH,
					courseCode, evaluationName));
			// matching email subject:
			if (!message.getSubject().equals(
					String.format(HEADER_EVALUATION_PUBLISH, courseCode,
							evaluationName))) {
				continue;
			} else {
				System.out.println("match");
			}

			// matching email content:
			String body = "";
			if (message.getContent() instanceof String) { // if message is a
															// string
				body = message.getContent().toString();
			} else if (message.getContent() instanceof Multipart) { // if its a
																	// multipart
																	// message
				Multipart multipart = (Multipart) message.getContent();
				BodyPart bodypart = multipart.getBodyPart(0);
				body = bodypart.getContent().toString();
			}

			System.out.println("message: \n" + body);

			// check line 1: "The results of the evaluation:"
			if (body.indexOf("The results of the evaluation:") == -1) {
				System.out.println("fail 1");
				continue;
			}
			// check line 2: courseCode evaluationName
			if (body.indexOf(body.indexOf(courseCode + " " + evaluationName)) == -1) {
				System.out.println("fail 2");
				continue;
			}
			// check line 3: "have been published."
			if (body.indexOf("have been published.") == -1) {
				System.out.println("fail 3");
				continue;
			}
			// check line 4: "You can view the result here: [URL]"
			if (body.indexOf(TEAMMATES_APP_URL) == -1) {
				System.out.println("fail 4");
				continue;

			}
			// check line 5: teammates signature
			if (body.indexOf(TEAMMATES_APP_SIGNATURE) == -1) {
				System.out.println("fail 5");
				continue;

			}

			// Mark the message as read
			message.setFlag(Flags.Flag.SEEN, true);

			return true;
		}
		return false;
	}

}