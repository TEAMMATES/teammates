package teammates.testing.lib;

import java.io.IOException;
import java.util.Calendar;
import java.util.StringTokenizer;
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

import teammates.manager.Emails;
import teammates.testing.config.Config;

public class SharedLib {

	/**
	 * Retrieve registration key sent to Gmail inbox. After retrieve, mark the
	 * email as read.
	 * 
	 * Can be easily modified to support other mail providers
	 * 
	 * @param gmail
	 * @param password
	 * @return
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

		try {
			// Retrieve the "Inbox"
			Folder inbox = store.getFolder("inbox");
			// Reading the Email Index in Read / Write Mode
			inbox.open(Folder.READ_WRITE);
			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
			Message messages[] = inbox.search(ft);

			// Loop over the last 5 messages
			for (int i = messages.length - 1; i >= messages.length - 5; i--) {
				Message message = messages[i];
				// If this is the right message (by matching header)

				// Pattern pattern = Pattern
				// .compile("^TEAMMATES: Registration Invitation: Register in the course (\\w+)$");
				// Matcher m = pattern.matcher(message.getSubject());
				String subject = message.getSubject();
				boolean isCorrectEmail = subject
						.contains(Emails.SUBJECT_PREFIX_STUDENT_COURSE_JOIN)
						&& (subject.contains(courseId));
				if (!isCorrectEmail)
					continue;

				System.out.println(subject);

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
	 * Retrieve evaluation reminder sent to Gmail inbox. After retrieve, mark
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
				+ Config.inst().TEAMMATES_LIVE_SITE;
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

	/**
	 * Return the date of next hour in format (YYYY,M,D)
	 * 
	 * @deprecated Only used in old testing method
	 * @return
	 */
	public static String getDateValue() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		return "(" + year + "," + month + "," + day + ")";
	}

	/**
	 * Returns the next hour from the next full hour. Example: if current time
	 * is 1050, this will return 12 (i.e., one hour after 11)
	 * 
	 * @deprecated Only used in old testing method
	 * @return
	 */
	public static String getNextTimeValue() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);

		return Integer.toString(calendar.get(Calendar.HOUR_OF_DAY) + 1);
	}

	/**
	 * Helper method to format date from format (YYYY,M,D) to DD/MM/YYYY.
	 * Usually used in conjunction with {@link #getDateValue()}
	 * 
	 * @deprecated Only used in old testing method
	 * @param date
	 * @return
	 */
	public static String formatDate(String date) {
		StringTokenizer st = new StringTokenizer(date, "(,)");
		String year = st.nextToken().trim();
		String month = st.nextToken();
		Integer monthInt = Integer.parseInt(month);
		month = String.format("%02d", monthInt);
		String day = st.nextToken();
		Integer dayInt = Integer.parseInt(day);
		day = String.format("%02d", dayInt);

		return day + "/" + month + "/" + year;
	}

	public static void main(String[] args) {
		try {
			System.out
					.println(SharedLib.getRegistrationKeyFromGmail(
							"benny.tmms@gmail.com",
							Config.inst().TEAMMATES_APP_PASSWORD,
							"CCDetailsUiT.CS2104"));
			// SharedLib.getEvaluationReminderFromGmail("alice.tmms@gmail.com",
			// Config.inst().TEAMMATES_APP_PASSWORD, "CS2103-TESTING",
			// "First Eval");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Thread println
	 */
	public static void tprintln(String message) {
		System.out.println("[" + Thread.currentThread().getName() + "]"
				+ message);
	}

}
