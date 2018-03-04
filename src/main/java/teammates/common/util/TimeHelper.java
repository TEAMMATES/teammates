package teammates.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import teammates.common.util.Const.SystemParams;

/** A helper class to hold time-related functions (e.g., converting dates to strings etc.).
 * Time zone is assumed as UTC unless specifically mentioned.
 */
public final class TimeHelper {

    private static final Logger log = Logger.getLogger();

    private static final Map<String, String> TIME_ZONE_CITIES_MAP = new HashMap<>();
    private static final List<Double> TIME_ZONE_VALUES = new ArrayList<>();

    /*
     *This time zone - city map was created by selecting major cities from each time zone.
     *reference: http://en.wikipedia.org/wiki/List_of_UTC_time_offsets
     *The map was verified by comparing with world clock from http://www.timeanddate.com/worldclock/
     *Note: No DST is handled here.
     */

    static {
        map("-12.0", "Baker Island, Howland Island");
        map("-11.0", "American Samoa, Niue");
        map("-10.0", "Hawaii, Cook Islands");
        map("-9.5", "Marquesas Islands");
        map("-9.0", "Gambier Islands, Alaska");
        map("-8.0", "Los Angeles, Vancouver, Tijuana");
        map("-7.0", "Phoenix, Calgary, Ciudad Juárez");
        map("-6.0", "Chicago, Guatemala City, Mexico City, San José, San Salvador, Tegucigalpa, Winnipeg");
        map("-5.0", "New York, Lima, Toronto, Bogotá, Havana, Kingston");
        map("-4.5", "Caracas");
        map("-4.0", "Santiago, La Paz, San Juan de Puerto Rico, Manaus, Halifax");
        map("-3.5", "St. John's");
        map("-3.0", "Buenos Aires, Montevideo, São Paulo");
        map("-2.0", "Fernando de Noronha, South Georgia and the South Sandwich Islands");
        map("-1.0", "Cape Verde, Greenland, Azores islands");
        map("0.0", "Accra, Abidjan, Casablanca, Dakar, Dublin, Lisbon, London");
        map("1.0", "Belgrade, Berlin, Brussels, Lagos, Madrid, Paris, Rome, Tunis, Vienna, Warsaw");
        map("2.0", "Athens, Sofia, Cairo, Kiev, Istanbul, Beirut, Helsinki, Jerusalem, Johannesburg, Bucharest");
        map("3.0", "Nairobi, Baghdad, Doha, Khartoum, Minsk, Riyadh");
        map("3.5", "Tehran");
        map("4.0", "Baku, Dubai, Moscow");
        map("4.5", "Kabul");
        map("5.0", "Karachi, Tashkent");
        map("5.5", "Colombo, Delhi");
        map("5.75", "Kathmandu");
        map("6.0", "Almaty, Dhaka, Yekaterinburg");
        map("6.5", "Yangon");
        map("7.0", "Jakarta, Bangkok, Novosibirsk, Hanoi");
        map("8.0", "Perth, Beijing, Manila, Singapore, Kuala Lumpur, Denpasar, Krasnoyarsk");
        map("8.75", "Eucla");
        map("9.0", "Seoul, Tokyo, Pyongyang, Ambon, Irkutsk");
        map("9.5", "Adelaide");
        map("10.0", "Canberra, Yakutsk, Port Moresby");
        map("10.5", "Lord Howe Islands");
        map("11.0", "Vladivostok, Noumea");
        map("12.0", "Auckland, Suva");
        map("12.75", "Chatham Islands");
        map("13.0", "Phoenix Islands, Tokelau, Tonga");
        map("14.0", "Line Islands");

    }

    private TimeHelper() {
        // utility class
    }

    private static void map(String timeZone, String cities) {
        TIME_ZONE_CITIES_MAP.put(timeZone, cities);
        TIME_ZONE_VALUES.add(Double.parseDouble(timeZone));
    }

    /**
     * Sets the system time zone if it differs from the standard one defined in {@link SystemParams#TIME_ZONE}.
     */
    public static void setSystemTimeZoneIfRequired() {
        TimeZone originalTimeZone = TimeZone.getDefault();
        if (SystemParams.TIME_ZONE.equals(originalTimeZone)) {
            return;
        }

        TimeZone.setDefault(SystemParams.TIME_ZONE);
        log.info("Time zone set to " + SystemParams.TIME_ZONE.getID() + " (was " + originalTimeZone.getID() + ")");
    }

    public static String getCitiesForTimeZone(String zone) {
        return TIME_ZONE_CITIES_MAP.get(zone);
    }

    public static List<Double> getTimeZoneValues() {
        return new ArrayList<>(TIME_ZONE_VALUES);
    }

