package teammates;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
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
}
