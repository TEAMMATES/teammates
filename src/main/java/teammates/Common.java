package teammates;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Common {
	
	//status messages
	public final static String MESSAGE_COURSE_ADDED = "The course has been added. Click the 'Enrol' link in the table below to add students to the course.";
	public final static String MESSAGE_COURSE_EXISTS = "The course already exists.";
	public final static String ERROR_COURSE_MISSING_FIELD = "Course ID and Course Name are compulsory fields.";
	public final static String ERROR_COURSE_INVALID_ID = "Please use only alphabets, numbers, dots, hyphens, underscores and dollars in course ID.";

	//data field sizes
	public static int COURSE_NAME_MAX_LENGTH = 38;
	public static int COURSE_ID_MAX_LENGTH = 21;
	
	//TeammatesServlet responses
	public static final String COORD_ADD_COURSE_RESPONSE_ADDED = "<status>course added</status>";
	public static final String COORD_ADD_COURSE_RESPONSE_EXISTS = "<status>course exists</status>";
	public static final String COORD_ADD_COURSE_RESPONSE_INVALID = "<status>course input invalid</status>";
	
	//APIServlet responses
	public static final String BACKEND_STATUS_SUCCESS = "[BACKEND_STATUS_SUCCESS]";
	

	public static void println(String message) {
		System.out.println(String.format("[%d - %s] %s", Thread.currentThread()
				.getId(), Thread.currentThread().getName(), message));
	}

	public static Date convertToDate(String date, int time) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();

		Date newDate = new Date();

		// Perform date manipulation
		try {
			newDate = sdf.parse(date);
			calendar.setTime(newDate);

			if (time == 24) {
				calendar.set(Calendar.HOUR, 23);
				calendar.set(Calendar.MINUTE, 59);
			}

			else {
				calendar.set(Calendar.HOUR, time);
			}

			return calendar.getTime();
		}

		catch (Exception e) {
			return null;
		}

	}
	
	public static Date convertToExactDateTime(String date, int time) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();

		Date newDate = new Date();

		// Perform date manipulation
		try {
			newDate = sdf.parse(date);
			calendar.setTime(newDate);

			if (time == 24) {
				calendar.set(Calendar.HOUR, 23);
				calendar.set(Calendar.MINUTE, 59);
			}

			else {
				calendar.set(Calendar.HOUR, time / 100);
				calendar.set(Calendar.MINUTE, time % 100);
			}

			return calendar.getTime();
		}

		catch (Exception e) {
			return null;
		}

	}



	
}
