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

    /**
     * Represents the ambiguity status for a {@link LocalDateTime} at a given time {@code zone},
     * brought about by Daylight Saving Time (DST).
     */
    public enum LocalDateTimeAmbiguityStatus {
        /**
         * The local date time can be unambiguously resolved to a single instant.
         * It has only one valid interpretation.
         */
        UNAMBIGUOUS,

        /**
         * The local date time falls within the gap period when clocks spring forward at the start of DST.
         * Strictly speaking, it is non-existent, and needs to be readjusted to be valid.
         */
        GAP,

        /**
         * The local date time falls within the overlap period when clocks fall back at the end of DST.
         * It has more than one valid interpretation.
         */
        OVERLAP;

        /**
         * Gets the ambiguity status for a {@link LocalDateTime} at a given time {@code zone}.
         */
        public static LocalDateTimeAmbiguityStatus of(LocalDateTime localDateTime, ZoneId zone) {
            if (localDateTime == null || zone == null) {
                return null;
            }

            List<ZoneOffset> offsets = zone.getRules().getValidOffsets(localDateTime);
            if (offsets.size() == 1) {
                return UNAMBIGUOUS;
            }
            if (offsets.isEmpty()) {
                return GAP;
            }
            return OVERLAP;
        }
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
     * Convert a date string and time string of only the hour into a Date object.
     * If the hour is 24, it is converted to 23:59. Returns null on error.
     * @param inputDate         the date string in EEE, dd MMM, yyyy format
     * @param inputTimeHours    the hour, 0-24
     * @return                  a LocalDateTime at the specified date and hour
     */
    public static LocalDateTime combineDateTime(String inputDate, String inputTimeHours) {
        if (inputDate == null || inputTimeHours == null) {
            return null;
        }

        String dateTimeString;
        if ("24".equals(inputTimeHours)) {
            dateTimeString = inputDate + " 23.59";
        } else {
            dateTimeString = inputDate + " " + inputTimeHours + ".00";
        }
        return parseLocalDateTime(dateTimeString, "EEE, dd MMM, yyyy H.mm");
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
     * Warning: this is required for correct interpretation of time fields in legacy FeedbackSession entities.
     * Do not remove until all FeedbackSession entities have been migrated to UTC.
     */
    @Deprecated
    public static Date convertLocalDateToUtc(Date localDate, double localTimeZone) {
        if (isSpecialTime(convertDateToInstant(localDate))) {
            return localDate;
        }
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
     * Format {@code localDateTime} according to a specified {@code pattern}.
     * PM is especially formatted as NOON if it's 12:00 PM, if present
     */
    private static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null || pattern == null) {
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
        if (instant == null || timeZone == null || pattern == null) {
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
        return formatInstant(instant, sessionTimeZone, "EEE, dd MMM yyyy, hh:mm a z");
    }

    public static String formatDateTimeForDisambiguation(Instant instant, ZoneId zone) {
        return formatInstant(instant, zone, "EEE, dd MMM yyyy, hh:mm a z ('UTC'Z)");
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

    /**
     * Formats {@code instant} in admin's activity log page.
     *
     * @param instant   the instant to be formatted
     * @param zoneId    the time zone to calculate local date and time
     * @return          the formatted string
     */
    public static String formatActivityLogTime(Instant instant, ZoneId zoneId) {
        return formatInstant(instant, zoneId, "dd/MM/yyyy HH:mm:ss.SSS");
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

    /**
     * Returns whether the given instant is being used as a special representation,
     * signifying its face value should not be used without proper processing.
     * A null instant is not a special time.
     */
    public static boolean isSpecialTime(Instant instant) {

        if (instant == null) {
            return false;
        }

        return instant.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)
                || instant.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)
                || instant.equals(Const.TIME_REPRESENTS_LATER)
                || instant.equals(Const.TIME_REPRESENTS_NEVER)
                || instant.equals(Const.TIME_REPRESENTS_NOW);

    }

    /**
     * Temporary method for transition from storing time zone as double.
     */
    @Deprecated
    public static ZoneId convertToZoneId(double timeZone) {
        return ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds((int) (timeZone * 60 * 60)));
    }

    /**
     * Inverse of {@link #convertToZoneId}.
     */
    @Deprecated
    public static double convertToOffset(ZoneId timeZone) {
        return ((double) timeZone.getRules().getOffset(Instant.now()).getTotalSeconds()) / 60 / 60;
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
     * Parses a {@code LocalDateTime} object from a date time string according to a pattern.
     * Returns {@code null} on error.
     *
     * @param dateTimeString    the string containing the date and time
     * @param pattern           the pattern of the date and time string
     * @return                  the parsed {@code LocalDateTime} object, or {@code null} if there are errors
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeString, String pattern) {
        if (dateTimeString == null || pattern == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a {@code LocalDateTime} object from separated date, hour and minute strings.
     * Example: date "Tue, 01 Apr, 2014", hour "23", min "59"
     *
     * @param date  date in format "EEE, dd MMM, yyyy"
     * @param hour  hour-of-day (0-23)
     * @param min   minute-of-hour (0-59)
     * @return      the parsed {@code LocalDateTime} object, or {@code null} if there are errors
     */
    public static LocalDateTime parseLocalDateTimeForSessionsForm(String date, String hour, String min) {
        if (date == null || hour == null || min == null) {
            return null;
        }
        return parseLocalDateTime(date + " " + hour + " " + min, "EEE, dd MMM, yyyy H m");
    }

}
