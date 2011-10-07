package teammates.testing.lib;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;
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


public class SharedLib {

	public static String getFileContents(String filename) {
		try {
			String ans = new Scanner(new FileReader(filename)).useDelimiter("\\Z")
					.next();
			return ans;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

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
		Session sessioned = Session
				.getDefaultInstance(System.getProperties(), null);
		Store store = null;
		try {
			store = sessioned.getStore("imaps");
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		try {
			store.connect("imap.gmail.com", gmail, password);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		try {
			// Retrieve the "Inbox"
			Folder inbox = store.getFolder("inbox");
			// Reading the Email Index in Read / Write Mode
			inbox.open(Folder.READ_WRITE);
			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
			Message messages[] = inbox.search(ft);

			// Loop over all of the messages
			for (int i = messages.length - 1; i >= 0; i--) {
				Message message = messages[i];
				// If this is the right message (by matching header)
				Pattern pattern = Pattern
						.compile("^Welcome to Teammates - New Course (\\w+)$");
				Matcher m = pattern.matcher(message.getSubject());

				if (!m.find())
					continue;

				// Grab the Course ID and check
				String grabbedCourseId = m.group(1);
				if (!courseId.equals(grabbedCourseId))
					continue;

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

				pattern = Pattern.compile("course: (.+)");
				m = pattern.matcher(body);
				
				if (!m.find())
					continue;

				// Mark the message as read
				message.setFlag(Flags.Flag.SEEN, true);
				
				// Return the key
				return m.group(1);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			
		} catch (MessagingException e) {
			e.printStackTrace();
			return "";
		}

		return "";
	}

	/**
	 * Retrieve evaluation reminder sent to Gmail inbox. After retrieve, mark the
	 * email as read.
	 * 
	 * Can be easily modified to support other mail providers
	 * 
	 * @param gmail
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static String getEvaluationReminderFromGmail(String gmail,
			String password, String courseId) throws Exception {
		Session sessioned = Session
				.getDefaultInstance(System.getProperties(), null);
		Store store = sessioned.getStore("imaps");
		store.connect("imap.gmail.com", gmail, password);

		// Retrieve the "Inbox"
		Folder inbox = store.getFolder("inbox");
		// Reading the Email Index in Read / Write Mode
		inbox.open(Folder.READ_WRITE);
		FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
		Message messages[] = inbox.search(ft);

		// Loop over all of the messages
		for (int i = messages.length - 1; i >= 0; i--) {
			Message message = messages[i];
			// If this is the right message (by matching header)
			Pattern pattern = Pattern
					.compile("^Teammates Evaluation - Reminder (\\w+)$");
			Matcher m = pattern.matcher(message.getSubject());

			if (!m.find())
				continue;

			// Grab the Course ID and check
			String grabbedCourseId = m.group(1);
			if (!courseId.equals(grabbedCourseId))
				continue;

			// Mark the message as read
			message.setFlag(Flags.Flag.SEEN, true);

			// Return the courseId
			return grabbedCourseId;
		}

		return "";
	}

	/**
	 * Helper function - Mark all emails of an account as read.
	 * 
	 */
	public static void markAllEmailsSeen(String username, String password)
			throws Exception {
		Session sessioned = Session
				.getDefaultInstance(System.getProperties(), null);
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
	 * @author wangsha
	 */
	public static int mailStressTestCount(String username, String password)
			throws Exception {
		Session sessioned = Session
				.getDefaultInstance(System.getProperties(), null);
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
		for (Message message : messages) {
			System.out.println(message.getSubject());
			Pattern pattern = Pattern
					.compile("^Teammates Mail Stree Testing ");
			Matcher m = pattern.matcher(message.getSubject());

			if (!m.find())
				continue;
			count++;
		
		}
		inbox.close(true);
		
		return count;
	}
	
	

	// Helper method to get the next available date for the evaluation (assuming
	// 1 hour units)
	public static String getDateValue() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		// int hour = calendar.get(Calendar.HOUR_OF_DAY);

		return "(" + year + "," + month + "," + day + ")";
	}

	public static String getDateString() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		// int hour = calendar.get(Calendar.HOUR_OF_DAY);

		return day + "/" + month + "/" + year;
	}

	// Helper method to format the date to DD/MM/YYYY
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

	// Helper method to get the next available time for the evaluation (assuming
	// 1 hour units)
	public static String getNextTimeValue() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);

		String nextHour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY) + 1);
		return nextHour;
	}

}