    /**
     * Returns the current date and time as a {@code Calendar} object for the given timezone.
     */
    public static Calendar now(double timeZone) {
        return TimeHelper.convertToUserTimeZone(
                Calendar.getInstance(SystemParams.TIME_ZONE), timeZone);
    }

    /**
     * Convert a date string and time string into a Date object. Returns null on error.
     *
     * @param inputDate
     *            The date in format EEE, dd MMM, yyyy
     * @param inputTimeHours
     *            The time as number of hours
     */
    public static Date combineDateTime(String inputDate, String inputTimeHours) {
        if (inputDate == null || inputTimeHours == null) {
            return null;
        }

        int inputTimeInt = 0;
        try {
            inputTimeInt = Integer.parseInt(inputTimeHours) * 100;
        } catch (NumberFormatException nfe) {
            return null;
        }
        return convertToDate(inputDate, inputTimeInt);
    }

    /**
     * Returns the date object with specified offset in number of days from now.
     */
    public static Date getDateOffsetToCurrentTime(int offsetDays) {
        Calendar cal = Calendar.getInstance(SystemParams.TIME_ZONE);
        cal.setTime(cal.getTime());
        cal.add(Calendar.DATE, +offsetDays);
        return cal.getTime();
    }

    /**
     * Returns the date object with specified offset in number of ms from now.
     */
    public static Date getMsOffsetToCurrentTime(int offsetMilliseconds) {
        Calendar cal = Calendar.getInstance(SystemParams.TIME_ZONE);
        cal.setTime(cal.getTime());
        cal.add(Calendar.MILLISECOND, +offsetMilliseconds);
        return cal.getTime();
    }

    public static Date getMsOffsetToCurrentTimeInUserTimeZone(int offset, double timeZone) {
        Date d = getMsOffsetToCurrentTime(offset);
        Calendar c = Calendar.getInstance(SystemParams.TIME_ZONE);
        c.setTime(d);
        return convertToUserTimeZone(c, timeZone).getTime();
    }

    public static Calendar convertToUserTimeZone(Calendar time, double timeZone) {
        Calendar newTime = (Calendar) time.clone();
        newTime.add(Calendar.MILLISECOND, (int) (60 * 60 * 1000 * timeZone));
        return newTime; // for chaining
    }

    /**
     * Converts the {@code localDate} from {@code localTimeZone}) to UTC through shifting by the offset.
     * Does not shift if {@code localDate} is a special representation.
     */
    public static Date convertLocalDateToUtc(Date localDate, double localTimeZone) {
        if (localDate == null) {
            return null;
        }
        if (isSpecialTime(localDate)) {
            return localDate;
        }
        Calendar localCal = dateToCalendar(localDate);
        localCal.add(Calendar.MINUTE, (int) (60 * (-localTimeZone)));
        return localCal.getTime();
    }

    /**
     * Inverse of {@link #convertLocalDateToUtc}.
     */
    public static Date convertUtcToLocalDate(Date utcDate, double timeZone) {
        return convertLocalDateToUtc(utcDate, -timeZone);
    }

    /**
     * Formats a date in the corresponding option value in 'Time' dropdowns The
     * hour just after midnight is converted to option 24 (i.e., 2359 as shown
     * to the user) 23.59 is also converted to 24. (i.e., 23.59-00.59 ---> 24)
     */
    public static int convertToOptionValueInTimeDropDown(Date date) {
        //TODO: see if we can eliminate this method (i.e., merge with convertToDisplayValueInTimeDropDown)
        Calendar c = Calendar.getInstance(SystemParams.TIME_ZONE);
        c.setTime(date);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        hour = hour == 0 ? 24 : hour;
        hour = hour == 23 && minutes == 59 ? 24 : hour;
        return hour;
    }

    /**
     * Formats a date in the format dd/MM/yyyy.
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(SystemParams.TIME_ZONE);
        return sdf.format(date);
    }

    /**
     * Formats a date in the format EEE, dd MMM, yyyy. Example: Sat, 05 May, 2012
     */
    public static String formatDateForSessionsForm(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM, yyyy");
        sdf.setTimeZone(SystemParams.TIME_ZONE);
        return sdf.format(date);
    }

    /**
     * Formats a date in the format dd MMM yyyy, hh:mm a. Example: 05 May 2012,
     * 2:04 PM<br>
     */
    public static String formatTime12H(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = null;
        Calendar c = Calendar.getInstance(SystemParams.TIME_ZONE);
        c.setTime(date);
        if (c.get(Calendar.HOUR_OF_DAY) == 12 && c.get(Calendar.MINUTE) == 0) {
            sdf = new SimpleDateFormat("EEE, dd MMM yyyy, hh:mm");
            sdf.setTimeZone(SystemParams.TIME_ZONE);
            return sdf.format(date) + " NOON";
        }
        sdf = new SimpleDateFormat("EEE, dd MMM yyyy, hh:mm a");
        sdf.setTimeZone(SystemParams.TIME_ZONE);
        return sdf.format(date);
    }

