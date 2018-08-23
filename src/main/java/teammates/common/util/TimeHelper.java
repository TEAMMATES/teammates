package teammates.common.util;

import java.lang.reflect.Field;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.zone.ZoneRulesProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Const.SystemParams;

/** A helper class to hold time-related functions (e.g., converting dates to strings etc.).
 * Time zone is assumed as UTC unless specifically mentioned.
 */
public final class TimeHelper {
    private static final Logger log = Logger.getLogger();
    private static final Map<ZoneId, String> TIME_ZONE_CITIES_MAP = new LinkedHashMap<>();

    /*
     *This time zone - city map was created by selecting major cities from each time zone.
     *reference: http://en.wikipedia.org/wiki/List_of_UTC_time_offsets
     *The map was verified by comparing with world clock from http://www.timeanddate.com/worldclock/
     *Note: No DST is handled here.
     */

    static {
        map("UTC-12:00", "Baker Island, Howland Island");
        map("UTC-11:00", "American Samoa, Niue");
        map("UTC-10:00", "Hawaii, Cook Islands");
        map("UTC-09:30", "Marquesas Islands");
        map("UTC-09:00", "Gambier Islands, Alaska");
        map("UTC-08:00", "Los Angeles, Vancouver, Tijuana");
        map("UTC-07:00", "Phoenix, Calgary, Ciudad Juárez");
        map("UTC-06:00", "Chicago, Guatemala City, Mexico City, San José, San Salvador, Tegucigalpa, Winnipeg");
        map("UTC-05:00", "New York, Lima, Toronto, Bogotá, Havana, Kingston");
        map("UTC-04:30", "Caracas");
        map("UTC-04:00", "Santiago, La Paz, San Juan de Puerto Rico, Manaus, Halifax");
        map("UTC-03:30", "St. John's");
        map("UTC-03:00", "Buenos Aires, Montevideo, São Paulo");
        map("UTC-02:00", "Fernando de Noronha, South Georgia and the South Sandwich Islands");
        map("UTC-01:00", "Cape Verde, Greenland, Azores islands");
        map("UTC", "Accra, Abidjan, Casablanca, Dakar, Dublin, Lisbon, London");
        map("UTC+01:00", "Belgrade, Berlin, Brussels, Lagos, Madrid, Paris, Rome, Tunis, Vienna, Warsaw");
        map("UTC+02:00", "Athens, Sofia, Cairo, Kiev, Istanbul, Beirut, Helsinki, Jerusalem, Johannesburg, Bucharest");
        map("UTC+03:00", "Nairobi, Baghdad, Doha, Khartoum, Minsk, Riyadh");
        map("UTC+03:30", "Tehran");
        map("UTC+04:00", "Baku, Dubai, Moscow");
        map("UTC+04:30", "Kabul");
        map("UTC+05:00", "Karachi, Tashkent");
        map("UTC+05:30", "Colombo, Delhi");
        map("UTC+05:45", "Kathmandu");
        map("UTC+06:00", "Almaty, Dhaka, Yekaterinburg");
        map("UTC+06:30", "Yangon");
        map("UTC+07:00", "Jakarta, Bangkok, Novosibirsk, Hanoi");
        map("UTC+08:00", "Perth, Beijing, Manila, Singapore, Kuala Lumpur, Denpasar, Krasnoyarsk");
        map("UTC+08:45", "Eucla");
        map("UTC+09:00", "Seoul, Tokyo, Pyongyang, Ambon, Irkutsk");
        map("UTC+09:30", "Adelaide");
        map("UTC+10:00", "Canberra, Yakutsk, Port Moresby");
        map("UTC+10:30", "Lord Howe Islands");
        map("UTC+11:00", "Vladivostok, Noumea");
        map("UTC+12:00", "Auckland, Suva");
        map("UTC+12:45", "Chatham Islands");
        map("UTC+13:00", "Phoenix Islands, Tokelau, Tonga");
        map("UTC+14:00", "Line Islands");

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
        TIME_ZONE_CITIES_MAP.put(ZoneId.of(timeZone), cities);
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

    public static String getCitiesForTimeZone(ZoneId zone) {
        return TIME_ZONE_CITIES_MAP.get(zone);
    }

    public static List<ZoneId> getTimeZoneValues() {
        return new ArrayList<>(TIME_ZONE_CITIES_MAP.keySet());
    }

    /**
     * Returns an Instant that is offset by a number of days from now.
     *
     * @param offsetInDays integer number of days to offset by
     * @return an Instant offset by {@code offsetInDays} days
     */
    public static Instant getInstantDaysOffsetFromNow(long offsetInDays) {
        return Instant.now().plus(Duration.ofDays(offsetInDays));
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
     * Formats a datetime stamp from a {@code LocalDateTime} using a formatting pattern.
     *
     * <p>Note: a formatting pattern containing 'a' (for the period; AM/PM) is treated differently at noon/midday.
     * Using that pattern with a datetime whose time falls on "12:00 PM" will cause it to be formatted as "12:00 NOON".</p>
     *
     * @param localDateTime the LocalDateTime to be formatted
     * @param pattern       formatting pattern, see Oracle docs for DateTimeFormatter for pattern table
     * @return the formatted datetime stamp string
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
     * Formats a datetime stamp from a {@code localDateTime}.
     * Example: Sun, 01 Apr 2018, 12:01 PM
     *
     * <p>Note: a datetime with time "12:00 PM" is specially formatted to "12:00 NOON"
     * Example: Sun, 01 Apr 2018, 12:00 NOON</p>
     *
     * @param localDateTime the LocalDateTime to be formatted
     * @return the formatted datetime stamp string
     */
    public static String formatDateTimeForDisplay(LocalDateTime localDateTime) {
        return formatLocalDateTime(localDateTime, "EEE, dd MMM yyyy, hh:mm a");
    }

    /**
     * Formats a date stamp from a {@code localDateTime} for populating the sessions form.
     * Example: Sun, 01 Apr, 2018
     *
     * <p>This method discards the time stored in the {@code localDateTime}.</p>
     *
     * @param localDateTime the LocalDateTime to be formatted
     * @return the formatted date stamp string
     */
    public static String formatDateForSessionsForm(LocalDateTime localDateTime) {
        return formatLocalDateTime(localDateTime, "EEE, dd MMM, yyyy");
    }

    /**
     * Formats a short datetime stamp from a {@code localDateTime} for the instructor's home page.
     * Example: 5 Apr 12:01 PM
     *
     * <p>Note: a datetime with time "12:00 PM" is specially formatted to "12:00 NOON"
     * Example: 5 Apr 12:01 NOON</p>
     *
     * @param localDateTime the LocalDateTime to be formatted
     * @return the formatted datetime stamp string
     */
    public static String formatDateTimeForInstructorHomePage(LocalDateTime localDateTime) {
        return formatLocalDateTime(localDateTime, "d MMM h:mm a");
    }

    /**
     * Convenience method to perform {@link #adjustLocalDateTimeForSessionsFormInputs} followed by
     * {@link #formatDateForSessionsForm} on a {@link LocalDateTime}.
     * @see #adjustAndFormatDateForSessionsFormInputs
     * @see #formatDateForSessionsForm
     */
    public static String adjustAndFormatDateForSessionsFormInputs(LocalDateTime localDateTime) {
        return formatDateForSessionsForm(adjustLocalDateTimeForSessionsFormInputs(localDateTime));
    }

    /**
     * Returns a copy of the {@link LocalDateTime} adjusted to be compatible with the format output by
     * {@link #parseDateTimeFromSessionsForm}, i.e. either the time is 23:59, or the minute is 0 and the hour is not 0.
     * The date time is first rounded to the nearest hour, then the special case 00:00 is handled.
     * @param ldt The {@link LocalDateTime} to be adjusted for compatibility.
     * @return a copy of {@code ldt} adjusted for compatibility, or null if {@code ldt} is null.
     * @see #parseDateTimeFromSessionsForm
     */
    public static LocalDateTime adjustLocalDateTimeForSessionsFormInputs(LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        }
        if (ldt.getMinute() == 0 && ldt.getHour() != 0 || ldt.getMinute() == 59 && ldt.getHour() == 23) {
            return ldt;
        }

        // Round to the nearest hour
        LocalDateTime rounded;
        LocalDateTime floor = ldt.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime ceiling = floor.plusHours(1);
        Duration distanceToCeiling = Duration.between(ldt, ceiling);
        if (distanceToCeiling.compareTo(Duration.ofMinutes(30)) <= 0) {
            rounded = ceiling;
        } else {
            rounded = floor;
        }

        // Adjust 00:00 -> 23:59
        if (rounded.getHour() == 0) {
            return rounded.minusMinutes(1);
        }
        return rounded;
    }

    /**
     * Formats a datetime stamp from an {@code instant} using a formatting pattern.
     *
     * <p>Note: a formatting pattern containing 'a' (for the period; AM/PM) is treated differently at noon/midday.
     * Using that pattern with a datetime whose time falls on "12:00 PM" will cause it to be formatted as "12:00 NOON".</p>
     *
     * @param instant  the instant to be formatted
     * @param timeZone the time zone to compute local datetime
     * @param pattern  formatting pattern, see Oracle docs for DateTimeFormatter for pattern table
     * @return the formatted datetime stamp string
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

    /**
     * Formats a datetime stamp from an {@code instant} including time zone name.
     * Example: Sun, 01 Apr 2018, 11:21 PM SGT
     *
     * <p>Note: a datetime with time "12:00 PM" is specially formatted to "12:00 NOON"
     * Example: Sun, 01 Apr 2018, 12:00 NOON SGT</p>
     *
     * @param instant         the instant to be formatted
     * @param sessionTimeZone the time zone to compute local datetime
     * @return the formatted datetime stamp string
     */
    public static String formatDateTimeForDisplay(Instant instant, ZoneId sessionTimeZone) {
        return formatInstant(instant, sessionTimeZone, "EEE, dd MMM yyyy, hh:mm a z");
    }

    /**
     * Formats a datetime stamp from an {@code instant} including time zone name and offset.
     * Example: Sun, 01 Apr 2018, 11:23 PM SGT (UTC+0800)
     *
     * <p>Note: a datetime with time "12:00 PM" is specially formatted to "12:00 NOON"
     * Example: Sun, 01 Apr 2018, 12:00 NOON SGT (UTC+0800)</p>
     *
     * @param instant the interpreted instant to be formatted
     * @param zone    the time zone to compute local datetime
     * @return the formatted datetime stamp string
     */
    public static String formatDateTimeForDisplayFull(Instant instant, ZoneId zone) {
        return formatInstant(instant, zone, "EEE, dd MMM yyyy, hh:mm a z ('UTC'Z)");
    }

    /**
     * Formats a date stamp from an {@code instant} for the instructor's courses page.
     * Example: 5 May 2017
     *
     * @param instant the instant to be formatted
     * @param zoneId  the time zone to calculate local date
     * @return the formatted date stamp string
     */
    public static String formatDateForInstructorCoursesPage(Instant instant, ZoneId zoneId) {
        return formatInstant(instant, zoneId, "d MMM yyyy");
    }

    /**
     * Formats a date stamp from an {@code instant} for the instructor's feedback sessions page.
     * Example: 5 May 2017
     * TODO: rename formatDateForInstructorCoursesPage and remove this method to avoid duplicated code
     *
     * @param instant the instant to be formatted
     * @param zoneId  the time zone to calculate local date
     * @return the formatted date stamp string
     */
    public static String formatDateForInstructorSessionsPage(Instant instant, ZoneId zoneId) {
        return formatInstant(instant, zoneId, "d MMM yyyy");
    }

    /**
     * Formats {@code instant} for the admin's activity log page.
     * Example: 01/04/2018 12:00:01.481
     *
     * <p>Timestamp precision to millisecond. Used for dev/admin-facing pages only.</p>
     *
     * @param instant the instant to be formatted
     * @param zoneId  the time zone to calculate local date and time
     * @return the formatted timestamp string
     */
    public static String formatDateTimeForAdminLog(Instant instant, ZoneId zoneId) {
        return formatInstant(instant, zoneId, "dd/MM/yyyy HH:mm:ss.SSS");
    }

    /**
     * Formats {@code instant} using the ISO8601 format in UTC.
     * Example: 2011-12-03T10:15:30Z
     *
     * <p>Used to inject a standardized date into date elements in Teammates for sortable tables.
     * Should not be used for anything user-facing.</p>
     *
     * @param instant the instant to be formatted
     * @return the formatted datetime ISO8601 stamp in UTC
     */
    public static String formatDateTimeToIso8601Utc(Instant instant) {
        return instant == null ? null : DateTimeFormatter.ISO_INSTANT.format(instant);
    }

    /**
     * Returns whether the given {@code instant} is being used as a special representation, signifying its face value
     * should not be used without proper processing.
     *
     * <p>A {@code null} instant is not a special time.</p>
     *
     * @param instant the instant to test
     * @return {@code true} if the given instant is used as a special representation, {@code false} otherwise
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
     * Parses an {@code Instant} object from a datetime string in the {@link SystemParams#DEFAULT_DATE_TIME_FORMAT}.
     *
     * @param dateTimeString should be in the format {@link SystemParams#DEFAULT_DATE_TIME_FORMAT}
     * @return the parsed {@code Instant} object
     * @throws AssertionError if there is a parsing error
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
     * Parses a {@code LocalDateTime} object from a datetime string and parsing pattern.
     *
     * @param dateTimeString the string containing the datetime
     * @param pattern        the parsing pattern of the datetime string
     * @return the parsed {@code LocalDateTime} object, or {@code null} if there are errors
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
     * Parses a {@code LocalDate} object from a date string and parsing pattern.
     *
     * @param dateString the string containing the date
     * @param pattern    the parsing pattern of the datetime string
     * @return the parsed {@code LocalDate} object, or {@code null} if there are errors
     */
    public static LocalDate parseLocalDate(String dateString, String pattern) {
        if (dateString == null || pattern == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a {@code LocalDate} object from a date string.
     * Example: date "Tue, 01 Apr, 2014"
     *
     * @param date date in format "EEE, dd MMM, yyyy"
     * @return the parsed {@code LocalDate} object, or {@code null} if there are errors
     */
    public static LocalDate parseDateFromSessionsForm(String date) {
        return parseLocalDate(date, "EEE, dd MMM, yyyy");
    }

    /**
     * Parses a {@code LocalDateTime} object from separated date, hour and minute strings.
     * Example: date "Tue, 01 Apr, 2014", hour "23", min "59"
     *
     * @param date date in format "EEE, dd MMM, yyyy"
     * @param hour hour-of-day (0-23)
     * @param min  minute-of-hour (0-59)
     * @return the parsed {@code LocalDateTime} object, or {@code null} if there are errors
     */
    public static LocalDateTime parseDateTimeFromSessionsForm(String date, String hour, String min) {
        LocalDate localDate = parseDateFromSessionsForm(date);
        if (localDate == null) {
            return null;
        }
        if (hour == null || min == null) {
            return null;
        }
        try {
            return localDate.atTime(Integer.parseInt(hour), Integer.parseInt(min));
        } catch (DateTimeException | NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a date string and a time string of only the hour into a LocalDateTime object.
     * If the {@code inputTimeHours} is "24", it is converted to "23:59".
     *
     * @param inputDate      date in format "EEE, dd MMM, yyyy"
     * @param inputTimeHours hour-of-day (0-24)
     * @return the parsed {@code LocalDateTime} at the specified date and hour, or null for invalid parameters
     */
    public static LocalDateTime parseDateTimeFromSessionsForm(String inputDate, String inputTimeHours) {
        if ("24".equals(inputTimeHours)) {
            return parseDateTimeFromSessionsForm(inputDate, "23", "59");
        }
        return parseDateTimeFromSessionsForm(inputDate, inputTimeHours, "0");
    }

    /**
     * Parses a {@code ZoneId} object from a string.
     * Example: "Asia/Singapore" or "UTC+04:00".
     *
     * @param timeZone a string containing the zone ID
     * @return {@code ZoneId.of(timeZone)}, or {@code null} if {@code timeZone} is invalid.
     */
    public static ZoneId parseZoneId(String timeZone) {
        if (timeZone == null) {
            return null;
        }

        try {
            return ZoneId.of(timeZone);
        } catch (DateTimeException dte) {
            return null;
        }
    }
}
