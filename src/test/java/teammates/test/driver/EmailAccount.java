package teammates.test.driver;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import teammates.logic.core.Emails;

public class EmailAccount {

	/**
	 * Retrieve registration key sent to Gmail inbox. After retrieving, marks 
	 * the email as read.
	 * 	 * Can be easily modified to support other mail providers
	 * 
	 * @param gmail
	 * @param password
	 * @return registration key (null if cannot be found).
	 * @throws Exception
	 */
	public static String getRegistrationKeyFromGmail(String gmail,
			String password, String courseId) {
		Session sessioned = Session.getDefaultInstance(System.getProperties(),
				null);
		Store store = null;
		try {
			store = sessioned.getStore("imaps");
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
		try {
			store.connect("imap.gmail.com", gmail, password);
		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		}

		//TODO: method too long. refactor.
		try {
			// Retrieve the "Inbox"
			Folder inbox = store.getFolder("inbox");
			// Reading the Email Index in Read / Write Mode
			inbox.open(Folder.READ_WRITE);
			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
			Message messages[] = inbox.search(ft);

			// Loop over up to 5 unread messages at the top of the inbox
			int maxEmailsToCheck = (messages.length<5? messages.length: 5);
			for (int i = messages.length - 1; i >= messages.length - maxEmailsToCheck; i--) {
				Message message = messages[i];
				// If this is the right message (by matching header)

				// Pattern pattern = Pattern
				// .compile("^TEAMMATES: Registration Invitation: Register in the course (\\w+)$");
				// Matcher m = pattern.matcher(message.getSubject());
				String subject = message.getSubject();
				
				if(subject == null){ //in case there are subject-less messages
					continue;
				}
				
				boolean isCorrectEmail = subject
						.contains(Emails.SUBJECT_PREFIX_STUDENT_COURSE_JOIN)
						&& (subject.contains(courseId));
				if (!isCorrectEmail)
					continue;


				String body = "";

				if (message.getContent() instanceof String) { // if message is a
					// string
					body = message.getContent().toString();
				} else if (message.getContent() instanceof Multipart) { // if
																		// its a
					// multipart
					// message
					Multipart multipart = (Multipart) message.getContent();
					BodyPart bodypart = multipart.getBodyPart(0);
					body = bodypart.getContent().toString();
				}

				String key;
				key = getKey(body);

				// Mark the message as read
				message.setFlag(Flags.Flag.SEEN, true);

				return key;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String getKey(String body) {
		String key;
		key = body.split("Registration key:")[1];
		key = key.trim().split("If you encounter")[0].trim();
		return key;
	}

	/**
	 * Retrieve evaluation reminder sent to Gmail inbox. After retrieving, marks
	 * the email as read.
	 * 
	 * Can be easily modified to support other mail providers
	 * 
	 * @param gmail
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static String getEvaluationReminderFromGmail(String gmail,
			String password, String courseId, String evalulationName)
			throws Exception {
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

		// Loop over the last 5 messages
		for (int i = messages.length - 1; i >= messages.length - 5; i--) {
			Message message = messages[i];

			String subject = message.getSubject();
			// TODO: make this test deeper
			// TODO: courseID is not used
			boolean isTheRightEmail = subject
					.contains(Emails.SUBJECT_PREFIX_STUDENT_EVALUATION_REMINDER)
					&& subject.contains(evalulationName);
			if (!isTheRightEmail)
				continue;

			// Mark the message as read
			message.setFlag(Flags.Flag.SEEN, true);

			// Return the courseId
			return courseId;
		}

		return "";
	}

	/**
	 * Checks whether the Publish had actually sent the e-mails to students
	 * 
	 * @param gmail
	 * @param password
	 * @param courseCode
	 * @param evaluationName
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public static boolean checkResultEmailsSent(String gmail, String password,
			String courseCode, String evaluationName)
			throws MessagingException, IOException {

		// Publish RESULTS Format
		final String HEADER_EVALUATION_PUBLISH = "TEAMMATES: Evaluation Published: %s %s";
		final String TEAMMATES_APP_URL = "You can view the result here: "
				+ TestProperties.inst().TEAMMATES_URL_IN_EMAILS;
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

		// Loop over the last 5 messages
		for (int i = messages.length - 1; i >= messages.length - 5; i--) {
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
			if (message.getContent() instanceof String) {
				body = message.getContent().toString();
			} else if (message.getContent() instanceof Multipart) {
				Multipart multipart = (Multipart) message.getContent();
				BodyPart bodypart = multipart.getBodyPart(0);
				body = bodypart.getContent().toString();
			}

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

	/**
	 * Helper function - Mark all emails of an account as read.
	 * 
	 */
	public static void markAllEmailsSeen(String username, String password)
			throws Exception {
		Session sessioned = Session.getDefaultInstance(System.getProperties(),
				null);
		Store store = sessioned.getStore("imaps");
		store.connect("imap.gmail.com", username, password);

		// Retrieve the "Inbox"
		Folder inbox = store.getFolder("inbox");

		// Reading the Email Index in Read / Write Mode
		inbox.open(Folder.READ_WRITE);

		FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
		Message messages[] = inbox.search(ft);
		// Message messages[] = inbox.getMessages();

		// Loop over all of the messages
		for (Message message : messages) {
			message.setFlag(Flags.Flag.SEEN, true);
		}
		inbox.close(true);
	}

	/**
	 * Count the number of stress test emails
	 * 
	 * @author wangsha
	 */
	public static int mailStressTestCount(String username, String password)
			throws Exception {
		Session sessioned = Session.getDefaultInstance(System.getProperties(),
				null);
		Store store = sessioned.getStore("imaps");
		store.connect("imap.gmail.com", username, password);

		// Retrieve the "Inbox"
		Folder inbox = store.getFolder("inbox");
		// Reading the Email Index in Read / Write Mode
		inbox.open(Folder.READ_WRITE);

		FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
		Message messages[] = inbox.search(ft);
		// Message messages[] = inbox.getMessages();
		int count = 0;
		// Loop over all of the messages
		Pattern pattern = Pattern.compile("^Teammates Mail Stree Testing ");
		for (Message message : messages) {
			System.out.println(message.getSubject());
			Matcher m = pattern.matcher(message.getSubject());

			if (!m.find())
				continue;
			count++;

		}
		inbox.close(true);

		return count;
	}


	public static void main(String[] args) {
		try {
			System.out
					.println(EmailAccount.getRegistrationKeyFromGmail(
							TestProperties.inst().TEST_STUDENT1_ACCOUNT,
							TestProperties.inst().TEST_STUDENT1_PASSWORD,
							"CCDetailsUiT.CS2104"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
