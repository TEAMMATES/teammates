package teammates;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Common {
	
	//data field sizes
	public static int COURSE_NAME_MAX_LENGTH = 38;
	public static int COURSE_ID_MAX_LENGTH = 21;
	
	//TeammatesServlet responses
	public static final String COORD_ADD_COURSE_RESPONSE_ADDED = "<status>course added</status>";
	public static final String COORD_ADD_COURSE_RESPONSE_EXISTS = "<status>course exists</status>";
	public static final String COORD_ADD_COURSE_RESPONSE_INVALID = "<status>course input invalid</status>";
	
	
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