    public static String formatDateTimeForSessions(Date dateInUtc, double sessionTimeZone) {
        if (dateInUtc == null) {
            return "";
        }
        SimpleDateFormat sdf = null;
        Calendar c = Calendar.getInstance(SystemParams.TIME_ZONE);
        TimeZone timeZone = getTimeZoneFromDoubleOffset(sessionTimeZone);
        c.setTimeZone(timeZone);
        c.setTime(dateInUtc);
        String periodIndicator =
                c.get(Calendar.HOUR_OF_DAY) == 12 && c.get(Calendar.MINUTE) == 0 ? "'NOON'" : "a";
        sdf = new SimpleDateFormat("EEE, dd MMM yyyy, hh:mm " + periodIndicator + " 'UTC'Z");
        sdf.setTimeZone(timeZone);
        return sdf.format(dateInUtc);
    }

    /**
     * Formats a date in the format d MMM h:mm a. Example: 5 May 11:59 PM
     */
    public static String formatDateTimeForInstructorHomePage(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = null;
        Calendar c = Calendar.getInstance(SystemParams.TIME_ZONE);
        c.setTime(date);
        if (c.get(Calendar.HOUR_OF_DAY) == 12 && c.get(Calendar.MINUTE) == 0) {
            sdf = new SimpleDateFormat("d MMM h:mm");
            sdf.setTimeZone(SystemParams.TIME_ZONE);
            return sdf.format(date) + " NOON";
        }
        sdf = new SimpleDateFormat("d MMM h:mm a");
        sdf.setTimeZone(SystemParams.TIME_ZONE);
        return sdf.format(date);
    }

    /**
     * Formats a date in the format d MMM yyyy. Example: 5 May 2017
     */
    public static String formatDateTimeForInstructorCoursesPage(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = null;
        Calendar c = Calendar.getInstance(SystemParams.TIME_ZONE);
        c.setTime(date);
        sdf = new SimpleDateFormat("d MMM yyyy");
        sdf.setTimeZone(SystemParams.TIME_ZONE);
        return sdf.format(date);
    }

