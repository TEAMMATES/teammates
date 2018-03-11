package teammates.common.util;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesProvider;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import teammates.common.exception.TeammatesException;
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
     * Registers the zone rules loaded from resources via {@link TzdbResourceZoneRulesProvider}.
     * Some manipulation of the system class loader is required to enable loading of a custom
     * {@link ZoneRulesProvider} in GAE.
     */
    public static void registerResourceZoneRules() {
        try {
            ClassLoader originalScl = ClassLoader.getSystemClassLoader();

            // ZoneRulesProvider uses the system class loader for loading a custom provider as the default provider.
            // However, GAE's system class loader includes only the Java runtime and not the application. Hence, we
            // use reflection to temporarily replace the system class loader with the class loader for the current context.
            Field scl = ClassLoader.class.getDeclaredField("scl");
            scl.setAccessible(true);
            scl.set(null, Thread.currentThread().getContextClassLoader());

            // ZoneRulesProvider reads this system property to determine which provider to use as the default.
            System.setProperty("java.time.zone.DefaultZoneRulesProvider",
                    TzdbResourceZoneRulesProvider.class.getCanonicalName());

            // This first reference to ZoneRulesProvider executes the class's static initialization block,
            // performing the actual registration of our custom provider named in the system property above.
            // The system class loader is used to load the class from the name.
            // If any exceptions occur, an Error is thrown.
            log.info("Registered zone rules version " + ZoneRulesProvider.getVersions("UTC").firstKey());

            // Restore the original system class loader.
            scl.set(null, originalScl);

        } catch (ReflectiveOperationException | Error e) {
            log.severe("Failed to register zone rules: " + TeammatesException.toStringWithStackTrace(e));
        }
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
    @Deprecated
    public static Calendar now(double timeZone) {
        return TimeHelper.convertToUserTimeZone(
                Calendar.getInstance(SystemParams.TIME_ZONE), timeZone);
    }

    /**
     * Returns the current instant in time as an {@code Instant} object.
     */
    public static Instant now() {
        return Instant.now();
    }

    /**
     * Convert a date string and time string of only the hour into a Date object.
     * If the hour is 24, it is converted to 23:59. Returns null on error.
     * @param inputDate
     *            The date in format EEE, dd MMM, yyyy
     * @param inputTimeHours
     *            0-24
     */
    @Deprecated
    public static Date combineDateTime(String inputDate, String inputTimeHours) {
        return convertLocalDateTimeToDate(combineDateTimeNew(inputDate, inputTimeHours));
    }

    /**
     * Convert a date string and time string of only the hour into a Date object.
     * If the hour is 24, it is converted to 23:59. Returns null on error.
     * @param inputDate         the date string in EEE, dd MMM, yyyy format
     * @param inputTimeHours    the hour, 0-24
     * @return                  a LocalDateTime at the specified date and hour
     */
    // TODO: Rename after deleting the deprecated combineDateTime
    public static LocalDateTime combineDateTimeNew(String inputDate, String inputTimeHours) {
        if (inputDate == null || inputTimeHours == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM, yyyy H.mm");
        String dateTimeString;
        if ("24".equals(inputTimeHours)) {
            dateTimeString = inputDate + " 23.59";
        } else {
            dateTimeString = inputDate + " " + inputTimeHours + ".00";
        }
        try {
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Returns the date object with specified offset in number of days from now.
     * @deprecated Use {@link TimeHelper#getInstantDaysOffsetFromNow(long)} instead.
     */
    @Deprecated
    public static Date getDateOffsetToCurrentTime(long offsetInDays) {
        return Date.from(getInstantDaysOffsetFromNow(offsetInDays));
    }

    /**
     * Returns an java.time.Instant object that is offset by a number of days from now.
     * @param offsetInDays number of days offset by (integer).
     * @return java.time.Instant offset by offsetInDays days.
     */
    public static Instant getInstantDaysOffsetFromNow(long offsetInDays) {
        return Instant.now().plus(Duration.ofDays(offsetInDays));
    }

    // User time zone is just a view of an Instant/ZonedDateTime,
    // which should be handled in formatting methods.
    // TODO: Remove this method and refactor where it is used.
    @Deprecated
    public static Calendar convertToUserTimeZone(Calendar time, double timeZone) {
        Calendar newTime = (Calendar) time.clone();
        newTime.add(Calendar.MILLISECOND, (int) (60 * 60 * 1000 * timeZone));
        return newTime; // for chaining
    }

    /**
     * Converts the {@code localDate} from {@code localTimeZone} to UTC through shifting by the offset.
     * Does not shift if {@code localDate} is a special representation.
     */
    public static Date convertLocalDateToUtc(Date localDate, double localTimeZone) {
        return convertInstantToDate(
                convertLocalDateTimeToInstant(convertDateToLocalDateTime(localDate),
                        convertToZoneId(localTimeZone)));
    }

    /**
     * Converts the {@code localDateTime} to {@code Instant} using the {@code timeZone}.
     */
    public static Instant convertLocalDateTimeToInstant(LocalDateTime localDateTime, ZoneId timeZone) {
        return localDateTime == null ? null : localDateTime.atZone(timeZone).toInstant();
    }

    /**
     * Converts the {@code Instant} at the specified {@code timeZone} to {@code localDateTime}.
     */
    public static LocalDateTime convertInstantToLocalDateTime(Instant instant, ZoneId timeZoneId) {
        return instant == null ? null : instant.atZone(timeZoneId).toLocalDateTime();
    }

    /**
     * Inverse of {@link #convertLocalDateToUtc}.
     */
    public static Date convertUtcToLocalDate(Date utcDate, double timeZone) {
        return convertLocalDateToUtc(utcDate, -timeZone);
    }

    /**
     * Format {@code localDateTime} according to a specified {@code pattern}.
     * PM is especially formatted as NOON if it's 12:00 PM, if present
     */
    private static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null) {
            return "";
        }
        String processedPattern = pattern;
        if (localDateTime.getHour() == 12 && localDateTime.getMinute() == 0) {
            processedPattern = pattern.replace("a", "'NOON'");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(processedPattern);
        return localDateTime.format(formatter);
    }

    /**
     * Formats a date in the format dd/MM/yyyy.
     */
    @Deprecated
    public static String formatDate(Date date) {
        return formatDate(convertDateToLocalDateTime(date));
    }

    /**
     * Formats a date in the format dd/MM/yyyy.
     */
    public static String formatDate(LocalDateTime localDateTime) {
        return formatLocalDateTime(localDateTime, "dd/MM/yyyy");
    }

    /**
     * Formats a date in the format EEE, dd MMM, yyyy. Example: Sat, 05 May, 2012
     */
    @Deprecated
    public static String formatDateForSessionsForm(Date date) {
        return formatDateForSessionsForm(convertDateToLocalDateTime(date));
    }

    /**
     * Formats a date in the format EEE, dd MMM, yyyy. Example: Sat, 05 May, 2012
     */
    public static String formatDateForSessionsForm(LocalDateTime localDateTime) {
        return formatLocalDateTime(localDateTime, "EEE, dd MMM, yyyy");
    }

    /**
     * Formats a date in the format dd MMM yyyy, hh:mm a. Example: 05 May 2012,
     * 2:04 PM<br>
     */
    @Deprecated
    public static String formatTime12H(Date date) {
        return formatTime12H(convertDateToLocalDateTime(date));
    }

    /**
     * Formats a date in the format dd MMM yyyy, hh:mm a. 12:00 PM is especially formatted as 12:00 NOON
     * Example: 05 May 2012, 2:04 PM<br>
     */
    public static String formatTime12H(LocalDateTime localDateTime) {
        return formatLocalDateTime(localDateTime, "EEE, dd MMM yyyy, hh:mm a");
    }

    /**
     * Format {@code instant} at a {@code timeZone} according to a specified {@code pattern}.
     * PM is especially formatted as NOON if it's 12:00 PM, if present.
     */
    private static String formatInstant(Instant instant, ZoneId timeZone, String pattern) {
        if (instant == null) {
            return "";
        }
        ZonedDateTime zonedDateTime = instant.atZone(timeZone);
        String processedPattern = pattern;
        if (zonedDateTime.getHour() == 12 && zonedDateTime.getMinute() == 0) {
            processedPattern = pattern.replace("a", "'NOON'");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(processedPattern);
        return zonedDateTime.format(formatter);
    }

    @Deprecated
    public static String formatDateTimeForSessions(Date dateInUtc, double sessionTimeZone) {
        return formatDateTimeForSessions(
                convertDateToInstant(dateInUtc), convertToZoneId(sessionTimeZone));
    }

    public static String formatDateTimeForSessions(Instant instant, ZoneId sessionTimeZone) {
        return formatInstant(instant, sessionTimeZone, "EEE, dd MMM yyyy, hh:mm a 'UTC'Z");
    }

    /**
     * Formats a date in the format d MMM h:mm a. Example: 5 May 11:59 PM
     */
    @Deprecated
    public static String formatDateTimeForInstructorHomePage(Date date) {
        return formatDateTimeForInstructorHomePage(convertDateToLocalDateTime(date));
    }

    /**
     * Formats a date in the format d MMM h:mm a. Example: 5 May 11:59 PM
     */
    public static String formatDateTimeForInstructorHomePage(LocalDateTime localDateTime) {
        return formatLocalDateTime(localDateTime, "d MMM h:mm a");
    }

    /**
     * Formats a date in the format d MMM yyyy. Example: 5 May 2017
     */
    @Deprecated
    public static String formatDateTimeForInstructorCoursesPage(Date date, String timeZoneId) {
        return formatDateTimeForInstructorCoursesPage(convertDateToInstant(date), ZoneId.of(timeZoneId));
    }

    /**
     * Formats a date in the format d MMM yyyy. Example: 5 May 2017
     */
    public static String formatDateTimeForInstructorCoursesPage(Instant instant, ZoneId timeZoneId) {
        return formatInstant(instant, timeZoneId, "d MMM yyyy");
    }

    /**
     * Formats {@code dateInUtc} according to the ISO8601 format.
     */
    @Deprecated
    public static String formatDateToIso8601Utc(Date dateInUtc) {
        return formatInstantToIso8601Utc(convertDateToInstant(dateInUtc));
    }

    /**
     * Formats {@code instant} according to the ISO8601 format.
     */
    public static String formatInstantToIso8601Utc(Instant instant) {
        return instant == null ? null : DateTimeFormatter.ISO_INSTANT.format(instant);
    }

    public static String formatActivityLogTime(Instant instant, ZoneId adminTimeZone) {
        return formatInstant(instant, adminTimeZone, "MM/dd/yyyy HH:mm:ss SSS");
    }

    /**
     * Converts the date string to a Date object.
     *
     * @param dateInStringFormat should be in the format {@link SystemParams#DEFAULT_DATE_TIME_FORMAT}
     * @deprecated Use {@link TimeHelper#parseInstant(String)} instead
     */
    @Deprecated
    public static Date convertToDate(String dateInStringFormat) {
        return convertInstantToDate(parseInstant(dateInStringFormat));
    }

    /**
     * Converts the datetime string to an Instant object.
     *
     * @param dateTimeString should be in the format {@link SystemParams#DEFAULT_DATE_TIME_FORMAT}
     */
    public static Instant parseInstant(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SystemParams.DEFAULT_DATE_TIME_FORMAT);
        try {
            return ZonedDateTime.parse(dateTimeString, formatter).toInstant();
        } catch (DateTimeParseException e) {
            Assumption.fail("Date in String is in wrong format.");
            return null;
        }
    }

    // TODO: both Date and Calendar will become Instant so this should be removed totally
    @Deprecated
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
     * @deprecated This method will be removed eventually once FeedbackSessionsDb is overhauled.
     */
    @Deprecated
    public static boolean isTimeWithinPeriod(Date startTime, Date endTime, Date time,
                                             boolean isStartInclusive, boolean isEndInclusive) {
        if (startTime == null || endTime == null || time == null) {
            return false;
        }

        boolean isAfterStartTime = time.after(startTime) || isStartInclusive && time.equals(startTime);
        boolean isBeforeEndTime = time.before(endTime) || isEndInclusive && time.equals(endTime);

        return isAfterStartTime && isBeforeEndTime;
    }

    /**
     * Temporary method for transition from storing time zone as double.
     */
    @Deprecated
    public static ZoneId convertToZoneId(double timeZone) {
        return ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds((int) (timeZone * 60 * 60)));
    }

    /**
     * Temporary method for transition from java.util.Date.
     * @param localDateTime will be assumed to be in UTC
     */
    @Deprecated
    public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Date.from(localDateTime.atZone(ZoneId.of("UTC")).toInstant());
    }

    /**
     * Temporary method for transition from java.util.Date.
     */
    @Deprecated
    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    /**
     * Temporary method for transition from java.util.Date.
     */
    @Deprecated
    public static Date convertInstantToDate(Instant instant) {
        return instant == null ? null : Date.from(instant);
    }

    /**
     * Temporary method for transition from java.util.Date.
     */
    @Deprecated
    public static Instant convertDateToInstant(Date date) {
        return date == null ? null : date.toInstant();
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
                (timeInMilliseconds % 60000) / 1000,
                timeInMilliseconds % 1000);
    }

    /**
     * Parse a `LocalDateTime` object from separated date, hour and minute strings.
     *
     * <p>required parameter format:
     * date: dd/MM/yyyy  hour: H   min:m
     * Example: If date is 01/04/2014, hour is 23, min is 59.
     */
    public static LocalDateTime parseLocalDateTime(String date, String hour, String min) {
        if (date == null || hour == null || min == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy H m");
        try {
            return LocalDateTime.parse(date + " " + hour + " " + min, formatter);
        } catch (DateTimeParseException e) {
            Assumption.fail("Date in String is in wrong format.");
            return null;
        }
    }

}