    /**
     * Formats {@code dateInUtc} according to the ISO8601 format.
     */
    public static String formatDateToIso8601Utc(Date dateInUtc) {
        if (dateInUtc == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(Const.TIME_FORMAT_ISO_8601_UTC);
        sdf.setTimeZone(SystemParams.TIME_ZONE);
        return sdf.format(dateInUtc);
    }

    public static String calendarToString(Calendar c) {
        if (c == null) {
            return "";
        }
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss SSS").format(c.getTime());
    }

    /**
     * Converts the date string to a Date object.
     *
     * @param dateInStringFormat should be in the format {@link SystemParams#DEFAULT_DATE_TIME_FORMAT}
     */
    public static Date convertToDate(String dateInStringFormat) {
        try {
            DateFormat df = new SimpleDateFormat(SystemParams.DEFAULT_DATE_TIME_FORMAT);
            return df.parse(dateInStringFormat);
        } catch (ParseException e) {
            Assumption.fail("Date in String is in wrong format.");
            return null;
        }
    }

    public static Calendar dateToCalendar(Date date) {
        Calendar c = Calendar.getInstance(SystemParams.TIME_ZONE);
        if (date == null) {
            return c;
        }
        c.setTime(date);
        return c;
    }

    /**
     * Returns whether the given date is being used as a special representation,
     * signifying it's face value should not be used without proper processing.
     * A null date is not a special time.
     */
    public static boolean isSpecialTime(Date date) {

        if (date == null) {
            return false;
        }

        return date.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)
               || date.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)
               || date.equals(Const.TIME_REPRESENTS_LATER)
               || date.equals(Const.TIME_REPRESENTS_NEVER)
               || date.equals(Const.TIME_REPRESENTS_NOW);

    }

    public static boolean isOlderThanAYear(Date compareDate) {
        Date currentDate = new Date();
        int differenceInDays;

        differenceInDays = (int) ((currentDate.getTime() - compareDate.getTime()) / (1000 * 60 * 60 * 24));

        return differenceInDays > 365;
    }

    /**
     * Returns true if the {@code time} falls within the last hour.
     * That is exactly one hour or less from the current time but earlier than current time.
     * Precision is at millisecond level.
     */
    public static boolean isWithinPastHourFromNow(Date time) {
        return isWithinPastHour(time, new Date());
    }

    /**
     * Returns true if the {@code time1} falls within past 1 hour of {@code time2}.
     * That is exactly one hour or less from time2 but earlier than time2.
     * Precision is at millisecond level.
     */
    public static boolean isWithinPastHour(Date time1, Date time2) {
        Calendar calendarTime1 = Calendar.getInstance(SystemParams.TIME_ZONE);
        calendarTime1.setTime(time1);

        Calendar calendarTime2 = Calendar.getInstance(SystemParams.TIME_ZONE);
        calendarTime2.setTime(time2);

        long time1Millis = calendarTime1.getTimeInMillis();
        long time2Millis = calendarTime2.getTimeInMillis();
        long differenceBetweenNowAndCal = (time2Millis - time1Millis) / (60 * 60 * 1000);
        return differenceBetweenNowAndCal == 0 && calendarTime2.after(calendarTime1);
    }

    /**
     * Checks if the time falls between the period specified. Possible scenarios:
     * <ul>
     *  <li>{@code startTime <= time <= endTime}</li>
     *  <li>{@code startTime <= time < endTime}</li>
     *  <li>{@code startTime < time <= endTime}</li>
     *  <li>{@code startTime < time < endTime}</li>
     * </ul>
     * @param startTime the start time of the period
     * @param endTime the end time of the period
     * @param time the time to be checked
     * @param isStartInclusive true to allow time to fall on start time
     * @param isEndInclusive true to allow time to fall on end time
     * @return true if the time falls between the start and end time
     */
    public static boolean isTimeWithinPeriod(Date startTime, Date endTime, Date time,
                                             boolean isStartInclusive, boolean isEndInclusive) {
        if (startTime == null || endTime == null || time == null) {
            return false;
        }

        boolean isAfterStartTime = time.after(startTime) || isStartInclusive && time.equals(startTime);
        boolean isBeforeEndTime = time.before(endTime) || isEndInclusive && time.equals(endTime);

        return isAfterStartTime && isBeforeEndTime;
    }

    public static double getLocalTimezoneHourOffset() {
        // getOffset returns the offset from UTC in milliseconds
        // so we need to divide it by (1000 * 60 * 60) to get it in hours
        return TimeZone.getDefault().getOffset(new Date().getTime()) / 1000.0 / 60.0 / 60.0;
    }

    private static Date convertToDate(String date, int time) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM, yyyy");
        sdf.setTimeZone(SystemParams.TIME_ZONE);
        Calendar calendar = Calendar.getInstance(SystemParams.TIME_ZONE);

        // Perform date manipulation
        try {
            Date newDate = sdf.parse(date);
            calendar.setTime(newDate);

            if (time == 2400) {
                calendar.set(Calendar.HOUR, 23);
                calendar.set(Calendar.MINUTE, 59);
            } else {
                calendar.set(Calendar.HOUR, time / 100);
                calendar.set(Calendar.MINUTE, time % 100);
            }

            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Returns Duration in format m:s:ms.
     *
     * <p>Example: 1200 milliseconds ---> 0:1:200.
     */

    public static String convertToStandardDuration(Long timeInMilliseconds) {

        return timeInMilliseconds == null
             ? ""
             : String.format("%d:%d:%d",
                             timeInMilliseconds / 60000,
                             timeInMilliseconds / 1000,
                             timeInMilliseconds % 1000);
    }

    /**
     * Combines separated date, hour and minute string into standard format.
     *
     * <p>required parameter format:
     * date: dd/MM/yyyy  hour: hh   min:mm
     *
     * @return Date String in the format {@link SystemParams#DEFAULT_DATE_TIME_FORMAT}.<br>
     *         Example: If date is 01/04/2014, hour is 23, min is 59, result will be  2014-04-01 11:59 PM UTC.
     */
    public static String convertToRequiredFormat(String date, String hour, String min) {

        if (date == null || hour == null || min == null) {
            return null;
        }

        final String OLD_FORMAT = "dd/MM/yyyy";
        final String NEW_FORMAT = "yyyy-MM-dd";

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        try {
            Date d = sdf.parse(date);
            sdf.applyPattern(NEW_FORMAT);
            int intHour = Integer.parseInt(hour);
            String amOrPm = intHour >= 12 ? "PM" : "AM";
            intHour = intHour >= 13 ? intHour - 12 : intHour;
            return sdf.format(d) + " " + intHour + ":" + min + " " + amOrPm + " UTC";
        } catch (ParseException e) {
            Assumption.fail("Date in String is in wrong format.");
            return null;
        }

    }

    public static TimeZone getTimeZoneFromDoubleOffset(double sessionTimeZone) {
        int hours = (int) sessionTimeZone;
        int minutes = (int) ((Math.abs(sessionTimeZone) - Math.floor(Math.abs(sessionTimeZone))) * 60);
        return TimeZone.getTimeZone(String.format("GMT%+03d:%02d", hours, minutes));
    }

}
